package com.karbox.carspeed;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ListView;

import com.google.android.gms.ads.MobileAds;

import java.util.ArrayList;

public class SettingsActivity extends AppCompatActivity {

    ImageView imageUndo,imageShare;
    ListView listView;

    ArrayList<item_model> items_model = new ArrayList<>();
    adapter_item_model adapter_item_model;

    public static final String APP_PREFERENCES = "mysettings_karbox_gpsspeed_app_v1";
    public static final String APP_PREFERENCES_LIMIT_SPEED = "limit_speed";
    public static final String APP_PREFERENCES_AUDIO = "audio";
    public static final String APP_PREFERENCES_VIBRO = "vibro";
    public static final String APP_PREFERENCES_COMPASS = "compass";
    public static final String APP_PREFERENCES_CONSTANT_PETROL = "petrol";
    public static final String APP_PREFERENCES_RACING = "racing";
    public static final String APP_PREFERENCES_FLG_PETROL = "flg_petrol";
    private SharedPreferences mSettings;

    boolean flg_audio;
    boolean flg_vibro;
    boolean flg_compass;
    int limit_speed;
    boolean flg_petrol;
    int petrol_consumption; // Средний расход топлива на 100км
    boolean flg_racing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_settings);
        MobileAds.initialize(this, "ca-app-pub-2038569899413155~5149521534");

        mSettings = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        onResume();

        imageUndo = (ImageView) findViewById(R.id.imageUndo);
        imageUndo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        imageShare = (ImageView) findViewById(R.id.imageShare);
        imageShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_TEXT, "https://play.google.com/store/apps/details?id=com.karbox.carspeed");
                startActivity(Intent.createChooser(shareIntent, "Поделиться приложением"));
            }
        });

        listView = (ListView) findViewById(R.id.listview);

        items_model.add(new item_model(false,"Ограничение скорости (км/ч)","Скорость, при которой будет звуковой и вибро сигнал",String.valueOf(limit_speed)));
        items_model.add(new item_model(true,"Звук","Звук при превышении максимальной скорости",flg_audio));
        items_model.add(new item_model(true,"Вибрация","Вибрация при превышении максимальной скорости",flg_vibro));
        items_model.add(new item_model(true,"Расход топлива","Показывать расход топлива автомобиля",flg_petrol));
        items_model.add(new item_model(false,"Расход (л/100км)","Средний расход топлива вашего автомобиля",String.valueOf(petrol_consumption)));
        items_model.add(new item_model(true,"Время разгона","Показывать данные о разгоне автомобиля",flg_racing));
        items_model.add(new item_model(true,"Компас","Показывать компас в нижней части экрана",flg_compass));


        adapter_item_model = new adapter_item_model(getApplicationContext(),items_model);
        listView.setAdapter(adapter_item_model);

        if(!items_model.get(0).getText_edit().equals("")) { limit_speed = (int) Integer.valueOf(items_model.get(0).getText_edit()); }
        flg_audio = items_model.get(1).getFlg_switch();
        flg_vibro = items_model.get(2).getFlg_switch();
        flg_petrol = items_model.get(3).getFlg_switch();
        if(!items_model.get(4).getText_edit().equals("")) { petrol_consumption = (int) Integer.valueOf(items_model.get(4).getText_edit()); }
        flg_racing = items_model.get(5).getFlg_switch();
        flg_compass = items_model.get(6).getFlg_switch();

    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mSettings.contains(APP_PREFERENCES_LIMIT_SPEED)) {
            // Получаем число из настроек
            limit_speed = mSettings.getInt(APP_PREFERENCES_LIMIT_SPEED, 0);
        } else {limit_speed = 90;}
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

    }

    @Override
    protected void onPause() {
        super.onPause();

        if(!items_model.get(0).getText_edit().equals("")) { limit_speed = (int) Integer.valueOf(items_model.get(0).getText_edit()); }
        flg_audio = items_model.get(1).getFlg_switch();
        flg_vibro = items_model.get(2).getFlg_switch();
        flg_petrol = items_model.get(3).getFlg_switch();
        if(!items_model.get(4).getText_edit().equals("")) { petrol_consumption = (int) Integer.valueOf(items_model.get(4).getText_edit()); }
        flg_racing = items_model.get(5).getFlg_switch();
        flg_compass = items_model.get(6).getFlg_switch();

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

    }
}
