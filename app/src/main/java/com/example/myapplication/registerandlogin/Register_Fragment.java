package com.example.myapplication.registerandlogin;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.myapplication.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Register_Fragment extends Fragment {


    public Register_Fragment() {
        // Required empty public constructor
    }

    Button btnNext;
    EditText inputEmail,inputPassword,inputConfirm;
    FragmentTransaction transaction;
    FirebaseAuth auth;
    ProgressDialog progressDialog;
    String email,password;
    FirebaseFirestore db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
         View view = inflater.inflate(R.layout.fragment_register_, container, false);
         inputEmail = view.findViewById(R.id.register_email);
         inputPassword = view.findViewById(R.id.register_password);
         inputConfirm = view.findViewById(R.id.register_confirm);
         btnNext = view.findViewById(R.id.register_btn_next);
         btnNext.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 checkEmailAndPassword();
             }
         });
        return view;
    }

    private void checkEmailAndPassword() {
         email = inputEmail.getText().toString();
         password = inputPassword.getText().toString();
        String confirm = inputConfirm.getText().toString();
        String regex = "^[a-zA-Z0-9_+&*-]+(?:\\."+
                "[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                "A-Z]{2,7}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(email);
        if(!matcher.matches()){
            Toast.makeText(getActivity(), "Invalid Email", Toast.LENGTH_SHORT).show();
            return;
        }
        else if(password.length()<8){
            Toast.makeText(getActivity(), "Password Must Be greater that 8", Toast.LENGTH_SHORT).show();
            return;
        }
        else if (!password.equals(confirm)){
            Toast.makeText(getActivity(), "Password don't match", Toast.LENGTH_SHORT).show();
            return;
        }

        createAccountForNewUser();
    }

    private void createAccountForNewUser() {
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle("Creating Account....");
        progressDialog.setMessage("please wait");
        progressDialog.setCancelable(false);
        progressDialog.show();
        Toast.makeText(getActivity(), "Progressing", Toast.LENGTH_SHORT).show();
        auth = FirebaseAuth.getInstance();
        auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Toast.makeText(getActivity(), "Created success fully", Toast.LENGTH_SHORT).show();
                    addUserToFireStore();
                }
                else{
                    Toast.makeText(getActivity(), task.getException().getLocalizedMessage(), Toast.LENGTH_LONG).show();
                    progressDialog.dismiss();
                }
            }
        });

    }

    private void addUserToFireStore() {
        db = FirebaseFirestore.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();
        Map<String,Object> user = new HashMap<>();
        user.put("uid",currentUser.getUid());
        user.put("email",currentUser.getEmail());
        db.collection("users").document(currentUser.getUid()).set(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(getActivity(), "add to firebase", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                    changeFragment(new PersenalDetailFragment());
                }
                else{
                    Toast.makeText(getActivity(), task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    void changeFragment(Fragment fragment){
        transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.addToBackStack(null);
        transaction.replace(R.id.fragment_container,fragment);
        transaction.commit();
    }
}