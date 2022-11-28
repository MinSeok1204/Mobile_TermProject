package com.example.termproject;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;
import android.util.Printer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ContextAdapter extends RecyclerView.Adapter<ContextAdapter.ViewHolder> {
    private Cursor   mData    = null;
    private Context  mContext = null;

    private int _uid;   //유저 아이디
    private int type;   //게시물 타입(공지: 1, 자유 게시판: 0)

    public ContextAdapter(Cursor mData, Context context, int type , int _uid){
        this.mContext = context;
        this.mData = mData;
        this.type = type;
        this._uid = _uid;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView            postIdField;
        private TextView            titleField;
        private TextView            writerField;
        private LinearLayout        contextArea;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            postIdField = itemView.findViewById(R.id.postIdField);
            titleField = itemView.findViewById(R.id.titleField);
            writerField = itemView.findViewById(R.id.writerField);
            contextArea = itemView.findViewById(R.id.contextArea);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.context_item,parent,false);
        ContextAdapter.ViewHolder ViewHolder= new ContextAdapter.ViewHolder(view);

        return ViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        mData.moveToPosition(position);
        Log.e("Context: ",Integer.toString(mData.getCount()));
        Log.e("position: ",Integer.toString(position));

        int postId = mData.getInt(mData.getColumnIndexOrThrow("_id"));
        String title = mData.getString(mData.getColumnIndexOrThrow("title"));
        String writer = mData.getString(mData.getColumnIndexOrThrow("userid"));
        if(type == 0){
            holder.postIdField.setText(Integer.toString(position+1));
            holder.titleField.setText(title);
            holder.writerField.setText(writer);
            holder.contextArea.setOnClickListener(e->{
                Intent intent = new Intent(mContext,DetailContextActivity.class);
                intent.putExtra("_uid",_uid);
                intent.putExtra("_id",postId);
                intent.putExtra("_type","community");
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                mContext.startActivity(intent);
            });
        } else {
            holder.postIdField.setText("공지");
            holder.titleField.setText(title);
            holder.writerField.setText("관리자");
            holder.contextArea.setOnClickListener(e->{
                Intent intent = new Intent(mContext,DetailContextActivity.class);
                intent.putExtra("_uid",_uid);
                intent.putExtra("_id",postId);
                intent.putExtra("_type","notice");
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                mContext.startActivity(intent);
            });
        }

    }

    @Override
    public int getItemCount() {
        return mData.getCount();
    }
}
