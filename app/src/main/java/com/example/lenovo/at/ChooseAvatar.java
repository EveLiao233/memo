package com.example.lenovo.at;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by lenovo on 2017/6/5.
 */
public class ChooseAvatar extends AppCompatActivity {

    private Button btn_selectAvatar;
    private ImageView iv_avatar;
    private Menu mMenu;
    private Bitmap photo = null;
    private TextView userName;
    private static final String url = "http://172.18.69.108:8080";
    private String logoutResult = "登出失败";
    private static int userId = -1;
//    private Button logout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.avatar);

        initial();
        initialToolBar();
    }

    private void initial() {
        btn_selectAvatar = (Button) findViewById(R.id.btn_selectAvatar);
        iv_avatar = (ImageView) findViewById(R.id.iv_avatar);
        userName = (TextView) findViewById(R.id.userName);
//        logout = (Button) findViewById(R.id.logout);

        //获取用户ID
        SharedPreferences sharedPreferences= getSharedPreferences("User", ChooseAvatar.MODE_PRIVATE);
        userName.setText(sharedPreferences.getString("userName", "用户名"));
        userId =sharedPreferences.getInt("userId", -1);

        try (FileInputStream fis = openFileInput("avatar.png")) {
            byte[] contents = new byte[fis.available()];
            fis.read(contents);
            fis.close();
            iv_avatar.setImageBitmap(BitmapFactory.decodeByteArray(contents, 0, contents.length));
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        btn_selectAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent pickIntent = new Intent(Intent.ACTION_PICK, null);
                pickIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        "image/*");
                startActivityForResult(pickIntent, 0);
            }
        });

//        logout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                String a = GetLogoutInfo();
//                if (a.equals("登出成功")) {
//                    userName.setText("用户名");
//                }
//                Toast.makeText(ChooseAvatar.this, a, Toast.LENGTH_LONG).show();
//            }
//        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 0:// 直接从相册获取
                    try {
                        startPhotoZoom(data.getData());
                    } catch (NullPointerException e) {
                        e.printStackTrace();// 用户点击取消操作
                    }
                    break;
                case 1:// 取得裁剪后的图片
                    if (data != null) {
                        setPicToView(data);
                    }
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void setPicToView(Intent picdata) {
        Bundle extras = picdata.getExtras();
        if (extras != null) {
            photo = extras.getParcelable("data");
            Drawable drawable = new BitmapDrawable(null, photo);
            iv_avatar.setImageDrawable(drawable);
        }
    }

    public void startPhotoZoom(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", true);
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 300);
        intent.putExtra("outputY", 300);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, 1);
    }

    private void initialToolBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("更换头像");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_menu, menu);
        mMenu = menu;
        mMenu.findItem(R.id.share_menu).setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add:
                saveAvatar();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void saveAvatar() {
        if (photo == null) {
            finish();
            return;
        }
        FileOutputStream fos = null;
        try {
            fos = openFileOutput("avatar.png", MODE_PRIVATE);
            photo.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        finish();
    }

    /*JSON解析*/
    public void parserJSON (String response) throws JSONException, IOException {
        JSONObject jsonObject = new JSONObject(response);
        String status = jsonObject.getString("resultCode");
        // 登出情况
        if (status.equals("1")) {
            logoutResult = "登出成功";
        }
    }

    private String GetLogoutInfo() {
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
                    connection.setRequestMethod("GET");          //设置以Post方式提交数据
                    connection.setDoInput(true);                  //打开输入流，以便从服务器获取数据
                    connection.setDoOutput(true);                 //打开输出流，以便向服务器提交数据
                    connection.setReadTimeout(8000);
                    connection.setConnectTimeout(8000);
                    connection.connect();

                    StringBuilder response = new StringBuilder();
                    // 网页获取json转换为字符串
                    InputStream inputStream = connection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "utf-8"));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    parserJSON(response.toString());   //解析服务器返回的数据
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
        return logoutResult;
    }
}
