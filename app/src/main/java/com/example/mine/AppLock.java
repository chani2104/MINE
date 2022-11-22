package com.example.mine;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class AppLock extends AppCompatActivity {

    boolean lock = true;

    Button btnSetLock;
    Button btnSetDelLock;
    Button btnChangePwd;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_lock);

        AppPassWordActivity.context_main= new AppPassWordActivity();

        btnSetLock = (Button) findViewById(R.id.btnSetLock);
        btnSetDelLock = (Button) findViewById(R.id.btnSetDelLock);
        btnChangePwd = (Button) findViewById(R.id.btnChangePwd);

    }

    public void btnSetLock(View view) {
        Intent intent = new Intent(this, AppPassWordActivity.class);
        intent.putExtra(AppLockConst.TYPE, AppLockConst.ENABLE_PASSLOCK);
        startActivity(intent);
    }

    public void btnSetDelLock(View view) {
        Intent intent = new Intent(this, AppPassWordActivity.class);
        intent.putExtra(AppLockConst.TYPE, AppLockConst.DISABLE_PASSLOCK);
        startActivity(intent);
    }

    public void btnChangePwd(View view) {
        Intent intent = new Intent(this, AppPassWordActivity.class);
        intent.putExtra(AppLockConst.TYPE, AppLockConst.CHANGE_PASSWORD);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case AppLockConst.ENABLE_PASSLOCK:
                if (resultCode == Activity.RESULT_OK) {
                    Toast.makeText(this, "암호 설정 됨", Toast.LENGTH_SHORT).show();
                    init();
                    lock = false;
                }
                break;
            case AppLockConst.DISABLE_PASSLOCK:
                if (resultCode == Activity.RESULT_OK) {
                    Toast.makeText(this, "암호 삭제 됨", Toast.LENGTH_SHORT).show();
                    init();
                }
                break;
            case AppLockConst.CHANGE_PASSWORD:
                if (resultCode == Activity.RESULT_OK) {
                    Toast.makeText(this, "암호 변경 됨", Toast.LENGTH_SHORT).show();
                    lock = false;
                }
                break;
            case AppLockConst.UNLOCK_PASSWORD:
                if (resultCode == Activity.RESULT_OK) {
                    Toast.makeText(this, "잠금 해제 됨", Toast.LENGTH_SHORT).show();
                    lock = false;
                }
                break;
        }
    }

    protected boolean isPassLock() {
            if (AppPassWordActivity.context_main.dataBase(3, ""))
                return true;
            else
                return false;
    }


    @Override
    protected void onStart() {
        super.onStart();
        if (lock && isPassLock()) {
            Intent intent = new Intent(this, AppPassWordActivity.class);
            intent.putExtra(AppLockConst.TYPE, AppLockConst.UNLOCK_PASSWORD);
            startActivity(intent);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isPassLock()) {
            lock = true;
        }
    }

    protected void init() {
        if (isPassLock()) {
            btnSetLock.setEnabled(false);
            btnSetDelLock.setEnabled(true);
            btnChangePwd.setEnabled(true);
            lock = true;
        } else {
            btnSetLock.setEnabled(true);
            btnSetDelLock.setEnabled(false);
            btnChangePwd.setEnabled(false);
            lock = false;
        }
    }
}
