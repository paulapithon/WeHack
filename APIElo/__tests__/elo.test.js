const Elo = require('../elo.js');

beforeAll(() => {
    jest.setTimeout(30000)

    this.args = {
        serverKeyEndpoint: 'http://hml-api.elo.com.br/user/v1/publickeys',
        graphqlEndpoint: 'http://hml-api.elo.com.br/graphql-private',
        client_id: '3c1b0c35-bd18-359c-ba20-cba732f14164',
        secret: 'ef2d5b4c-379e-3712-9899-5eb906c4a424',
        debug: true
    };

    this.elo = new Elo(this.args);
});

test('Fornecendo client_id e secret o authorization deve ser gerado corretamente', () => {
    const authorization = this.elo.authorization;

    const correctAuthorization = 'Basic ' + Buffer.from(`${this.args.client_id}:${this.args.secret}`).toString('base64');

    expect(authorization).toBe(correctAuthorization);
});

test('Com as credenciais corretas, o login é efetuado com sucesso retornando hash string com accessToken de 36 caracteres', async () => {
    const accessToken = await this.elo.login("rafael.nunes@gmail.com", "123456");

    expect(typeof(accessToken)).toBe('string');
    expect(accessToken.length).toBe(36);
});

test('Com as credenciais incorretas, o login deve retornar accessToken undefined', async () => {
    const accessToken = await this.elo.login("rafael.nunes@gmail.com", "12345");
    expect(accessToken).toBeUndefined();
});

