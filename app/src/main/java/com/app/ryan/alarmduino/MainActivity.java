package com.app.ryan.alarmduino;

import android.app.NotificationManager;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class MainActivity extends AppCompatActivity {
    private final String CHANNEL_ID = "personal_notifications";
    private final int NOTIFICATION_ID = 001;
    private NotificationHelper mNotificationHelperl;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mNotificationHelperl = new NotificationHelper(this);

        ImageButton deviceButton = (ImageButton)findViewById(R.id.deviceBut);
        ImageButton alarmButton = (ImageButton)findViewById(R.id.alarmBut);
        ImageButton infoButton = (ImageButton)findViewById(R.id.infoBut);

       deviceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,DevicesActivity.class));
            }
        });
        alarmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,AlarmActivity.class));
            }
        });
        infoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               showNotification(v);
            }
        });
    }
    public void showNotification(View v) {
        String title = "test";
        String message = " test test test";
        NotificationCompat.Builder nb = mNotificationHelperl.getChannel(title, message);
        mNotificationHelperl.getManager().notify(1, nb.build());

    }
}
