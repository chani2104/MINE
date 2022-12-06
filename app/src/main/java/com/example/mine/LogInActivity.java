package com.example.mine;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class LogInActivity extends AppCompatActivity {
    EditText login_id, login_pw;
    Button login_btn;
    @SuppressLint("StaticFieldLeak")
    public static Context context_login;
    public String doc = "";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_login);

        context_login = this;

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        login_id = findViewById(R.id.login_id);
        login_pw = findViewById(R.id.login_password);
        login_btn = findViewById(R.id.login_button);

        SharedPreferences sharedPref = getSharedPreferences("sharedPreferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        String loginID = sharedPref.getString("inputID", null);
        String loginPW = sharedPref.getString("inputPW", null);
        boolean isFirst = sharedPref.getBoolean("isFirst", true);

        if (isFirst){
            Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
            editor.putBoolean("isFirst", false);
            editor.apply();
            startActivity(intent);
        }

        if (loginID != null && loginPW != null) {
            Intent intent = new Intent(getApplicationContext(), Calendar.class);
            intent.putExtra("id", loginID);
            startActivity(intent);
            finish();
        } else if (loginID == null && loginPW == null) {
            login_btn.setOnClickListener(view -> db.collection("user_info")
                    .get()
                    .addOnSuccessListener(task -> {
                        if (login_id.getText().toString().isEmpty())
                            Toast.makeText(this, "아이디를 입력해주세요.", Toast.LENGTH_SHORT).show();
                        else if (login_pw.getText().toString().isEmpty())
                            Toast.makeText(this, "패스워드를 입력해주세요.", Toast.LENGTH_SHORT).show();
                        else {
                            boolean docFound = false;
                            Object pw = null;
                            for (DocumentSnapshot ds : task.getDocuments()) {
                                if (ds.getId().equals(login_id.getText().toString())) {
                                    docFound = true;
                                    pw = ds.get("비밀번호");
                                    doc = ds.getId();
                                }
                            }
                            System.out.println(docFound);
                            if (docFound) {
                                if (!Objects.requireNonNull(pw).toString().equals(login_pw.getText().toString())) {
                                    Toast.makeText(LogInActivity.this, "비밀번호가 틀립니다.", Toast.LENGTH_LONG).show();
                                } else if (Objects.requireNonNull(pw).toString().equals(login_pw.getText().toString())) {
                                    Intent intent = new Intent(getApplicationContext(), Calendar.class);
                                    Intent lockIntent = new Intent(getApplicationContext(), AppPassWordActivity.class);
                                    intent.putExtra("id", doc);
                                    lockIntent.putExtra("lockID", doc);
                                    editor.putString("inputID", doc);
                                    editor.putString("inputPW", pw.toString());
                                    editor.apply();
                                    Toast.makeText(LogInActivity.this, "로그인에 성공했습니다.", Toast.LENGTH_LONG).show();
                                    startActivity(intent);
                                }
                            } else {
                                Toast.makeText(LogInActivity.this, "아이디가 존재하지 않습니다.", Toast.LENGTH_LONG).show();
                            }
                        }
                    }));
        }
    }
}
