package com.example.myapplication.appFeatures;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.myapplication.ChatActivity;
import com.example.myapplication.R;
import com.example.myapplication.addapter.ChatListAdapter;
import com.example.myapplication.addapter.NewChatListAdapter;
import com.example.myapplication.model.ChatListModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;


public class NewChatListFragment extends Fragment {

    NewChatListAdapter chatListAdapter;
    FirebaseFirestore db;
    List<ChatListModel> list = new ArrayList<>();
    ProgressDialog progressDialog;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_new_chat_list, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.new_chat_list);

        chatListAdapter = new NewChatListAdapter(list, getContext(), new ChatListAdapter.ChatSelectedListener() {
            @Override
            public void chatSelected(ChatListModel chatListModel) {
              //changeFragment(new ChatFragment(),"chat");
                Intent intent = new Intent(getActivity(), ChatActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("listmodel",chatListModel);
                intent.putExtra("listmodel",bundle);
                startActivity(intent);
            }
        });
        recyclerView.setAdapter(chatListAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false));
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(),DividerItemDecoration.VERTICAL));
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setTitle("Please Wait");
        progressDialog.setMessage("getting all users");
        progressDialog.setCancelable(false);
        progressDialog.show();
        fetchAllUsers();
        return view;
    }
    private void fetchAllUsers() {
        db = FirebaseFirestore.getInstance();
        db.collection("users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    Toast.makeText(getActivity(), "All Users", Toast.LENGTH_SHORT).show();
                    for(QueryDocumentSnapshot snapshot : task.getResult()){
                        ChatListModel chatListModel = snapshot.toObject(ChatListModel.class);
                        if(chatListModel!=null){
                            Log.e("result",chatListModel.getEmail());
                            list.add(chatListModel);
                        }

                    }
                    ;
                    chatListAdapter.setChatList(list);
                }
                else{
                    Toast.makeText(getActivity(), task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }
                progressDialog.dismiss();
            }
        });
    }
    private void changeFragment(Fragment fragment,String tag) {
        FragmentManager manager = getActivity().getSupportFragmentManager();
        manager.beginTransaction().replace(R.id.frame_fragment,fragment,tag).addToBackStack(null).commit();
    }
}