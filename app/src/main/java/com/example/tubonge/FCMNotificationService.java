package com.example.tubonge;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class FCMNotificationService extends FirebaseMessagingService {

    private static final String TAG = "FCMNotificationService";

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);

        // Log the token for debugging purposes
        Log.d(TAG, "Refreshed token: " + token);

        // You can handle the token update here, such as sending it to your server
        // sendTokenToServer(token);
    }

    // Uncomment the below method and implement it according to your server logic
    /*
    private void sendTokenToServer(String token) {
        // Implement your logic to send the token to your server
        Log.d(TAG, "Sending token to server: " + token);
    }
    */

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        // Handle FCM messages received here
        // This method is called when the app receives a message from FCM while it is in the foreground
        // You can customize the behavior of your app when receiving messages
    }
}
