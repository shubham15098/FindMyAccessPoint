package com.example.shubzz.findmyaccesspoint;

import android.content.Context;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.util.Log;
import android.widget.TextView;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;


//https://stackoverflow.com/questions/6063889/can-i-find-the-mac-address-of-my-access-point-in-android

public class MainActivity extends AppCompatActivity
{

    public Map<String, String> myMap = new HashMap<>();

   public String getMacId()
   {
       ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
       NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

       if (!mWifi.isConnected())
       {
           return null;
       }
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        return wifiInfo.getBSSID();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);




        //Toast.makeText(getApplicationContext(),xyz,Toast.LENGTH_LONG).show();


        //make map of csv file
        readCSV();

        //https://stackoverflow.com/questions/29533934/correct-way-to-run-a-continuously-called-method-in-own-thread?utm_medium=organic&utm_source=google_rich_qa&utm_campaign=google_rich_qa

        new Thread(new Runnable()
        {
            TextView changeIt = (TextView) findViewById(R.id.mac);
            TextView changeIt2 = (TextView) findViewById(R.id.mac2);
            @Override
            public void run()
            {
                while(true)
                {
                    try
                    {
                        Thread.sleep(100);
                    }
                    catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                    runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            String xyz = getMacId();

                            if(xyz != null && !xyz.isEmpty())
                            {
                                xyz = xyz.substring(0, xyz.length() - 1);

                                String location = myMap.get(xyz);
                                changeIt.setText(xyz);

                                if (myMap.containsKey(xyz))
                                {
                                    changeIt2.setText(location);
                                }
                                else
                                {
                                    changeIt2.setText("No mapped location");
                                }
                            }
                            else
                            {
                                //this means he is not in the wifi zone
                                changeIt2.setText("string is null");
                                changeIt.setText("string is null");
                            }


                        }
                    });

                }
            }
        }).start();
    }

    private void readCSV()
    {
        InputStream is = getResources().openRawResource(R.raw.data);
        BufferedReader reader = new BufferedReader(
            new InputStreamReader(is, Charset.forName("UTF-8"))
        );

        String line = "";

        try
        {
            while((line = reader.readLine()) != null)
            {
                String[] tokens = line.split(",");

                String t = tokens[4].substring(0, tokens[4].length() - 1);
                myMap.put(t,tokens[2]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    //https://www.youtube.com/watch?time_continue=25&v=i-TqNzUryn8

}
