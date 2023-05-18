package com.example.myapplication.addapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.model.TopStories;

import java.util.ArrayList;
import java.util.List;

public class TopRecylcerAdapter extends RecyclerView.Adapter<TopRecylcerAdapter.ViewHolder>{

   List<TopStories> list = new ArrayList<>();
   Context context;
   public TopRecylcerAdapter(List<TopStories> list,Context context){
       this.list = list;
       this.context = context;
   }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.top_home_recyler_card,parent,false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
          int imageSource = context.getResources().getIdentifier(list.get(position).getImgUri(),null,context.getPackageName());
          holder.topImg.setImageDrawable(context.getResources().getDrawable(imageSource));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
         ImageView topImg;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            topImg = itemView.findViewById(R.id.top_img_view);
        }
    }
}
