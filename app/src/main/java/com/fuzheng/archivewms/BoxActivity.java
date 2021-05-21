package com.fuzheng.archivewms;

import android.content.DialogInterface;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//boxing
public class BoxActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_box);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        BindingGridview();
    }


    public void BindingGridview()
    {
        EditText XZID=findViewById(R.id.XZID);
        XZID.setText("后台接受箱号");
        List<Map<String, Object>> items = new ArrayList<Map<String,Object>>();
        Map<String, Object> title = new HashMap<String, Object>();
        title.put("row", "序号");
        title.put("address", "案卷号");
        items.add(title);

        for (int i = 1; i < 4; i++) {
            Map<String, Object> item = new HashMap<String, Object>();
            item.put("row", i);
            item.put("address", i+"2222");
            items.add(item);
        }
        final GridView gv=findViewById(R.id.ajGrid);
        SimpleAdapter sa=new SimpleAdapter(
                BoxActivity.this, //上下文环境
                items,    //数据源
                R.layout.activity_ajgrid_item,  //内容布局
                new String[]{"row","address"},  //数据源的arrayName
                new int[]{R.id.text_item1,R.id.text_item2}  //装载数据的控件
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

                gridView中点击 item为选中状态(背景颜色)
                for(int i=0;i<parent.getCount();i++){
                    View item = goodGridView.getChildAt(i).findViewById(R.id.texdt);
                    if (position == i) {//当前选中的Item改变背景颜色
                        item.setBackgroundResource(R.drawable.yuanjiao_choice);
                    } else {
                        item.setBackgroundResource(R.drawable.yuanjiao);
                    }
                }
                */

                Button Delete=findViewById(R.id.Btn_Delete);
                Delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new AlertDialog.Builder(BoxActivity.this).setTitle("提示")
                                .setMessage("确定要删除吗？")
                                .setPositiveButton("确定",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                //删除所有视图
                                              //  gv.removeAllViews();

                                            }
                                        }
                                )
                                .setNegativeButton("取消",null)
                                .show();
                    }
                });
            }
        });
    }
}
