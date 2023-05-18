package com.example.myapplication.appFeatures;

import android.graphics.Rect;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.myapplication.R;
import com.example.myapplication.addapter.TopRecylcerAdapter;
import com.example.myapplication.model.TopStories;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {


    RecyclerView toprecylerView;
    List<TopStories> list;
    TextView txtUserName;
    FirebaseUser user;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        list = new ArrayList<>();
        list.add(new TopStories("@drawable/img1"));
        list.add(new TopStories("@drawable/img2"));
        list.add(new TopStories("@drawable/img3"));
        list.add(new TopStories("@drawable/img4"));
        TopRecylcerAdapter adapter = new TopRecylcerAdapter(list,getContext());
        toprecylerView = view.findViewById(R.id.home_top_recycler_view);
        toprecylerView.setAdapter(adapter);
        toprecylerView.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false));
        toprecylerView.addItemDecoration(new Decorator(10));
        //
        user = FirebaseAuth.getInstance().getCurrentUser();
        txtUserName = view.findViewById(R.id.textview_username);
        txtUserName.setText(user.getEmail());
        return view;
    }
    class Decorator extends RecyclerView.ItemDecoration{
        int margin;
        Decorator(int m){
            this.margin = m;
        }
        @Override
        public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
            outRect.right = margin;
        }
    }
}