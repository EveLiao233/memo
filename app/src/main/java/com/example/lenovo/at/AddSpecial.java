package com.example.lenovo.at;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by lenovo on 2016/12/21.
 */
public class AddSpecial  extends AppCompatActivity {
    private static final String DB_NAME = "myDB.db";
    private static final String TABLE_NAME = "BirthNote";
    private static final int DB_VERSION = 1;
    private myDB mydb;

    private static int CATEGORY_FOURTH = 3;

    private Intent intent;
    private Bundle bl;

    private ArrayList<ImageView> myImg = new ArrayList<ImageView>();
    private int icon = 1;
    private boolean iconIsChecked = false;
    private ImageView hindIcon;
    private ScrollView icon_view;
    private RelativeLayout rela;
    
    private String Start;
    private String End;
    private String Thing;
    private int Process;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_special);
        initializeViews();

        //获取到上一个页面传过来的Intent
        intent = this.getIntent();
        //获取Intent中的Bundle数据
        bl = intent.getExtras();
        if (bl.getInt("icon") != -1) {
            icon = bl.getInt("icon");
        }
        myImg.get(icon - 1).setBackground(getResources().getDrawable(R.drawable.shadow));
    }

    /**
     * image点击事件监听器
     */
    private View.OnClickListener myImgListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            myImg.get(icon - 1).setBackgroundColor(Color.TRANSPARENT);
            switch (v.getId()) {
                case R.id.img1: icon = 1; break;
                case R.id.img2: icon = 2; break;
                case R.id.img3: icon = 3; break;
                case R.id.img4: icon = 4; break;
                case R.id.img5: icon = 5; break;
                case R.id.img6: icon = 6; break;
                case R.id.img7: icon = 7; break;
                case R.id.img8: icon = 8; break;
                case R.id.img9: icon = 9; break;
                case R.id.img10: icon = 10; break;
                case R.id.img11: icon = 11; break;
                case R.id.img12: icon = 12; break;
                case R.id.img13: icon = 13; break;
                case R.id.img14: icon = 14; break;
                case R.id.img15: icon = 15; break;
                case R.id.img16: icon = 16; break;
                case R.id.img17: icon = 17; break;
                case R.id.img18: icon = 18; break;
                case R.id.img19: icon = 19; break;
                case R.id.img20: icon = 20; break;
                case R.id.img21: icon = 21; break;
                case R.id.img22: icon = 22; break;
                case R.id.img23: icon = 23; break;
                case R.id.img24: icon = 24; break;
                case R.id.img25: icon = 25; break;
                case R.id.img26: icon = 26; break;
                case R.id.img27: icon = 27; break;
                case R.id.img28: icon = 28; break;
            }
            v.setBackground(getResources().getDrawable(R.drawable.shadow));
        }
    };

    /**
     * 初始化控件和UI视图
     */
    private void initializeViews(){
        mydb = new myDB(this, DB_NAME, null, DB_VERSION);
        

        hindIcon = (ImageView) findViewById(R.id.hindIcon);
        icon_view = (ScrollView) findViewById(R.id.icon_view);
        rela = (RelativeLayout) findViewById(R.id.rela);
        myImg.add((ImageView)findViewById(R.id.img1));
        myImg.add((ImageView)findViewById(R.id.img2));
        myImg.add((ImageView)findViewById(R.id.img3));
        myImg.add((ImageView)findViewById(R.id.img4));
        myImg.add((ImageView)findViewById(R.id.img5));
        myImg.add((ImageView)findViewById(R.id.img6));
        myImg.add((ImageView)findViewById(R.id.img7));
        myImg.add((ImageView)findViewById(R.id.img8));
        myImg.add((ImageView)findViewById(R.id.img9));
        myImg.add((ImageView)findViewById(R.id.img10));
        myImg.add((ImageView)findViewById(R.id.img11));
        myImg.add((ImageView)findViewById(R.id.img12));
        myImg.add((ImageView)findViewById(R.id.img13));
        myImg.add((ImageView)findViewById(R.id.img14));
        myImg.add((ImageView)findViewById(R.id.img15));
        myImg.add((ImageView)findViewById(R.id.img16));
        myImg.add((ImageView)findViewById(R.id.img17));
        myImg.add((ImageView)findViewById(R.id.img18));
        myImg.add((ImageView)findViewById(R.id.img19));
        myImg.add((ImageView)findViewById(R.id.img20));
        myImg.add((ImageView)findViewById(R.id.img21));
        myImg.add((ImageView)findViewById(R.id.img22));
        myImg.add((ImageView)findViewById(R.id.img23));
        myImg.add((ImageView)findViewById(R.id.img24));
        myImg.add((ImageView)findViewById(R.id.img25));
        myImg.add((ImageView)findViewById(R.id.img26));
        myImg.add((ImageView)findViewById(R.id.img27));
        myImg.add((ImageView)findViewById(R.id.img28));

        for (ImageView i : myImg)
            i.setOnClickListener(myImgListener);

        rela.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (iconIsChecked) {
                    icon_view.setVisibility(View.GONE);
                    hindIcon.setImageResource(R.mipmap.triangle_right);
                    iconIsChecked = false;
                } else {
                    icon_view.setVisibility(View.VISIBLE);
                    hindIcon.setImageResource(R.mipmap.triangle_down);
                    iconIsChecked = true;
                }
            }
        });

    }

    public int  differentTime(Date date1,Date date2) {
        return  (int)(((date2.getTime() - date1.getTime()) % (1000*3600*24)) / (1000*60));
    }
    public int differentDays(Date date1,Date date2) {
        return  (int)((date2.getTime() - date1.getTime()) / (1000*3600*24));
    }

    public int CalculatePro() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH) + 1;
            int date = calendar.get(Calendar.DATE);
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);
            int day_of_week = calendar.get(Calendar.DAY_OF_WEEK) - 1;

            System.out.println("year="+ year + "month=" + month + "date="+ date + "hour=" + hour + "minute="+ minute + "day_of_week=" + day_of_week);

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

            System.out.println("leapyear="+ leapyear + "days=" + days);

            if (bl.getString("thing").equals("今天")) {
                Thing = "今天";
                Start = "00:00";
                End = "23:59";
                String date_current = "2016-11-22 " + (hour < 10 ? "0" + hour : hour) + ":" + (minute < 10 ? "0" + minute : minute) + ":00";
                Date date_start = format.parse("2016-11-22 " + Start + ":00");
                Date date_end = format.parse("2016-11-22 " + End + ":59");
                Date date_cur = format.parse(date_current);
                System.out.println("两个时间的差距：" +  differentTime(date_start, date_end));
                System.out.println("两个时间的差距：" +  differentTime(date_start, date_cur));
                return differentTime(date_start, date_cur) * 100 / differentTime(date_start, date_end);
            } else if(bl.getString("thing").equals("本周")) {
                Thing = "本周";
                Start = "周一";
                End = "周日";
                System.out.println("day_of_week：" +  day_of_week * 100 / 7);
                return day_of_week * 100 / 7;
            }  else if(bl.getString("thing").equals("本月")) {
                Thing = "本月";
                Start = month + "月1";
                End = month + "月" + days;
                String date_current = year + "-" + ((month + 1) < 10 ? "0" + month : month) + "-"
                        + ((date < 10) ? "0" + date : date) + " 00:00:00";
                Date date_start = format.parse(year + "-" + ((month + 1) < 10 ? "0" + month : month) + "-01" + " 00:00:00");
                Date date_end = format.parse(year + "-" + ((month + 1) < 10 ? "0" + month : month) + "-" + days + " 00:00:00");
                Date date_cur = format.parse(date_current);
                return differentDays(date_start, date_cur) * 100 / differentDays(date_start, date_end);
            }  else if(bl.getString("thing").equals("今年")) {
                Thing = "今年";
                Start = "1月1";
                End = "12月31";
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

    //处理标题栏
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add:
                int pro = CalculatePro();
                if (bl.getInt("icon") != -1) {
                    mydb.updateOneData(new Affair(Thing, pro, bl.getString("start"), bl.getString("end"), CATEGORY_FOURTH, icon));
                } else {
                    if (mydb.insertOneData(new Affair(Thing, pro, Start, End, CATEGORY_FOURTH, icon))) {
                        finish();
                    } else {
                        Toast.makeText(AddSpecial.this, "事件名称重复啦，请核查", Toast.LENGTH_LONG).show();
                    }
                }
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
