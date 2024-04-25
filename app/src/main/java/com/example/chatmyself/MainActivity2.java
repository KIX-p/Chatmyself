package com.example.chatmyself;

import static android.os.Build.VERSION_CODES.TIRAMISU;
import static com.example.chatmyself.MainActivity.CHANNEL_ID;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.RemoteInput;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity2 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main2);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Intent intent = getIntent();
        Bundle bundle = RemoteInput.getResultsFromIntent(intent);
        if (bundle != null) {
            String message = bundle.getString("key");
            TextView textView = findViewById(R.id.sms2);
            textView.setText(message);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.cancel(1);
        }


        createNotificationChannel();
        if (android.os.Build.VERSION.SDK_INT >= TIRAMISU) {
            askForPermission();
        }

        Button button = findViewById(R.id.button2);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity2.this, MainActivity.class);
                PendingIntent pendingIntent = PendingIntent.getActivity(MainActivity2.this, 0, intent, PendingIntent.FLAG_MUTABLE);

                RemoteInput remoteInput = new RemoteInput.Builder("key")
                        .setLabel("Enter your message")
                        .build();

                // Create a new Notification.Actions
                NotificationCompat.Action action = new NotificationCompat.Action.Builder(
                        android.R.drawable.sym_action_chat,
                        "Open",
                        pendingIntent
                ).build();

                NotificationCompat.Action replayAction = new NotificationCompat.Action.Builder(
                        android.R.drawable.ic_dialog_info,
                        "Answer",
                        pendingIntent
                ).addRemoteInput(remoteInput).build();


                // Create a new Notification
                Notification notification = new NotificationCompat.Builder(MainActivity2.this, CHANNEL_ID)
                        .setSmallIcon(android.R.drawable.sym_action_chat)
                        .setContentTitle("Message")
                        .setContentText("You have a new message")
                        .addAction(action)
                        .addAction(replayAction)
                        .setAutoCancel(true)
                        .build();

                // Show the notification
                NotificationManager notificationManager = getSystemService(NotificationManager.class);//(NotificationManager) getSystemService(NOTIFICATION_SERVICE) is deprecated
                notificationManager.notify(1, notification);
            }
        });

    }
    private void createNotificationChannel() {
        // Create a new NotificationChannel
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        CharSequence name = "Channel Name";
        String description = "Channel Description";
        int importance = NotificationManager.IMPORTANCE_DEFAULT;
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
        channel.setDescription(description);
        notificationManager.createNotificationChannel(channel);
    }

    @RequiresApi(TIRAMISU)
    private void askForPermission() {
        if(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.RECEIVE_SMS) != android.content.pm.PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.RECEIVE_SMS}, 1);
        }
    }
}