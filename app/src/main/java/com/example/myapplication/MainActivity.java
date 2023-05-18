package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;

import com.example.myapplication.registerandlogin.Login_Fragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    FragmentTransaction transaction;
    FirebaseUser user;
    FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        transaction = getSupportFragmentManager().beginTransaction();
//        transaction.replace(R.id.fragment_container,new Login_Fragment());
//        transaction.commit();
         auth = FirebaseAuth.getInstance();
         user = auth.getCurrentUser();
         if(user!=null){
             Intent intent = new Intent(this, PageContainer.class);
             startActivity(intent);
             finish();
         }
         else{
             changeFragment(new Login_Fragment());
         }
    }
    void changeFragment(Fragment fragment){
        transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container,fragment);
        transaction.commit();
    }

}