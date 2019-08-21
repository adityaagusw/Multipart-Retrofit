package com.example.examplemultipart.Api;

import com.example.examplemultipart.Response.PersonOrg;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface ApiInterface {

    @Multipart
    @POST("addMhs.php")
    Call<PersonOrg> uploadData(@Part("nim")RequestBody nim,
                               @Part("nama")RequestBody nama,
                               @Part("kelas")RequestBody kelas,
                               @Part MultipartBody.Part image);

}
