package br.com.elo.lio.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class User implements Serializable {

    private String nome;
    private String CPF;
    private String email;
    private String ID;
    private int wallet;
    private String timestamp;
    private List<Produto> produtos;

    public void decode(String user) {
        try {
            JSONObject jsonObject = new JSONObject(user);
            ID = jsonObject.getString("id");
            nome = jsonObject.getString("nome");
            CPF = jsonObject.getString("cpf");
            email = jsonObject.getString("email");
            if (jsonObject.has("wallet")) {
                wallet = jsonObject.getInt("wallet");
            }
            if (jsonObject.has("time")) {
                timestamp = jsonObject.getString("time");
            }
            produtos = new ArrayList<>();
            if (jsonObject.has("produtos")) {
                JSONArray produtoArray = jsonObject.getJSONArray("produtos");
                for (int i = 0; i < produtoArray.length(); i++) {
                    Produto produto = new Produto();
                    produto.decode(produtoArray.getString(i));
                    produtos.add(produto);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String encode() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("id", ID);
            jsonObject.put("nome", nome);
            jsonObject.put("cpf", CPF);
            jsonObject.put("email", email);
            jsonObject.put("wallet", wallet);
            jsonObject.put("time", timestamp);

            JSONArray jsonArray = new JSONArray();
            for(Produto produto : produtos) {
                jsonArray.put(produto.encode());
            }
            jsonObject.put("produtos", jsonArray);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    public List<Produto> getProdutos() {
        return produtos;
    }

    public void setProdutos(List<Produto> produtos) {
        this.produtos = produtos;
        updateWallet();
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public int getWallet() {
        return wallet;
    }

    public void setWallet(int wallet) {
        this.wallet = wallet;
    }

    private void updateWallet() {
        wallet = 0;
        for (Produto produto : produtos) {
            wallet += produto.getValor() * produto.getQuantidade();
        }
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCPF() {
        return CPF;
    }

    public void setCPF(String CPF) {
        this.CPF = CPF;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }
}
