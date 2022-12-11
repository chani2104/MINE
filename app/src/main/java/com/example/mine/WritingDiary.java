package com.example.mine;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.StorageReference;
import java.time.Instant;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class WritingDiary extends AppCompatActivity implements View.OnClickListener {

    Button writingBack;
    Button deleteDiary;
    Button saveDiary;
    EditText writeDiary;
    String userInput;

    LocalDate date;

    private int position;

    //시간
    Instant time;

    DocumentReference docRef;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.user_diary);

        Intent intent = getIntent();
        date =  (LocalDate) getIntent().getSerializableExtra("localDate");
        position = intent.getIntExtra("position",-1);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        StorageReference reference;
        SharedPreferences sharedPref = LogInActivity.context_login.getSharedPreferences("sharedPreferences", Context.MODE_PRIVATE);
        String loginID = sharedPref.getString("inputID", "");

        String str = position + "." + date;
        docRef = db.collection("user_photo").document(loginID).collection("Photo").document(str);

        writingBack = findViewById(R.id.writing_back);
        deleteDiary = findViewById(R.id.delete_Diary);
        saveDiary = findViewById(R.id.save_diary);

        writeDiary = findViewById(R.id.write_diary);
        writeDiary.requestFocus();

        writingBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        deleteDiary.setOnClickListener(this);
        saveDiary.setOnClickListener(this);


    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override

     public void onClick(View view) {
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot document = task.getResult();
                Map<String, Object> input = new HashMap<>();
                if (view == deleteDiary) {
                    writeDiary.setText("");
                }


                else if (view == saveDiary) {
                    userInput = writeDiary.getText().toString();
                    input.put("일기",userInput);

                    docRef.set(input);
                }
            }
        });
    }
}