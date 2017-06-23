package com.example.lenovo.at;

import android.Manifest;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by lenovo on 2016/12/8.
 */
public class MainActivity extends AppCompatActivity {
    private static final String DB_NAME = "myDB.db";
    private static final String TABLE_NAME = "BirthNote";
    private static final int DB_VERSION = 1;

    private static int CATEGORY_DDL = 0;
    private static int CATEGORY_TIME = 1;
    private static int CATEGORY_IN_YOURS = 2;
    private static int CATEGORY_DEMO = 3;

    private ListView main_list;
    private myDB mydb = new myDB(this, DB_NAME, null, DB_VERSION);
    private List<Affair> AffairList = new ArrayList<>();
    private List<Affair> AffairList1 = new ArrayList<>();
    private List<Affair> AffairList2 = new ArrayList<>();
    private List<Affair> AffairList3 = new ArrayList<>();
    private AffairAdapter affairAdapter;

    // 网络服务地址
    private static final String url_weather = "http://apistore.baidu.com/microservice/weather";
    private ConnectivityManager connManager;
    private NetworkInfo networkInfo;
    private static final int UPDATE_CONTENT = 0;

    private String w = "";
    private ImageView iv_avatar;

    private static final String url = "http://172.18.69.108:8080";
    private String SynchronizeResult = "同步失败";
    private int userId = -1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);

        //获取用户ID
        SharedPreferences sharedPreferences= getSharedPreferences("User", Context.MODE_PRIVATE);
        userId =sharedPreferences.getInt("userId", -1);
        System.out.println("MainActivity用户ID为：" + userId);

        initialToolbar();
        initialAvatar();

        main_list = (ListView) findViewById(R.id.main_list);

        //从服务器的数据库中导入数据
        EventProcess("/memo/user/getEventByUserId", "");
        //从本地数据库中导入数据
        AffairList1 = mydb.getAllData();  //获取全部数据，存入list中

        //将服务器的数据导入到本地数据库
        Set<Affair> affairs = new HashSet<Affair>();
        if (AffairList1 != null)
            affairs.addAll(AffairList1);
        if (AffairList2 != null) {
            affairs.addAll(AffairList2);
            System.out.println("服务器导入数据成功");
        }
        List<Affair> affairs_all = new ArrayList<>(affairs);
        for (int i = 0; i < affairs_all.size(); i++) {
            mydb.insertOneData(affairs_all.get(i));
        }
        //将本地数据库中未导入的数据同步
        if (AffairList2 != null) {
            affairs_all.removeAll(AffairList2);  //删除已同步的数据
            for (int i = 0; i < affairs_all.size(); i++) {
                System.out.println(affairs_all.get(i).getThing());
                String message = "userId=" + userId + "&process=" + affairs_all.get(i).getProcess() +
                        "&icon=" + affairs_all.get(i).getIcon() +
                        "&category=" +  affairs_all.get(i).getCategory() +
                        "&eventName=" + affairs_all.get(i).getThing() +
                        "&content=" + affairs_all.get(i).getRemarks() +
                        "&startTime=" + affairs_all.get(i).getStart_time() +
                        "&endTime=" + affairs_all.get(i).getEnd_time() +
                        "&timestamps=" + affairs_all.get(i).getTimeStamp();
                EventProcess("/memo/user/addEvent", message);
                Toast.makeText(MainActivity.this, SynchronizeResult, Toast.LENGTH_LONG).show();
            }
        }

        AffairList = mydb.getAllData();  //获取全部数据，存入list中
        affairAdapter = new AffairAdapter(getApplicationContext(), AffairList);
        main_list.setAdapter(affairAdapter);
        handler.post(myRunnable);

        //短按点击事件(1214)
        main_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle bundle = new Bundle();
                bundle.putString("thing", AffairList.get(position).getThing());
                bundle.putString("start", AffairList.get(position).getStart_time());
                bundle.putString("end", AffairList.get(position).getEnd_time());
                bundle.putString("remarks", AffairList.get(position).getRemarks());
                bundle.putInt("icon", AffairList.get(position).getIcon());
                if (AffairList.get(position).getCategory() == CATEGORY_DDL) {
                    Intent intent = new Intent(MainActivity.this, AddDDL.class);
                    intent.putExtras(bundle);
                    startActivity(intent);
                } else if (AffairList.get(position).getCategory() == CATEGORY_TIME) {
                    Intent intent = new Intent(MainActivity.this, AddTime.class);
                    intent.putExtras(bundle);
                    startActivity(intent);
                } else if (AffairList.get(position).getCategory() == CATEGORY_IN_YOURS) {
                    Intent intent = new Intent(MainActivity.this, AddInYours.class);
                    bundle.putString("process", AffairList.get(position).getProcess() + "");
                    intent.putExtras(bundle);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(MainActivity.this, AddSpecial.class);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            }
        });

        //长按点击事件
        main_list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("是否删除");
                builder.setPositiveButton("是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if (AffairList.get(position).getCategory() == CATEGORY_DDL
                                || AffairList.get(position).getCategory() == CATEGORY_TIME) {
                            long timeStamp = AffairList.get(position).getTimeStamp(); // 时间戳
                            int ddl_code = (int) (timeStamp / 1000); // id
                            String itemName = AffairList.get(position).getThing();// 名称
                            int icon_id = AffairList.get(position).getIcon();// icon
                            // 旧ddl_operation
                            PendingIntent ddl_operation = PendingIntent.getBroadcast(MainActivity.this,
                                    ddl_code, new Intent(MainActivity.this, AlarmReceiver.class).
                                            putExtra("id", ddl_code).
                                            putExtra("thing_time", itemName).
                                            putExtra("icon_id", icon_id),
                                    PendingIntent.FLAG_UPDATE_CURRENT);
                            AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
                            am.cancel(ddl_operation); // 取消ddl_operation
                        }
                        // 从数据库中删除
                        mydb.deleteOneData(AffairList.get(position));
                        // 从服务器数据库中删除
                        EventProcess("/memo/user/deleteEventByUserId_EventName", AffairList.get(position).getThing());
                        Toast.makeText(MainActivity.this, SynchronizeResult, Toast.LENGTH_LONG).show();
                        // 从链表中删除
                        AffairList.remove(position);
                        affairAdapter.notifyDataSetChanged();
                    }
                });
                builder.setNegativeButton("否", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                builder.show();
                return true;
            }
        });

    }

    public void setWeather() {
        connManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        networkInfo = connManager.getActiveNetworkInfo();
        if (networkInfo == null || !networkInfo.isAvailable()) {
            Toast.makeText(MainActivity.this, getResources().getString(R.string.noInternet), Toast.LENGTH_SHORT).show();
        } else {
            sendRequestWithHttpURLConnection();
        }
    }

    // HTTP请求 & Message传递
    private void sendRequestWithHttpURLConnection() {
        final String a = "";
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                try {
                    //HTTP请求操作
                    // 建立Http连接
                    Log.i("key", "Begin the connection");
                    String current_url = url_weather + "?cityname=guangzhou";
                    connection = (HttpURLConnection) (new URL(current_url).openConnection());
                    // 设置访问方法和时间设置
                    connection.setRequestMethod("GET");
                    connection.setReadTimeout(8000);
                    connection.setConnectTimeout(8000);
                    // 设置apikey
                    connection.setRequestProperty("apikey", "f14815740955a01038ce277ce1ac23b5");
                    connection.connect();
                    // 网页获取json转换为字符串
                    InputStream in = connection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    w = parserJSON_weather(response.toString());
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
    }

    /*JSON解析*/
    public String parserJSON_weather (String response) throws JSONException, IOException {
        String weather = new String();
        JSONObject jsonObject = new JSONObject(response);
        String status = jsonObject.getString("errMsg");
        // 查询城市存在时
        if (status.contains("success")) {
            // 天气情况描述
            weather = jsonObject.getJSONObject("retData").getString("weather");
            return weather;
        }
        return null;
    }

    //同步删除服务器数据库中的数据
    public void parserJSON (String response) throws JSONException, IOException {
        JSONObject jsonObject = new JSONObject(response);
        String status = jsonObject.getString("resultCode");
        if (status.equals("1")) {
            SynchronizeResult = "同步成功";
        }
    }

    //解析从服务器获取的事件
    public void parserJSONEvent (String response) throws JSONException, IOException {
        JSONObject jsonObject = new JSONObject(response);
        String status = jsonObject.getString("resultCode");
        // 登录情况
        if (status.equals("1")) {
            JSONArray arr = jsonObject.getJSONArray("data");
            for (int i = 0; i < arr.length(); i++) {
                JSONObject event = (JSONObject) arr.get(i);
                int event_icon = event.getInt("icon");
                int event_category = event.getInt("category");
                String event_name = event.getString("eventName");
                String event_remark = event.getString("content");
                String event_start = event.getString("startTime");
                String event_end = event.getString("endTime");
                long event_timeStamp = Long.parseLong(event.getString("timestamps"));
                int event_process = event.getInt("process");
                Affair affair = new Affair(event_name, event_process, event_start, event_end,
                        event_category, event_icon, event_remark, event_timeStamp);
                AffairList2.add(affair);
            }
        }
    }

    private String EventProcess(final String path, final String eventName) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                try {
                    //HTTP请求操作
                    // 建立Http连接
                    String current_url = url + path;
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
                    String message = "";
                    if (eventName.isEmpty()) {
                        message = "userId=" + userId;
                    } else if (eventName.contains("userId")) {
                        message = eventName;
                    } else {
                        message = "userId=" + userId + "&eventName=" + eventName;
                    }
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
                        if (eventName.isEmpty()) {
                            parserJSONEvent(response.toString());   //解析服务器返回的数据
                        } else {
                            parserJSON(response.toString());   //解析服务器返回的数据
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
        return SynchronizeResult;
    }

    //处理标题栏
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        setWeather();
        boolean weatherExist = true;
        getMenuInflater().inflate(R.menu.title_menu, menu);
        if (w.contains("雨")) {
            menu.findItem(R.id.weather_menu).setIcon(R.mipmap.rain);
        } else if (w.contains("晴")) {
            menu.findItem(R.id.weather_menu).setIcon(R.mipmap.sunny);
        } else if (w.contains("云")) {
            menu.findItem(R.id.weather_menu).setIcon(R.mipmap.cloudy);
        } else if (w.contains("风")) {
            menu.findItem(R.id.weather_menu).setIcon(R.mipmap.windy_b);
        } else if (w.contains("雪")) {
            menu.findItem(R.id.weather_menu).setIcon(R.mipmap.snow);
        } else {
            weatherExist = false;
            menu.findItem(R.id.weather_menu).setIcon(R.mipmap.windy);
            menu.findItem(R.id.weather_menu).setTitle("获取失败");
        }
        if (weatherExist)
            menu.findItem(R.id.weather_menu).setTitle(w);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Toast.makeText(this,
                        "You clicked on the Application icon",
                        Toast.LENGTH_LONG).show();
                Intent intent = new Intent(MainActivity.this, ChooseAvatar.class);
                startActivity(intent);
                mydb.close();
                break;
            case R.id.plus:
                Intent intent_ = new Intent(MainActivity.this, AddActivity.class);
                startActivity(intent_);
                mydb.close();
                break;
            case R.id.weather_menu:
                setWeather();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public int CalculatePro(String thing) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH) + 1;
            int date = calendar.get(Calendar.DATE);
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);
            int day_of_week = calendar.get(Calendar.DAY_OF_WEEK) - 1;

            boolean leapyear = false;
            int days;

            if(year % 4 == 0 && year % 100 != 0 || year % 400 == 0) {
                leapyear = true;
            }

            if(month == 1 || month == 3 || month == 5 || month == 7 || month == 8 || month == 10 || month ==12) {
                days = 31;
            } else if (month == 2 && leapyear) {
                days = 29;
            } else if (month == 2 && (!leapyear)) {
                days = 28;
            } else {
                days = 30;
            }

            if (thing.equals("今天")) {
                String date_current = "2016-11-22 " + (hour < 10 ? "0" + hour : hour) + ":" + (minute < 10 ? "0" + minute : minute) + ":00";
                Date date_start = format.parse("2016-11-22 00:00:00");
                Date date_end = format.parse("2016-11-22 23:59:59");
                Date date_cur = format.parse(date_current);
                return differentTime(date_start, date_cur) * 100 / differentTime(date_start, date_end);
            } else if(thing.equals("本周")) {
                return day_of_week * 100 / 7;
            }  else if(thing.equals("本月")) {
                String date_current = year + "-" + ((month + 1) < 10 ? "0" + month : month) + "-"
                        + ((date < 10) ? "0" + date : date) + " 00:00:00";
                Date date_start = format.parse(year + "-" + ((month + 1) < 10 ? "0" + month : month) + "-01" + " 00:00:00");
                Date date_end = format.parse(year + "-" + ((month + 1) < 10 ? "0" + month : month) + "-" + days + " 00:00:00");
                Date date_cur = format.parse(date_current);
                return differentDays(date_start, date_cur) * 100 / differentDays(date_start, date_end);
            }  else if(thing.equals("今年")) {
                String date_current = year + "-" + ((month + 1) < 10 ? "0" + month : month) + "-"
                        + ((date < 10) ? "0" + date : date) + " 00:00:00";
                Date date_start = format.parse(year + "-01-01" + " 00:00:00");
                Date date_end = format.parse(year + "-12-31" + " 00:00:00");
                Date date_cur = format.parse(date_current);
                return differentDays(date_start, date_cur) * 100 / differentDays(date_start, date_end);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    private void initialAvatar() {
        iv_avatar = (ImageView)findViewById(R.id.iv_avatar);
        iv_avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ChooseAvatar.class);
                startActivity(intent);
            }
        });
        setAvatar();
    }

    private void initialToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
    }

    private void setAvatar() {
        try (FileInputStream fis = openFileInput("avatar.png")) {
            byte[] contents = new byte[fis.available()];
            fis.read(contents);
            fis.close();
            iv_avatar.setImageBitmap(BitmapFactory.decodeByteArray(contents, 0, contents.length));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private Handler handler = new Handler();
    private Runnable myRunnable = new Runnable() {
        public void run() {
            try {
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Calendar calendar = Calendar.getInstance();
                for (int i = 0; i < affairAdapter.getCount(); i++) {
                    if (AffairList.get(i).getCategory() == CATEGORY_DDL) {
                        int year = calendar.get(Calendar.YEAR);
                        int month = calendar.get(Calendar.MONTH) + 1;
                        int date = calendar.get(Calendar.DATE);
                        String date_current = year + "-" + ((month + 1) < 10 ? "0" + month : month) + "-"
                                + ((date < 10) ? "0" + date : date) + " 00:00:00";
                        Date date_start = format.parse(AffairList.get(i).getStart_time() + " 00:00:00");
                        Date date_end = format.parse(AffairList.get(i).getEnd_time() + " 00:00:00");
                        Date date_cur = format.parse(date_current);
                        int pro = differentDays(date_start, date_cur) * 100 / differentDays(date_start, date_end);
                        AffairList.get(i).setProcess(pro);
                    } else if (AffairList.get(i).getCategory() == CATEGORY_TIME) {
                        int hour = calendar.get(Calendar.HOUR_OF_DAY);
                        int minute = calendar.get(Calendar.MINUTE);
                        String date_current = "2016-11-22 " + (hour < 10 ? "0" + hour : hour) + ":" + (minute < 10 ? "0" + minute : minute) + ":00";
                        Date date_start = format.parse("2016-11-22 " + AffairList.get(i).getStart_time() + ":00");
                        Date date_end = format.parse("2016-11-22 " + AffairList.get(i).getEnd_time() + ":00");
                        Date date_cur = format.parse(date_current);
                        int pro = differentTime(date_start, date_cur) * 100 / differentTime(date_start, date_end);
                        AffairList.get(i).setProcess(pro);
                    } else if (AffairList.get(i).getCategory() == CATEGORY_DEMO) {
                        int pro = CalculatePro(AffairList.get(i).getThing());
                        AffairList.get(i).setProcess(pro);
                    }
                    affairAdapter.notifyDataSetChanged();
                }
                handler.postDelayed(myRunnable, 50);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    public int differentDays(Date date1, Date date2) {
        return (int) ((date2.getTime() - date1.getTime()) / (1000 * 3600 * 24));
    }

    public int differentTime(Date date1, Date date2) {
        return (int) (((date2.getTime() - date1.getTime()) % (1000 * 3600 * 24)) / (1000 * 60));
    }

    @Override
    public void onRestart() {
        super.onRestart();
        main_list = (ListView) findViewById(R.id.main_list);
        AffairList = mydb.getAllData();  //获取全部数据，存入list中
        affairAdapter = new AffairAdapter(getApplicationContext(), AffairList);
        main_list.setAdapter(affairAdapter);
        setAvatar();
    }
}
