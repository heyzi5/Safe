package com.example.safe.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;


public class FocusedTextview extends TextView {

    //用代码new对象时走此方法
    public FocusedTextview(Context context) {
        super(context);
    }

    //有属性时走此方法
    public FocusedTextview(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    //有style样式会走此方法
    public FocusedTextview(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * 判断有没有获得焦点，设为true强制获得
     *
     */
    @Override
    public boolean isFocused() {
        return true;
    }
}
