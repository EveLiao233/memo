package com.example.lenovo.at;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ListView;
import android.widget.RelativeLayout;
import java.util.List;
import java.util.Map;


/**
 * Created by lenovo on 2016/12/8.
 */

public class AddActivity extends AppCompatActivity {
    private ListView add_list;
    List<Map<String, Object>> list_item;
    RelativeLayout date, time, in_yours, eg;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_layout);

        date = (RelativeLayout) findViewById(R.id.date_button);
        time = (RelativeLayout) findViewById(R.id.time_button);
        in_yours = (RelativeLayout) findViewById(R.id.in_yours_button);
        eg = (RelativeLayout) findViewById(R.id.eg_button);

        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddActivity.this, AddDDL.class);
                startActivity(intent);
                finish();
            }
        });

        time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddActivity.this, AddTime.class);
                startActivity(intent);
                finish();
            }
        });

        in_yours.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddActivity.this, AddInYours.class);
                startActivity(intent);
                finish();
            }
        });

        eg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddActivity.this, AddDemo.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
