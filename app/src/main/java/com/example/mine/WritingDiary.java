package com.example.mine;

import android.content.Intent;
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

import java.time.Instant;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class WritingDiary extends AppCompatActivity implements View.OnClickListener {

    Button writingBack;
    Button deleteDiary;
    Button saveDiary;
    EditText writeDiary;
    String date;
    String userInput;

    //시간
    Instant time;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.user_diary);

        Intent intent = getIntent();
        date = intent.getStringExtra("localDate");

        writingBack = findViewById(R.id.writing_back);
        deleteDiary = findViewById(R.id.delete_Diary);
        saveDiary = findViewById(R.id.save_diary);
        writeDiary = findViewById(R.id.write_diary);

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

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String lockID = ((LogInActivity) LogInActivity.context_login).doc;
        System.out.println(lockID);
        DocumentReference docRef = db.collection("user_photo").document(lockID).collection("photo").document(date);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {

            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot document = task.getResult();
                Map<String, Object> input = new HashMap<>();
                if (view == deleteDiary) {
                    writeDiary.setText("");
                }


                else if (view == saveDiary) {

                    if(time == null){
                        time = Instant.now();
                    }
                    userInput = writeDiary.getText().toString();
                    input.put(String.valueOf(time),userInput);
                    docRef.update(input);
                }
            }
        });
    }
}
