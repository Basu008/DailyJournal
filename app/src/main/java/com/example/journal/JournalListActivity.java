package com.example.journal;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import Util.JournalAPI;
import model.Journal;
import ui.JournalRecyclerAdapter;

public class JournalListActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser currentUser;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference reference = db.collection("Journal");
    private StorageReference storage;

    private RecyclerView recyclerView;
    private JournalRecyclerAdapter recyclerAdapter;
    private List<Journal> journalList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_journal_list);
        Objects.requireNonNull(getSupportActionBar()).setElevation(0);

        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();

        journalList = new ArrayList<>();

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.addPostMenu:
                if(currentUser != null && auth != null)
                    startActivity(new Intent(
                            JournalListActivity.this, PostJournal.class
                    ));
                break;
            case R.id.signOut:

                if(currentUser != null && auth != null){
                    auth.signOut();
                    startActivity(new Intent(
                            JournalListActivity.this, MainActivity.class
                    ));
                    finish();
                }

                break;
        }

        return super.onOptionsItemSelected(item);

    }

    @Override
    protected void onStart() {

        if(recyclerAdapter == null){
            reference.whereEqualTo("userId", JournalAPI.getInstance().getUserId())
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            if(!queryDocumentSnapshots.isEmpty()){
                                for(QueryDocumentSnapshot journals : queryDocumentSnapshots){
                                    Journal journal = journals.toObject(Journal.class);
                                    journalList.add(journal);
                                }
                                recyclerAdapter = new JournalRecyclerAdapter(journalList, JournalListActivity.this);
                                recyclerView.setAdapter(recyclerAdapter);
                                recyclerAdapter.notifyDataSetChanged();
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });
        }
        super.onStart();
    }
}