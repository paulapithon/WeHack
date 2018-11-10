package br.com.elo.lio.model;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class Produto implements Serializable{

    private String nome;
    private int valor;
    private int quantidade;

    public void decode(String string) {
        try {
            JSONObject jsonObject = new JSONObject(string);
            nome = jsonObject.getString("nome");
            valor = jsonObject.getInt("valor");
            quantidade = jsonObject.getInt("quantidade");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String encode() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("nome", nome);
            jsonObject.put("valor", valor);
            jsonObject.put("quantidade", quantidade);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();

    }

    public int getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public int getValor() {
        return valor;
    }

    public void setValor(int valor) {
        this.valor = valor;
    }
}
