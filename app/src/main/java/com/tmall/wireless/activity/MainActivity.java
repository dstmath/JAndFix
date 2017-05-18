package com.tmall.wireless.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.TextView;

import com.tmall.wireless.R;
import com.tmall.wireless.test.Test1;


/**
 * Created by jingchaoqinjc on 17/3/20.
 */

public class MainActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView textView = (TextView) findViewById(R.id.text);
        textView.setText("本应该显示:Test1;实现显示:" + new Test1().string());
    }


}
