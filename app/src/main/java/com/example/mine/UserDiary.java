package com.example.mine;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.example.mine.model.CalendarData;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.checkerframework.checker.units.qual.C;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

import java.time.LocalDate;

public class UserDiary extends AppCompatActivity implements View.OnClickListener {

    Button diaryBack;
    Button setPicture;
    Button setDiary;
    ImageView backgroundImage;

    private FirebaseStorage storage;

    private Uri cameraPhotoUri;
    Calendar calendar;
    private int selectedPosition = -1;
    //사진을 불러오는 장소
    private int imgFrom = -1;

    protected String imageFileName;

    LocalDate selectedDay;
    private CalendarAdapter adapter;

    private final ActivityResultLauncher<Uri> takePicture = registerForActivityResult(
            new ActivityResultContracts.TakePicture(),
            result -> {
                if (!result) return;
                adapter.setImage(selectedPosition, cameraPhotoUri);
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
                adapter.setImage(selectedPosition, uri);
            });


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.user_diary_before);

        //이 액티비티에서 calendar로 돌아갈 때 잠금 안뜨도록
        Calendar.lock = false;

        diaryBack = findViewById(R.id.diary_back);
        setDiary = findViewById(R.id.set_diary);
        setPicture = findViewById(R.id.set_picture);
        backgroundImage = findViewById(R.id.background_image);

        diaryBack.setOnClickListener(this);
        setPicture.setOnClickListener(this);
        setDiary.setOnClickListener(this);

        storage = FirebaseStorage.getInstance();


        calendar = new Calendar();
        adapter = Calendar.adapter;
        selectedPosition = getIntent().getIntExtra("localDate", -1);

        //const storage = getStorage(firebassApp)

    }

    @Override
    public void onClick(View view) {
        if (view == diaryBack) {
            Intent intent = new Intent(this, Calendar.class);
            startActivity(intent);
        } else if (view == setDiary) {
            Intent intent = new Intent(this, WritingDiary.class);
            startActivity(intent);
        } else if (view == setPicture) {
            showImagePicker();
            CalendarData item = adapter.dayList.get(selectedPosition);
            backgroundImage.setImageURI(item.imageUri);


            selectedDay = item.localDate;
            //uploadImg(item.imageUri);


        }
    }

    //firebase에 업로딩
    protected void uploadImg(Uri uri) {
        UploadTask uploadTask = null;
        StorageReference reference;
        switch (imgFrom) {
            case 1:
                String timeStamp = String.valueOf(selectedDay);
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
