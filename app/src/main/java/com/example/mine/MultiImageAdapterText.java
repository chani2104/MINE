package com.example.mine;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.StorageReference;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class MultiImageAdapterText extends RecyclerView.Adapter<MultiImageAdapterText.ViewHolder> {
    private ArrayList<Uri> mData = null;
    private Context mContext = null;
    private LocalDate date = null;
    private int num = 0;

    public interface ItemClickListener {
        void onItemClick(int position);
    }

    ItemClickListener itemClickListener;

    // 생성자에서 데이터 리스트 객체, Context를 전달받음.
    public MultiImageAdapterText(ArrayList<Uri> list, Context context, LocalDate date) {
        mData = list;
        mContext = context;
        this.date = date;


    }

    // 아이템 뷰를 저장하는 뷰홀더 클래스.
    public static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView image;
        TextView text;


        ViewHolder(View itemView, ItemClickListener itemClickListener) {
            super(itemView);

            // 뷰 객체에 대한 참조.
            image = itemView.findViewById(R.id.image);
            text = itemView.findViewById(R.id.text);

            text.setVisibility(View.INVISIBLE);
        }
    }


    // onCreateViewHolder() - 아이템 뷰를 위한 뷰홀더 객체 생성하여 리턴.
    // LayoutInflater - XML에 정의된 Resource(자원) 들을 View의 형태로 반환.
    @Override
    public MultiImageAdapterText.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);    // context에서 LayoutInflater 객체를 얻는다.
        View view = inflater.inflate(R.layout.multi_photo_item, parent, false);    // 리사이클러뷰에 들어갈 아이템뷰의 레이아웃을 inflate.
        MultiImageAdapterText.ViewHolder vh = new MultiImageAdapterText.ViewHolder(view, itemClickListener); //  수정

        return vh;
    }

    // onBindViewHolder() - position에 해당하는 데이터를 뷰홀더의 아이템뷰에 표시.
    @Override
    public void onBindViewHolder(MultiImageAdapterText.ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        Uri image_uri = mData.get(position);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        StorageReference reference;
        SharedPreferences sharedPref = LogInActivity.context_login.getSharedPreferences("sharedPreferences", Context.MODE_PRIVATE);
        String loginID = sharedPref.getString("inputID", "");

        String str = position + "." + date;


        final String[] text_diary = new String[1];
        DocumentReference docRef = db.collection("user_photo").document(loginID).collection("Photo").document(str);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot document = task.getResult();
                Map<String, Object> userDiary = new HashMap<>();
                text_diary[0] = document.getString("일기");
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), WritingDiary.class);
                intent.putExtra("localDate", date);
                intent.putExtra("position", position);
                v.getContext().startActivity(intent);
            }
        });



        String context = text_diary[0];
        Glide.with(mContext)
                .load(image_uri)
                .into(holder.image);
    }

    // getItemCount() - 전체 데이터 갯수 리턴.
    @Override
    public int getItemCount() {
        return mData.size();
    }


    public void showingText(int num) {
        this.num = num;
    }
}
