package com.example.mine;

import static com.example.mine.AppLockConst.ENABLE_PASSLOCK;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

public class AppLock extends AppCompatActivity implements View.OnClickListener {

    private ActivityResultLauncher<Intent> launcher;


    Button btnSetLock;
    Button btnSetDelLock;
    Button btnChangePwd;
    AppPassWordActivity appPassWordActivity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_app_lock);
        appPassWordActivity = new AppPassWordActivity();

        btnSetLock = (Button) findViewById(R.id.btnSetLock);
        btnSetDelLock = (Button) findViewById(R.id.btnSetDelLock);
        btnChangePwd = (Button) findViewById(R.id.btnChangePwd);

        btnSetLock.setOnClickListener(this);
        btnSetDelLock.setOnClickListener(this);
        btnChangePwd.setOnClickListener(this);

        launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        handleActivityResult(result);
                    }
                });


        init();
    }


    @Override
    public void onClick(View view) {
        if (view == btnSetLock) {
            Intent intent = new Intent(this, AppPassWordActivity.class);
            intent.putExtra(AppLockConst.TYPE, (int) ENABLE_PASSLOCK);
            launcher.launch(intent);
        } else if (view == btnSetDelLock) {
            Intent intent = new Intent(this, AppPassWordActivity.class);
            intent.putExtra(AppLockConst.TYPE, (int)AppLockConst.DISABLE_PASSLOCK);
            launcher.launch(intent);

        } else {
            Intent intent = new Intent(this, AppPassWordActivity.class);
            intent.putExtra(AppLockConst.TYPE, (int)AppLockConst.CHANGE_PASSWORD);
            launcher.launch(intent);
        }
    }

    private void handleActivityResult(ActivityResult result) {
        if (result.getResultCode() != RESULT_OK) {
            System.out.println("cancel");
            return;
        }
        Intent data = result.getData();
        assert data != null;

        int requestCode = data.getIntExtra("type", 0);

        switch ((int) requestCode) {
            case ENABLE_PASSLOCK:
                Toast.makeText(this, "암호 설정 됨", Toast.LENGTH_SHORT).show();
                init();
                break;
            case AppLockConst.DISABLE_PASSLOCK:
                Toast.makeText(this, "암호 삭제 됨", Toast.LENGTH_SHORT).show();
                init();
                break;
            case AppLockConst.CHANGE_PASSWORD:
                Toast.makeText(this, "암호 변경 됨", Toast.LENGTH_SHORT).show();
                break;
            case AppLockConst.UNLOCK_PASSWORD:
                Toast.makeText(this, "잠금 해제 됨", Toast.LENGTH_SHORT).show();
                break;

            }
    }



    protected boolean isPassLock() {
            appPassWordActivity.database(4,null);
        return Calendar.isPassword;
    }


    private void init() {
        if (isPassLock()) {
            btnSetLock.setEnabled(false);
            btnSetDelLock.setEnabled(true);
            btnChangePwd.setEnabled(true);
            Calendar.lock = true;
        } else {
            btnSetLock.setEnabled(true);
            btnSetDelLock.setEnabled(false);
            btnChangePwd.setEnabled(false);
            Calendar.lock = false;
        }
    }

}