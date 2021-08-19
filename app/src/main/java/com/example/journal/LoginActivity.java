package com.example.journal;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Objects;

import Util.JournalAPI;
import model.Journal;

public class LoginActivity extends AppCompatActivity {

    private Button signUp, login;
    private EditText emailText, passwordText;
    private ProgressBar progressBar;

    private FirebaseAuth auth;
    private FirebaseUser currentUser;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference ref = db.collection("Users");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Objects.requireNonNull(getSupportActionBar()).setElevation(0);

        progressBar = findViewById(R.id.loginProgress);
        emailText = findViewById(R.id.loginEmail);
        passwordText = findViewById(R.id.loginPassword);
        signUp = findViewById(R.id.signUpNavButton);
        login = findViewById(R.id.loginButton);


        auth = FirebaseAuth.getInstance();

        signUp.setOnClickListener( v -> {
            startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
        });

        login.setOnClickListener(v -> {
            loginUser(emailText.getText().toString().trim(), passwordText.getText().toString().trim());
        });
    }

    private void loginUser(String email, String password) {
        progressBar.setVisibility(View.VISIBLE);
        if(!email.isEmpty() && !password.isEmpty()){
            auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            progressBar.setVisibility(View.INVISIBLE);
                            FirebaseUser user = auth.getCurrentUser();
                            assert user != null;
                            String userId = user.getUid();

                            ref.whereEqualTo("userId", userId)
                                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                        @Override
                                        public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                                            if(error != null){}

                                            assert value != null;
                                            if(!value.isEmpty()){
                                                for(QueryDocumentSnapshot snapshot : value){
                                                    JournalAPI journalAPI = JournalAPI.getInstance();
                                                    journalAPI.setUsername(snapshot.getString("username"));
                                                    journalAPI.setUserId(snapshot.getString("userId"));

                                                    startActivity(new Intent(
                                                            LoginActivity.this, JournalListActivity.class
                                                    ));
                                                    finish();
                                                }
                                            }
                                        }
                                    });

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressBar.setVisibility(View.INVISIBLE);
                            Toast.makeText(LoginActivity.this, "Incorrect email or password", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
        else
            Toast.makeText(LoginActivity.this, "Fill all the fields", Toast.LENGTH_SHORT).show();
    }
}