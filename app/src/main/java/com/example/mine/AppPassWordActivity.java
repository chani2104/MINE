package com.example.mine;

import static android.content.ContentValues.TAG;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Source;

import java.util.HashMap;
import java.util.Map;

public class AppPassWordActivity extends Activity {
    private String oldPwd="";
    private boolean changePwdUnlock = false;
    protected EditText etPasscode1 = null;
    protected EditText etPasscode2 = null;
    protected EditText etPasscode3 = null;
    protected EditText etPasscode4 = null;

    public static AppPassWordActivity context_main;

    @Override
    protected void onCreate(Bundle saveInstanceState){
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_app_lock_password);

        context_main=this;

        etPasscode1 = (EditText) findViewById(R.id.etPasscode1);
        etPasscode2 = (EditText) findViewById(R.id.etPasscode2);
        etPasscode3 = (EditText) findViewById(R.id.etPasscode3);
        etPasscode4 = (EditText) findViewById(R.id.etPasscode4);

        ((Button) findViewById(R.id.btn0)).setOnClickListener(btnListener);
        ((Button) findViewById(R.id.btn1)).setOnClickListener(btnListener);
        ((Button) findViewById(R.id.btn2)).setOnClickListener(btnListener);
        ((Button) findViewById(R.id.btn3)).setOnClickListener(btnListener);
        ((Button) findViewById(R.id.btn4)).setOnClickListener(btnListener);
        ((Button) findViewById(R.id.btn5)).setOnClickListener(btnListener);
        ((Button) findViewById(R.id.btn6)).setOnClickListener(btnListener);
        ((Button) findViewById(R.id.btn7)).setOnClickListener(btnListener);
        ((Button) findViewById(R.id.btn8)).setOnClickListener(btnListener);
        ((Button) findViewById(R.id.btn9)).setOnClickListener(btnListener);
    }

    public boolean dataBase(int num, String password){

        final boolean[] TF = {false};

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("user_info").document(String.valueOf("chani2104"));
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    Map<String, Object> lockNum = new HashMap<>();
                    switch (num){
                        case 0:
                            lockNum.put("잠금번호",password);
                            docRef.update(lockNum);
                            break;
                        case 1:
                            lockNum.put("잠금번호","");
                            docRef.update(lockNum);
                            break;
                        case 2:
                            if(String.valueOf(docRef.get(Source.valueOf("잠금번호")))==password){
                                TF[0] =true;
                            }
                            break;
                        case 3:
                            if(String.valueOf(docRef.get(Source.valueOf("잠금번호")))!=""){
                                TF[0] =true;
                            }
                            break;
                    }
                }
            }
        });
        return TF[0];
    }

    View.OnClickListener btnListener= new View.OnClickListener() {

        int currentValue = -1;
        @Override
        public void onClick(View view) {
            int id = view.getId();
            if (id == R.id.btn0) {
                currentValue = 0;
            } else if (id == R.id.btn1) {
                currentValue = 1;
            } else if (id == R.id.btn2) {
                currentValue = 2;
            } else if (id == R.id.btn3) {
                currentValue = 3;
            } else if (id == R.id.btn4) {
                currentValue = 4;
            } else if (id == R.id.btn5) {
                currentValue = 5;
            } else if (id == R.id.btn6) {
                currentValue = 6;
            } else if (id == R.id.btn7) {
                currentValue = 7;
            } else if (id == R.id.btn8) {
                currentValue = 8;
            } else if (id == R.id.btn9) {
                currentValue = 9;
            } else if(id == R.id.btnClear){
                onClear();
            } else if(id == R.id.btnErase){
                onDeleteKey();
            }else{}

            String strCurrentValue = String.valueOf(currentValue);
            if (currentValue !=-1) {
                if (etPasscode1.isFocused()) {
                    etPasscode1.setText(strCurrentValue);
                    etPasscode2.requestFocus();
                    etPasscode2.setText("");
                } else if (etPasscode2.isFocused()) {
                    etPasscode2.setText(strCurrentValue);
                    etPasscode3.requestFocus();
                    etPasscode3.setText("");
                } else if (etPasscode3.isFocused()) {
                    etPasscode3.setText(strCurrentValue);
                    etPasscode4.requestFocus();
                    etPasscode4.setText("");
                } else if (etPasscode4.isFocused()) {
                    etPasscode4.setText(strCurrentValue);
                }
            }

            if (etPasscode4.getText().toString().length()>0
                    && etPasscode3.getText().toString().length() > 0
                    && etPasscode2.getText().toString().length() > 0
                    && etPasscode1.getText().toString().length() > 0) {
                inputType(getIntent().getIntExtra("type",0));
            }
        }

    };


    protected String inputedPassword(){
        return etPasscode1.getText().toString()
                + etPasscode2.getText().toString()
                + etPasscode3.getText().toString() + etPasscode4.getText();
    }

    protected void inputType(int type) {
        switch (type) {
            case AppLockConst.ENABLE_PASSLOCK:
                if (oldPwd == null) {
                    oldPwd = inputedPassword();
                    onClear();
                    Toast.makeText(this, "다시 한 번 입력", Toast.LENGTH_SHORT).show();
                } else {
                    if (inputedPassword().equals(oldPwd)) {
                        dataBase(0,inputedPassword());
                        setResult(RESULT_OK);
                        finish();
                    } else {
                        onClear();
                        oldPwd = null;
                        Toast.makeText(this, "처음부터 다시 입력하세요", Toast.LENGTH_SHORT).show();
                    }
                }
                break;

            case AppLockConst.DISABLE_PASSLOCK:
                if (dataBase(3,null)) {
                    if(dataBase(2,(inputedPassword()))){
                        dataBase(1,null);
                        setResult(RESULT_OK);
                        finish();
                    }
                    else{
                        setResult(RESULT_CANCELED);
                        finish();
                    }
                }
                break;

            case AppLockConst.UNLOCK_PASSWORD:
                if (dataBase(2,inputedPassword())){
                    setResult(RESULT_OK);
                    finish();
                } else {
                    Log.d(TAG, "비밀번호가 틀립니다.");
                    onClear();
                }
                break;

            case AppLockConst.CHANGE_PASSWORD:
                if (dataBase(2,inputedPassword()) && !changePwdUnlock) {
                    onClear();
                    changePwdUnlock = true;
                    Log.d(TAG, "새로운 비밀번호 입력");
                } else if(changePwdUnlock){
                    if(oldPwd.isEmpty()){
                        oldPwd=inputedPassword();
                        onClear();
                        Log.d(TAG, "새로운 비밀번호 다시 입력");
                    }else{
                        if(oldPwd==inputedPassword()){
                            dataBase(0,inputedPassword());
                            setResult(Activity.RESULT_OK);
                            finish();
                        }else{
                            onClear();;
                            oldPwd="";
                            Log.d(TAG, "현재 비밀번호 다시 입력");
                            changePwdUnlock = false;
                        }
                    }
                   }else{
                    Log.d(TAG, "비밀번호가 틀립니다.");
                    changePwdUnlock = false;
                    onClear();
                }
                break;

            default:
                break;
        }
    }

    private void onDeleteKey() {
        if (etPasscode1.isFocused()) {
            etPasscode1.setText("");
        } else if (etPasscode2.isFocused()) {
            etPasscode1.requestFocus();
            etPasscode1.setText("");
        } else if (etPasscode3.isFocused()) {
            etPasscode2.requestFocus();
            etPasscode2.setText("");
        } else if (etPasscode4.isFocused()) {
            etPasscode3.requestFocus();
            etPasscode3.setText("");
        }
    }

    private void onClear() {
        etPasscode1.setText("");
        etPasscode2.setText("");
        etPasscode3.setText("");
        etPasscode4.setText("");
        etPasscode1.requestFocus();
    }

}

