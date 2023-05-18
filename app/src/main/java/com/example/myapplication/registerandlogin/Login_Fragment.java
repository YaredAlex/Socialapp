package com.example.myapplication.registerandlogin;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.PageContainer;
import com.example.myapplication.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.regex.Pattern;

public class Login_Fragment extends Fragment {


    View view;
    Button btnLogin;
    EditText editTextuserEmail,editTextuserPassword;
    FirebaseAuth auth;
    AlertDialog.Builder builder;
    AlertDialog alertDialog;
    TextView signup;
    FragmentTransaction transaction;
    String userEmail;
    public Login_Fragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        transaction = getActivity().getSupportFragmentManager().beginTransaction();
        view = inflater.inflate(R.layout.fragment_login_, container, false);
        btnLogin = view.findViewById(R.id.btn_login);
        editTextuserEmail = view.findViewById(R.id.edit_user_email);
        editTextuserPassword = view.findViewById(R.id.edit_user_password);
        signup = view.findViewById(R.id.txt_signup);
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               changeFragment(new Register_Fragment());
            }
        });
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginUser();
            }
        });
        buildDialog();
        return view;
    }

    private void buildDialog() {
        View alertView = LayoutInflater.from(getContext()).inflate(R.layout.alert_dialog_layout,null);
        builder = new AlertDialog.Builder(getActivity());
        builder.setView(alertView);
        builder.setTitle("Please wait");
        alertDialog = builder.create();
    }

    private void loginUser() {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                alertDialog.show();
            }
        });
        t.run();
        auth = FirebaseAuth.getInstance();
        userEmail = editTextuserEmail.getText().toString();
        String userPassword = editTextuserPassword.getText().toString();
        if(checkUserEmail()) {
            auth.signInWithEmailAndPassword(userEmail, userPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()) {
                        Toast.makeText(getActivity(), "User sign in successful", Toast.LENGTH_SHORT).show();
                        startPageContainerActivity();
                    } else {
                        Toast.makeText(getActivity(), task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();

                    }
                    alertDialog.dismiss();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getActivity(), e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                    alertDialog.dismiss();
                }
            });
            t.interrupt();
        }
        else{
            Toast.makeText(getActivity(), "invalid Email", Toast.LENGTH_SHORT).show();
            editTextuserEmail.setError("invalid");
        }
    }

    private boolean checkUserEmail() {
      String regex = "^[a-zA-Z0-9_+&*-]+(?:\\."+
              "[a-zA-Z0-9_+&*-]+)*@" +
              "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
              "A-Z]{2,7}$";
        Pattern pattern = Pattern.compile(regex);
        if(pattern.matcher(userEmail).matches()){
            return true;
        }
        return false;

    }

    void changeFragment(Fragment fragment){
        transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container,fragment);
        transaction.commit();
    }
    void startPageContainerActivity(){
        Intent intent = new Intent(getContext(), PageContainer.class);
        startActivity(intent);
        getActivity().finish();
    }
}