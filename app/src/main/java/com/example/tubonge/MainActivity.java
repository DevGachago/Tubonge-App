package com.example.tubonge;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tubonge.adapter.RecentChatRecyclerAdapter;
import com.example.tubonge.models.ChatroomModel;
import com.example.tubonge.utils.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.firestore.Query;
import com.google.firebase.messaging.FirebaseMessaging;

public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    RecentChatRecyclerAdapter adapter;

    BottomNavigationView bottomNavigationView;

    ImageButton searchButton;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        searchButton = findViewById(R.id.main_search_btn);

        searchButton.setOnClickListener((v)->{
            startActivity(new Intent(MainActivity.this,SearchUserActivity.class));
        });

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                // Handle item clicks based on their IDs
                if (id == R.id.chats) {
                    // Handle clicks on the "Chats" item
                    // Example: Start a new activity for chats
                    startActivity(new Intent(MainActivity.this, ChatActivity.class));
                    return true;
                } else if (id == R.id.status) {
                    // Handle clicks on the "Status" item
                    // Example: Start a new activity for status
                    startActivity(new Intent(MainActivity.this, ProfileActivity.class));
                    return true;
                } else if (id == R.id.calls) {
                    // Handle clicks on the "Calls" item
                    // Example: Start a new activity for calls
                    startActivity(new Intent(MainActivity.this, MainActivity.class));
                    return true;
                }
                 return false;
            }
        });

        getFCMToken();


        recyclerView = findViewById(R.id.recyclerView_main);
        setupRecyclerView();
    }

    void getFCMToken(){
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                String token = task.getResult();
                FirebaseUtil.currentUserDetails().update("fcmToken",token);

            }
        });
    }

    void setupRecyclerView(){
        Query query = FirebaseUtil.allChatroomCollectionReference()
                .whereArrayContains("userIds", FirebaseUtil.currentUserId())
                .orderBy("lastMessageTimestamp", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<ChatroomModel> options = new FirestoreRecyclerOptions.Builder<ChatroomModel>()
                .setQuery(query, ChatroomModel.class).build();

        adapter = new RecentChatRecyclerAdapter(options, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    @Override
    public void onStart() {
        super.onStart();
        if(adapter!=null)
            adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        if(adapter!=null)
            adapter.stopListening();
    }

    @Override
    public void onResume() {
        super.onResume();
        if(adapter!=null)
            adapter.notifyDataSetChanged();
    }
}