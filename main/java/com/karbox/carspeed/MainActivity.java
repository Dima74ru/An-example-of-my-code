package com.karbox.carspeed;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Application;
import android.app.Dialog;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.provider.ContactsContract;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.squareup.leakcanary.LeakCanary;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements GpsStatus.Listener, SensorEventListener {

    LocationManager manager;
    MyTimer myTimer; // Класс с секундомером для измерения времени разгона и тд

    double speed = 0; // Реальная скорость
    double max_speed = 0; // Максимальная скорость
    double average_speed = 0; // Средняя скорость
    int limit_speed = 90; // Ограничение скорости
    double way = 0; // Пройденный путь
    String times = "t"; // Время со спутника
    long all_time_move = 0; // Время непосредственного движения ( скорость не равна 0 )
    double accuaru = 0.0; // Точность GPS в метрах
    int satellites,satellitesInFix; // Количество используемых спутников/общее количество спутников
    double petrol; // Количество сожжённого топлива

    // Вспомогательные переменные ...
    double new_lat,new_lon,old_lat,old_lon;
    boolean racer = true;
    boolean r1 = true,r2 = true,r3 = true;
    long timeStart,timeStop30,timeStop60,timeStop100;

    static final double CONSTANT_ACCUARU = 25.0; // Точность в метрах (граница работы)
    static final int CONSTANT_SATELLITES = 5; // Минимальное количество спутников для работы приложения
    int petrol_consumption; // Средний расход топлива на 100км

    TextView textSpeed, textWay, textTime, textMaxSpeed, textAverageSpeed,textAccuaru,textSatellites,textLimitSpeed,textPetrol;
    TextView textTime30,textTime60,textTime100;
    ImageView reset,settings,satellit_icon;
    LinearLayout linearLayout_setLimitSpeed;


    TextView text_ed;
    TextView textinfo2;
    LinearLayout LLRacer1,LLRacer2,LLRacer3,LLPetrol,LLAll;

    // Диалоговые переменные

    EditText edit_lim_speed;
    TextView but_lim_speed;
    RelativeLayout RLSpeed;

    // Сохранение настроек и тд в память
    public static final String APP_PREFERENCES = "mysettings_karbox_gpsspeed_app_v1";
    public static final String APP_PREFERENCES_LIMIT_SPEED = "limit_speed";
    public static final String APP_PREFERENCES_AUDIO = "audio";
    public static final String APP_PREFERENCES_VIBRO = "vibro";
    public static final String APP_PREFERENCES_COMPASS = "compass";
    public static final String APP_PREFERENCES_CONSTANT_PETROL = "petrol";
    public static final String APP_PREFERENCES_RACING = "racing";
    public static final String APP_PREFERENCES_FLG_PETROL = "flg_petrol";
    private SharedPreferences mSettings;

    MediaPlayer mPlayer1;

    Vibrator v;

    // Переменные настроек
    boolean flg_limit_speed_audio_vibro = false;

    boolean flg_audio;
    boolean flg_vibro;
    boolean flg_compass;
    boolean flg_racing;
    boolean flg_petrol;

    // Компас

    Animation anim;
    ImageView image_compas;
    private float[] mGravity = new float[3];
    private float[] mGeomagnetic = new float[3];
    private float azimuth = 0f;
    private float currectAzimuth = 0f;
    private SensorManager mSensorManager;

    private InterstitialAd mInterstitialAd; // Реклама

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        MobileAds.initialize(this, "ca-app-pub-2038569899413155~5149521534");
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-2038569899413155/5878658583");
        mInterstitialAd.loadAd(new AdRequest.Builder().addTestDevice("C0A567CE7574B533FE8659B57FFF7861").build());

        mSettings = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        mPlayer1 = MediaPlayer.create(this, R.raw.audio_speed_limit2);

        textSpeed = (TextView) findViewById(R.id.textView1);
        textWay = (TextView) findViewById(R.id.textView2);
        textTime = (TextView) findViewById(R.id.textView3);
        textMaxSpeed = (TextView) findViewById(R.id.textView4);
        textSatellites = (TextView) findViewById(R.id.textSatellites);
        textinfo2 = (TextView) findViewById(R.id.textViewInfo2);
        text_ed = (TextView) findViewById(R.id.text_ed);
        textTime30 = (TextView) findViewById(R.id.textViewTime30);
        textTime60 = (TextView) findViewById(R.id.textViewTime60);
        textTime100 = (TextView) findViewById(R.id.textViewTime100);
        reset = (ImageView) findViewById(R.id.imageView2);
        settings = (ImageView) findViewById(R.id.imageView3);
        satellit_icon = (ImageView) findViewById(R.id.imageView);
        textAccuaru = (TextView) findViewById(R.id.textAccuaru);
        textLimitSpeed = (TextView) findViewById(R.id.textViewLimitSpeed);
        linearLayout_setLimitSpeed = (LinearLayout) findViewById(R.id.limitLinerLay);
        textPetrol = (TextView) findViewById(R.id.text_petrol);
        textAverageSpeed = (TextView) findViewById(R.id.text_average_speed);
        image_compas = (ImageView) findViewById(R.id.image_compass);
        LLRacer1 = (LinearLayout) findViewById(R.id.LLRacer1);
        LLRacer2 = (LinearLayout) findViewById(R.id.LLRacer2);
        LLRacer3 = (LinearLayout) findViewById(R.id.LLRacer3);
        RLSpeed = (RelativeLayout) findViewById(R.id.RLSPEED);
        LLPetrol = (LinearLayout) findViewById(R.id.LLPetrol);
        LLAll = (LinearLayout) findViewById(R.id.LLAll);

        // Компас
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        //
        if(myTimer==null){ myTimer = new MyTimer(100);}
        //
        manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        assert manager != null;
        manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 100, 0, listener);
        manager.addGpsStatusListener(this);

        final Handler handler = new Handler(); // Очень быстрая функция
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(satellitesInFix < CONSTANT_SATELLITES){speed = 0.0;}
                if(speed>=0.1){all_time_move+=50;}

                update_view();
                speedRacer();
                speedLimit();

                handler.postDelayed(this, 50);
            }
        },0);

        final Handler handlerAverageSpeed = new Handler(); // Очень быстрая функция
        handlerAverageSpeed.postDelayed(new Runnable() {
            @Override
            public void run() {

                average_speed = (double) (way/(all_time_move/1000d))*3600d;
                int speed_average_normaly = (int) Math.round(average_speed);
                textAverageSpeed.setText(String.valueOf(speed_average_normaly));

                handlerAverageSpeed.postDelayed(this, 5000);
            }
        },0);

        final Handler handlerAudio = new Handler(); // Очень быстрая функция
        handlerAudio.postDelayed(new Runnable() {
            @Override
            public void run() {

                if(flg_limit_speed_audio_vibro) {

                    if (flg_audio) {
                        mPlayer1.start();
                    }

                    v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

                    if (flg_vibro) {
                        // Vibrate for 500 milliseconds
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            v.vibrate(VibrationEffect.createOneShot(300, VibrationEffect.DEFAULT_AMPLITUDE));
                        } else {
                            //deprecated in API 26
                            assert v != null;
                            v.vibrate(300);
                        }
                    }
                }
               handlerAudio.postDelayed(this, 3000);
            }
        },0);

        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Анимация для поворота View
                Animation butAnim = AnimationUtils.loadAnimation(MainActivity.this, R.anim.anim_but);
                // Подключаем анимацию к нужному View
                reset.clearAnimation();
                reset.startAnimation(butAnim);

                speed = 0;
                max_speed = 0;
                way = 0;

                racer = true;
                timeStart = 0;
                timeStop30 = 0;
                timeStop60 = 0;
                timeStop100 = 0;
            }
        });

        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (mInterstitialAd.isLoaded()) {
                    mInterstitialAd.show();

                    mInterstitialAd.setAdListener(new AdListener() {
                        @Override
                        public void onAdClosed() {
                            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                            startActivity(intent);
                            intent = null;
                        }
                    });
                }else
                {
                    Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                    startActivity(intent);
                    intent = null;
                }
            }
        });

        linearLayout_setLimitSpeed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog alert = new Dialog(view.getContext());
                alert.setContentView(R.layout.lay_limspeed);

                edit_lim_speed = alert.findViewById(R.id.edit_limspeed);
                but_lim_speed = alert.findViewById(R.id.text_but_leemspeed);

                edit_lim_speed.setHint(String.valueOf(limit_speed));

                but_lim_speed.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if(!edit_lim_speed.getText().toString().equals(""))
                        {
                            limit_speed = Integer.valueOf(edit_lim_speed.getText().toString());
                        }

                        alert.dismiss();
                    }
                });

                alert.show();
            }
        });

    }

    private LocationListener listener = new LocationListener() {
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void onLocationChanged(Location location) {
            if( location != null)
            {
                accuaru = (double)location.getAccuracy();

                if(accuaru <= CONSTANT_ACCUARU) {
                    if(accuaru <= 6) {
                        // Очень хороший сигнал
                        satellit_icon.setImageResource(R.drawable.satellitesicon3);
                    }else {  // Хороший сигнал
                        satellit_icon.setImageResource(R.drawable.satellitesicon);
                    }
                    // Берём скорость
                    speed = location.getSpeed() * 3.6d;
                    // Сохраняем максимальную скорость
                    if(speed>=max_speed)
                    {
                        max_speed = speed;
                    }
                    // Время
                    times = new SimpleDateFormat("H:mm").format(new Date(location.getTime()));
                    // Координаты
                    new_lat = (double) location.getLatitude();
                    new_lon = (double) location.getLongitude();
                    if (old_lat == 0) {
                        old_lat = (double) location.getLatitude();
                    }
                    if (old_lon == 0) {
                        old_lat = (double) location.getLongitude();
                    }
                    way += (double) calc_way(new_lat, new_lon, old_lat, old_lon);
                }
                else
                {
                    // Плохой сигнал
                    satellit_icon.setImageResource(R.drawable.satellitesicon2);
                    // Обнуляем скорость, пока точность не улучшится
                    speed=0.0;
                }
            }
            else {
                // Обнуляем скорость, пока есть нулевые данные. Но это почему-то не работает
                speed=0.0;
                times = "00:00";
            }
        }
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
        @Override
        public void onProviderEnabled(String provider) {
        }
        @Override
        public void onProviderDisabled(String provider) {
        }

    };

    public double calc_way(double startLat, double startLon, double endLat, double endLon)
    {
        final int R = 6371;
        // Radius of the earth in km
        double dLat = deg2rad(endLat - startLat);
        // deg2rad below
        double dLon = deg2rad(endLon - startLon);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.cos(deg2rad(startLat)) * Math.cos(deg2rad(endLat)) * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double d = R * c;
        //Присваиваем старым коорлинатам новые
        old_lat = startLat;
        old_lon = startLon;
        // Distance in km
        if(d>=100 | speed<=0.1){d=0;} //Примитивная защита
        return d;

    }
    private double deg2rad(double deg)
    {
        return deg * (Math.PI / 180);
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onGpsStatusChanged(int i) {
        satellites = 0;
        satellitesInFix = 0;

        for (GpsSatellite sat : manager.getGpsStatus(null).getSatellites()) {
            if(sat.usedInFix()) {
                satellitesInFix++;
            }
            satellites++;
        }
    }

    @SuppressLint("SetTextI18n")
    public void update_view()
    {
        int speed_normaly = (int) Math.round(speed);
        textSpeed.setText(String.valueOf(speed_normaly));

        int speed_max_normaly = (int) Math.round(max_speed);
        textMaxSpeed.setText(String.valueOf(speed_max_normaly));

        textLimitSpeed.setText(String.valueOf(limit_speed));

        if(way>=1.0) {
            double way_normaly = Math.round(way * 10.0d) / 10.0d;
            textWay.setText(String.valueOf(way_normaly));
            text_ed.setText(" км");
        }else {
            int way_normaly = (int) Math.round(way*1000);
            textWay.setText(String.valueOf(way_normaly));
            text_ed.setText(" м");
        }

        textTime.setText(times);
        textSatellites.setText(String.valueOf(satellitesInFix)+"/"+String.valueOf(satellites));

        accuaru = (double) Math.round(accuaru*10.0d)/10.0d;
        if(accuaru==0.0)
        {
            textAccuaru.setText("Соединение со спутниками...");
        }
        else {
            textAccuaru.setText("Точность gps: "+String.valueOf(accuaru)+" м");
        }

        if(flg_petrol) {
            LLPetrol.setVisibility(View.VISIBLE);
            petrol = way * petrol_consumption / 100;
            petrol = Math.round(petrol * 10.0d) / 10.0d;
            textPetrol.setText(String.valueOf(petrol) + "L");
        }else {
            LLPetrol.setVisibility(View.GONE);
        }

        textinfo2.setText(String.valueOf(myTimer.getSeconds())+"  "+String.valueOf(petrol_consumption));


    }

    public void speedRacer()
    {
        if(flg_racing) {
            LLRacer1.setVisibility(View.VISIBLE);
            LLRacer2.setVisibility(View.VISIBLE);
            LLRacer3.setVisibility(View.VISIBLE);
            float density = getResources().getDisplayMetrics().density;
            int paddingPixel = (int) (300 * density);
            RLSpeed.setPadding(0,0,0,paddingPixel);

            if (speed >= 1.5 & speed < 20 & racer) {
                timeStart = myTimer.getSeconds();
                racer = false;
                r1 = false;
                r2 = false;
                r3 = false;
            }

            if (speed >= 30.0 & !r1) {
                timeStop30 = myTimer.getSeconds();
                r1 = true;
            }
            if (speed >= 60.0 & !r2) {
                timeStop60 = myTimer.getSeconds();
                r2 = true;
            }
            if (speed >= 100.0 & !r3) {
                timeStop100 = myTimer.getSeconds();
                r3 = true;
            }

            if (r1) {
                double r1 = (double) (timeStop30 - timeStart) / 10.0d;
                String str_r1 = Double.toString(Math.round(r1 * 10.0d) / 10.0d);
                textTime30.setText(str_r1);
            }

            if (r2) {
                double r2 = (double) (timeStop60 - timeStart) / 10.0d;
                String str_r2 = Double.toString(Math.round(r2 * 10.0d) / 10.0d);
                textTime60.setText(str_r2);
            }

            if (r3) {
                double r3 = (double) (timeStop100 - timeStart) / 10.0d;
                String str_r3 = Double.toString(Math.round(r3 * 10.0d) / 10.0d);
                textTime100.setText(str_r3);
            }
        }else
        {
            LLRacer1.setVisibility(View.GONE);
            LLRacer2.setVisibility(View.GONE);
            LLRacer3.setVisibility(View.GONE);
            float density = getResources().getDisplayMetrics().density;
            int paddingPixel = (int) (180 * density);
            RLSpeed.setPadding(0,0,0,paddingPixel);
        }
    }

    public void speedLimit()
    {
        if(speed>=limit_speed)
        {
            textSpeed.setTextColor(getResources().getColor(R.color.colorSpeedLimit));
            flg_limit_speed_audio_vibro = true;
        }
        else
        {
            textSpeed.setTextColor(getResources().getColor(R.color.color_text));
            flg_limit_speed_audio_vibro = false;
        }
    }

    public void restartActivity(){
        Intent mIntent = getIntent();
        finish();
        startActivity(mIntent);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mSettings.contains(APP_PREFERENCES_LIMIT_SPEED)) {
            // Получаем число из настроек
            limit_speed = mSettings.getInt(APP_PREFERENCES_LIMIT_SPEED, 0);
        }  else {limit_speed = 90;}
        if (mSettings.contains(APP_PREFERENCES_AUDIO)) {
            // Получаем число из настроек
            flg_audio = mSettings.getBoolean(APP_PREFERENCES_AUDIO, false);
        } else  {flg_audio = true; }
        if (mSettings.contains(APP_PREFERENCES_VIBRO)) {
            // Получаем число из настроек
            flg_vibro = mSettings.getBoolean(APP_PREFERENCES_VIBRO, false);
        } else  {flg_vibro = true; }
        if (mSettings.contains(APP_PREFERENCES_COMPASS)) {
            // Получаем число из настроек
            flg_compass = mSettings.getBoolean(APP_PREFERENCES_COMPASS, false);
        } else  {flg_compass = true; }
        if (mSettings.contains(APP_PREFERENCES_CONSTANT_PETROL)) {
            // Получаем число из настроек
            petrol_consumption = mSettings.getInt(APP_PREFERENCES_CONSTANT_PETROL, 0);
        }  else {petrol_consumption = 11;}
        if (mSettings.contains(APP_PREFERENCES_RACING)) {
            // Получаем число из настроек
            flg_racing = mSettings.getBoolean(APP_PREFERENCES_RACING, false);
        } else  {flg_racing = true; }
        if (mSettings.contains(APP_PREFERENCES_FLG_PETROL)) {
            // Получаем число из настроек
            flg_petrol = mSettings.getBoolean(APP_PREFERENCES_FLG_PETROL, false);
        } else  {flg_petrol = true; }


        mSensorManager.registerListener(this,mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),SensorManager.SENSOR_DELAY_GAME);
        mSensorManager.registerListener(this,mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),SensorManager.SENSOR_DELAY_GAME);

    }

    @Override
    protected void onPause() {
        super.onPause();

        // Запоминаем данные
        SharedPreferences.Editor editor = mSettings.edit();
        editor.putInt(APP_PREFERENCES_LIMIT_SPEED, limit_speed);
        editor.putBoolean(APP_PREFERENCES_AUDIO, flg_audio);
        editor.putBoolean(APP_PREFERENCES_VIBRO, flg_vibro);
        editor.putBoolean(APP_PREFERENCES_COMPASS, flg_compass);
        editor.putInt(APP_PREFERENCES_CONSTANT_PETROL, petrol_consumption);
        editor.putBoolean(APP_PREFERENCES_RACING, flg_racing);
        editor.putBoolean(APP_PREFERENCES_FLG_PETROL, flg_petrol);
        editor.apply();

        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        final float alpha = 0.97f;
        synchronized (this)
        {
            if(sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
            {
                mGravity[0] = alpha*mGravity[0]+(1-alpha)*sensorEvent.values[0];
                mGravity[1] = alpha*mGravity[1]+(1-alpha)*sensorEvent.values[1];
                mGravity[2] = alpha*mGravity[2]+(1-alpha)*sensorEvent.values[2];
            }

            if(sensorEvent.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
            {
                mGeomagnetic[0] = alpha*mGeomagnetic[0]+(1-alpha)*sensorEvent.values[0];
                mGeomagnetic[1] = alpha*mGeomagnetic[1]+(1-alpha)*sensorEvent.values[1];
                mGeomagnetic[2] = alpha*mGeomagnetic[2]+(1-alpha)*sensorEvent.values[2];
            }

            float R[] = new float[9];
            float I[] = new float[9];
            boolean success = SensorManager.getRotationMatrix(R,I,mGravity,mGeomagnetic);
            if(success)
            {
                float orientathion[] = new float[3];
                SensorManager.getOrientation(R,orientathion);
                azimuth = (float)Math.toDegrees(orientathion[0]);
                azimuth = (azimuth+360)%360;

                //
                if(flg_compass) {
                    image_compas.setVisibility(View.VISIBLE);
                    anim = new RotateAnimation(-currectAzimuth, -azimuth, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                    currectAzimuth = azimuth;
                    anim.setDuration(500);
                    anim.setRepeatCount(0);
                    anim.setFillAfter(true);

                    image_compas.setAnimation(anim);
                }else
                {
                    anim = null;
                    image_compas.animate().cancel();
                    image_compas.clearAnimation();
                    image_compas.setVisibility(View.INVISIBLE);
                }


                orientathion = null;
                anim = null;
            }
            R = null;
            I = null;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

}