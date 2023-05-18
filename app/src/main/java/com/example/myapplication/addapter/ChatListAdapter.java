package com.example.myapplication.addapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.model.ChatListModel;

import java.util.ArrayList;
import java.util.List;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ViewHolder>{

    List<ChatListModel> list = new ArrayList<>();
    Context context;
    ChatSelectedListener listener;
    public ChatListAdapter(List<ChatListModel> list,
                           Context context,ChatSelectedListener listener){
        this.list = list;
        this.context = context;
        this.listener = listener;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_list_layout,parent,false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
           int pos = position;
         // int source = context.getResources().getIdentifier(list.get(pos).getImgUrl(),null,context.getPackageName());
         //  holder.personImage.setImageDrawable(context.getResources().getDrawable(source));
           holder.chatUserName.setText(list.get(pos).getEmail());
           holder.lastText.setText(list.get(pos).getLastChat());
           holder.cardView.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view) {
                   listener.chatSelected(list.get(pos));
               }
           });
    }
    public void setList(List<ChatListModel> l){
        this.list.clear();
        this.list.addAll(l);
        this.notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        ImageView personImage;
        TextView lastText,chatUserName;
        CardView cardView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            personImage = itemView.findViewById(R.id.chat_person_img);
            cardView = itemView.findViewById(R.id.cardview_chat);
            lastText = itemView.findViewById(R.id.textview_last_text);
            chatUserName = itemView.findViewById(R.id.chat_user_name);
        }
    }
   public interface ChatSelectedListener{
        void chatSelected(ChatListModel chatListModel);
    }
}
