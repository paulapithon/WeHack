package br.com.elo.lio.util;

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
public class StoreConstants {

    public static String AppJson = "application/json";
    public static String MerchantId = "d9a6696f-708e-4c58-9977-62290337944d";
    public static String MerchantKey = "NYSUGODOTIOIFTPGQWWGOTPEJVXAYRVGIJTFJYGT";

    public static String encode () {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("MerchantId", MerchantId);
            jsonObject.put("MerchantKey", MerchantKey);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

}
