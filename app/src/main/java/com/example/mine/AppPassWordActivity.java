package com.example.mine;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class AppPassWordActivity extends AppCompatActivity {
    private boolean changePwdUnlock = false;
    protected EditText etPasscode1 = null;
    protected EditText etPasscode2 = null;
    protected EditText etPasscode3 = null;
    protected EditText etPasscode4 = null;
    protected InputFilter[] filters = null;
    private int type = -1;
    private String oldPwd = "";
    static boolean[] TF = {false};
    public AppPassWordActivity context_main;


    @Override
    protected void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_app_lock_password);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String message = extras.getString(AppLockConst.MESSAGE);
            if (message != null)
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            type = extras.getInt(AppLockConst.TYPE, -1);
        }

        context_main = this;

        filters = new InputFilter[2];
        filters[0] = new InputFilter.LengthFilter(1);
        filters[1] = numberFilter;

        etPasscode1 = (EditText) findViewById(R.id.etPasscode1);
        setupEditText(etPasscode1);
        etPasscode2 = (EditText) findViewById(R.id.etPasscode2);
        setupEditText(etPasscode2);
        etPasscode3 = (EditText) findViewById(R.id.etPasscode3);
        setupEditText(etPasscode3);
        etPasscode4 = (EditText) findViewById(R.id.etPasscode4);
        setupEditText(etPasscode4);

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
        ((Button) findViewById(R.id.btnClear)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClear();
            }
        });
        ((Button) findViewById(R.id.btnErase)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onDeleteKey();
            }
        });

        switch (type) {
            case AppLockConst.DISABLE_PASSLOCK:
                this.setTitle("Disable Passcode");
                break;
            case AppLockConst.ENABLE_PASSLOCK:
                this.setTitle("Enable Passcode");
                break;
            case AppLockConst.CHANGE_PASSWORD:
                this.setTitle("Change Passcode");
                break;
            case AppLockConst.UNLOCK_PASSWORD:
                this.setTitle("Unlock Passcode");
                break;
        }
    }

    public void database(int num, String password) {

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String lockID = ((LogInActivity)LogInActivity.context_login).doc;
        boolean firstLogin = ((LogInActivity)LogInActivity.context_login).isFirst;
        DocumentReference docRef = db.collection("user_info").document(lockID);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {

            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot document = task.getResult();
                Map<String, Object> lockNum = new HashMap<>();
                String lock = document.getString("잠금번호");

                if(num==4) {
                    System.out.println("//////////////////////");
                    if (Objects.equals(lock, "") || lock == null) {
                        Calendar.isPassword = false;
                    }
                    else{
                        Calendar.isPassword= true;
                    }
                    return;
                }
                if (!Objects.equals(lock, "")) {
                    TF[0] = true;
                    Calendar.isPassword = true;
                    ((LogInActivity)LogInActivity.context_login).isFirst = false;
                }
                else
                    Calendar.isPassword = false;

                switch (num) {
                    case AppLockConst.ENABLE_PASSLOCK:
                        if (oldPwd == null || oldPwd.equals("")) {
                            oldPwd = password;
                            onClear();
                            Toast.makeText(AppPassWordActivity.this, "다시 한 번 입력", Toast.LENGTH_SHORT).show();
                        } else {
                            if (password.equals(oldPwd)) {
                                lockNum.put("잠금번호", password);
                                docRef.update(lockNum);

                                Calendar.isPassword = true;
                                Calendar.lock = false;

                                Intent intent = new Intent(AppPassWordActivity.this, AppLock.class);
                                intent.putExtra(AppLockConst.TYPE, (int) AppLockConst.ENABLE_PASSLOCK);
                                setResult(RESULT_OK, intent);
                                finish();
                            } else {
                                onClear();
                                oldPwd = null;
                                Toast.makeText(AppPassWordActivity.this, "처음부터 다시 입력하세요", Toast.LENGTH_SHORT).show();
                            }
                        }
                        break;

                    case AppLockConst.DISABLE_PASSLOCK:

                        if (!Objects.equals(lock, "")) {
                            if (lock.equals(password)) {
                                lockNum.put("잠금번호", "");


                                docRef.update(lockNum);
                                Calendar.isPassword = false;
                                Intent intent = new Intent(AppPassWordActivity.this, AppLock.class);
                                intent.putExtra(AppLockConst.TYPE, (int) AppLockConst.DISABLE_PASSLOCK);
                                setResult(RESULT_OK, intent);
                                finish();
                            } else {
                                Toast.makeText(AppPassWordActivity.this, "비밀번호가 틀립니다.", Toast.LENGTH_SHORT).show();
                                onClear();
                            }
                        } else {
                            setResult(RESULT_CANCELED);
                            finish();
                        }
                        break;
                    case AppLockConst.CHANGE_PASSWORD:
                        if (!changePwdUnlock && Objects.equals(lock, password)) {
                            onClear();
                            changePwdUnlock = true;
                            Toast.makeText(AppPassWordActivity.this, "새로운 비밀번호 입력", Toast.LENGTH_SHORT).show();
                        } else if (changePwdUnlock) {
                            if (oldPwd.equals("")) {
                                oldPwd = password;
                                onClear();
                                Toast.makeText(AppPassWordActivity.this, "새로운 비밀번호 다시 입력", Toast.LENGTH_SHORT).show();
                            } else {
                                if (oldPwd.equals(password)) {
                                    lockNum.put("잠금번호", password);
                                    docRef.update(lockNum);
                                    Calendar.lock = false;

                                    Intent intent = new Intent(AppPassWordActivity.this, AppLock.class);
                                    intent.putExtra(AppLockConst.TYPE, (int) AppLockConst.CHANGE_PASSWORD);
                                    setResult(RESULT_OK, intent);
                                    finish();
                                } else {
                                    onClear();
                                    oldPwd = "";
                                    Toast.makeText(AppPassWordActivity.this, "현재 비밀번호 다시 입력", Toast.LENGTH_SHORT).show();
                                    changePwdUnlock = false;
                                }
                            }
                        } else {
                            Toast.makeText(AppPassWordActivity.this, "비밀번호가 틀립니다.", Toast.LENGTH_SHORT).show();
                            changePwdUnlock = false;
                            onClear();
                        }
                        break;


                    case AppLockConst.UNLOCK_PASSWORD:
                        if (Objects.equals(lock, password)) {
                            Intent intent = new Intent(AppPassWordActivity.this, AppLock.class);
                            intent.putExtra(AppLockConst.TYPE, (int) AppLockConst.UNLOCK_PASSWORD);
                            Calendar.lock = false;
                            setResult(RESULT_OK, intent);
                            finish();
                        } else {
                            Toast.makeText(AppPassWordActivity.this, "비밀번호가 틀립니다.", Toast.LENGTH_SHORT).show();
                            onClear();
                        }
                        break;
                    default:
                        break;
                }
            }
        });
    }


    private final View.OnClickListener btnListener = new View.OnClickListener() {

        @Override
        public void onClick(View view) {
            int currentValue = -1;
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
            } else if (id == R.id.btnClear) {
                onClear();
            } else if (id == R.id.btnErase) onDeleteKey();

            String strCurrentValue = String.valueOf(currentValue);

            if (currentValue != -1) {
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
            if (etPasscode4.getText().toString().length() > 0
                    && etPasscode3.getText().toString().length() > 0
                    && etPasscode2.getText().toString().length() > 0
                    && etPasscode1.getText().toString().length() > 0) {
                String str = etPasscode1.getText().toString()
                        + etPasscode2.getText().toString()
                        + etPasscode3.getText().toString() + etPasscode4.getText();
                database(type, str);
            }
        }

    };

    @SuppressLint("ClickableViewAccessibility")
    protected void setupEditText(EditText editText) {
        editText.setInputType(InputType.TYPE_NULL);
        editText.setFilters(filters);
        editText.setOnTouchListener(touchListener);
        editText.setTransformationMethod(PasswordTransformationMethod.getInstance());
    }

    private final InputFilter numberFilter = (charSequence, i, i1, spanned, i2, i3) -> {
        if (charSequence.length() > 1) {
            return "";
        }
        if (charSequence.length() == 0) {
            return null;
        }
        try {
            int number = Integer.parseInt(charSequence.toString());
            if ((number >= 0) && (number <= 9))
                return String.valueOf(number);
            else
                return "";
        } catch (NumberFormatException e) {
            return "";
        }
    };

    private final View.OnTouchListener touchListener = (view, motionEvent) -> {
        view.performClick();
        onClear();
        return false;
    };

    @Override
    public void onBackPressed() {
        if (type == AppLockConst.UNLOCK_PASSWORD) {
            // back to home screen
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            this.startActivity(intent);
        }
        finish();
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_DEL) {
            onDeleteKey();
            return true;
        }
        return super.onKeyDown(keyCode, event);
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

    protected void onClear() {
        etPasscode1.setText("");
        etPasscode2.setText("");
        etPasscode3.setText("");
        etPasscode4.setText("");
        etPasscode1.postDelayed(() -> etPasscode1.requestFocus(), 200);
    }
}

