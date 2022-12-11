package com.example.mine;

import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.util.Consumer;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.mine.model.CalendarData;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;

public class CalendarAdapter extends RecyclerView.Adapter<CalendarAdapter.CalendarViewHolder> {

    ArrayList<CalendarData> dayList;
    private Consumer<Integer> onClickListener;

    public CalendarAdapter(ArrayList<CalendarData> dayList) {
        this.dayList = dayList;
    }

    @NonNull
    @Override
    //뷰홀더를 만들 때 호출되는 함수
    public CalendarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.calendar_cube, parent, false);
        return new CalendarViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    //뷰홀더 안에서 어떤 작업을 할 것이냐
    public void onBindViewHolder(@NonNull CalendarViewHolder holder, int position) {
        CalendarData item = dayList.get(position);
        holder.itemView.setOnClickListener(null);

        //날짜 세팅
        if (item == null) {
            holder.dayText.setText("");

        } else {
            LocalDate day = item.localDate;
            holder.dayText.setText(String.valueOf(day.getDayOfMonth()));

            //오늘 날짜 카메라 이미지
            if(day.getYear()<=LocalDate.now().getYear()&&day.getDayOfYear() <= LocalDate.now().getDayOfYear()){
                holder.itemView.setOnClickListener(v -> {
                    if (onClickListener != null) {
                        onClickListener.accept(position);
                    }
                });
                if (day.getYear() == LocalDate.now().getYear() && day.getDayOfYear() == LocalDate.now().getDayOfYear()) {
                    holder.cube_parentView.setBackgroundResource(R.drawable.camera);
                    holder.dayText.setText("");}
            }
            // 선택한 이미지 삽입
         if (item.imageUri != null) {
                holder.dayImageView.setImageURI(item.imageUri);
            }

        }


    }


    //뷰홀더를 몇 개나 만들어서 RecyclerView에 넣을 것이냐
    @Override
    public int getItemCount() {
        return dayList.size();
    }

    public void setImage(int position, Uri uri) {
        if (position < 0 || position >= dayList.size()) return;

        dayList.get(position).imageUri = uri;
        notifyItemChanged(position);
    }

    public void setOnItemClickListener(Consumer<Integer> listener)
    {
        this.onClickListener = listener;
    }

    //ViewHolder를 만들어서 그 안에 있는 View들을 찾아주기
    static class CalendarViewHolder extends RecyclerView.ViewHolder {
        TextView dayText;
        ImageView dayImageView;
        View cube_parentView;
       ;
        //gs://mine-9e585.appspot.com
        public CalendarViewHolder(@NonNull View itemView) {
            super(itemView);

            cube_parentView = itemView.findViewById(R.id.cube_parentView);
            dayImageView = itemView.findViewById(R.id.dayImage);
            dayText = itemView.findViewById(R.id.dayText);
        }
    }
}
