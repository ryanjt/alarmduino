package com.app.ryan.alarmduino;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;

import java.io.UnsupportedEncodingException;
import java.util.Date;

public class ActiveAlarmActivity extends AppCompatActivity {
    private final static int REQUEST_ENABLE_BT = 1; // used to identify adding bluetooth names
    private final static int MESSAGE_READ = 2; // used in bluetooth handler to identify message update
    private final static int CONNECTING_STATUS = 3; // used in bluetooth handler to identify message status
    private Handler mHandler; // Our main handler that will receive callback notifications
    private TextView mReadBuffer;
    private TextView method;
    private Button manualButton;
    static String fileName = "alarms.json";
    public String alarms = " ";
    private final String CHANNEL_ID = "personal_notifications";
    private final int NOTIFICATION_ID = 001;
    private NotificationHelper mNotificationHelperl;
    private boolean shake = false;


    private SensorManager mSensorManager;

    private ShakeEventListener mSensorListener;

        @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activealarm);
        mReadBuffer = (TextView) findViewById(R.id.timeTV);
        method = (TextView) findViewById(R.id.methodTV);
        manualButton = (Button) findViewById(R.id.snoozeBut);
        manualButton.setVisibility(View.INVISIBLE);


            mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
            mSensorListener = new ShakeEventListener();

            mSensorListener.setOnShakeListener(new ShakeEventListener.OnShakeListener() {

                public void onShake() {
                    Toast.makeText(ActiveAlarmActivity.this, "Shaked alarm off!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(ActiveAlarmActivity.this, MainActivity.class));
                }
            });

        final Date currentTime = Calendar.getInstance().getTime();
        updateGui();

//        AsyncTask.execute(new Runnable() {
//            @Override
//            public void run() {
//                mHandler = new Handler(){
//                    public void handleMessage(android.os.Message msg){
//                        if(msg.what == MESSAGE_READ){
//                            String readMessage = null;
//                            try {
//                                readMessage = new String((byte[]) msg.obj, "UTF-8");
//                            } catch (UnsupportedEncodingException e) {
//                                e.printStackTrace();
//                            }
//                            if(readMessage == "1"){
//                                showNotification();
//                                updateGui();
//
//
//
//                            }
//
//                        }
//
//
//                    }
//                };
//            }
//        });

    }


    public void updateGui(){
        File f = new File(this.getFilesDir().getPath() + "/" + fileName);
        if(f.exists() && !f.isDirectory()) {
            alarms = getData(this.getBaseContext());

            try{
                final JSONArray jarray = new JSONArray(alarms);
                for(int i = 0; i < jarray.length(); i++) {
                    final JSONObject item = jarray.getJSONObject(i);
                    final int i2 = i;
                    mReadBuffer.setText(item.get("alarmHours").toString() + ":" + item.get("alarmMinutes").toString());
                    method.setText(item.get("method").toString());
                    if(item.get("method").toString() == "Manual Button"){
                        manualButton.setVisibility(View.VISIBLE);

                    }
                }
            }catch(JSONException e){

            }
        }else{
            try{
                Log.d("file created", "yes");
                JSONObject alarm = new JSONObject();
                FileWriter file = new FileWriter(this.getFilesDir().getPath() + "/" + fileName);

                file.write("[]");
                file.close();
            }catch (IOException e){
                e.printStackTrace();
            }
        }


    }

    public void showNotification() {
        String title = "Alarmduino";
        String message = "Wakey wakey! Alarm time!";
        NotificationCompat.Builder nb = mNotificationHelperl.getChannel(title, message);
        mNotificationHelperl.getManager().notify(1, nb.build());

    }
    public static String getData(Context context) {
        try {
            File f = new File(context.getFilesDir().getPath() + "/" + fileName);

            //check whether file exists
            FileInputStream is = new FileInputStream(f);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            return new String(buffer);
        } catch (IOException e) {
            Log.e("TAG", "Error in Reading: " + e.getLocalizedMessage());
            return null;
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(mSensorListener,
                mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    protected void onPause() {
        mSensorManager.unregisterListener(mSensorListener);
        super.onPause();
    }

}
