package com.aaronnebbs.peersplitandroidapplication.Helpers.Network;

import java.util.List;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface PeerSplitClient {

    @Multipart
    @POST("/api/upload.php")
    Call<ResponseBody> uploadMultipleFilesDynamic(
            @Part("uid") RequestBody  uid,
            @Part List<MultipartBody.Part> files
    );

    @Multipart
    @POST("/api/download.php")
    Call<ResponseBody> downloadFileWithFixedUrl(
            @Part("fileToDownload") RequestBody fileToDownload
    );

}
