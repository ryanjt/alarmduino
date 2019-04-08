package com.app.ryan.alarmduino;

import android.content.Context;
import android.content.DialogInterface;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.UUID;

public class AlarmActivity extends AppCompatActivity {

    static String fileName = "alarms.json";
    public String alarms = " ";
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);
        FloatingActionButton fabButton = (FloatingActionButton) findViewById(R.id.FAButton);
        LinearLayout layout = (LinearLayout) findViewById(R.id.alarmLL);

        alarms = getData(this.getBaseContext());
        File f = new File(this.getFilesDir().getPath() + "/" + fileName);
        if(f.exists() && !f.isDirectory()) {
            try {
                final JSONArray jarray = new JSONArray(alarms);
                for(int i = 0; i < jarray.length(); i++)
                {
                    final JSONObject item = jarray.getJSONObject(i);
                    final int i2 = i;
                    Button alarmButtons = new Button (this);
                    alarmButtons.setWidth(displayMetrics.widthPixels - 10);
                    alarmButtons.setHeight(200);
                    alarmButtons.setText(item.get("alarmName").toString() + " - " + item.get("alarmHours").toString() + ":" + item.get("alarmMinutes").toString() );
                    alarmButtons.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                    layout.addView(alarmButtons);
                    alarmButtons.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(final View v) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());

                        builder.setTitle("Confirm");
                        builder.setMessage("Would you like to delete this alarm?");

                        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int which) {
                                // Do nothing but close the dialog
                                jarray.remove(i2);
                                alarms = jarray.toString();
                                saveData(v.getContext(), alarms);
                                dialog.dismiss();
                            }
                        });

                        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                // Do nothing
                                dialog.dismiss();
                            }
                        });

                        AlertDialog alert = builder.create();
                        alert.show();
                        return true;
                    }
                });


                    alarmButtons.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            AlertDialog.Builder mBuilder = new AlertDialog.Builder(AlarmActivity.this);

                            View mView = getLayoutInflater().inflate(R.layout.activity_alarmsettings, null);
                            final EditText alarmName = (EditText) mView.findViewById(R.id.alarmET);
                            final Spinner timeHours = (Spinner) mView.findViewById(R.id.timeCombo);
                            final Spinner timeMinutes = (Spinner) mView.findViewById(R.id.timeCombo2);
                            final Spinner tone = (Spinner) mView.findViewById(R.id.toneCombo);
                            final Spinner method = (Spinner) mView.findViewById(R.id.methodCombo);
                            Button editAlarmButton = (Button) mView.findViewById(R.id.addAlarmBut);
                            mBuilder.setView(mView);
                            final AlertDialog dialog = mBuilder.create();
                            dialog.show();
                            try{

                                alarmName.setText(item.get("alarmName").toString());
                                timeHours.setSelection(((ArrayAdapter)timeHours.getAdapter()).getPosition(item.get("alarmHours")));
                                timeMinutes.setSelection(((ArrayAdapter)timeMinutes.getAdapter()).getPosition(item.get("alarmMinutes")));
                                tone.setSelection(((ArrayAdapter)tone.getAdapter()).getPosition(item.get("tone")));
                                method.setSelection(((ArrayAdapter)method.getAdapter()).getPosition(item.get("method")));

                            }catch(JSONException f){
                                f.printStackTrace();

                            }

                            editAlarmButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    try{
                                        JSONObject alarm = new JSONObject();
                                        JSONArray editJarray = new JSONArray(alarms);
                                        for(int i = 0; i < editJarray.length(); i++) {
                                            final JSONObject newItem = editJarray.getJSONObject(i);
                                            if(newItem.get("id").toString().equals(item.get("id").toString())){
                                                jarray.remove(i);
                                                alarm.put("id", UUID.randomUUID().toString());
                                                alarm.put("alarmName", alarmName.getText().toString());
                                                alarm.put("alarmHours", timeHours.getSelectedItem().toString());
                                                alarm.put("alarmMinutes", timeMinutes.getSelectedItem().toString());
                                                alarm.put("tone", tone.getSelectedItem().toString());
                                                alarm.put("method", method.getSelectedItem().toString());
                                                jarray.put(alarm);
                                                alarms = jarray.toString();
                                                saveData(v.getContext(), alarms);

                                            }
                                        }

                                    }catch(JSONException f){
                                        f.printStackTrace();

                                    }
                                    if(!alarmName.getText().toString().isEmpty()){
                                        Toast.makeText(AlarmActivity.this,
                                                "Alarm edited!",
                                                Toast.LENGTH_SHORT).show();
                                        dialog.dismiss();

                                    }
                                }
                            });





                        }
                    });

                }
            }catch(JSONException e){
                e.printStackTrace();

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








        fabButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(AlarmActivity.this);
                View mView = getLayoutInflater().inflate(R.layout.activity_newalarm, null);
                final EditText alarmName = (EditText) mView.findViewById(R.id.alarmET);
                final Spinner timeHours = (Spinner) mView.findViewById(R.id.timeCombo);
                final Spinner timeMinutes = (Spinner) mView.findViewById(R.id.timeCombo2);
                final Spinner tone = (Spinner) mView.findViewById(R.id.toneCombo);
                final Spinner method = (Spinner) mView.findViewById(R.id.methodCombo);
                Button addAlarmButton = (Button) mView.findViewById(R.id.addAlarmBut);

                mBuilder.setView(mView);
                final AlertDialog dialog = mBuilder.create();
                dialog.show();


                addAlarmButton.setOnClickListener(new View.OnClickListener() {
                    @Override

                    public void onClick(View v) {
                        try {

                            JSONArray jarray = new JSONArray(alarms);
                            JSONObject alarm = new JSONObject();
                            try{
                                alarm.put("id", UUID.randomUUID().toString());
                                alarm.put("alarmName", alarmName.getText().toString());
                                alarm.put("alarmHours", timeHours.getSelectedItem().toString());
                                alarm.put("alarmMinutes", timeMinutes.getSelectedItem().toString());
                                alarm.put("tone", tone.getSelectedItem().toString());
                                alarm.put("method", method.getSelectedItem().toString());

                            }catch(JSONException e){
                                e.printStackTrace();
                            }
                            jarray.put(alarm);
                            alarms = jarray.toString();
                            saveData(v.getContext(), alarms);
                        } catch (JSONException e) {
                            Log.e("MYAPP", "unexpected JSON exception", e);
                            // Do something to recover ... or kill the app.
                        }
                       alarms = getData(v.getContext());
                        Log.d("Test alarm", alarms);



                        if(!alarmName.getText().toString().isEmpty()){
                            Toast.makeText(AlarmActivity.this,
                                    "Alarm added!",
                                    Toast.LENGTH_SHORT).show();


                        }

                        dialog.dismiss();
                    }
                });



            }
        });
    }

    public static void saveData(Context context, String mJsonResponse) {
        try {
            FileWriter file = new FileWriter(context.getFilesDir().getPath() + "/" + fileName);

            file.write(mJsonResponse);
            file.flush();
            file.close();
        } catch (IOException e) {
            Log.e("TAG", "Error in Writing: " + e.getLocalizedMessage());
        }
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

}
