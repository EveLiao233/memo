package com.example.lenovo.at;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

/**
 * Created by Administrator on 2016/12/14 0014.
 */

public class WelcomActivity extends Activity {
    private final long SPLASH_LENGTH = 2000;
    private int userId = -1;

    Handler handler = new Handler();
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome);

        //获取用户ID
        SharedPreferences sharedPreferences= getSharedPreferences("User", Context.MODE_PRIVATE);
        userId =sharedPreferences.getInt("userId", -1);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (userId == -1) {
                    Intent intent = new Intent(WelcomActivity.this, LoginActivity.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(WelcomActivity.this, MainActivity.class);
                    startActivity(intent);
                    System.out.println("WelcomActivity用户ID为："+userId);
                }
                finish();
            }
        },SPLASH_LENGTH);
    }
}
