package com.example.journal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import Util.JournalAPI;

public class SignUpActivity extends AppCompatActivity {

    private EditText email, username;
    private EditText password;
    private Button signUp;
    private ProgressBar progressBar;

    private FirebaseAuth auth;
    private FirebaseUser currentUser;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference usersRef = db.collection("Users");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        Objects.requireNonNull(getSupportActionBar()).setElevation(0);

        email = findViewById(R.id.signupEmail);
        username = findViewById(R.id.signupUsername);
        password = findViewById(R.id.signupPassword);
        progressBar = findViewById(R.id.signupProgress);

        signUp = findViewById(R.id.signUpButton);

        auth = FirebaseAuth.getInstance();


        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailText = email.getText().toString().trim();
                String passwordText = password.getText().toString().trim();
                String usernameText = username.getText().toString().trim();

                if(emailText.isEmpty() && passwordText.isEmpty() && usernameText.isEmpty()){
                    Toast.makeText(SignUpActivity.this, "Fields can't be left empty", Toast.LENGTH_SHORT).show();
                }
                else
                    signUpUser(emailText, passwordText, usernameText);
            }
        });
    }

    protected void signUpUser(String email, String password, String username){
        progressBar.setVisibility(View.VISIBLE);
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            currentUser = auth.getCurrentUser();
                            if(currentUser != null){
                                String userId = currentUser.getUid();
                                Map<String, String> map = new HashMap<>();
                                map.put("userId", userId);
                                map.put("username", username);

                                usersRef.add(map)
                                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                            @Override
                                            public void onSuccess(DocumentReference documentReference) {
                                                documentReference.get()
                                                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                if(Objects.requireNonNull(task.getResult()).exists()){
                                                                    progressBar.setVisibility(View.INVISIBLE);
                                                                    String user = task.getResult().getString("username");

                                                                    JournalAPI journalAPI = JournalAPI.getInstance();
                                                                    journalAPI.setUserId(userId);
                                                                    journalAPI.setUsername(user);

                                                                    Intent intent = new Intent(SignUpActivity.this, PostJournal.class);
                                                                    intent.putExtra("username", user);
                                                                    intent.putExtra("userId", userId);
                                                                    startActivity(intent);
                                                                }
                                                                else
                                                                    progressBar.setVisibility(View.INVISIBLE);
                                                            }
                                                        });
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {

                                            }
                                        });

                            }
                        }
                        else{
                            Toast.makeText(SignUpActivity.this, "There was a problem", Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.INVISIBLE);
                        }
                    }
                });
    }
}

//startActivity(new Intent(SignUpActivity.this, PostJournal.class));