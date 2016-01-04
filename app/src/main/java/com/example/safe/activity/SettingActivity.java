package com.example.safe.activity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.safe.R;
import com.example.safe.view.SettingItemView;


public class SettingActivity extends Activity {

    private SettingItemView settingItemView;
    private SharedPreferences spf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        settingItemView = (SettingItemView) findViewById(R.id.siv_textView);
        //settingItemView.setTiTle("自动更新设置");
        spf = getSharedPreferences("config",MODE_PRIVATE);
        boolean autoUpdate = spf.getBoolean("auto_update", true);
        if (autoUpdate) {
            //settingItemView.setZt("自动更新已开启");
            settingItemView.setChecked(true);
        } else {
            //settingItemView.setZt("自动更新已关闭");
            settingItemView.setChecked(false);
        }



        settingItemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (settingItemView.isChecked()) {
                    Log.d("SettingActivity","nihao");
                    settingItemView.setChecked(false);
                    //settingItemView.setZt("自动更新已关闭");

                    spf.edit().putBoolean("auto_update", false).commit();
                } else {
                    Log.d("SettingActivity","2");
                    settingItemView.setChecked(true);
                    //settingItemView.setZt("自动更新已开启");
                    spf.edit().putBoolean("auto_update", true).commit();

                }
            }
        });
    }
}
