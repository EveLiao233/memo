package com.example.lenovo.at;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

/**
 * Created by lenovo on 2016/12/21.
 */
public class AddDemo extends AppCompatActivity {
    private TextView Today;
    private TextView ThisWeek;
    private TextView ThisMonth;
    private TextView ThisYear;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_demo);

        Today = (TextView)findViewById(R.id.Today);
        ThisWeek = (TextView)findViewById(R.id.ThisWeek);
        ThisMonth = (TextView)findViewById(R.id.ThisMonth);
        ThisYear = (TextView)findViewById(R.id.ThisYear);

        Today.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddDemo.this, AddSpecial.class);
                Bundle bundle = new Bundle();
                bundle.putString("thing", Today.getText().toString());
                bundle.putInt("icon", -1);
                intent.putExtras(bundle);
                startActivity(intent);
                finish();
            }
        });

        ThisWeek.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddDemo.this, AddSpecial.class);
                Bundle bundle = new Bundle();
                bundle.putString("thing", ThisWeek.getText().toString());
                bundle.putInt("icon", -1);
                intent.putExtras(bundle);
                startActivity(intent);
                finish();
            }
        });

        ThisMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddDemo.this, AddSpecial.class);
                Bundle bundle = new Bundle();
                bundle.putString("thing", ThisMonth.getText().toString());
                bundle.putInt("icon", -1);
                intent.putExtras(bundle);
                startActivity(intent);
                finish();
            }
        });

        ThisYear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddDemo.this, AddSpecial.class);
                Bundle bundle = new Bundle();
                bundle.putString("thing", ThisYear.getText().toString());
                bundle.putInt("icon", -1);
                intent.putExtras(bundle);
                startActivity(intent);
                finish();
            }
        });
    }
}
