package com.example.mine;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Source;

import java.util.Objects;

public class LogInActivity extends AppCompatActivity {
    EditText login_id, login_pw;
    Button login_btn;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        setContentView(R.layout.user_login);

        login_id = findViewById(R.id.login_id);
        login_pw = findViewById(R.id.login_password);
        login_btn = findViewById(R.id.login_button);

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
            login_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    db.collection("user_info")
                            .get()
                            .addOnSuccessListener(task -> {
                                for (DocumentSnapshot ds : task.getDocuments()) {
                                    if(ds.getId().equals(login_id.getText().toString())) {
                                        DocumentReference docRef = db.collection("user_info").document(ds.getId());
                                        if (!Objects.requireNonNull(ds.get("비밀번호")).toString().equals(login_pw.getText().toString())){

                                        }

                                    }
                                }
                            });
                }
            });
        }
    }
}
