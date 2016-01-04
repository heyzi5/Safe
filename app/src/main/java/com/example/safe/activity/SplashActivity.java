package com.example.safe.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.safe.R;
import com.example.safe.utils.Streamutils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class SplashActivity extends Activity {

    private TextView tvVersion;
    private String mVersionName ;
    private TextView tvProgress;

    private int mVersionCode;
    private String mDescription;
    private String mDownLoadUrl;

    private static final int CODE_UPDATE_DIALOG = 0;
    private static final int CODE_URL_ERROR = 1;
    private static final int CODE_INT_ERROR = 2;
    private static final int CODE_JSON_ERROR = 3;
    private static final int CODE_NO_UPDATE = 4;
    private SharedPreferences spf;

    private Handler mHandler = new Handler() {
        public void handleMessage (android.os.Message msg) {
            switch (msg.what) {
                case CODE_UPDATE_DIALOG:
                    showUpdateDialog();
                    break;
                case CODE_URL_ERROR:
                    Toast.makeText(SplashActivity.this, "url异常",Toast.LENGTH_SHORT ).show();
                    enterHome();
                    break;
                case CODE_INT_ERROR:
                    Toast.makeText(SplashActivity.this, "网络异常",Toast.LENGTH_SHORT ).show();
                    enterHome();
                    break;
                case CODE_JSON_ERROR:
                    Toast.makeText(SplashActivity.this, "数据解析异常",Toast.LENGTH_SHORT ).show();
                    enterHome();
                    break;
                case CODE_NO_UPDATE:
                    enterHome();
                    break;
                default:
                    break;

            }
        }
    };
    private RelativeLayout rlRoot;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // requestWindowFeature(getWindow().FEATURE_NO_TITLE);
        setContentView(R.layout.activity_splash);


        tvVersion = (TextView) findViewById(R.id.tv_version);
        tvProgress = (TextView) findViewById(R.id.tv_progress);

        tvVersion.setText("版本号：" + getVersionName());

        spf = getSharedPreferences("config",MODE_PRIVATE);
        boolean autoUpdate = spf.getBoolean("auto_update",true);
        if(autoUpdate) {
            checkVersion();
        } else {
            mHandler.sendEmptyMessageDelayed(CODE_NO_UPDATE,2000);
        }

        rlRoot = (RelativeLayout) findViewById(R.id.rl_root);
        AlphaAnimation alphaAnimation = new AlphaAnimation(0.3f, 1f);
        rlRoot.startAnimation(alphaAnimation);
    }

    private String getVersionName () {
        PackageManager packageManager = getPackageManager();
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(
                    getPackageName(),0);
            String versionName = packageInfo.versionName;
            Log.d("Splash","versionName is " + versionName);
            return versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "";
    }

    private int getVersionCode () {
        PackageManager packageManager = getPackageManager();
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(
                    getPackageName(),0);
            int versionCode = packageInfo.versionCode;
            Log.d("Splash","versionCode is " + versionCode);
            return versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return -1;
    }

    private void checkVersion () {
        final long startTime = System.currentTimeMillis();

        new Thread() {
            Message msg = Message.obtain();
            HttpURLConnection connection = null;
            @Override
            public void run() {
                try {
                    URL url = new URL("http://192.168.1.111/update.json");
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");//设置请求方法
                    connection.setConnectTimeout(5000);//设置连接超时为5s
                    connection.setReadTimeout(5000);//设置读取超时为5s
                    connection.connect();//连接服务器

                    int responseCode = connection.getResponseCode();//获取响应码
                    if (responseCode == 200) {
                        InputStream inputStream = connection.getInputStream();
                        String result = Streamutils.readFromStream(inputStream);
                        //Log.d("Splash", "网络结果：" + result);
                        JSONObject jo = new JSONObject(result);

                        mVersionName = jo.getString("versionName");
                        mVersionCode = jo.getInt("versionCode");
                        mDescription = jo.getString("description");
                        mDownLoadUrl = jo.getString("downloadUrl");
                        // Log.d("Splash", "description is" + mDescription);
                        if (mVersionCode > getVersionCode()) {
                            //服务器版本号大于本地版本号，说明有更新，弹出对话框
                            msg.what = CODE_UPDATE_DIALOG;

                        } else {
                            //没有版本更新
                            msg.what = CODE_NO_UPDATE;
                        }

                    }
                } catch (MalformedURLException e) {
                    //url异常
                    msg.what = CODE_URL_ERROR;
                    e.printStackTrace();
                } catch (IOException e) {
                    //网络异常
                    msg.what = CODE_INT_ERROR;
                    e.printStackTrace();
                } catch (JSONException e) {
                    //json异常
                    msg.what = CODE_JSON_ERROR;
                    e.printStackTrace();
                } finally {
                    long endTime = System.currentTimeMillis();
                    long time = endTime - startTime;
                    if (time < 2000) {
                        try {
                            Thread.sleep(2000 - time);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    mHandler.sendMessage(msg);


                    if(connection != null) {
                        connection.disconnect();//关闭网络连接
                    }
                }
            }
        }.start();

    }

    private void showUpdateDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("最新版本：" + mVersionName);
        builder.setMessage(mDescription);
        builder.setPositiveButton("立即更新", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
//                Log.d("Splash", "立即更新");
                downlond();
            }
        });
        builder.setNegativeButton("暂不更新", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                enterHome();
            }
        });
        //builder.setCancelable(false);//不让用户使用返回键取消对话框,用户体验很不好，尽量不要

        //监听返回键，用户点击时触发
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                enterHome();
            }
        });

        builder.show();

    }

    private void downlond() {
        tvProgress.setVisibility(View.VISIBLE);//显示下载进度
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            String target = Environment.getExternalStorageDirectory() + "/update.apk";
            HttpUtils http = new HttpUtils();
            http.download(mDownLoadUrl, target, new RequestCallBack<File>() {
                @Override
                //下载文件的进度
                public void onLoading(long total, long current, boolean isUploading) {
                    super.onLoading(total, current, isUploading);
                    Log.d("Splash", "下载进度:" + current + "/" + total);
                    tvProgress.setText("下载进度：" + current*100/total + "%");

                }

                @Override
                //下载成功
                public void onSuccess(ResponseInfo<File> responseInfo) {
                    Log.d("Splash", "下载成功");

                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.addCategory(Intent.CATEGORY_DEFAULT);
                    intent.setDataAndType(Uri.fromFile(responseInfo.result),
                            "application/vnd.android.package-archive");
                    //startActivity(intent);
                    startActivityForResult(intent, 0);//如果用户取消安装会返回结果，回调方法onActivityResult
                }

                @Override
                //下载失败
                public void onFailure(HttpException e, String s) {
                    Toast.makeText(SplashActivity.this, "下载失败", Toast.LENGTH_SHORT).show();
                    enterHome();
                }
            });
        } else {
            Toast.makeText(SplashActivity.this, "没有检测到sd卡", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        enterHome();
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void enterHome() {
        Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
        startActivity(intent);
        SplashActivity.this.finish();

    }
}
