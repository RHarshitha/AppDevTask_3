package com.example.harshitha.fetchdata;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.os.Handler;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends Activity {
    TextView timerfield;
    TextView data;
    Button exit;
    Timer timer;
    TimerTask timerTask;
    final Handler handler1 = new Handler();
    final Handler handler2 = new Handler();
    //public String urlString=;
    public long time = 10000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        timerfield = (TextView) findViewById(R.id.textView);
        data = (TextView) findViewById(R.id.textView2);
        exit = (Button) findViewById(R.id.button);
        startTimer();
        exit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.d("exit clicked", "App exited");
                if (timer != null) {
                    timer.cancel();
                    timer = null;
                }
                finish();
                System.exit(0);
            }
        });
    }

    public void startTimer() {
        timer = new Timer();
        handler2.post(new Runnable() {
            @Override
            public void run() {
                timerfield.setText(String.valueOf(time / 1000));
                Log.d("timerfield", String.valueOf(time));
            }
        });

        initializeTimerTask();
        //timer.schedule(timerTask,0,time);
        timer.schedule(timerTask, 10000);
    }

    public void initializeTimerTask() {
        Log.d("in", "initializeTimerTask");
        timerTask = new TimerTask() {
            public void run() {
                Log.d("in", "TimerTask,run()");
                final Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Log.d("in", "new thread's, run()");
                            URL url = new URL("http://spider.nitt.edu/~vishnu/time.php");
                            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                            conn.setRequestMethod("GET");
                            conn.setDoInput(true);
                            conn.connect();
                            InputStream in = new BufferedInputStream(conn.getInputStream());
                            long info;
                            try {
                                //info = Integer.parseInt(readStream(in).replaceAll("[\\D]",""));
                                //Log.d("with toString",readStream(in));
                                //Log.d("with replaceALL",readStream(in).replaceAll("[\\D]",""));
                                //Log.d("only readStream",readStream(in));
                                info = Long.parseLong(readStream(in));
                                final long reserve = info;
                                Log.d("reserve:", String.valueOf(reserve));
                                if (info % 10 != 0) {
                                    time = (reserve % 10) * 1000;
                                } else {
                                    while (info % 10 == 0) {
                                        info /= 10;
                                    }
                                    time = (info % 10) * 1000;
                                }
                                Log.d("time:", String.valueOf(time));
                                handler1.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        data.setText(String.valueOf(reserve));
                                        Log.d("data", "updated with" + String.valueOf(reserve));
                                        // timerfield.setText(String.valueOf(time/1000));
                                    }
                                });
                            } catch (NumberFormatException nfe) {
                                System.out.println("Could not parse " + nfe);
                            }
                           /* final int reserve= info;
                            Log.d("reserve:",String.valueOf(reserve));
                            if(info%10!=0)
                            {time= (reserve%10)*1000;}
                            else{
                                while(info%10==0)
                                {info/=10;}
                                time= (info%10)*1000;
                            }
                            Log.d("time:",String.valueOf(time));
                            handler1.post(new Runnable() {
                                @Override
                                public void run() {
                                    data.setText(String.valueOf(reserve));
                                    Log.d("data","updated with" + String.valueOf(reserve));
                                   // timerfield.setText(String.valueOf(time/1000));
                                }
                            });*/
                            conn.disconnect();
                            setTimer(time);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    private String readStream(InputStream in) throws IOException {
                        Log.d("in", "readStream in timertask");
                        StringBuilder sb = new StringBuilder();
                        BufferedReader r = new BufferedReader(new InputStreamReader(in));
                        for (String line = r.readLine(); line != null; line = r.readLine()) {
                            sb.append(line);
                        }
                        Log.d("data in stringBuilder", sb.toString());
                        in.close();
                        return sb.toString();
                    }
                });
                thread.start();

            }
        };

    }


    public void setTimer(final long time) {
        Log.d("in", "setTimer");
        if (timer != null) {
            Log.d("in", "if, timer!=null");
            timer.cancel();
            timer = new Timer();
            handler2.post(new Runnable() {
                @Override
                public void run() {
                    timerfield.setText(String.valueOf(time / 1000));
                    Log.d("timerfield", "update with" + String.valueOf(time));
                }
            });
            initializeTimerTask();
            timer.schedule(timerTask, time);
            //timer.schedule(timerTask,10000);
        }
    }
}