package com.Kuczon.ch10_ex5;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;
import android.content.Context;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity
implements OnClickListener{
    private TextView messageTextView;
    private TextView downloadedTextView;
    private Button startButton;
    private Button stopButton;
    private Timer timer;
    private static int counter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        messageTextView = (TextView) findViewById(R.id.messageTextView);
        downloadedTextView = (TextView) findViewById(R.id.downloadedTextView);
        startButton = (Button) findViewById(R.id.startButton);
        stopButton = (Button) findViewById(R.id.stopButton);
        startButton.setOnClickListener(this);
        stopButton.setOnClickListener(this);

        startTimer();
    }
    @Override
    public void onPause() {
        stopTimer();

        super.onPause();
    }
    private void startTimer() {
        final long startMillis = System.currentTimeMillis();
        timer = new Timer(true);
        TimerTask task = new TimerTask() {
            
            @Override
            public void run() {
                long elapsedMillis = System.currentTimeMillis() - startMillis;
                final String FILENAME = "news_feed.xml";
                try {
                    URL url = new URL("http://rss.cnn.com/rss/cnn_tech.rss");
                    InputStream in = url.openStream();
                    FileOutputStream out =
                            openFileOutput(FILENAME, Context.MODE_PRIVATE);
                    byte[] buffer = new byte[1024];
                    int bytesRead = in.read(buffer);
                    while (bytesRead != -1) {
                        out.write(buffer, 0, bytesRead);
                        bytesRead = in.read(buffer);
                    }
                    out.close();
                    in.close();
                    counter++;
                }
                catch (IOException e) {
                    Log.e("News reader", e.toString());
                }
                updateView(elapsedMillis);
            }
        };
        timer.schedule(task, 0, 10000);
    }
    private void stopTimer() {
        if (timer != null) {
            timer.cancel();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.startButton:
                startTimer();
                break;
            case R.id.stopButton:
                stopTimer();
                break;
        }
    }
    private void updateView(final long elapsedMillis) {
        messageTextView.post(new Runnable() {
            int elapsedSeconds = (int) elapsedMillis/1000;

            @Override
            public void run() {
                downloadedTextView.setText("File downloaded " + counter + " time(s)");
                messageTextView.setText("Seconds: " + elapsedSeconds);
            }
        });
    }
}