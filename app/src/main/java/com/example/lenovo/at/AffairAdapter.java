package com.example.lenovo.at;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.cpiz.android.bubbleview.BubbleTextView;

import java.util.List;

/**
 * Created by lenovo on 2016/12/8.
 */
public class AffairAdapter extends BaseAdapter {
    private List<Affair> list;
    private Context context;
    private static int CATEGORY_FIRST = 0;
    private static int CATEGORY_SECOND = 1;
    private static int CATEGORY_THIRD = 2;
    private static int CATEGORY_FOURTH = 3;

    private int[] color = new int[]{Color.rgb(255,153,153), Color.rgb(204,166,166),
            Color.rgb(153,179,179), Color.rgb(102,191,191), Color.rgb(153,179,179),  Color.rgb(204,166,166)};
    private int[] resID = new int[] {R.mipmap.pokemon1, R.mipmap.pokemon2, R.mipmap.pokemon3, R.mipmap.pokemon4,
            R.mipmap.pokemon5, R.mipmap.pokemon6, R.mipmap.pokemon7, R.mipmap.pokemon8,
            R.mipmap.xmas5, R.mipmap.xmas6, R.mipmap.xmas7, R.mipmap.xmas8,
            R.mipmap.xmas1, R.mipmap.xmas2, R.mipmap.xmas3, R.mipmap.xmas4,
            R.mipmap.icon1, R.mipmap.icon2, R.mipmap.icon3, R.mipmap.icon4,
            R.mipmap.animal1, R.mipmap.animal2, R.mipmap.animal3, R.mipmap.animal4,
            R.mipmap.halloween1, R.mipmap.halloween2, R.mipmap.halloween3, R.mipmap.halloween4};

    public AffairAdapter (Context context, List<Affair> list) {
        this.list = list;
        this.context = context;
    }

    @Override
    public int getCount() {
        if (list == null)
            return 0;
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        if (list == null)
            return null;
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private class ViewHolder {
        public TextView thing;
        public ProgressBar process_bar;
        public TextView start_time;
        public TextView end_time;
        public TextView progress;
        public ImageView img;
        public BubbleTextView bbl;
        public LinearLayout bkg;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View convertView;
        ViewHolder viewHolder;
        if(view == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.listitem_main, null);
            viewHolder = new ViewHolder();
            viewHolder.thing = (TextView) convertView.findViewById(R.id.thing);
            viewHolder.process_bar = (ProgressBar) convertView.findViewById(R.id.process_bar);
            viewHolder.start_time = (TextView) convertView.findViewById(R.id.start_time);
            viewHolder.end_time = (TextView) convertView.findViewById(R.id.end_time);
            viewHolder.progress = (TextView) convertView.findViewById(R.id.progress);

            viewHolder.img = (ImageView) convertView.findViewById(R.id.img);
            viewHolder.bbl = (BubbleTextView) convertView.findViewById(R.id.bb1);
            viewHolder.bkg = (LinearLayout) convertView.findViewById(R.id.bkg);
            convertView.setTag(viewHolder);
        } else {
            convertView = view;
            viewHolder = (ViewHolder)convertView.getTag();
        }
        viewHolder.thing.setText(list.get(i).getThing());
        if (list.get(i).getCategory() == CATEGORY_THIRD) {
            int pro = (list.get(i).getProcess() - Integer.parseInt(list.get(i).getStart_time())) * 100
                    / (Integer.parseInt(list.get(i).getEnd_time()) - Integer.parseInt(list.get(i).getStart_time()));
            viewHolder.process_bar.setProgress(pro);
        } else {
            viewHolder.process_bar.setProgress(list.get(i).getProcess());
        }
        String a = viewHolder.process_bar.getProgress() + "%";
        viewHolder.progress.setText(a);
        if (list.get(i).getCategory() == CATEGORY_FIRST) {
            //设置开始时间和结束时间的主界面显示
            String start = list.get(i).getStart_time().split("-")[1] + "月"
                    + list.get(i).getStart_time().split("-")[2];
            viewHolder.start_time.setText(start);
            String end = list.get(i).getEnd_time().split("-")[1] + "月"
                    + list.get(i).getEnd_time().split("-")[2];
            viewHolder.end_time.setText(end);
        } else {
            //设置开始时间和结束时间的主界面显示
            viewHolder.start_time.setText(list.get(i).getStart_time());
            viewHolder.end_time.setText(list.get(i).getEnd_time());
        }

        viewHolder.img.setImageResource(resID[list.get(i).getIcon() - 1]);
        viewHolder.img.setX(viewHolder.process_bar.getX() + viewHolder.process_bar.getProgress() * viewHolder.process_bar.getWidth() / 100 - viewHolder.img.getWidth() / 2);
        viewHolder.bbl.setX(viewHolder.img.getX());
        viewHolder.bbl.setText(viewHolder.process_bar.getProgress() + "%");
        viewHolder.bkg.setBackgroundColor(color[i % color.length]);
        return convertView;
    }
}
