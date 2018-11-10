package br.com.elo.lio.api;

import org.json.JSONObject;

import br.com.elo.lio.model.User;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface APIService {
    @POST("/payment")
    Call<User> pay(
            @Header("Content-Type") String contentType,
            @Header("MerchantId") String merchantID,
            @Header("MerchantKey") String merchantKey,
            @Body JSONObject user
    );

    @POST("/update")
    Call<User> update(
            @Header("Content-Type") String contentType,
            @Header("MerchantId") String merchantID,
            @Header("MerchantKey") String merchantKey,
            @Body JSONObject user
    );

}
