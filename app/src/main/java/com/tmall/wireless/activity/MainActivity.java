package com.tmall.wireless.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.TextView;

import com.tmall.wireless.R;
import com.tmall.wireless.test.Test1;

import java.lang.reflect.Method;


/**
 * Created by jingchaoqinjc on 17/3/20.
 */

public class MainActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView textView = (TextView) findViewById(R.id.text);
        try {
            Test1 test1 = new Test1();
            Method privateMethod = Test1.class.getDeclaredMethod("privateString");
            privateMethod.setAccessible(true);

            Method protectedMethod = Test1.class.getDeclaredMethod("protectedString");
            protectedMethod.setAccessible(true);
            textView.setText("本应该显示:Test1;实际显示:" + test1.string()
                    + "\n" + "本应该显示:privateTest1;实际显示:" + privateMethod.invoke(test1)
                    + "\n" + "本应该显示:protectedTest1;实际显示:" + protectedMethod.invoke(test1)
                    + "\n" + "本应该显示:StaticTest1;实际显示:" + test1.staticString());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


}
