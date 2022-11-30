package com.example.mine;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

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
        EditText passwordCheck = findViewById(R.id.passwordcheck_edit);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Map<String, Object> data = new HashMap<>();
        data.put("닉네임", nickName.getText().toString());
        data.put("아이디", ID.getText().toString());
        data.put("비밀번호", password.getText().toString());
        data.put("가입날짜", getTime());
        data.put("잠금번호", "");

        if (nickName.getText().toString().isEmpty())
            Toast.makeText(this, "닉네임을 입력해주세요.", Toast.LENGTH_SHORT).show();
        else if (ID.getText().toString().isEmpty())
            Toast.makeText(this, "아이디를 입력해주세요.", Toast.LENGTH_SHORT).show();
        else if (password.getText().toString().isEmpty())
            Toast.makeText(this, "패스워드를 입력해주세요.", Toast.LENGTH_SHORT).show();
        else if (passwordCheck.getText().toString().isEmpty())
            Toast.makeText(this, "패스워드 확인을 입력해주세요.", Toast.LENGTH_SHORT).show();
        else if (!password.getText().toString().equals(passwordCheck.getText().toString()))
            Toast.makeText(this, "비밀번호 확인이 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
        else {
            db.collection("user_info")
                    .get()
                    .addOnSuccessListener(task -> {
                        boolean docFound = false;
                        for (DocumentSnapshot ds : task.getDocuments()) {
                            if (ds.getId().equals(ID.getText().toString())) {
                                docFound = true;
                            }
                        }
                        if (docFound)
                            Toast.makeText(this, "이미 존재하는 아이디 입니다.", Toast.LENGTH_LONG).show();
                        else {
                            db.collection("user_info").document(ID.getText().toString())
                                    .set(data)
                                    .addOnSuccessListener(documentReference -> Log.d(TAG, "DocumentSnapshot Success"))
                                    .addOnFailureListener(e -> Log.w(TAG, "Error adding document", e));
                        }
                    });
        }
    }




}