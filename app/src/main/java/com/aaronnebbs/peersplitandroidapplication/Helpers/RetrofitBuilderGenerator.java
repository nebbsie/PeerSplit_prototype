package com.aaronnebbs.peersplitandroidapplication.Helpers;

import com.aaronnebbs.peersplitandroidapplication.Helpers.Network.PeerSplitClient;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitBuilderGenerator {

    private static Retrofit.Builder generate(String url){
        Gson gson = new GsonBuilder().setLenient().create();

        // Create the base retrofit file.
        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create(gson));

        return builder;
    }

    public static PeerSplitClient generatePeerSplitClient(){
        // Create  a retrofit object.
        Retrofit retrofit = generate("http://peersplit.com/").build();
        // Link retrofit to PeerSplitClient class.
        return retrofit.create(PeerSplitClient.class);
    }

}
