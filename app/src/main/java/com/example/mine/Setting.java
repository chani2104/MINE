package com.example.mine;

import static com.example.mine.R.layout.*;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Setting extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        ListView listview=findViewById(R.id.listview);
        List<String> list=new ArrayList<>();

        list.add("잠금 설정");
        list.add("닉네임 변경");
        list.add("로그아웃");
        list.add("회원 탈퇴");

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String loginID = ((LogInActivity)LogInActivity.context_login).doc;

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,list);
        listview.setAdapter(adapter);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                //잠금설정
                if(i==0) {
                    Intent intent = new Intent(getApplicationContext(), AppLock.class);
                    startActivity(intent);
                }
                //닉네임변경
                if(i==1){
                    LinearLayout linear= (LinearLayout) View.inflate(Setting.this, dialog_editext,null);

                    new AlertDialog.Builder(Setting.this)
                            .setView(linear)
                            .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    EditText nick1 = (EditText) linear.findViewById(R.id.nickname_edit);
                                    String nick=nick1.getText().toString();
                                    String loginname = ((LogInActivity)LogInActivity.context_login).doc;
                                    DocumentReference docRef = db.collection("user_info").document(loginname);
                                    docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            DocumentSnapshot document = task.getResult();
                                            Map<String, Object> nickName = new HashMap<>();

                                            String nickn = document.getString("닉네임");
                                            nickName.put("닉네임", nick);
                                            docRef.update(nickName);
                                        }
                                    });

                                    // db.collection("user_info").document(nickname)

                                    dialog.dismiss();

                                }
                            })
                            .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    dialog.dismiss();
                                }
                            })
                            .show();}

                //로그아웃
                if(i==2){
                    Calendar.login = false;
                    new AlertDialog.Builder(Setting.this)
                            .setTitle("로그아웃").setMessage("로그아웃 하시겠습니까?")
                            .setPositiveButton("로그아웃", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialogInterface, int i) {

                                    Calendar.login = false;
                                    Intent intent = new Intent(getApplicationContext() , LogInActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);

                                    Toast.makeText(Setting.this, "로그아웃에 성공했습니다.", Toast.LENGTH_SHORT).show();
                                    startActivity(intent);

                                    SharedPreferences sharedPref = getSharedPreferences("sharedPreferences", Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sharedPref.edit();
                                    editor.clear();
                                    editor.apply();
                                    finish();


                                }
                            })
                            .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                }
                            })
                            .show();
                }
                //회원탈퇴
                        if(i==3){
                    new AlertDialog.Builder(Setting.this).setMessage("회원 탈퇴를 하시겠습니까?")
                            .setPositiveButton("네", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                    Intent intent = new Intent(Setting.this , LogInActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                    db.collection("user_info").document(loginID)
                                            .delete();
                                    startActivity(intent);
                                    SharedPreferences sharedPref = getSharedPreferences("sharedPreferences", Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sharedPref.edit();
                                    editor.clear();
                                    editor.apply();
                                    finish();

                                }


                            })
                            .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int i) {
                                }
                            })
                            .show();

                }

            }
        });
    }

}