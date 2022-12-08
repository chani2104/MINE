package com.example.mine;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class WritingDiary extends AppCompatActivity implements View.OnClickListener {

    Button writingBack;
    Button deleteDiary;
    Button saveDiary;
    EditText writeDiary;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.user_diary);
        writingBack = findViewById(R.id.writing_back);
        deleteDiary = findViewById(R.id.delete_Diary);
        saveDiary = findViewById(R.id.save_diary);
        writeDiary = findViewById(R.id.write_diary);

        writingBack.setOnClickListener(this);
        deleteDiary.setOnClickListener(this);
        saveDiary.setOnClickListener(this);


    }


    @Override
    public void onClick(View view) {
        if(view == writingBack){
            Intent intent = new Intent(this,UserDiary.class);
            startActivity(intent);
        }
        else if(view == deleteDiary){
            writeDiary.setText("");
        }

        else if(view == saveDiary){


        }
    }
}
