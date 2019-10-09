package com.example.internal_read_write;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    Handler h = null;
    byte[] buf;
    TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv = (TextView) findViewById(R.id.textView);
        h = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 0x001:
                        // call update gui method.
                        tv.setText(new String(buf));
                        break;
                    default:
                        break;
                }
            }
        };
        Button bt1 = (Button) findViewById(R.id.button1);
        Button bt2 = (Button) findViewById(R.id.button2);
        Button bt3 = (Button) findViewById(R.id.button3);
        Button bt4 = (Button) findViewById(R.id.button4);

        bt1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    PrintWriter pw = new PrintWriter(openFileOutput("test.txt", MODE_APPEND));
                    pw.println("This is internal storage");
                    pw.close();
                } catch (Exception e) {
                    Log.e("io", "write error");
                }
            }
        });

        bt2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    TextView tv = (TextView) findViewById(R.id.textView);
                    BufferedInputStream bis = new BufferedInputStream(openFileInput("test.txt"));
                    byte[] buffer = new byte[10000];
                    while (bis.read(buffer) != -1) {
                        tv.setText(new String(buffer));//将buffer转化为字符串
                    }
                    bis.close();
                } catch (Exception e) {
                    Log.e("io", "read error");
                }
            }
        });

        bt3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread() {
                    public void run() {
                        try {
                            URL url = new URL("https://www.sohu.com");
                            HttpURLConnection con = (HttpURLConnection) url.openConnection();
                            BufferedInputStream bis = new BufferedInputStream((con.getInputStream()));
                            buf = new byte[10000];
                            bis.read(buf);
                            h.sendEmptyMessage(0x001);
                        } catch (Exception e) {
                            Log.e("io", e.getMessage());
                        }
                    }
                }.start();
                ;
            }
        });

        bt4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    //判断SD卡是否已经挂载
                    if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                        PrintWriter pw = new PrintWriter(new FileOutputStream(Environment.getExternalStorageDirectory()
                                + "//" + "ext.txt"));
                        pw.println("This is a test to write external.");
                        pw.close();

                        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(Environment.getExternalStorageDirectory()
                                + "//" + "ext.txt"));
                        buf = new byte[bis.available()];
                        bis.read(buf);
                        h.sendEmptyMessage(0x001); //将信息发给handle进行处理
                        bis.close();
                    }
                } catch (Exception e) {
                    Log.e("io", "write error");
                }
            }
        });
    }
}
