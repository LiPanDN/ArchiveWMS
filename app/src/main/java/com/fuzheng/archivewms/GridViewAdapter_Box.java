package com.fuzheng.archivewms;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.fuzheng.archivewms.R;

import java.util.ArrayList;
import java.util.HashMap;

public class GridViewAdapter_Box  extends BaseAdapter {
    private ArrayList<HashMap<String, Object>> data;
    private Context mContext;
    private TextView tv1;
    private TextView tv2;
    private View deleteView;
    private boolean isShowDelete;// 根据这个变量来判断是否显示删除图标，true是显示，false是不显示

    public GridViewAdapter_Box(Context mContext,
                           ArrayList<HashMap<String, Object>> data) {
        this.mContext = mContext;
        // this.names=names;
        this.data = data;
    }

    public void setIsShowDelete(boolean isShowDelete) {
        this.isShowDelete = isShowDelete;
        notifyDataSetChanged();//数据更新
    }

    @Override
    public int getCount() {//获取view数量

        return data.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(mContext).inflate(R.layout.activity_ajgrid_item, null);
        tv1 = (TextView) convertView.findViewById(R.id.text_item0);
        tv2 = (TextView) convertView.findViewById(R.id.text_item1);
        deleteView = convertView.findViewById(R.id.delete_markView);
        if(position != 0) {
            deleteView.setVisibility(isShowDelete ? View.VISIBLE : View.GONE);// 设置删除按钮是否显示
        }
        tv1.setText(data.get(position).get("row").toString());
        tv2.setText(data.get(position).get("ID").toString());
        return convertView;
    }
}
