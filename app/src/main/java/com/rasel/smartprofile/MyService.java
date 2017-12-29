package com.rasel.smartprofile;

import android.app.LoaderManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by RaSeL on 25-Dec-17.
 */

public class MyService extends Service implements SensorEventListener{
    SensorManager sensorManager;
    Sensor sensorAccleration, sensorProximity;
    SensorEventListener sensorEventListener;
    Boolean flipOver, onTable;
    AudioManager audioManager;

    @Override
    public void onCreate() {
        super.onCreate();
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager != null) {
            sensorAccleration = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            sensorProximity = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        }else{
            Log.d("rasel","sensorManager is Null Pointing");
        }
        flipOver = false; onTable=false;
        sensorManager.registerListener(this, sensorAccleration, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, sensorProximity, SensorManager.SENSOR_DELAY_NORMAL);
        audioManager = (AudioManager) getSystemService(getApplicationContext().AUDIO_SERVICE);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        sensorManager.unregisterListener(this);
        Toast.makeText(getApplicationContext()," Stop Listening",Toast.LENGTH_LONG).show();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(getApplicationContext()," App is Listening",Toast.LENGTH_LONG).show();

        Runnable runnable=new Runnable() {
            @Override
            public void run() {
                while (true){
                    synchronized (this){
                        try {
                            if(flipOver && onTable){

                                if(audioManager !=null){
                                    audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                                }else {
                                    Toast.makeText(getApplicationContext(), "AudioManager is null",Toast.LENGTH_SHORT).show();
                                }
                            }else if(onTable){
                                if(audioManager !=null){
                                    audioManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
                                }else {
                                    Toast.makeText(getApplicationContext(), "AudioManager is null",Toast.LENGTH_SHORT).show();
                                }
                            }
                            else{
                                if(audioManager !=null){
                                    audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                                }else {
                                    Toast.makeText(getApplicationContext(), "AudioManager is null",Toast.LENGTH_SHORT).show();
                                }

                            }
                            /*if(!(MainActivity.lat > 23.77 && MainActivity.lat <23.79 && MainActivity.lon >90.41 && MainActivity.lon < 90.43)){
                                if(audioManager !=null){
                                    audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                                }else {
                                    Toast.makeText(getApplicationContext(), "AudioManager is null",Toast.LENGTH_SHORT).show();
                                }
                            }*/
                        }catch (Exception e){
                           e.printStackTrace();
                        }
                    }
                }
            }
        };
        Thread thread= new Thread(runnable);
        thread.start();
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

        if(sensorEvent.sensor.getType()==Sensor.TYPE_ACCELEROMETER){
            if(sensorEvent.values[2] < 0){
                flipOver = true;
            }else {
                flipOver = false;
            }
        }
        else if(sensorEvent.sensor.getType()==Sensor.TYPE_PROXIMITY){
            if(sensorEvent.values[0]<sensorProximity.getMaximumRange()){
                onTable=true;
            }else {
                onTable=false;
            }
        }
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}

