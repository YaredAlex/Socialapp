package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.myapplication.appFeatures.ChatList_Fragment;
import com.example.myapplication.appFeatures.ExplorePage;
import com.example.myapplication.appFeatures.HomeFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class PageContainer extends AppCompatActivity {

    BottomNavigationView navigationView;
    FragmentManager fragmentManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page_container);
        navigationView = findViewById(R.id.navigation_bar);
        HomeFragment home = new HomeFragment();
        ExplorePage explore = new ExplorePage();
        ChatList_Fragment chat = new ChatList_Fragment();
        fragmentManager = getSupportFragmentManager();
        changeFragment(home,"home");
        navigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.menu_home:
                        changeFragment(home,"home");
                        Toast.makeText(PageContainer.this, "Menu Home touched", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.menu_explore:
                        changeFragment(explore,"explore");
                        Toast.makeText(PageContainer.this, "Explore menu", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.menu_chat:
                        changeFragment(chat,"chat");
                        Toast.makeText(PageContainer.this, "Chat touched", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.menu_person:
                        Toast.makeText(PageContainer.this, "Personal ", Toast.LENGTH_SHORT).show();
                        break;
                }
                return true;
            }
        });
    }
    void changeFragment(Fragment fragment,String tag){
        FragmentManager manager = getSupportFragmentManager();
        if(manager.findFragmentByTag("home")!=null){
           manager.beginTransaction().hide(manager.findFragmentByTag("home")).commit();
        }
        if(manager.findFragmentByTag("personal")!=null){
            manager.beginTransaction().hide(manager.findFragmentByTag("personal")).commit();
        }
        if(manager.findFragmentByTag("explore")!=null){
            manager.beginTransaction().hide(manager.findFragmentByTag("explore")).commit();
        }
        if(manager.findFragmentByTag("chat")!=null){
            manager.beginTransaction().hide(manager.findFragmentByTag("chat")).commit();
        }
        if(manager.findFragmentByTag(tag)!=null){
            manager.beginTransaction().show(manager.findFragmentByTag(tag)).commit();
        }
        else{
            manager.beginTransaction().add(R.id.frame_fragment,fragment,tag).commit();
        }

    }


}