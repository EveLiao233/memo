package com.example.lenovo.at;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by lenovo on 2017/6/13.
 */
public class RegisterActivity extends AppCompatActivity {
    private EditText username_re;
    private EditText password_re;
    private EditText confirm_password;
    private Button registerButton;
    private static final String url = "http://172.18.69.108:8080";
    private String registerResult = "注册失败";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

        //NetworkUtil.initNetwork();

        username_re = (EditText)findViewById(R.id.username_re);
        password_re = (EditText)findViewById(R.id.password_re);
        confirm_password = (EditText)findViewById(R.id.confirm_password);
        registerButton = (Button)findViewById(R.id.registerButton);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = username_re.getText().toString();
                String password1 = password_re.getText().toString();
                String password2 = confirm_password.getText().toString();
                if (name.isEmpty()) {
                    Toast.makeText(RegisterActivity.this, "请输入用户名", Toast.LENGTH_SHORT).show();
                } else if (password1.isEmpty()) {
                    Toast.makeText(RegisterActivity.this, "请输入密码", Toast.LENGTH_SHORT).show();
                } else if (password2.isEmpty()) {
                    Toast.makeText(RegisterActivity.this, "请输入确认密码", Toast.LENGTH_SHORT).show();
                } else if (!password1.equals(password2)) {
                    Toast.makeText(RegisterActivity.this, "确认密码匹配错误,请重新输入", Toast.LENGTH_SHORT).show();
                } else {
                    String a = PostRegisterInfo(name, password1);
                    Toast.makeText(RegisterActivity.this, a, Toast.LENGTH_SHORT).show();
                    if (a.equals("注册成功")) {
                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
            }
        });
    }

    /*JSON解析*/
    public boolean parserJSON (String response) throws JSONException, IOException {
        JSONObject jsonObject = new JSONObject(response);
        String status = jsonObject.getString("resultCode");
        // 注册情况
        if (status.equals("1")) {
            return true;
        }
        return false;
    }

    private String PostRegisterInfo(String a, String b) {
        final String username = a;
        final String password = b;
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                try {
                    //HTTP请求操作
                    // 建立Http连接
                    Log.i("key", "register");
                    String current_url = url + "/memo/user/register";
                    connection = (HttpURLConnection) (new URL(current_url).openConnection());

                    // 设置访问方法和时间设置
                    connection.setRequestMethod("POST");          //设置以Post方式提交数据
                    connection.setDoInput(true);                  //打开输入流，以便从服务器获取数据
                    connection.setDoOutput(true);                 //打开输出流，以便向服务器提交数据
                    connection.setReadTimeout(8000);
                    connection.setConnectTimeout(8000);
                    //connection.setUseCaches(false);               //使用Post方式不能使用缓存

                    connection.connect();
                    PrintWriter out = new PrintWriter(connection.getOutputStream());
                    //在输出流中写入参数
                    String registerMessage = "userName=" + username + "&password=" +
                            password + "&description=" + "Register" + "&avatar=" + "nothing";
                    out.print(registerMessage);
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
                        boolean result = parserJSON(response.toString());   //解析服务器返回的数据
                        if(result) {
                            registerResult = "注册成功";
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
        return registerResult;
    }
}
