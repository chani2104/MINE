package com.example.mine;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mine.model.CalendarData;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.storage.FirebaseStorage;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

public class Calendar extends AppCompatActivity {
    private FirebaseStorage storage;

    private TextView monthYearText;
    private RecyclerView recyclerView;
    private CalendarAdapter adapter;

    private int selectedPosition = -1;
    private Uri cameraPhotoUri;

    AppPassWordActivity appPassWordActivity;
    static boolean isPassword = true;
    static boolean lock = true;

    private final ActivityResultLauncher<Uri> takePicture = registerForActivityResult(
            new ActivityResultContracts.TakePicture(),
            result -> {
                if (!result) return;
                adapter.setImage(selectedPosition, cameraPhotoUri);
            });

    private final ActivityResultLauncher<String> requestPermission = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            result -> {
                if (!result) return;
                takePicture.launch(cameraPhotoUri);
            });

    private final ActivityResultLauncher<String> getContent = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri == null) return;
                adapter.setImage(selectedPosition, uri);
            });

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        appPassWordActivity = new AppPassWordActivity();
        appPassWordActivity.database(4,null);

        storage = FirebaseStorage.getInstance();
        monthYearText = findViewById(R.id.monthYearText);
        recyclerView = findViewById(R.id.recycle_view);

        ImageButton album = findViewById(R.id.album);
        ImageButton set = findViewById(R.id.setting);

        //잠금설정
        appPassWordActivity.database(4,null);


        //현재 날짜
        CalendarUtil.selectedDate = LocalDate.now();
        CalendarUtil.selectedYear = LocalDate.now().getYear();
        CalendarUtil.selectedMonth = LocalDate.now().getMonth();
        setMonthView();

        ///스와이프 화면 전환
        recyclerView.setItemAnimator(null);

        adapter.setOnItemClickListener(position -> {
            selectedPosition = position;
            showImagePicker();
        });

/* View pager 로 월을 chage
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1) {
                return false;
            }

            @Override
            public boolean isLongPressDragEnabled() {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                if (direction == ItemTouchHelper.RIGHT) {
                    CalendarUtil.selectedDate = CalendarUtil.selectedDate.minusMonths(1);
                    setMonthView();
                } else if (direction == ItemTouchHelper.LEFT) {
                    CalendarUtil.selectedDate = CalendarUtil.selectedDate.plusMonths(1);
                    setMonthView();
                }
            }
        }).attachToRecyclerView(recyclerView);
*/
        set.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), AppLock.class);
                startActivity(intent);
            }
        });

    }



    private void showImagePicker() {
        String[] items = Arrays.asList("카메라", "갤러리").toArray(new String[]{});
        new MaterialAlertDialogBuilder(this)
                .setTitle("사진 추가")
                .setItems(items, (dialog, index) -> {
                    if (index == 0) {
                        takePicture();
                    } else if (index == 1) {
                        getContent.launch("image/*");
                    }
                })
                .show();
    }

    private void takePicture() {
        File photoFile = null;
        try {
            photoFile = createImageFile();
        } catch (IOException ex) {
            ex.printStackTrace();
            return;
        }

        this.cameraPhotoUri = FileProvider.getUriForFile(this,
                "com.example.mine.fileprovider",
                photoFile);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermission.launch(Manifest.permission.CAMERA);
            return;
        }

        takePicture.launch(cameraPhotoUri);
    }

    //화면 세팅
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setMonthView() {
        //년월 텍스트뷰
        monthYearText.setText(monthYearFromDate(CalendarUtil.selectedDate));

        //월 가져옴
        ArrayList<CalendarData> dayList = daysInMonthArray(CalendarUtil.selectedDate);
        adapter = new CalendarAdapter(dayList);

        // 일~월 열 레이아웃
        RecyclerView.LayoutManager manager = new GridLayoutManager(getApplicationContext(), 7);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);
    }

    //연도, 월 출력
    @RequiresApi(api = Build.VERSION_CODES.O)
    private String monthYearFromDate(LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy MM월");
        return date.format(formatter);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private ArrayList<CalendarData> daysInMonthArray(LocalDate date) {
        ArrayList<CalendarData> dayList = new ArrayList<>();

        YearMonth yearMonth = YearMonth.from(date);

        //마지막&첫날 날짜 가져오기
        int lastDay = yearMonth.lengthOfMonth();
        LocalDate firstDay = CalendarUtil.selectedDate.withDayOfMonth(1);

        //첫날 요일 월1~일7
        int dayOfWeek = firstDay.getDayOfWeek().getValue();

        //날짜 생성
        for (int i = 1; i < 42; i++) {
            if (i <= dayOfWeek || i > lastDay + dayOfWeek) {
                dayList.add(null);
            } else {
                LocalDate day = LocalDate.of(CalendarUtil.selectedDate.getYear(), CalendarUtil.selectedDate.getMonth(), i - dayOfWeek);
                dayList.add(new CalendarData(day));
            }
        }

        return dayList;
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.KOREA).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        return File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
    }

    //잠금기능

    protected boolean isPassLock() {
        appPassWordActivity.database(4,null);
        return isPassword;
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


}