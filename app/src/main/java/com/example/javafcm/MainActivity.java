package com.example.javafcm;

import static com.example.javafcm.Constants.BASE_URL;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.VolleyError;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.iid.internal.FirebaseInstanceIdInternal;
import com.google.firebase.installations.FirebaseInstallations;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;

import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final String TOPIC = "/tokens/test";
    private static final String SEND_URL = BASE_URL + "/fcm/send";
    private VolleyInstance volleyInstance;
    private IResult mResultCallback = null;
    private TextInputEditText title, message, token;
    private MaterialButton sendNotificationBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseMessaging.getInstance().subscribeToTopic(TOPIC);

        initVolleyCallback();
        volleyInstance = new VolleyInstance(mResultCallback, getApplicationContext());

        title = findViewById(R.id.notification_title);
        message = findViewById(R.id.notification_message);
        token = findViewById(R.id.notification_token);

        SharedPreferences preferences = getApplicationContext().getSharedPreferences("FCMToken", 0);
        String tokenValue = preferences.getString("Token", "");
        token.setText(tokenValue);

        sendNotificationBtn = findViewById(R.id.send_notification_button);
        sendNotificationBtn.setOnClickListener(view -> {
            String titleToSend = (title.getText() != null) ? title.getText().toString() : "";
            String messageToSend = (message.getText() != null) ? message.getText().toString() : "";
            String tokenToSend = (token.getText() != null) ? token.getText().toString() : "";
//            if (!titleToSend.isEmpty() && !messageToSend.isEmpty()) {
//                sendNotificationToTopic(titleToSend, messageToSend);
//            }
            if (!titleToSend.isEmpty() && !messageToSend.isEmpty() && !tokenToSend.isEmpty()) {
                sendNotificationToToken(titleToSend, messageToSend, tokenToSend);
            }
        });
    }

    private void sendNotificationToTopic(String title, String message) {
        JSONObject notificationData = new JSONObject();
        try {
            notificationData.put("title", title);
            notificationData.put("message", message);
        } catch (Exception e) {
            e.printStackTrace();
        }
        JSONObject notification = new JSONObject();
        try {
            notification.put("data", notificationData);
            notification.put("to", TOPIC);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String requestBody = notification.toString();
        volleyInstance.post("NotificationToTopic", SEND_URL, null, requestBody);
    }

    private void sendNotificationToToken(String title, String message, String token) {
        JSONObject notificationData = new JSONObject();
        try {
            notificationData.put("title", title);
            notificationData.put("message", message);
        } catch (Exception e) {
            e.printStackTrace();
        }
        JSONObject notification = new JSONObject();
        try {
            notification.put("data", notificationData);
            notification.put("to", token);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String requestBody = notification.toString();
        volleyInstance.post("NotificationToToken", SEND_URL, null, requestBody);
    }

    void initVolleyCallback() {
        mResultCallback = new IResult() {

            @Override
            public void notifySuccess(String requestType, JSONObject response) {
                Log.d(TAG, "Volley requestType " + requestType);
                Log.d(TAG, "Volley JSONObject" + response);
//                if (requestType.equalsIgnoreCase("NotificationToTopic")) {
//
//                }
            }

            @Override
            public void notifyError(String requestType, VolleyError error) {
                Log.d(TAG, "Volley requestType " + requestType);
                Log.d(TAG, "Volley VolleyError" + error);
//                if (requestType.equalsIgnoreCase("NotificationToTopic")) {
//
//                }
            }
        };
    }
}