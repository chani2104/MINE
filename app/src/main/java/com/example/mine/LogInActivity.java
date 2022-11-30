package com.example.mine;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class LogInActivity extends AppCompatActivity {
    EditText login_id, login_pw;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        setContentView(R.layout.user_login);
    }

    public void onLogInClicked(View view){

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        SharedPreferences sharedPref = getSharedPreferences("sharedPreferences", Context.MODE_PRIVATE);
        String loginID = sharedPref.getString("inputID", null);
        String loginPW = sharedPref.getString("inputPW", null);



        if (loginID != null && loginPW != null) {
            Intent intent = new Intent(getApplicationContext(), AppLock.class);
            intent.putExtra("id", loginID);
            startActivity(intent);
            finish();
        }

        else if (loginID == null && loginPW == null){
            SharedPreferences.Editor editor = sharedPref.edit();

            db.collection("user_info")
                    .get()
                    .addOnSuccessListener(task -> {
                        boolean docFound = false;
                        Object pw = null;
                        String doc = "";
                        for (DocumentSnapshot ds : task.getDocuments()) {
                            if (ds.getId().equals(login_id.getText().toString())) {
                                docFound = true;
                                pw = ds.get("비밀번호");
                                doc = ds.getId();
                            }
                        }
                            if (docFound) {
                                if (!Objects.requireNonNull(pw).toString().equals(login_pw.getText().toString())) {
                                    Toast.makeText(LogInActivity.this, "비밀번호가 틀립니다.", Toast.LENGTH_LONG).show();
                                } else if (Objects.requireNonNull(pw).toString().equals(login_pw.getText().toString())) {
                                    Intent intent = new Intent(getApplicationContext(), AppLock.class);
                                    intent.putExtra("id", doc);
                                    editor.putString("inputID", doc);
                                    editor.apply();
                                    startActivity(intent);
                                }
                            }
                            else {
                                Toast.makeText(LogInActivity.this, "아이디가 존재하지 않습니다.", Toast.LENGTH_LONG).show();
                            }
                    });
        }
    }
}
