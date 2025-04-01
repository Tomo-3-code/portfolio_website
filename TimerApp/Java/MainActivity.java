package com.example.timerapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.timerapp.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    Handler handler = new Handler();
    Runnable runnable;
    Button btn_start, btn_stop, btn_reset;
    long startTime;
    TextView text_timer;
    long t, elapsedTime;
    ArrayList<String> lapTime;
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn_start = (Button) findViewById(R.id.btn_start);
        btn_stop = (Button) findViewById(R.id.btn_stop);
        btn_reset = (Button) findViewById(R.id.btn_reset);
        text_timer = (TextView) findViewById(R.id.text_timer);
        listView = findViewById(R.id.list);
        lapTime = new ArrayList<String>();

        btnState(true, false, false);
    }

    public void btnState(boolean start, boolean stop, boolean reset) {
        btn_start.setEnabled(start);
        btn_stop.setEnabled(stop);
        btn_reset.setEnabled(reset);
    }

    public void startTimer(View view) {
        startTime = System.currentTimeMillis();
        runnable = new Runnable() {
            @Override
            public void run() {
                t = System.currentTimeMillis() - startTime + elapsedTime;
                SimpleDateFormat sdf = new SimpleDateFormat("mm:ss.SSS", Locale.US);
                text_timer.setText(sdf.format(t));
                handler.removeCallbacks(runnable);
                handler.postDelayed(runnable, 10);
            }
        };
        handler.postDelayed(runnable, 10);
        btnState(false, true, false);
    }

    public void stopTimer(View view) {
        elapsedTime += System.currentTimeMillis() - startTime;
        handler.removeCallbacks(runnable);
        btnState(true, false, true);
    }

    public void resetTimer(View view) {
        elapsedTime = 0;
        t = 0;
        text_timer.setText("00:00.000");
        btnState(true, false, false);
        lapTime.clear();
        listView.setAdapter(new ArrayAdapter<String>(MainActivity.this,R.layout.list_items,lapTime));
    }

    public void tapLap(View view){
        lapTime.add(text_timer.getText().toString());
        ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(MainActivity.this,R.layout.list_items,lapTime);
        listView.setAdapter(adapter);
    }
}
