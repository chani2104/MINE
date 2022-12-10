package com.example.mine;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

public class UserDiary extends AppCompatActivity implements View.OnClickListener {
    private final FirebaseStorage storage = FirebaseStorage.getInstance();

    private Button diaryBack;
    private Button setPicture;
    private Button setDiary;
    private ImageView backgroundImage;

    private LocalDate date;
    private Uri cameraPhotoUri;

    //사진을 불러오는 장소
    private int imgFrom = -1;

    protected String imageFileName;

    private final ActivityResultLauncher<Uri> takePicture = registerForActivityResult(
            new ActivityResultContracts.TakePicture(),
            result -> {
                if (!result) return;
                // 이 화면에서 Calendar 의 화면에 접근할 수 없습니다.
                // 이 화면에서는 데이터를 DB 에 올리는 기능만 해야 하고,
                // Calendar 화면에서는 선택한 달의 데이터를 전부 가져와서 보여주는 기능을 가지도록 코딩하시면 됩니다.
            });


    private final ActivityResultLauncher<String> requestPermission = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            result -> {
                if (!result) return;
                takePicture.launch(cameraPhotoUri);
            });

    protected final ActivityResultLauncher<String> getContent = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri == null) return;
            });


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_diary_before);

        // Calendar 화면에서 선택한 날짜 입니다.
        date = (LocalDate) getIntent().getSerializableExtra("localDate");
        Log.d("UserDiary", date.toString());

        //이 액티비티에서 calendar로 돌아갈 때 잠금 안뜨도록
        Calendar.lock = false;

        diaryBack = findViewById(R.id.diary_back);
        setDiary = findViewById(R.id.set_diary);
        setPicture = findViewById(R.id.set_picture);
        backgroundImage = findViewById(R.id.background_image);

        diaryBack.setOnClickListener(this);
        setPicture.setOnClickListener(this);
        setDiary.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view == diaryBack) {
            onBackPressed();

        } else if (view == setDiary) {
            Intent intent = new Intent(this, WritingDiary.class);
            startActivity(intent);

        } else if (view == setPicture) {
            showImagePicker();
        }
    }

    //firebase에 업로딩
    protected void uploadImg(Uri uri) {
        UploadTask uploadTask = null;
        StorageReference reference;
        switch (imgFrom) {
            case 1:
                String timeStamp = String.valueOf(date);
                String imageFileName = "IMAGE_" + timeStamp + "_.png";
                reference = storage.getReference().child("item").child(imageFileName);
                uploadTask = reference.putFile(uri);
                break;

            case 0:
                //reference = storage.getReference().child("item").child(imageFileName);
                break;
        }

       /* uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                //업로드 성공 시 동작
            }
        });
*/
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.KOREA).format(new Date());
        imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        return File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
    }

    protected void takePicture() {
        File photoFile = null;
        try {
            photoFile = createImageFile();
        } catch (IOException ex) {
            ex.printStackTrace();
            return;
        }

        this.cameraPhotoUri = FileProvider.getUriForFile(this,
                "com.example.mine.fileprovider",
                photoFile);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermission.launch(Manifest.permission.CAMERA);
            return;
        }
        takePicture.launch(cameraPhotoUri);
    }

    protected void showImagePicker() {
        String[] items = Arrays.asList("카메라", "갤러리").toArray(new String[]{});
        new MaterialAlertDialogBuilder(this)
                .setTitle("사진 추가")
                .setItems(items, (dialog, index) -> {
                    if (index == 0) {
                        imgFrom = 0;
                        takePicture();
                    } else if (index == 1) {
                        imgFrom = 1;
                        getContent.launch("image/*");
                    }
                })
                .show();
    }
}