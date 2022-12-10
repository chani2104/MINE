package com.example.mine;

import static com.example.mine.AppLockConst.ENABLE_PASSLOCK;

import android.Manifest;
import android.content.ClipData;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

public class UserDiary extends AppCompatActivity implements View.OnClickListener {
    private final FirebaseStorage storage = FirebaseStorage.getInstance();
    private static final String TAG = "MultiImageActivity";

    ArrayList<Uri> uriList = new ArrayList<>();

    UserDiaryAdapter adapter;

    private Button diaryBack;
    private Button setPicture;
    private Button setDiary;
    private RecyclerView diary_image;
    private ActivityResultLauncher<Intent> launcher;

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


    @RequiresApi(api = Build.VERSION_CODES.O)
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
        diary_image = findViewById(R.id.diary_image);

        diaryBack.setOnClickListener(this);
        setPicture.setOnClickListener(this);
        setDiary.setOnClickListener(this);


        launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        handleActivityResult(result);
                    }
                });

    }

    private void handleActivityResult(ActivityResult result) {
        /*if (result.getResultCode() != RESULT_OK) {
            System.out.println("cancel");
            return;
        }*/

        Intent data = result.getData();

        if(data == null){
            Toast.makeText(getApplicationContext(), "이미지를 선택하지 않았습니다.", Toast.LENGTH_SHORT).show();
        }
        else{
            //이미지 하나만 선택
            if(data.getClipData()==null){
                Log.e("single choice : ",String.valueOf(data.getData()));
                Uri imageUri = data.getData();
                uriList.add(imageUri);

                adapter = new UserDiaryAdapter(uriList, getApplicationContext());
                diary_image.setAdapter(adapter);
                diary_image.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true));
            }
            else{ // 이미지 여러장 선택
                ClipData clipData = data.getClipData();
                Log.e("clipData", String.valueOf(clipData.getItemCount()));

                if(clipData.getItemCount() > 10){   // 선택한 이미지가 11장 이상인 경우
                    Toast.makeText(getApplicationContext(), "사진은 10장까지 선택 가능합니다.", Toast.LENGTH_LONG).show();
                }
                else{   // 선택한 이미지가 1장 이상 10장 이하인 경우
                    Log.e(TAG, "multiple choice");

                    for (int i = 0; i < clipData.getItemCount(); i++){
                        Uri imageUri = clipData.getItemAt(i).getUri();  // 선택한 이미지들의 uri를 가져온다.
                        try {
                            uriList.add(imageUri);  //uri를 list에 담는다.

                        } catch (Exception e) {
                            Log.e(TAG, "File select error", e);
                        }
                    }

                    adapter = new UserDiaryAdapter(uriList, getApplicationContext());
                    diary_image.setAdapter(adapter);   // 리사이클러뷰에 어댑터 세팅
                    diary_image.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true));     // 리사이클러뷰 수직 스크롤 적용
                }
            }
        }

        int requestCode = data.getIntExtra("type", 0);

        switch ((int) requestCode) {

        }
    }



    @Override
    public void onClick(View view) {
        if (view == diaryBack) {
            onBackPressed();

        } else if (view == setDiary) {
            Intent intent = new Intent(this, WritingDiary.class);
            startActivity(intent);

        } else if (view == setPicture) {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
            //다중이미지 가능하도록
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true);
            intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            launcher.launch(intent);

            //showImagePicker();
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