var express = require('express');
var router = express.Router();
const moment = require('moment');
const Elo = require('../elo.js');
var args = {
    // serverKeyEndpoint: 'https://hml-api.elo.com.br/user/v1/publickeys',
    graphqlEndpoint: 'https://hml-api.elo.com.br/graphql-private',
    client_id: 'bc46c745-550d-373f-b26a-00859a22a31a',
    secret: '21cef1e4-b749-3cb1-8d7c-629ca1e731cf',
    debug: true
};
const email = "tato@hotmail.com";
const senha = "123456";
const elo = new Elo(args);

/* GET users listing. */
router.get('/cards', function(req, res, next) {
    const authorization = elo.authorization
    const bcryptPassword = elo.getBcryptPassword(email, senha);
    elo.login(email, senha).then(async (accessToken) => {
      const cards = await elo.getCards(accessToken);
        res.send(cards)
    });
});    
router.get('/historico', function(req, res, next) {
    const authorization = elo.authorization
    const bcryptPassword = elo.getBcryptPassword(email, senha);
    elo.login(email, senha).then(async (accessToken) => {
      const historico = await elo.historico(accessToken);
        res.send(historico)
    });
});
  
  
  


module.exports = router;
