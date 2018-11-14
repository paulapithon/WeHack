# Eloise

![Eloise](https://image.ibb.co/g9hpLL/eloise-preto-transparente.png)

_Prazer, eu sou a Eloise, mas pode me chamar de Elô, a sua nova inteligência artificial para assistir as suas compras._

A Elô, além de é uma interface conversacional multiplataforma, é uma plataforma de pagamento completa e transparente ao usuário. A Eloise passa a ser integrada a uma plataforma de pagamento em **QRcode**, não mais baseada na transação, e sim, na autenticação do usuário.

Cada QR code gerado não diz respeito a um valor a ser pago, como acontece com esse formato de pagamento atualmente, e sim, a uma sessão. Essa sessão pode representar qualquer coisa! Um carrinho de compras, uma bandeja de restaurante ou uma sacola de roupas, o que importa é que ela vai te autenticar em frente aos estabelecimentos que possuam a maquininha com a aplicação de validação da EloiseLIO.

![Tutorial](https://preview.ibb.co/f8M5uf/tutorial.png)

O time ficou em primeiro lugar no **WeHack 2018**, além de ganhar melhor desenvolvedor Back-end e melhor analista de negócios. Para mais informações, veja nosso [pitch](https://drive.google.com/open?id=1giNGhuFBLDIdTxHOO3fxE-fo6EgtRbJelmEI_VL_lVI). Você
também já pode conversar com a [Eloise](https://t.me/elo_chatbot)!

## O nosso time incrível

- [Michael Barney](https://www.linkedin.com/in/michael-barney-junior/): Desenvolvedor Back-end.
- [Otacilio Maia](https://www.linkedin.com/in/otacilio/): Desenvolvedor Front-end (Chatbot);
- [Paula Pithon](https://www.linkedin.com/in/paulapithon/): Desenvolvedora Front-end (Android) e analista de negócios;
- [Paula Soares](https://www.linkedin.com/in/soaresdelapaula/): Designer UX/UI;
- [Ramon Wanderley](https://www.linkedin.com/in/ramonwanderley/): Desenvolvedor Back-end;

## Arquitetura

### API de Cadastro
<img src="https://proxydata.com.br/gestao/share/adm_usuarios/59ca9e4d98f19.png" alt="drawing" width="200"/>
  
Para cadastro foi utilizada a API de [Cadastro do portador](https://dev.elo.com.br/apis/cadastro-do-portador) da **Elo**, liberando o cartão do usuário para consulta de dados. O código se encontra no folder [APIElo](https://github.com/OtacilioN/WeHack-Argen-4/tree/master/APIElo).

### API de Pagamento
<img src="https://upload.wikimedia.org/wikipedia/commons/thumb/b/bb/Logo_of_Cielo.svg/1200px-Logo_of_Cielo.svg.png" alt="drawing" width="200"/>

No pagamento foram utilizadas as APIs de [Tokenização de cartões](https://developercielo.github.io/manual/cielo-ecommerce) e [pagamento através de Token](https://developercielo.github.io/manual/cielo-ecommerce) da **Cielo**. O código relativo ao pagamento está no folder [BackEnd](https://github.com/OtacilioN/WeHack-Argen-4/tree/master/BackEnd) junto com o código relativo ao backend do ChatBot.

### ChatBot
Para o desenvolvimento do fluxo conversacional do ChatBot foi utilizado o [DialogFlow](http://dialogflow.com) utilizando a arquitetura "WebHook Proxy", onde invertemos o fluxo tradicional da plataforma, utilizando da seguinte maneira:

_Cliente (Telegram, WhatsApp, Messenger, etc) -> WebHook -> DialogFlow -> WebHook -> Cliente (Telegram, WhatsApp, Messenger, etc)_

Isso permite que o bot **não seja restrito** apenas a uma postura reativa, mas que **também inicie diálogos** a partir e ações estratégicas, como por exemplo, **in bot push notifications**. Todos os arquivos que descrevem o Bot estão disponíveis no folder [ChatBot](https://github.com/OtacilioN/WeHack-Argen-4/tree/master/ChatBot)

### WebHook
O nosso WebHook está condensado junto com o módulo de pagamento no folder [BackEnd](https://github.com/OtacilioN/WeHack-Argen-4/tree/master/BackEnd). O WebHook é responsável por fornecer os dados necessários para o ChatBot, tais como o **QRCode**, itens do carrinho, nome do usuário, etc. 

O WebHook também é responsável por gerar os QR Codes e cuidar de toda a lógica de negócio, atualmente utilizamos o serviço **Amazon Cloud9** para hospedar e rodar o webhook em ambiente de desenvolvimento, servindo nosso WebHook através de https.

### Eloise Lio App
Eloise **Lio** App é uma aplicação Android desenvolvida para rodar perfeitamente na máquina Lio da Cielo, tem como objetivo realizar a leitura do QR Code e se comunicar com o WebHook, enviando o id de usuário obtido e os itens consumidos para que o Webhook. O código de Eloise Lio App está disponível em [LIO](https://github.com/OtacilioN/WeHack-Argen-4/tree/master/LIO)
