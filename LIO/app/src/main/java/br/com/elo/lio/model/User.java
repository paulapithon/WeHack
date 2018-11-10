package br.com.elo.lio.model;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * LG ELECTRONICS INC.
 * LGEBR - SCA R&D, Sao Paulo, SP, Brazil
 * LGEMR - Santa Clara, CA, USA
 * Copyright(c) 2017 by LG Electronics Inc.
 * <p>
 * <p>
 * All rights reserved. No part of this work may be reproduced, stored in a retrieval system, or transmitted by any
 * means without prior written Permission of LG Electronics Inc.
 */
public class User {

    private String nome;
    private String CPF;
    private String email;
    private String ID;
    private int wallet;

    public void encode(String user) {
        try {
            JSONObject jsonObject = new JSONObject(user);
            ID = jsonObject.getString("id");
            nome = jsonObject.getString("nome");
            CPF = jsonObject.getString("cpf");
            email = jsonObject.getString("email");
            if (jsonObject.has("wallet")) {
                wallet = jsonObject.getInt("wallet");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String decode() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("id", ID);
            jsonObject.put("nome", nome);
            jsonObject.put("cpf", CPF);
            jsonObject.put("email", email);
            jsonObject.put("wallet", wallet);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    public int getWallet() {
        return wallet;
    }

    public void setWallet(int wallet) {
        this.wallet = wallet;
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
