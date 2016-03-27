package com.jishuli.Moco;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class Adapter_ClassDetailItem extends BaseAdapter {
    public List<ClassDetailItem> classDetails;
    Context context;

    public class ViewHolder{
        TextView statusTextView = null;
        TextView dateTextView = null;
        TextView timeTextView = null;
        TextView loginNumberTextView = null;
        TextView classNameTextView = null;
        TextView agentNameTextView = null;
        TextView distanceTextView = null;
    }

    //构造函数
    public Adapter_ClassDetailItem(Context context, List<ClassDetailItem> classDetails){
        this.classDetails = classDetails;
        this.context = context;
    }

    @Override
    public int getCount() {
        int i = 0;
        if (classDetails.size() == 0){
            i = 0;
        }
        else {
            i = classDetails.size();
        }
        return i;
    }

    @Override
    public Object getItem(int position) {
        return classDetails.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ClassDetailItem classDetailItem = (ClassDetailItem)getItem(position);
        ViewHolder viewHolder = null;

        if (viewHolder == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.classdetailitem, null);

            viewHolder = new ViewHolder();
            viewHolder.statusTextView = (TextView)convertView.findViewById(R.id.statusTextView);
            viewHolder.dateTextView = (TextView)convertView.findViewById(R.id.dateTextView);
            viewHolder.timeTextView = (TextView)convertView.findViewById(R.id.timeTextView);
            viewHolder.loginNumberTextView = (TextView)convertView.findViewById(R.id.loginNumberTextView);
            viewHolder.classNameTextView = (TextView)convertView.findViewById(R.id.classNameTextView);
            viewHolder.agentNameTextView = (TextView)convertView.findViewById(R.id.agentNameTextView);
            viewHolder.distanceTextView = (TextView)convertView.findViewById(R.id.distanceTextView);

            convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder)convertView.getTag();
        }

        /*
        if (classDetailItem.status == 1){
            viewHolder.statusTextView.setText("已开课");
        }
        else {
            viewHolder.statusTextView.setText("未开课");
        }
        */

        viewHolder.dateTextView.setText(classDetailItem.beginTime + "~" + classDetailItem.endTime);
        //viewHolder.timeTextView.setText(classDetailItem.time);
        //viewHolder.loginNumberTextView.setText(classDetailItem.number + "人报名");
        viewHolder.classNameTextView.setText(classDetailItem.courseName);
        viewHolder.agentNameTextView.setText(classDetailItem.name);
        //viewHolder.distanceTextView.setText(classDetailItem.distance + "KM");

        return convertView;
    }
}
