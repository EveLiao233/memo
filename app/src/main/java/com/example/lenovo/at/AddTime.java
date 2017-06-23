package com.example.lenovo.at;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by lenovo on 2016/12/11.
 */
public class AddTime extends AppCompatActivity {
    private static final String DB_NAME = "myDB.db";
    private static final String TABLE_NAME = "BirthNote";
    private static final int DB_VERSION = 1;
    private myDB mydb;

    private static int CATEGORY_SECOND = 1;

    private Button pick_startTime = null;
    private Button pick_endTime = null;
    private EditText thing_time;
    private Intent intent;
    private Bundle bl;

    private ArrayList<ImageView> myImg = new ArrayList<ImageView>();
    private int icon = 1;
    private boolean iconIsChecked = false;
    private ImageView hindIcon;
    private ScrollView icon_view;
    private RelativeLayout rela;

    private static final int START_TIMEPICK = 0;
    private static final int START_TIME_DIALOG = 1;
    private static final int END_TIMEPICK = 2;
    private static final int END_TIME_DIALOG = 3;

    private int mHour;
    private int mMinute;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_time);
        initializeViews();

        //获取到上一个页面传过来的Intent
        intent = this.getIntent();
        //获取Intent中的Bundle数据
        bl = intent.getExtras();
        if (bl != null) {
            //thing_time.setVisibility(View.GONE);
            pick_startTime.setText(bl.getString("start"));
            pick_endTime.setText(bl.getString("end"));
            thing_time.setText(bl.getString("thing"));
            thing_time.setEnabled(false);
            icon = bl.getInt("icon");
        }

        final Calendar c = Calendar.getInstance();
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);
        myImg.get(icon - 1).setBackground(getResources().getDrawable(R.drawable.shadow));
    }

    public int  differentTime(Date date1,Date date2) {
        return  (int)(((date2.getTime() - date1.getTime()) % (1000*3600*24)) / (1000*60));
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
        pick_startTime = (Button)findViewById(R.id.pick_startTime);
        pick_endTime = (Button)findViewById(R.id.pick_endTime);
        thing_time = (EditText)findViewById(R.id.thing_time);
        mydb = new myDB(this, DB_NAME, null, DB_VERSION);

        pick_startTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Message msg = new Message();
                if (pick_startTime.equals((Button) v)) {
                    msg.what = AddTime.START_TIMEPICK;
                }
                AddTime.this.dateandtimeHandler.sendMessage(msg);
            }
        });

        pick_endTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Message msg = new Message();
                if (pick_endTime.equals((Button) v)) {
                    msg.what = AddTime.END_TIMEPICK;
                }
                AddTime.this.dateandtimeHandler.sendMessage(msg);
            }
        });

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

    /**
     * 更新时间显示
     */
    private void StartTimeDisplay(){
        pick_startTime.setText(new StringBuilder().append((mHour < 10) ? "0" + mHour : mHour).append(":")
                .append((mMinute < 10) ? "0" + mMinute : mMinute));
    }

    /**
     * 时间控件事件
     */
    private TimePickerDialog.OnTimeSetListener StartTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            mHour = hourOfDay;
            mMinute = minute;
            StartTimeDisplay();
        }
    };

    private void EndTimeDisplay(){
        pick_endTime.setText(new StringBuilder().append((mHour < 10) ? "0" + mHour : mHour).append(":")
                .append((mMinute < 10) ? "0" + mMinute : mMinute));
    }

    /**
     * 时间控件事件
     */
    private TimePickerDialog.OnTimeSetListener EndTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            mHour = hourOfDay;
            mMinute = minute;
            EndTimeDisplay();
        }
    };

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case START_TIME_DIALOG:
                return new TimePickerDialog(this, StartTimeSetListener, mHour, mMinute, true);
            case END_TIME_DIALOG:
                return new TimePickerDialog(this, EndTimeSetListener, mHour, mMinute, true);
            default:
                break;
        }

        return null;
    }

    @Override
    protected void onPrepareDialog(int id, Dialog dialog) {
        switch (id) {
            case START_TIME_DIALOG:
            case END_TIME_DIALOG:
                ((TimePickerDialog) dialog).updateTime(mHour, mMinute);
                break;
            default:
                break;
        }
    }

    /**
     * 处理日期和时间控件的Handler
     */
    Handler dateandtimeHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case AddTime.START_TIMEPICK:
                    showDialog(START_TIME_DIALOG);
                    break;
                case AddTime.END_TIMEPICK:
                    showDialog(END_TIME_DIALOG);
                    break;
                default:
                    break;
            }
        }

    };

    public int CalculatePro() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if ((pick_startTime.getText() != null) && (pick_endTime.getText() != null)) {
            try
            {
                Calendar calendar = Calendar.getInstance();
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minute = calendar.get(Calendar.MINUTE);
                String date_current = "2016-11-22 " + (hour < 10 ? "0" + hour : hour) + ":" + (minute < 10 ? "0" + minute : minute) + ":00";
                Date date_start = format.parse("2016-11-22 " + pick_startTime.getText().toString() + ":00");
                Date date_end = format.parse("2016-11-22 " + pick_endTime.getText().toString() + ":00");
                Date date_cur = format.parse(date_current);
                System.out.println("两个时间的差距：" +  differentTime(date_start, date_end));
                System.out.println("两个时间的差距：" +  differentTime(date_start, date_cur));
                return differentTime(date_start, date_cur) * 100 /  differentTime(date_start, date_end);
            } catch (ParseException e) {
                e.printStackTrace();
            }
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
                if(bl != null) {
                    int pro = CalculatePro();
                    mydb.updateOneData(new Affair(bl.getString("thing"), pro, pick_startTime.getText().toString(),
                            pick_endTime.getText().toString(), CATEGORY_SECOND, icon));
                    finish();
                } else {
                    if (thing_time.getText().toString().isEmpty()) {
                        Toast.makeText(AddTime.this, "事件名称为空，请完善", Toast.LENGTH_LONG).show();
                    } else if (pick_endTime.getText().toString().contains("选择结束时间")
                            || pick_endTime.getText().toString().contains("选择开始时间")){
                        Toast.makeText(AddTime.this, "请输入时间", Toast.LENGTH_LONG).show();
                    } else if(str2timeStamp(pick_endTime.getText().toString()) <= str2timeStamp(pick_startTime.getText().toString())) {
                        Toast.makeText(AddTime.this, "结束时间应在开始时间之后", Toast.LENGTH_LONG).show();
                    } else {
                        int pro = CalculatePro();
                        if (mydb.insertOneData(new Affair(thing_time.getText().toString(), pro, pick_startTime.getText().toString(),
                                pick_endTime.getText().toString(), CATEGORY_SECOND, icon))) {
                            finish();
                        } else {
                            Toast.makeText(AddTime.this, "事件名称重复啦，请核查", Toast.LENGTH_LONG).show();
                        }
                    }
                    break;
                }
        }
        return super.onOptionsItemSelected(item);
    }
}
