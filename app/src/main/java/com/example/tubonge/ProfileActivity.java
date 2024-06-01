package com.example.tubonge;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.tubonge.models.UserModel;
import com.example.tubonge.utils.AndroidUtil;
import com.example.tubonge.utils.FirebaseUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

public class ProfileActivity extends AppCompatActivity {

    ImageView profilePic;
    EditText usernameInput;
    EditText phoneInput;
    Button updateProfileBtn;
    ProgressBar progressBar;
    TextView logoutBtn;

    UserModel currentUserModel;
    Uri selectedImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_profile);

        profilePic = findViewById(R.id.profile_image_view);
        usernameInput = findViewById(R.id.profile_username);
        phoneInput = findViewById(R.id.profile_phone);
        updateProfileBtn = findViewById(R.id.profle_update_btn);
        progressBar = findViewById(R.id.profile_progress_bar);
        logoutBtn = findViewById(R.id.logout_btn);

        getUserData();

        updateProfileBtn.setOnClickListener((v -> {
            updateBtnClick();
        }));

        logoutBtn.setOnClickListener((v) -> {
            FirebaseMessaging.getInstance().deleteToken().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        FirebaseUtil.logout();
                        Intent intent = new Intent(ProfileActivity.this, SplashActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    }
                }
            });
        });

        profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, 45);
            }
        });
    }

    void updateBtnClick() {
        String newUsername = usernameInput.getText().toString();
        if (newUsername.isEmpty() || newUsername.length() < 3) {
            usernameInput.setError("Username length should be at least 3 chars");
            return;
        }
        currentUserModel.setUsername(newUsername);
        setInProgress(true);

        if (selectedImageUri != null) {
            FirebaseUtil.getCurrentProfilePicStorageRef().putFile(selectedImageUri)
                    .addOnCompleteListener(task -> {
                        updateToFirestore();
                    });
        } else {
            updateToFirestore();
        }
    }

    void updateToFirestore() {
        FirebaseUtil.currentUserDetails().set(currentUserModel)
                .addOnCompleteListener(task -> {
                    setInProgress(false);
                    if (task.isSuccessful()) {
                        AndroidUtil.showToast(ProfileActivity.this, "Updated successfully");
                    } else {
                        AndroidUtil.showToast(ProfileActivity.this, "Updated failed");
                    }
                });
    }

    void getUserData() {
        setInProgress(true);

        FirebaseUtil.getCurrentProfilePicStorageRef().getDownloadUrl()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Uri uri = task.getResult();
                        AndroidUtil.setProfilePic(ProfileActivity.this, uri, profilePic);
                    }
                });

        FirebaseUtil.currentUserDetails().get().addOnCompleteListener(task -> {
            setInProgress(false);
            currentUserModel = task.getResult().toObject(UserModel.class);
            assert currentUserModel != null;
            usernameInput.setText(currentUserModel.getUsername());
            phoneInput.setText(currentUserModel.getPhone());
        });
    }

    void setInProgress(boolean inProgress) {
        if (inProgress) {
            progressBar.setVisibility(View.VISIBLE);
            updateProfileBtn.setVisibility(View.GONE);
        } else {
            progressBar.setVisibility(View.GONE);
            updateProfileBtn.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 45 && resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.getData();
            profilePic.setImageURI(selectedImageUri);
        }
    }

}
