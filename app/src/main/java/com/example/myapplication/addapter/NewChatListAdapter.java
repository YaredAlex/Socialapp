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

public class NewChatListAdapter  extends RecyclerView.Adapter<NewChatListAdapter.ViewHolder>{

    List<ChatListModel> list = new ArrayList<>();
    Context context;
    ChatListAdapter.ChatSelectedListener listener;
    public NewChatListAdapter(List<ChatListModel> list,
                           Context context, ChatListAdapter.ChatSelectedListener listener){
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
        int source = context.getResources().getIdentifier("@drawable/img1",null,context.getPackageName());
        holder.personImage.setImageDrawable(context.getResources().getDrawable(source));
        holder.lastText.setVisibility(View.GONE);
        holder.userChatName.setText(list.get(pos).getEmail());
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.chatSelected(list.get(pos));
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        ImageView personImage;
        TextView lastText,userChatName;
        CardView cardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            personImage = itemView.findViewById(R.id.chat_person_img);
            cardView = itemView.findViewById(R.id.cardview_chat);
            lastText = itemView.findViewById(R.id.textview_last_text);
            userChatName = itemView.findViewById(R.id.chat_user_name);
        }
    }
    public void setChatList(List<ChatListModel> list){
        this.list = list;
        notifyDataSetChanged();
    }
    public interface ChatSelectedListener{
        void chatSelected(ChatListModel chatListModel);
    }
}
