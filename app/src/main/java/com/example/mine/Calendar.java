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
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.example.mine.model.CalendarData;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class Calendar extends AppCompatActivity {
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ViewPager2 viewPager;
    private TextView monthYearText;

    private AppPassWordActivity appPassWordActivity;
    static boolean isPassword = false;
    static boolean lock = true;
    static boolean login = false;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        appPassWordActivity = new AppPassWordActivity();

        if (login) {
            // 잠금설정
            isPassLock();
        }

        onInit();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void onInit() {
        monthYearText = findViewById(R.id.monthYearText);
        ImageButton album = findViewById(R.id.album);
        ImageButton set = findViewById(R.id.setting);
        viewPager = findViewById(R.id.view_pager);

        set.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Setting.class);
                startActivity(intent);
            }
        });

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);

                try {
                    LocalDate yearMonth = ((CalendarViewPagerAdapter) Objects.requireNonNull(viewPager.getAdapter()))
                            .yearMonthList
                            .get(position);
                    setMonthView(yearMonth);
                } catch (Exception ignore) {
                }
            }
        });

        // 달력이 깜빡거리는게 싫으시면 아래 코드를 주석 처리 하시면 됩니다.
        // 아래 코드 주석처리시, Firestore 에서 데이터를 읽기 전까지는 화면에 달력이 표시되지 않습니다.
        viewPager.setAdapter(new CalendarViewPagerAdapter(this, Collections.singletonList(LocalDate.now())));

        db.collection("user_info")
                .orderBy("가입날짜")
                .limit(1)
                .get()
                .addOnCompleteListener(this, task -> {
                    LocalDate now = LocalDate.now();
                    LocalDate oldestDate = now;

                    if (task.getException() != null) {
                        task.getException().printStackTrace();
                    }

                    QuerySnapshot snapshot = task.getResult();
                    if (snapshot != null && !snapshot.isEmpty()) {
                        try {
                            String source = snapshot.getDocuments().get(0).getString("가입날짜");
                            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM", Locale.US);
                            Date date = dateFormat.parse(source);
                            oldestDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }

                    ArrayList<LocalDate> yearMonthList = new ArrayList<>();
                    LocalDate start = LocalDate.of(oldestDate.getYear(), oldestDate.getMonth(), 1);
                    LocalDate end = LocalDate.of(now.getYear(), now.getMonth(), 1);

                    while (!start.isAfter(end)) {
                        yearMonthList.add(start);
                        // Log.d("Calendar", start.toString());

                        start = start.plusMonths(1);
                    }

                    viewPager.setAdapter(new CalendarViewPagerAdapter(this, yearMonthList));
                    viewPager.setCurrentItem(yearMonthList.size() - 1, false);
                });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setMonthView(LocalDate yearMonth) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy MM월");
        monthYearText.setText(yearMonth.format(formatter));
    }

    // 잠금기능
    protected boolean isPassLock() {
        appPassWordActivity.database(4, null);
        return isPassword;
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (login) {
            if (lock && isPassLock()) {
                Intent intent = new Intent(this, AppPassWordActivity.class);
                intent.putExtra(AppLockConst.TYPE, AppLockConst.UNLOCK_PASSWORD);
                startActivity(intent);
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (login) {
            if (isPassLock()) {
                lock = true;
            }
        }
    }

    private static class CalendarViewPagerAdapter extends FragmentStateAdapter {
        private final List<LocalDate> yearMonthList;

        public CalendarViewPagerAdapter(@NonNull FragmentActivity fragmentActivity,
                                        List<LocalDate> yearMonthList) {
            super(fragmentActivity);
            this.yearMonthList = yearMonthList;
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        @NonNull
        @Override
        public Fragment createFragment(int position) {
            return CalendarFragment.newInstance(yearMonthList.get(position));
        }

        @Override
        public int getItemCount() {
            return yearMonthList.size();
        }
    }
}