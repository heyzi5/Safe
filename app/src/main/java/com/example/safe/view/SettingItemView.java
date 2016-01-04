package com.example.safe.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.safe.R;

public class SettingItemView extends RelativeLayout {
    private TextView tvTitle;
    private TextView tvZhuangTai;
    private CheckBox cbStatus;
    private String NAMESPACE = "http://schemas.android.com/apk/res-auto";
    private String title;
    private String ztOn;
    private String ztOff;

    public SettingItemView(Context context) {
        super(context);
        initView();
    }

    public SettingItemView(Context context, AttributeSet attrs) {
        super(context, attrs);

//        int k = attrs.getAttributeCount();
//        for (int i = 0; i < k;i++) {
//            String name = attrs.getAttributeName(i);
//            String values = attrs.getAttributeValue(i);
//            Log.d("Setting", name + "=" + values);
//        }

        title = attrs.getAttributeValue(NAMESPACE, "Title");
        ztOn = attrs.getAttributeValue(NAMESPACE, "ZT_On");
        ztOff = attrs.getAttributeValue(NAMESPACE, "ZT_Off");
        initView();
    }

    public SettingItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    public void initView() {
        View.inflate(getContext(), R.layout.view_setting_list,this);
        tvTitle = (TextView) findViewById(R.id.tv_title);
        tvZhuangTai = (TextView) findViewById(R.id.tv_zhuangtai);
        cbStatus = (CheckBox) findViewById(R.id.cbStatus);
        setTiTle(title);
    }

    public void setTiTle(String title) {
        tvTitle.setText(title);
    }

    public void setZt(String zt) {
        tvZhuangTai.setText(zt);
    }

    public boolean isChecked() {
        return cbStatus.isChecked();
    }

    public void setChecked(boolean check) {
        if (check) {
            setZt(ztOn);
            cbStatus.setChecked(true);
        } else {
            setZt(ztOff);
            cbStatus.setChecked(false);
        }
    }
}
