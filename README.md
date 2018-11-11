# Argen

## O nosso time incrível

- Paula Pithon
- Paula Soares
- Ramon Wanderley
- Otacilio Maia
- Michael Barney

## Arquitetura

### Cadastrar
  
<img src="https://proxydata.com.br/gestao/share/adm_usuarios/59ca9e4d98f19.png" alt="drawing" width="200"/>
  
Para cadastro foi utilizada a API de [Cadastro do portador](https://dev.elo.com.br/apis/cadastro-do-portador) da Elo, liberando o cartão do usuário para consulta de dados, o código se encontra no folder [APIElo](https://github.com/OtacilioN/WeHack-Argen-4/tree/master/APIElo)

### Pagamento

<img src="https://upload.wikimedia.org/wikipedia/commons/thumb/b/bb/Logo_of_Cielo.svg/1200px-Logo_of_Cielo.svg.png" alt="drawing" width="200"/>

No pagamento foram utilizadas as APIs de [Tokenização de cartões](https://developercielo.github.io/manual/cielo-ecommerce) e [pagamento através de Token](https://developercielo.github.io/manual/cielo-ecommerce)

O código relativo ao pagamento está no folder [BackEnd](https://github.com/OtacilioN/WeHack-Argen-4/tree/master/BackEnd) junto com o código relativo ao backend do ChatBot

### ChatBot

Para o desenvolvimento do fluxo conversacional do ChatBot foi utilizado o [DialogFlow](http://dialogflow.com) utilizando a arquitetura "WebHook Proxy", onde invertemos o fluxo tradicional da plataforma, utilizando da seguinte maneira:

Cliente (Telegram, WhatsApp, Messenger, etc) -> WebHook -> DialogFlow -> WebHook -> Cliente (Telegram, WhatsApp, Messenger, etc)

Permitindo que o bot não seja restrito apenas a uma postura reativa, mas que também inicie diálogos a partir e ações estratégicas, como por exemplo, in bot push notifications.

Todos os arquivos que descrevem o Bot estão disponíveis no folder [ChatBot](https://github.com/OtacilioN/WeHack-Argen-4/tree/master/ChatBot)

### WebHook

O nosso WebHook está condensado junto com o módulo de pagamento no folder [BackEnd](https://github.com/OtacilioN/WeHack-Argen-4/tree/master/BackEnd) o WebHook é responsável por fornecer os dados necessários para o ChatBot, tais como o QRCode, itens do carrinho, nome do usuário, etc. 

O WebHook também é responsável por gerar os QR Codes e cuidar de toda a lógica de negócio, atualmente utilizamos o serviço Amazon Cloud9 para hospedar e rodar o webhook em ambiente de desenvolvimento, servindo nosso WebHook através de https.
