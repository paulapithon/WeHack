const crypto = require('crypto');
const bcrypt = require('bcryptjs');
const fs = require('fs-extra');
const jose = require('node-jose');
const fetch = require('node-fetch');
const _ = require('lodash');

const keyPairDir = (client_id) => {
    return `./saved-keys/${client_id}`;
}

const saveKeyPair = (key, client_id) => {
    fs.mkdirpSync(keyPairDir(client_id));
    fs.writeFileSync(`${keyPairDir(client_id)}/keypair.json`, JSON.stringify(key, null, '\t'));
}

//mutations configurados para requisições
const mutations = {
    createCardHolderForUser:` 
    mutation ($accessToken: ID!){
            createCardHolderForUser(input:{
                clientMutationId: "123",
                userId: $accessToken,
                companyName: "companyName",
            })
            {
                clientMutationId,
                user{
                    id,
                    verified,
                    name,
                    displayName
                },
                cardHolder{
                    id,
                    name,
                    companyName
                }
            }
        }
    `
    ,
    create:`
    mutation ($bcryptPassword: String!, $email: String!) {
            createUser(input:{
                clientMutationId: "100",
                username: $email,
                bcryptPassword: $bcryptPassword,
                name: "Ramon Wanderley",
                firstName: "Ramon",
                lastName: "Wanderley",
                displayName: "Ramon Wanderley",
                contacts: [{type: PHONE, context: "Principal", value: "+5519012345678"}, {type: EMAIL, context: "", value: $email}]
                legalIds:{
                  cpf: "25223141814"
                }
            })
            {
                clientMutationId,
                id,
                name
            }
        }
        `,
    login: `
        mutation ($clientMutationId: String!, $username: String!, $challenge: String!) {
            login(input:{
                clientMutationId: $clientMutationId
                username: $username
                challenge: $challenge
            })
            {
                clientMutationId
                accessToken
            }
        }
    `,
    createLoginSalt: `
        mutation ($clientMutationId: String!, $username: String!) {
            createLoginSalt(input: {
                clientMutationId: $clientMutationId,
                username: $username
            }) {
                clientMutationId
                username
                salt
                expiry
            }
        }
    `,
    addPublicKeyToUserMutation: `
        mutation ($clientMutationId: String!, $accessToken: ID!, $key: String!) {
            addPublicKeyToUser(input:{
                clientMutationId: $clientMutationId,
                userId: $accessToken,
                key: $key,
                format:JWK
            })
            {
                clientMutationId,
                user{
                    id,
                    firstName,
                    lastName
                },
                publicKey{
                    key
                }
            }
        }
    `,
    createCard:`
            mutation ($id: ID!, $sensitive: String!) {
            createCard(input: {
                clientMutationId: "123",             
                sensitive: $sensitive,
                holderId: $id,
                billingAddress: {
                    context: "Casa",
                    number: 123,
                    country: "BRA",
                    city: "Campinas",
                    state: "São Paulo",
                    zip: "1234",
                    place: "via"
                }
            })
            {
                clientMutationId,
                card {
                    id,
                    last4,
                    billingAddress{
                    context,
                    country,
                    city
                    }
                }
            }
        }`
}

const queries = {
    cardHolderId: `
        query ($id: String!) {
            user(id: $id) {
                id,
                name
                cardHolders {
                    id
                }
            }
        }
    `
    ,
    queryServerPublicKey:`
    query {
    serverPublicKey {
        key
     }
    }`
    ,
    queryCards:
    `query {  
    user {
        cardHolders{
            cards{
                edges{
                    node{
                        id
                        last4
                        expiry{
                            month
                            year
                        }
                    }
                }
            }
        }
    }
}`,
    historico:`query {
    user {
        cardHolders {
            cards {
                edges {
                    node {
                        transactionsSummary(filter: {
                            startTimestamp: "2017-10-01T00:00:00Z", 
                            endTimestamp: "2017-12-07T00:00:00Z",
                            includeMerchantCategories:[{ min:7, max:8 }], 
                            excludeMerchantCategories:[{ min:1, max:6 }]
                        }) 
                        {
                            category{ id, iso , name }, 
                            count, 
                            value
                        }
                    }
                }
            }
        }
    }
}
`
    
}

class Elo {

    constructor ({ serverKeyEndpoint, graphqlEndpoint, client_id, secret, debug = false }) {
        this.serverKeyEndpoint = serverKeyEndpoint;
        this.graphqlEndpoint = graphqlEndpoint;
        this.client_id = client_id;
        this.secret = secret;
        this.debug = debug;
      
        this.authorization = 'Basic ' + Buffer.from(`${client_id}:${secret}`).toString('base64');
        console.log("Authorization: ", this.authorization);
        // if (this.debug) {
        //     console.log("Authorization: ", this.authorization);
        // }
    }

    // --------------
    // Login methods
    // --------------

    async login(username, password) {
        // if (this.debug) {
            
        // }
        console.log("Start Login");
        const challenge = await this.getChallenge(username, password);

        return await this.graphql({
            headers: {
                'client_id': this.client_id,
                'Authorization': this.authorization
            },
            query: mutations.login,
            variables: {
                clientMutationId: '121',
                username: username,
                challenge: challenge
            }
        })
        .then(async (response) => {
            const accessToken = _.get(response, 'body.data.login.accessToken');
            console.log('accessToken: ', accessToken);    
            // if (this.debug) {
            //     console.log('accessToken: ', accessToken);
            // }
             this.access_token = accessToken
            return accessToken;
        })
    }

    async getChallenge(username, password) {
        // if (this.debug) {
        //     console.log("Getting challenge");
        // }
        console.log("Getting challenge");
        let bcryptPassword = this.getBcryptPassword(username, password);
        console.log("Getting loginSalt");
        // if (this.debug) {
        //     console.log("Getting loginSalt");
        // }

        // Tendo o bcryptPassword em mãos é preciso obter o loginSalt de seu username
        // para isso execute a mutation `createLoginSalt` no graphql

        return await this.graphql({
            headers: {
                'client_id': this.client_id
            },
            query: mutations.createLoginSalt,
            variables: {
                clientMutationId: '100',
                username: username
            }
        })
        .then(async (response) => {
            const salt = _.get(response, 'body.data.createLoginSalt.salt');
            console.log('   loginSalt: ', salt);
            // if (this.debug) {
            //     console.log('   loginSalt: ', salt);
            // }

            return salt;
        })
        .then((salt) => {
            // Aplicar o algoritmo bcrypt utilizando o bcryptPassword e o salt gerado no item anterior.
            let challenge = bcrypt.hashSync(bcryptPassword, salt);
            console.log('   challenge: ', challenge);
            // if (this.debug) {
            //     console.log('   challenge: ', challenge);
            // }

            return challenge;
        });
    }
    
    getBcryptPassword(username, password) {
        // if (this.debug) {
        //     console.log("Getting bcryptPassword");
        // }
        console.log("Getting bcryptPassword");
        // 1º Passo: Gerar um salt a partir do nome de usuário:
        // Aplicar o algoritmo SHA256 no nome do usuário e reservar os 16 primeiros caracteres.
        let hash = crypto.createHash('sha256').update(username).digest();

        // Concatenar o resultado do item anterior com o texto "$2a$12". Exemplo de resultado:
        let usernameSalt = `$2a$12$${ bcrypt.encodeBase64(hash, 16) }`; // Ex.: $2a$12$N9qo8uLOickgx2ZMRZoMye

        // 2º Passo: Aplicar o algoritmo bcrypt utilizando a senha do usuário e o salt gerado no primeiro passo:
        // Aplicar o algoritmo SHA256 na senha do usuário.
        // Aplicar a codificação Base64 sobre o resultado do item anterior.
        let sha256Password = crypto.createHash('sha256').update(password)
                                                        .digest('base64');

        // Aplicar o algoritmo bcrypt utilizando o resultado do item anterior e o salt gerado no primeiro passo.
        let bcryptPassword = bcrypt.hashSync(sha256Password, usernameSalt);
        // uhull \o/ temos o bcryptPassword

        // if (this.debug) {
        //     console.log('   bcryptPassword:', bcryptPassword);
        // }
         console.log('   bcryptPassword:', bcryptPassword);
        return bcryptPassword;
    }

    //--------------------
    // CreateUser
    //--------------------
    
   async createCardHolderForUser(accessToken) {
        // if (this.debug) {
        //     console.log("Start Create Card Holder");
        // }
        console.log("Start Create Card Holder");
        return await this.graphql({
            headers: {
                'client_id': this.client_id,
                'access_token': accessToken
            },
            query: mutations.createCardHolderForUser,
            variables: {
               accessToken : accessToken,
            }
        })
        .then(async (response) => {
            const cardHolderId = _.get(response, 'body.data');

            // if (this.debug) {
            //     console.log('CardHolderId: ', cardHolderId);
            // }
            console.log('CardHolderId: ', cardHolderId);
            return cardHolderId;
        }).catch(async (error) => { 
            
            console.log(error);
            return "não funcionou a criação do HolderId"});
    }
    
   async create(username, password) {
        // if (this.debug) {
        //     console.log("Start Create");
        // }
        console.log("Start Create");
        const bcryptPassword = this.getBcryptPassword(username, password)
        console.log(bcryptPassword)
        return await this.graphql({
            headers: {
                'client_id': this.client_id,
                'Authorization': this.authorization
            },
            query: mutations.create,
            variables: {
               bcryptPassword : bcryptPassword,
               email: username
            }
        })
        .then(async (response) => {
            const accessToken = _.get(response, 'body.data.id');
            console.log('accessToken: ', accessToken);
            // if (this.debug) {
            //     console.log('accessToken: ', accessToken);
            // }
            this.access_token = accessToken
            return accessToken;
        }).catch(async (error) => { 
            
            console.log(error);
            return "haha"});
    }
    // --------------------
    // Create Card Methods
    // --------------------
    async createCard(sensitive, id , accessToken){
        //   if (this.debug) {
        //     console.log("Start add card in user");
        // }
        console.log("Start add card in user");
        return await this.graphql({
            headers: {
                'client_id': this.client_id,
                'access_token': accessToken
            },
            query: mutations.createCard,
            variables: {
               id : id,
               sensitive : sensitive
            }
        })
        .then(async (response) => {
            const result = _.get(response, 'body.data');
            console.log('Resultado: ', result);
            // if (this.debug) {
            //     console.log('Resultado: ', result);
            // }

            return result;
        }).catch(async (error) => { 
            
            console.log(error);
            return "não funcionou a criação"});
    
    }
    
    async getCardHolderId(accessToken) {
        return await this.graphql({
            headers: {
                'client_id': this.client_id,
                'access_token': accessToken
            },
            query: queries.cardHolderId,
            variables: { id: accessToken }
        })
        .then(async (response) => {
            // const cardHolderId = _.get(response.body, 'data.user.cardHolders[0].id');
            const cardHolderId = _.get(response.body, 'data');
            console.log(response.body.data)
            // if (this.debug) {
            //     console.log('cardHolderId:', cardHolderId);
            // }
            console.log('cardHolderId:', cardHolderId);
            return cardHolderId;
        });
    }

    async fetchOrGenerateJWK(keyId, client_id) {
        const filename = `${keyPairDir(client_id)}/keypair.json`;

        if (fs.existsSync(filename)) {
            return require(filename);
        }

        return this.generateJWK(keyId, client_id);
    }

    async generateJWK(keyId, client_id) {
        const jwk = await jose.JWK.createKey("EC", "P-256", { kid: keyId })

        const key = {
            pair: jwk.toJSON(true),
            public: jwk.toJSON(false)
        }

        saveKeyPair(key, client_id);

        return key;
    }

    async addPublicKeyToUser(key, accessToken) {
        //delete key.public['kid'];
        const keyString = JSON.stringify(key.public)
        console.log(keyString);
        
        return await this.graphql({
            headers: {
                'client_id': this.client_id,
                'access_token': accessToken
            },
            query: mutations.addPublicKeyToUserMutation,
            variables: {
                clientMutationId: '123',
                accessToken: accessToken,
                key: keyString
            }
        })
        .then((response) =>{  
            
            const result = _.get(response,'body.data.addPublicKeyToUser.publicKey.key')
            
            console.log('key: '+ result)
            })
        .catch((error) => console.log('ta'));
    }

    async getSensitive(myKey, card, accessToken) {
        // Objeto do cartão é assinado por sua chave privada
    
        const signed = await this.sign(myKey.pair,card);

        // Buscando chave pública na plataforma Elo
        const serverKey = await this.getServerKey(accessToken);

        // Criptografando o documento assinado com chave publica da plataforma Elo
        const sensitive = await this.encrypt(serverKey, signed);

        // if (this.debug) {
        //     console.log("Sensitive: ", sensitive);
        // }
        
        console.log("Sensitive: ", sensitive);
        return sensitive;
    }
     async historico(accessToken){
        return await this.graphql({
            headers: {
                'client_id': this.client_id,
                'access_token': accessToken
            },
            query: queries.historico,
        })
        .then(async (response) => {
            // const cardHolderId = _.get(response.body, 'data.user.cardHolders[0].id');
            const historicos = _.get(response.body, 'data');
            console.log(response.body.data)
            // if (this.debug) {
            //     console.log('cardHolderId:', cardHolderId);
            // }
            console.log('historico:', historicos);
            return historicos;
        });
     }
    async getCards(accessToken){
        return await this.graphql({
            headers: {
                'client_id': this.client_id,
                'access_token': accessToken
            },
            query: queries.queryCards,
        })
        .then(async (response) => {
            // const cardHolderId = _.get(response.body, 'data.user.cardHolders[0].id');
            const cards = _.get(response.body, 'data');
            console.log(response.body.data)
            // if (this.debug) {
            //     console.log('cardHolderId:', cardHolderId);
            // }
            console.log('cards:', cards);
            return cards;
        });
     }
    async sign(key, object) {
        return await jose.JWS.createSign({ format: 'compact', alg: 'ES256' }, key)
                             .update(JSON.stringify(object))
                             .final();
    }

    async encrypt(key, data) {
        return await jose.JWE.createEncrypt({ format: 'compact' }, key)
                             .update(data)
                             .final();
    }

    //

    graphql({ endpoint, headers, query, variables }) {
        endpoint = endpoint || this.graphqlEndpoint;

        headers = _.defaults(headers, {
            'Content-Type': 'application/json'
        });

        return fetch(endpoint, {
            method: 'POST',
            headers: headers,
            body: JSON.stringify({
                query: query,
                variables: variables,
            })
        })
        .then(async (response) => {
            const body = await response.json();

            if (body.errors) {
                return console.log("Error:", body.errors[0].message);
            }

            response.body = body;

            return response;
        })
    }

    async getServerKey(accessToken) {

        const serverKeyPath = './saved-keys/server-key.json';

        if (fs.existsSync(serverKeyPath)) {
            return require(serverKeyPath);
        }

        return await fetch(this.serverKeyEndpoint, {
            method: "GET",
            headers: {
                client_id: this.client_id,
                access_token: accessToken
            },
        }).then(async (response) => {
            const json = await response.json()

            fs.writeFileSync(serverKeyPath, JSON.stringify(json));

            return json;
        });
    }
}

module.exports = Elo;
