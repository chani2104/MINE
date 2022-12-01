package com.example.mine;

import android.net.Uri;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.util.Consumer;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mine.model.CalendarData;
import com.example.mine.model.CalendarData;
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
    public CalendarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.calendar_cube, parent, false);
        return new CalendarViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
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
            if (day.equals(CalendarUtil.selectedDate) && CalendarUtil.selectedYear == day.getYear() && (CalendarUtil.selectedMonth.equals(day.getMonth()))) {
                holder.cube_parentView.setBackgroundResource(R.drawable.camera);
                holder.dayText.setText("");

                holder.itemView.setOnClickListener(v -> {
                    if (onClickListener != null) {
                        onClickListener.accept(position);
                    }
                });
            }

            if (item.imageUri != null) {
                holder.dayImageView.setImageURI(item.imageUri);
            }
        }
    }

    @Override
    public int getItemCount() {
        return dayList.size();
    }

    public void setImage(int position, Uri uri) {
        if (position < 0 || position >= dayList.size()) return;

        dayList.get(position).imageUri = uri;
        notifyItemChanged(position);
    }

    public void setOnItemClickListener(Consumer<Integer> listener) {
        this.onClickListener = listener;
    }

    static class CalendarViewHolder extends RecyclerView.ViewHolder {
        TextView dayText;
        ImageView dayImageView;
        View cube_parentView;

        public CalendarViewHolder(@NonNull View itemView) {
            super(itemView);
            cube_parentView = itemView.findViewById(R.id.cube_parentView);
            dayImageView = itemView.findViewById(R.id.dayImage);
            dayText = itemView.findViewById(R.id.dayText);
        }
    }
}
