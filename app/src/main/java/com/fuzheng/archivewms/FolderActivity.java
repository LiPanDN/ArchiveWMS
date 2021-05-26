package com.fuzheng.archivewms;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FolderActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_folder);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        BindingGridview();
    }

    public void BindingGridview()
    {
        List<Map<String, Object>> items = new ArrayList<Map<String,Object>>();
        Map<String, Object> title = new HashMap<String, Object>();
        title.put("row", "行号");
        title.put("name", "题名");
        title.put("address", "案卷号");
        items.add(title);

        for (int i = 0; i < 10; i++) {
            Map<String, Object> item = new HashMap<String, Object>();
            item.put("row", i);
            item.put("name", "1111");
            item.put("address", "2222");
            items.add(item);
        }
        GridView gv=(GridView)findViewById(R.id.ajGrid);
        SimpleAdapter sa=new SimpleAdapter(
                FolderActivity.this, //上下文环境
                items,    //数据源
                R.layout.activity_ajgrid_item,  //内容布局
                new String[]{"row","name", "address"},  //数据源的arrayName
                new int[]{R.id.text_item0,R.id.text_item0, R.id.text_item1}  //装载数据的控件
        );
        gv.setAdapter(sa);   //与gridview绑定

        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // 获取item对应的值

                String str = "ddd";
                TextView txtView = findViewById(R.id.etBarCode);
                txtView.setText(str+position);
                //System.out.println("商品名称" +  position);
               /* CharSequence goods = ((TextView) gv.getChildAt(position).findViewById(R.id.texdt)).getText();
                System.out.println("商品名称" + goods.toString() + position);

                // gridView中点击 item为选中状态(背景颜色)
                for(int i=0;i<parent.getCount();i++){
                    View item = goodGridView.getChildAt(i).findViewById(R.id.texdt);
                    if (position == i) {//当前选中的Item改变背景颜色
                        item.setBackgroundResource(R.drawable.yuanjiao_choice);
                    } else {
                        item.setBackgroundResource(R.drawable.yuanjiao);
                    }
                }
                */

            }
        });
    }
}
