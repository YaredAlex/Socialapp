package com.example.myapplication;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.addapter.ChatedMessageAdapter;
import com.example.myapplication.model.ChatListModel;
import com.example.myapplication.model.DBUserInfoModel;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.SuccessContinuation;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ServerTimestamp;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.model.ServerTimestamps;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.firestore.v1.DocumentTransform;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {
  //filed
    FirebaseFirestore db;
    FirebaseAuth auth;
    DBUserInfoModel userInfoModel;
    EditText inputTextMessage;
    Button btnSendMessage;
    ImageButton btnAttach;
    List<Map<String,String>> list = new ArrayList<>();
    List<Map<String,String>> listReceiver = new ArrayList<>();
    ChatedMessageAdapter adapter;
    ChatListModel chatModel;
    String chatAddress;
    Vibrator vibrator;
    DateFormat dateFormat;
    NotificationCompat.Builder notificationBuilder;
    NotificationManagerCompat notificationManager;
    String newMessage = "";
    RecyclerView recyclerMessage;
    Uri fileUri;
    ActivityResultLauncher<Intent> launcher;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Bundle bundle = getIntent().getBundleExtra("listmodel");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "CHANNEL";
            String description = "OURCHANNEL";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("CHANNEL", name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
         launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                 new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if(result.getData()!=null&&result!=null){
                    Intent intent = result.getData();
                    fileUri = intent.getData();
                    Toast.makeText(ChatActivity.this, fileUri.toString(), Toast.LENGTH_SHORT).show();
                    uploadFileToFireStore();
                }
                else
                    Toast.makeText(ChatActivity.this, "please Select file", Toast.LENGTH_SHORT).show();

            }
        });
        chatModel = (ChatListModel) bundle.getSerializable("listmodel");
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        inputTextMessage = findViewById(R.id.edit_text_message);
        btnSendMessage = findViewById(R.id.btn_send_text);
        btnAttach = findViewById(R.id.btn_attach);
        TextView receiverName = findViewById(R.id.receiver_name);
        receiverName.setText(chatModel.getEmail());
        recyclerMessage = findViewById(R.id.recycler_messages);
        dateFormat = new SimpleDateFormat("MM-dd-yyyy hh:mm:ss");
        adapter = new ChatedMessageAdapter(list, this, new ChatedMessageAdapter.ChatSelectedListener() {
            @Override
            public void chatSelected(ChatListModel chatListModel) {
            }
        });
        recyclerMessage.setAdapter(adapter);
        recyclerMessage.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        btnSendMessage.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                sendMessage();
            }
        });
        btnAttach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attachDocument();
            }
        });
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        CheckChatExist();
        //Building Notification
       // buildNotificaiton();
        notificationManager = NotificationManagerCompat.from(this);
        //Listnening To changes in Data Base

    }

    private void uploadFileToFireStore() {
        StorageReference firstReference = FirebaseStorage.getInstance().getReference();
        StorageReference secondReference = firstReference.child("images/"+fileUri.getLastPathSegment());
        UploadTask uploadTask = secondReference.putFile(fileUri);
        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }

                // Continue with the task to get the download URL
                return task.getResult().getStorage().getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    Toast.makeText(ChatActivity.this, "Download uri is"+downloadUri.toString(), Toast.LENGTH_SHORT).show();
                    sendFileMessage(downloadUri);
                } else {
                    // Handle failures
                    // ...
                }
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void sendFileMessage(Uri uri) {
        String msg = uri.toString();
        if(!msg.isEmpty()) {
            Map<String, Object> chatInfo = new HashMap<>();
            SimpleDateFormat s = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            Date d = new Date();
            Map<String,Object> mapMsg = new HashMap<>();
            mapMsg.put("uri",msg);
            mapMsg.put("file","true");
            mapMsg.put("msg","");
            mapMsg.put("timeStamp", s.format(d));
            mapMsg.put("sendTime",java.time.LocalTime.now().format(java.time.format.DateTimeFormatter.ofPattern("hh:mm .a")));
            mapMsg.put("sender",auth.getCurrentUser().getUid());
            chatInfo.put(auth.getCurrentUser().getUid()+java.time.LocalTime.now().format(java.time.format.DateTimeFormatter.ofPattern("hh:mm:ss .a")), mapMsg);
            db.collection("chats").document(chatAddress).set(chatInfo,SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(ChatActivity.this, "File sent Successfully", Toast.LENGTH_SHORT).show();
                    } else
                        Toast.makeText(ChatActivity.this, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }
            });
            Map<String,Object> lastMsg = new HashMap<>();
            lastMsg.put("lastMsg","");
            db.collection("chats").document(chatAddress).set(lastMsg,SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                    } else
                        Toast.makeText(ChatActivity.this, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }
            });
            listenToChanges();
        }

    }

    private void attachDocument() {


        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        launcher.launch(intent);
    }

    private void buildNotificaiton() {
        String CHANNEL_ID = "CHANNEL";
        notificationBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.notification_icon)
                .setContentTitle("Message")
                .setContentText(newMessage)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);
    }

    private void listenToChanges() {
        db.collection("chats").document(chatAddress).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if(error!=null){
                    Toast.makeText(ChatActivity.this, error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }
                else{
                    Map<String,Object> change = new HashMap<>();
                    change =  value.getData();
                    if(change!=null) {
                        change.remove("lastMsg");
                        list = new ArrayList<>();
                        Map<String, String> msg=new HashMap<>();
                        for (Map.Entry<String, Object> entry : change.entrySet()) {
                            msg = new HashMap<>();
                            msg = (Map<String, String>) entry.getValue();

                                list.add(msg);

                        }
                        sortMessage();
                        newMessage = list.get(list.size()-1).get("msg");
                        buildNotificaiton();
                        notificationManager.notify(1,notificationBuilder.build());
                        adapter.setList(list);
                        recyclerMessage.smoothScrollToPosition(adapter.getItemCount()-1);
                        vibrator.vibrate(300);
                    }
                }
            }
        });
    }
    private void CheckChatExist(){
        db.collection("users").document(auth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                  if(task.isSuccessful()){
                      Toast.makeText(ChatActivity.this, "connected To User", Toast.LENGTH_SHORT).show();
                        DocumentSnapshot snapshot = task.getResult();
                      Map<String,Object> exist = new HashMap<>();
                        exist = snapshot.getData();
                        if(exist.get(chatModel.getUid())!=null){
                            chatAddress = exist.get(chatModel.getUid()).toString();
                            fetchExistingMessage(exist.get(chatModel.getUid()).toString());
                        }
                        else{
                            registerChatToUsers();
                        }

                  }
                  else{
                      Toast.makeText(ChatActivity.this, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                      //make that app to chat

                  }
            }
        });
    }

    private void fetchExistingMessage(String chatAddress) {
        db.collection("chats").document(chatAddress).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    Map<String,Object> chats = new HashMap<>();
                    List<String> listOfChats = new ArrayList<>();
                    if(task.getResult().getData()!=null){
                        int flag = 0;
                        Map<String,String> msg = new HashMap<>();
                         chats = task.getResult().getData();
                         chats.remove("lastMsg");
                         for(Map.Entry<String,Object> entry : chats.entrySet()){
                             msg = (Map<String, String>) entry.getValue();

                                 list.add(msg);

                            sortMessage();
                         }
                         adapter.setList(list);
                         recyclerMessage.smoothScrollToPosition(adapter.getItemCount()-1);
                         recyclerMessage.scrollToPosition(adapter.getItemCount()-1);
                    }
                }
            }
        });
    }
   private void getPreviousConversation(String chatAddress){

   }
    private void registerChatToUsers() {
        Map<String, Object> chatInfo = new HashMap<>();
        chatInfo.put(chatModel.getUid(), chatModel.getUid()+auth.getCurrentUser().getUid());
        chatAddress = chatModel.getUid()+auth.getCurrentUser().getUid();
        Log.e("result of map is ",chatInfo.toString());
        db.collection("users").document(auth.getCurrentUser().getUid()).set(chatInfo, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(ChatActivity.this, "first address if formed created", Toast.LENGTH_SHORT).show();
                    registerChatToSecondUser();
                }
                else
                    Toast.makeText(ChatActivity.this, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void registerChatToSecondUser() {
        Map<String, Object> chatInfo = new HashMap<>();
        chatInfo.put(auth.getCurrentUser().getUid(), chatModel.getUid()+auth.getCurrentUser().getUid());
        db.collection("users").document(chatModel.getUid()).set(chatInfo, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(ChatActivity.this, "second address is formed ", Toast.LENGTH_SHORT).show();
                }
                else
                    Toast.makeText(ChatActivity.this, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void sendMessage() {
        String msg = inputTextMessage.getText().toString();
        if(!msg.isEmpty()) {
            Map<String, Object> chatInfo = new HashMap<>();
            SimpleDateFormat s = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            Date d = new Date();
            Map<String,Object> mapMsg = new HashMap<>();
            mapMsg.put("msg",msg);
            mapMsg.put("file","false");
            mapMsg.put("timeStamp", s.format(d));
            mapMsg.put("sendTime",java.time.LocalTime.now().format(java.time.format.DateTimeFormatter.ofPattern("hh:mm .a")));
            mapMsg.put("sender",auth.getCurrentUser().getUid());
            chatInfo.put(auth.getCurrentUser().getUid()+java.time.LocalTime.now().format(java.time.format.DateTimeFormatter.ofPattern("hh:mm:ss .a")), mapMsg);
            db.collection("chats").document(chatAddress).set(chatInfo,SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(ChatActivity.this, "Sent", Toast.LENGTH_SHORT).show();
                    } else
                        Toast.makeText(ChatActivity.this, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }
            });
            Map<String,Object> lastMsg = new HashMap<>();
            lastMsg.put("lastMsg",msg);
            db.collection("chats").document(chatAddress).set(lastMsg,SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                    } else
                        Toast.makeText(ChatActivity.this, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }
            });
            listenToChanges();
        }
        inputTextMessage.setText("");

    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void sortMessage(){
        list.sort(new Comparator<Map<String, String>>() {
            @Override
            public int compare(Map<String, String> t1, Map<String, String> t2) {
                return t1.get("timeStamp").compareTo(t2.get("timeStamp"));
            }
        });

    }
}