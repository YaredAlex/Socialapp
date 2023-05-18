package com.example.myapplication.addapter;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.myapplication.R;
import com.example.myapplication.model.ChatListModel;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ChatedMessageAdapter extends RecyclerView.Adapter<ChatedMessageAdapter.ViewHolder> {
    List<Map<String,String>> list;
    Context context;
    ChatSelectedListener listener;
    FirebaseAuth auth;
    public ChatedMessageAdapter(List<Map<String,String>> list,
                           Context context, ChatSelectedListener listener){
        this.list = list;
        this.context = context;
        this.listener = listener;
        auth = FirebaseAuth.getInstance();
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int viewId = viewType==0 ? R.layout.recycler_chated_message:R.layout.recycler_chat_received_layout;
        View view = LayoutInflater.from(parent.getContext()).inflate(viewId,parent,false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }
   public int getItemViewType(int pos){
        if(list.get(pos).get("sender").equals(auth.getCurrentUser().getUid()))
            return 0;
        return 1;
   }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        int pos = position;
       // int source = context.getResources().getIdentifier(list.get(pos).getImgUrl(),null,context.getPackageName());
       // holder.personImage.setImageDrawable(context.getResources().getDrawable(source));
//        holder.cardView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                listener.chatSelected(list.get(pos));
//            }
//        });
        if(list.get(pos).get("file").equals("true")){
            Glide.with(context).load(list.get(pos).get("uri")).fitCenter().into(holder.chatImg);
        }
        else
            holder.chatImg.setVisibility(View.GONE);

        holder.textMessage.setText(list.get(pos).get("msg"));
        holder.msgTime.setText(list.get(pos).get("sendTime"));

    }
    public void setList(List<Map<String,String>> updated){
        list = updated;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
       // ImageView personImage;
        TextView textMessage;
        TextView msgTime;
        ImageView chatImg;
        //CardView cardView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            //personImage = itemView.findViewById(R.id.chat_person_img);
            //cardView = itemView.findViewById(R.id.cardview_chat);
            textMessage = itemView.findViewById(R.id.text_message);
            msgTime = itemView.findViewById(R.id.msg_time);
            chatImg = itemView.findViewById(R.id.chat_img);
        }
    }
    public interface ChatSelectedListener{
        void chatSelected(ChatListModel chatListModel);
    }
}
