![elo.jpg](elo.jpg)
# elo-client-sdk-js

O objetivo desta lib é facilitar a integração para clients da Plataforma Elo utilizando `javascript`.  
A classe contida no arquivo `elo.js` facilita a criação de campos necessários para criar usuário, login, criar cartão.

## Adicionando ao seu projeto
1. Adicione o arquivo `elo.js` dentro de seu projeto
2. Adicione as dependências necessárias:
```bash
npm install --save bcryptjs fs-extra generate-rsa-keypair json-to-graphql-query lodash moment node-fetch node-jose
```

3. Importe a classe no arquivo necessário e instancie um objeto js com as configurações para seu client:

```javascript
const Elo = require('./elo.js');

const args = {
    serverKeyEndpoint: 'http://hml-api.elo.com.br/user/v1/publickeys',
    graphqlEndpoint: 'http://localhost:5000/graphql-private',
    client_id: '<client-id>',
    secret: '<client-secret>',    
    debug: true
};

const elo = new Elo(args);
```  

## Métodos e atributos disponíveis


| Acessar com              | Descrição                                                                                                                |
| ------------------------ | ------------------------------------------------------------------------------------------------------------------------ |
| elo.authorization        | Utilizado na header das requisições que necessitam de autorização                                                        |
| elo.getBcryptPassword()  | Parâmetro utilizado em createUser e na criação do challenge para o login                                                 |
| elo.getChallenge()       | Parâmetro utilizado na mutation login                                                                                     |
| elo.fetchOrGenerateJWK() | Busca uma chave local para o client_id utilizado na pasta `saved-keys` ou gera uma nova chave utilizando o algoritmo JWK |
| elo.getSensitive()       | Assina e criptografa informações de um cartão. Este parametro é utilizado na mutation createCard                         |


## Testando os recursos

Caso queira efetuar testes, o arquivo `usage.js` possuí exemplos de utilização da classe, altere as informações de `args` necessárias para seu client e depois é possível executar:  
```bash
npm install  

node usage.js
```

