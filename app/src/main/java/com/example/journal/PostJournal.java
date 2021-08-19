package com.example.journal;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Date;
import java.util.Objects;

import Util.JournalAPI;
import model.Journal;

public class PostJournal extends AppCompatActivity {

    private EditText title, desc;
    private TextView postUser, postDate;
    private Button submitPostButton;
    private ImageView post, addPostImage;
    private ProgressBar progressBar;

    private String currentUserId;
    private String currentUsername;
    private Uri uri;

    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser currentUser;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private StorageReference storage;
    private CollectionReference jourRef = db.collection("Journal");

    private ActivityResultLauncher<Intent> imageResultLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_journal);
        Objects.requireNonNull(getSupportActionBar()).setElevation(0);

        progressBar = findViewById(R.id.postProgressBar);
        title = findViewById(R.id.postTitleText);
        desc = findViewById(R.id.titleDescription);
        postUser = findViewById(R.id.postUser);
        postDate = findViewById(R.id.postDate);
        submitPostButton = findViewById(R.id.postButton);
        post = findViewById(R.id.postImage);
        addPostImage = findViewById(R.id.addPost);

        imageResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if(result.getResultCode() == Activity.RESULT_OK){
                            Intent data = result.getData();
                            previewImage(data);
                        }
                    }
                });

        storage = FirebaseStorage.getInstance().getReference();

        auth = FirebaseAuth.getInstance();

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                currentUser = auth.getCurrentUser();
                if(currentUser != null){

                }
                else{

                }
            }
        };

        if(JournalAPI.getInstance() != null){
            currentUsername = JournalAPI.getInstance().getUsername();
            currentUserId = JournalAPI.getInstance().getUserId();

            postUser.setText(currentUsername);
        }

        addPostImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            imageResultLauncher.launch(intent);
        });

        submitPostButton.setOnClickListener(v -> {
            saveJournal();
        });

    }

    private void saveJournal(){
        String titleText = title.getText().toString().trim();
        String descText = desc.getText().toString().trim();

        progressBar.setVisibility(View.VISIBLE);

        if(!titleText.isEmpty() && !descText.isEmpty()
        && uri != null){
            StorageReference filepath = storage
                    .child("journal_image")
                    .child("image_" + Timestamp.now().getSeconds());
            filepath.putFile(uri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String url = uri.toString();
                                    Journal journal = new Journal();
                                    journal.setImageUrl(url);
                                    journal.setTitle(titleText);
                                    journal.setThought(descText);
                                    journal.setUsername(currentUsername);
                                    journal.setUserId(currentUserId);
                                    journal.setTimeAdded(new Timestamp(new Date()));

                                    jourRef.add(journal)
                                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                @Override
                                                public void onSuccess(DocumentReference documentReference) {
                                                    progressBar.setVisibility(View.INVISIBLE);
                                                    startActivity(new Intent(PostJournal.this, JournalListActivity.class));
                                                    finish();
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    progressBar.setVisibility(View.INVISIBLE);
                                                }
                                            });
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressBar.setVisibility(View.INVISIBLE);
                        }
                    });
        }
        else{
            Toast.makeText(PostJournal.this, "Fill all the fields", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.INVISIBLE);
        }
    }

    protected void previewImage(Intent data){
        try{
            uri = data.getData();
            post.setImageURI(uri);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        currentUser = auth.getCurrentUser();
        auth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(auth != null)
            auth.removeAuthStateListener(authStateListener);
    }
}