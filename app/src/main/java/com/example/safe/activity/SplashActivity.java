package com.example.safe.activity;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.example.safe.R;

public class SplashActivity extends Activity {

    private TextView tvVersion;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvVersion = (TextView) findViewById(R.id.tv_version);

        tvVersion.setText("版本号：" + getVersionName());
    }

    private String getVersionName () {
        PackageManager packageManager = getPackageManager();
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(
                    getPackageName(),0);
            int versionCode = packageInfo.versionCode;
            String versionName = packageInfo.versionName;
            Log.d("Splash","versionCode is " + versionCode);
            Log.d("Splash","versionName is " + versionName);
            return versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "";
    }
}
