package com.example.lenovo.at;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by lenovo on 2016/12/8.
 */
public class MainActivity extends AppCompatActivity {
    private static final String DB_NAME = "myDB.db";
    private static final String TABLE_NAME = "BirthNote";
    private static final int DB_VERSION = 1;

    private static int CATEGORY_FIRST = 0;
    private static int CATEGORY_SECOND = 1;
    private static int CATEGORY_THIRD = 2;
    private static int CATEGORY_FOURTH = 3;

    private ListView main_list;
    private myDB mydb = new myDB(this, DB_NAME, null, DB_VERSION);
    private List<Affair> AffairList = new ArrayList<>();
    private AffairAdapter affairAdapter;


    private String things = "";
    Intent intent = new Intent("STATICACTION");
    Boolean isInYours = true;

    // 网络服务地址
    private static final String url = "http://apis.baidu.com/heweather/weather/free";
    private ConnectivityManager connManager;
    private NetworkInfo networkInfo;
    private static final int UPDATE_CONTENT = 0;

    private String w = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);
        handler.post(myRunnable);

        main_list = (ListView) findViewById(R.id.main_list);
        AffairList = mydb.getAllData();  //获取全部数据，存入list中
        affairAdapter = new AffairAdapter(getApplicationContext(), AffairList);
        main_list.setAdapter(affairAdapter);

        //短按点击事件(1214)
        main_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle bundle = new Bundle();
                bundle.putString("thing", AffairList.get(position).getThing());
                bundle.putString("start", AffairList.get(position).getStart_time());
                bundle.putString("end", AffairList.get(position).getEnd_time());
                bundle.putInt("icon", AffairList.get(position).getIcon());
                if (AffairList.get(position).getCategory() == CATEGORY_FIRST) {
                    Intent intent = new Intent(MainActivity.this, AddDDL.class);
                    intent.putExtras(bundle);
                    startActivity(intent);
                } else if (AffairList.get(position).getCategory() == CATEGORY_SECOND) {
                    Intent intent = new Intent(MainActivity.this, AddTime.class);
                    intent.putExtras(bundle);
                    startActivity(intent);
                } else if (AffairList.get(position).getCategory() == CATEGORY_THIRD) {
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
                        mydb.deleteOneData(AffairList.get(position));
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


        final Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

        //广播(1214)
        Handler handler_board = new Handler();
        handler_board.postDelayed(new Runnable() {
            public void run() {
                Bundle bundle = new Bundle();
                things = "";
                if (AffairList != null) {
                    for (int i = 0; i < AffairList.size(); i++) {
                        isInYours = true;
                        if (AffairList.get(i).getCategory() != CATEGORY_THIRD)
                            isInYours = false;
                        if (!isInYours && AffairList.get(i).getProcess() >= 75) {
                            things = things + AffairList.get(i).getThing() + " ";
                        } else if (isInYours) {
                            int current = AffairList.get(i).getProcess();
                            int start = Integer.parseInt(AffairList.get(i).getStart_time());
                            int end = Integer.parseInt(AffairList.get(i).getEnd_time());
                            double progress = (current - start) / (end - start);
                            //System.out.println("1!!!!!!!!!!!!!!" + current + " + " + start + " " + end);
                            if (progress >= 0.75)
                                things = things + AffairList.get(i).getThing() + " ";
                        }
                    }
                    if (things.length() > 0) {
                        bundle.putString("things", things);
                        intent.putExtras(bundle);
                        sendBroadcast(intent);
                        vibrator.vibrate(300);
                    }
                }
            }
        }, 500);
    }

    public void setWeather() {
        connManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        networkInfo = connManager.getActiveNetworkInfo();
        if (networkInfo == null || !networkInfo.isAvailable()) {
            Toast.makeText(MainActivity.this, getResources().getString(R.string.noInternet), Toast.LENGTH_SHORT).show();
        } else {
            // 发送HTTP请求
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
                    /*HTTP请求操作*/
                    // 建立Http连接
                    Log.i("key", "Begin the connection");
                    String current_url = url + "?city=广州";
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
                    w = parserJSON(response.toString());
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
    public String parserJSON (String response) throws JSONException, IOException {
        String weather = new String();
        JSONObject jsonObject = new JSONObject(response);
        JSONArray result = jsonObject.getJSONArray("HeWeather data service 3.0");
        JSONObject jo = result.getJSONObject(0);
        String status = jo.getString("status");
        // 查询城市存在时
        if (status.contains("ok")) {
            // 天气情况描述
            weather = jo.getJSONObject("now").getJSONObject("cond").getString("txt");
            return weather;
        }
        return null;
    }

    //处理标题栏
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        setWeather();
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
            menu.findItem(R.id.weather_menu).setIcon(R.mipmap.windy);
        }
        menu.findItem(R.id.weather_menu).setTitle(w);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.plus:
                Intent intent = new Intent(MainActivity.this, AddActivity.class);
                startActivity(intent);
                mydb.close();
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

    private Handler handler = new Handler();
    private Runnable myRunnable = new Runnable() {
        public void run() {
            try {
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Calendar calendar = Calendar.getInstance();
                for (int i = 0; i < affairAdapter.getCount(); i++) {
                    if (AffairList.get(i).getCategory() == CATEGORY_FIRST) {
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
                    } else if (AffairList.get(i).getCategory() == CATEGORY_SECOND) {
                        int hour = calendar.get(Calendar.HOUR_OF_DAY);
                        int minute = calendar.get(Calendar.MINUTE);
                        String date_current = "2016-11-22 " + (hour < 10 ? "0" + hour : hour) + ":" + (minute < 10 ? "0" + minute : minute) + ":00";
                        Date date_start = format.parse("2016-11-22 " + AffairList.get(i).getStart_time() + ":00");
                        Date date_end = format.parse("2016-11-22 " + AffairList.get(i).getEnd_time() + ":00");
                        Date date_cur = format.parse(date_current);
                        int pro = differentTime(date_start, date_cur) * 100 / differentTime(date_start, date_end);
                        AffairList.get(i).setProcess(pro);
                    } else if (AffairList.get(i).getCategory() == CATEGORY_FOURTH) {
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
    }
}
