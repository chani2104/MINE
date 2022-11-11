package com.example.mine;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class SignupActivity extends AppCompatActivity {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_signup);

    }

    private String getTime(){
        long timeNow =  System.currentTimeMillis();
        Date date = new Date(timeNow);
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM");

        return dateFormat.format(date);
    }

    public void UserSaveFirebase(View view){
        EditText nickName = findViewById(R.id.nickname_edit);
        EditText ID = findViewById(R.id.id_edit);
        EditText password = findViewById(R.id.password_edit);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> data = new HashMap<>();
        data.put("닉네임", nickName.getText().toString());
        data.put("아이디", ID.getText().toString());
        data.put("비밀번호", password.getText().toString());
        data.put("가입날짜", getTime());

        db.collection("user_info").document(ID.getText().toString())
                .set(data)
                .addOnSuccessListener(documentReference -> Log.d(TAG, "DocumentSnapshot Success"))
                .addOnFailureListener(e -> Log.w(TAG, "Error adding document", e));
    }




}
