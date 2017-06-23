package com.example.lenovo.at;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by lenovo on 2017/6/13.
 */
public class LoginActivity extends AppCompatActivity {
    private ImageView userImage;
    private EditText username;
    private EditText password;
    private TextView goRegister;
    private Button loginButton;
    private static final String url = "http://172.18.69.108:8080";
    private String loginResult = "登录失败";
    private static int userId = -1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        userImage = (ImageView)findViewById(R.id.userImage);
        username = (EditText)findViewById(R.id.username);
        password = (EditText)findViewById(R.id.password);
        goRegister = (TextView)findViewById(R.id.goRegister);
        loginButton = (Button)findViewById(R.id.loginButton);

        //NetworkUtil.initNetwork();

        //没有账号，转入注册
        goRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(intent);
            }
        });

        //登录
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String a = PostLoginInfo(username.getText().toString(), password.getText().toString());
                Toast.makeText(LoginActivity.this, a, Toast.LENGTH_SHORT).show();
                if (a.equals("登录成功")) {
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putInt("userId", userId);
                    intent.putExtras(bundle);
                    startActivity(intent);
                    saveLoginInfo(getApplicationContext(), username.getText().toString());
                    finish();
                }
            }
        });
    }

    /*JSON解析*/
    public int parserJSON (String response) throws JSONException, IOException {
        JSONObject jsonObject = new JSONObject(response);
        String status = jsonObject.getString("resultCode");
        // 登录情况
        if (status.equals("1")) {
            String userId = jsonObject.getJSONObject("data").getString("userId");
            System.out.println("用户ID为：" + userId);
            return Integer.parseInt(userId);
        }
        return -1;
    }

    private String PostLoginInfo(final String userName, final String password) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                try {
                    //HTTP请求操作
                    // 建立Http连接
                    String current_url = url + "/memo/user/login";
                    connection = (HttpURLConnection) (new URL(current_url).openConnection());
                    // 设置访问方法和时间设置
                    connection.setRequestMethod("POST");          //设置以Post方式提交数据
                    connection.setDoInput(true);                  //打开输入流，以便从服务器获取数据
                    connection.setDoOutput(true);                 //打开输出流，以便向服务器提交数据
                    connection.setReadTimeout(8000);
                    connection.setConnectTimeout(8000);
                    connection.setUseCaches(false);               //使用Post方式不能使用缓存
                    connection.connect();
                    PrintWriter out = new PrintWriter(connection.getOutputStream());
                    String message = "userName="+ userName + "&password=" + password;
                    out.print(message);  //在输出流中写入参数
                    out.flush();
                    StringBuilder response = new StringBuilder();
                    if(connection.getResponseCode() == 200){
                        // 网页获取json转换为字符串
                        InputStream inputStream = connection.getInputStream();
                        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "utf-8"));
                        String line;
                        while ((line = reader.readLine()) != null) {
                            response.append(line);
                        }
                        userId = parserJSON(response.toString());   //解析服务器返回的数据
                        if(userId > 0) {
                            loginResult = "登录成功";
                        }
                    }
                    System.out.println("服务器返回的结果是：" + response.toString());   //打印服务器返回的数据

                } catch (Exception e) {
                    // 抛出异常
                    e.printStackTrace();
                } finally {
                    // 关闭connection
                    if (connection != null) {
                        connection.disconnect();
                    }
                }
            }
        });
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }
        return loginResult;
    }

    public static void saveLoginInfo(Context context, String userName) {
        //获取SharedPreferences对象
        SharedPreferences sharedPre = context.getSharedPreferences("User", Context.MODE_PRIVATE);
        //获取Editor对象
        SharedPreferences.Editor editor=sharedPre.edit();
        //设置参数
        editor.putInt("userId", userId);
        editor.putString("userName", userName);
        //提交
        editor.apply();
    }
}
