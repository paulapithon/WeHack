const moment = require('moment');

const Elo = require('./elo.js');

var args = {
    // serverKeyEndpoint: 'https://hml-api.elo.com.br/user/v1/publickeys',
    graphqlEndpoint: 'https://hml-api.elo.com.br/graphql-private',
    client_id: 'bc46c745-550d-373f-b26a-00859a22a31a',
    secret: '21cef1e4-b749-3cb1-8d7c-629ca1e731cf',
    debug: true
};
//conta padrão para testes
const email = "tato@hotmail.com";
const senha = "123456";
const elo = new Elo(args);

// Para obter a authorization:
const authorization = elo.authorization

// Caso queira apenas o bcryptPassword:
const bcryptPassword = elo.getBcryptPassword(email, senha);


//Cria o usuário na aplicação da Elo
// elo.create(email,senha).then(async (acessToken1) => {
//     console.log(acessToken1)
//     acessToken = acessToken1
//     elo.createCardHolderForUser(accessToken).then(async (cardHolderId1) => {
//     cardHolderId = cardHolderId1
//     console.log("cheguei aqui fora")
// })
//     console.log("GO!!")
// })

// Caso queira apenas o challenge:
const challenge = elo.getChallenge(email, senha);

// Efetue o login obtendo internamente o bcryptPassword e challenge
elo.login(email, senha).then(async (accessToken) => {
        // elo.createCardHolderForUser(accessToken).then(async (cardHolderId1) => {
        // cardHolderId = cardHolderId1
        // console.log("cheguei aqui fora")
        // });
        
    let key = await elo.fetchOrGenerateJWK('my-key-id', args.client_id);
       
  // Registre sua chave na plataforma Elo
    await elo.addPublicKeyToUser(key, accessToken)

   //Informações para criar o sensitive
    //em um objeto como o exemplo abaixo
    const card = {
        pan: "5094827890123456",
        expiry: {
            month: "02",
            year: "2020"
        },
        name: "João da Silva",
        csc: "012",
        cscEntryTime: moment("2017-01-00").toISOString(),
        authCode: "AB123Z1Y",
        authCodeEntryTime: moment("2017-01-00").toISOString()
    };

//     // // Gerar sensitive utilizando chave public e chave do servidor
//     // // Veja mais detalhes dentro da função
    const sensitive = elo.getSensitive(key, card);
    
//     // // // Consulte o cardHolderId
    const cardHolderId = await elo.getCardHolderId(accessToken);
});
