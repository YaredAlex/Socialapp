package com.example.myapplication.appFeatures;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
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
import com.example.myapplication.model.ChatListModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


public class ChatList_Fragment extends Fragment {

    RecyclerView recyclerView;
    List<ChatListModel> list = new ArrayList<>();
    CardView addChat;
    FirebaseFirestore db;
    Map<String,Object> userChats;
    ChatListAdapter chatListadapter;
    ProgressDialog progressDialog;
    ChatListModel chatModel;
    Iterator<Map.Entry<String,Object>> iterator;
    int flaglastMsg;
    public ChatList_Fragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat_list, container, false);
        recyclerView = view.findViewById(R.id.chat_list);
        addChat = view.findViewById(R.id.add_chat);
       chatListadapter = new ChatListAdapter(list, getContext(), new ChatListAdapter.ChatSelectedListener() {
            @Override
            public void chatSelected(ChatListModel chatListModel) {
               // changeFragment(new ChatFragment(),"chatlist");
                Intent intent = new Intent(getActivity(), ChatActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("listmodel",chatListModel);
                intent.putExtra("listmodel",bundle);
                startActivity(intent);
            }
        });
        recyclerView.setAdapter(chatListadapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(),DividerItemDecoration.VERTICAL));
        //handling click on addChat
        addChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeFragment(new NewChatListFragment(),"Newchatlist");
            }
        });
        db = FirebaseFirestore.getInstance();
        //Building Dialog
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle("Please Wait");
        progressDialog.setMessage("getting Your chat list");
        progressDialog.setCancelable(false);
        progressDialog.show();
        getChatList();
        return view;
    }

    private void getChatList() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        db.collection("users").document(auth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                   userChats = new HashMap<>();
                    userChats= task.getResult().getData();
                    userChats.remove("email");
                    userChats.remove("uid");
                    Log.e("user Chats",userChats.toString());
                    iterator = userChats.entrySet().iterator();
                    list = new ArrayList<>();
                    fetchUserDetails();
                }
               else
                    Toast.makeText(getContext(), task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    interface LastChatCallBack{
        void getLastChat();
    }
    private void fetchUserDetails() {

        if(iterator.hasNext()){
            Map.Entry<String,Object> entry = iterator.next();
            getUserDetailDB(entry.getKey(),entry.getValue().toString(), new LastChatCallBack() {
                @Override
                public void getLastChat() {
                    fetchUserDetails();
                }
            });
        }

       // Log.e("UpdatingChat List","called right now !!!!!");
       // chatListadapter.setList(list);
        progressDialog.dismiss();
    }

    private void getUserDetailDB(String id,String chatAddress,LastChatCallBack callBack) {
        db.collection("users").document(id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    chatModel = new ChatListModel();
                    String email = task.getResult().getData().get("email").toString();
                    String id = task.getResult().getData().get("uid").toString();
                     chatModel.setEmail(email);
                     chatModel.setUid(id);
                     getLastChat(chatAddress,callBack);
                    // Log.e("After","this message should be printed after the chat result is ");
                     //list.add(chatModel);
                   // chatListadapter.setList(list);
                }
                else
                    Toast.makeText(getActivity(), task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private int getLastChat(String chatAddress,LastChatCallBack callBack) {

        db.collection("chats").document(chatAddress).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    try{
                        chatModel.setLastChat(task.getResult().getData().get("lastMsg").toString());
                      }catch (Exception e){
                        Log.e("Last Message",e.getLocalizedMessage());
                     }finally {
                        Log.e("last chat function ", chatModel.getEmail());
                        callBack.getLastChat();
                        list.add(chatModel);
                        chatListadapter.setList(list);
                    }
                }
                else
                    Toast.makeText(getActivity(), task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();

            }
        });

        return 0;
    }


    private void changeFragment(Fragment fragment,String tag) {
        FragmentManager manager = getActivity().getSupportFragmentManager();
        manager.beginTransaction().replace(R.id.frame_fragment,fragment,tag).addToBackStack(null).commit();
    }
}