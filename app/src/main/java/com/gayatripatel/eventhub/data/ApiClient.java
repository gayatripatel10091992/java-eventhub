package com.gayatripatel.eventhub.data;

import com.google.gson.Gson;
import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public final class ApiClient {
    public static final String BASE_URL = "https://api.eventhub.example.com/";

    private ApiClient() {
    }

    public static EventApi create(Clock clock, boolean debug) {
        Gson gson = new Gson();
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(debug ? HttpLoggingInterceptor.Level.BASIC : HttpLoggingInterceptor.Level.NONE);
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .addInterceptor(logging)
                .addInterceptor(new MockApiInterceptor(gson, clock))
                .build();
        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
                .create(EventApi.class);
    }
}
