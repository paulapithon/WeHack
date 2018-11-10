package br.com.elo.lio.api;

import br.com.elo.lio.model.User;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers("{'MerchantId':'d9a6696f-708e-4c58-9977-62290337944d', 'MerchantKey':'NYSUGODOTIOIFTPGQWWGOTPEJVXAYRVGIJTFJYGT'}")
    @POST("/payment")
    Call<User> pay(@Body String user);

}
