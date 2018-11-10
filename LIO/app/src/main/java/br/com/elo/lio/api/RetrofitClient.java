package br.com.elo.lio.api;
import com.google.gson.GsonBuilder;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    public static APIService getAPIService() {
        GsonBuilder gsonBuilder = new GsonBuilder().serializeNulls();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://elo-michaelbarney.c9users.io")
                .addConverterFactory(GsonConverterFactory.create(gsonBuilder.create()))
                .build();

        return retrofit.create(APIService.class);
    }
}
