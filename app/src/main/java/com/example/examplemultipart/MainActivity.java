package com.example.examplemultipart;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.examplemultipart.Api.ApiClient;
import com.example.examplemultipart.Api.ApiInterface;
import com.example.examplemultipart.Response.PersonOrg;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    File imgFile;
    EditText edtNim, edtNama, edtKelas;
    Button btnKirimData;
    ImageView imgViewGambar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        edtNim = findViewById(R.id.edtNim);
        edtNama = findViewById(R.id.edtNama);
        edtKelas = findViewById(R.id.edtKelas);
        imgViewGambar = findViewById(R.id.imgGambar);
        btnKirimData = findViewById(R.id.btnKirimData);

        Permission();

        btnKirimData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                kirimData();
            }
        });

        imgViewGambar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EasyImage.openChooserWithGallery(MainActivity.this,
                        "Pilih Gambar",
                        3);
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
        EasyImage.handleActivityResult(requestCode, resultCode, data, this, new DefaultCallback() {
            @Override
            public void onImagePicked(File imageFile, EasyImage.ImageSource source, int type) {
                CropImage.activity(Uri.fromFile(imageFile))
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setCropShape(CropImageView.CropShape.RECTANGLE)
                        .setFixAspectRatio(true)
                        .start(MainActivity.this);
            }

            @Override
            public void onImagePickerError(Exception e, EasyImage.ImageSource source, int type) {
                super.onImagePickerError(e, source, type);
                Toast.makeText(MainActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCanceled(EasyImage.ImageSource source, int type) {
                super.onCanceled(source, type);
            }
        });

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK){
                Uri uri = result.getUri();

                Glide.with(getApplicationContext())
                        .load(new File(uri.getPath()))
                        .into(imgViewGambar);

                imgFile = new File(uri.getPath());
            }else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE){
                Exception exception = result.getError();
                Toast.makeText(this, ""+exception.toString(), Toast.LENGTH_SHORT).show();
            }

        }
    }

    private void kirimData(){

        final ProgressDialog pd = new ProgressDialog(this);
        pd.setTitle("Loading ...");
        pd.setCancelable(false);
        pd.show();

        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/from-data"), imgFile);
        MultipartBody.Part body = MultipartBody.Part.createFormData("image",imgFile.getName(),requestFile);

        RequestBody nim = RequestBody.create(MediaType.parse("text/plain"),edtNim.getText().toString().trim());
        RequestBody nama = RequestBody.create(MediaType.parse("text/plain"),edtNama.getText().toString().trim());
        RequestBody kelas = RequestBody.create(MediaType.parse("text/plain"),edtKelas.getText().toString().trim());

        ApiInterface apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
        Call<PersonOrg> call = apiInterface.uploadData(nim,nama,kelas,body);

        call.enqueue(new Callback<PersonOrg>() {
            @Override
            public void onResponse(Call<PersonOrg> call, Response<PersonOrg> response) {
                pd.dismiss();
                Toast.makeText(MainActivity.this, "Berhasil kirim data", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<PersonOrg> call, Throwable t) {
                pd.dismiss();
                Toast.makeText(MainActivity.this, "Gagal cuk", Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void Permission() {
        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
            } else {
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            }
        }
    }


}
