package com.example.myapplication.registerandlogin;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.PageContainer;
import com.example.myapplication.R;


public class PersenalDetailFragment extends Fragment {


   TextView txtSkip;
    public PersenalDetailFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_persenal_detail, container, false);
        txtSkip = view.findViewById(R.id.txt_skip);
        txtSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), "Home page", Toast.LENGTH_SHORT).show();
                startPageContainerActivity();
            }
        });
        return view;
    }
    void startPageContainerActivity(){
        Intent intent = new Intent(getActivity(), PageContainer.class);
        startActivity(intent);
        getActivity().finish();
    }
}