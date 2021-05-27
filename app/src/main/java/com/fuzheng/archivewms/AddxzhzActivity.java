package com.fuzheng.archivewms;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.content.Context;
import android.view.inputmethod.EditorInfo;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.fuzheng.archivewms.Util.AlterDialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddxzhzActivity extends AppCompatActivity {
    public EditText RQ;
    public EditText BarCode;
    List<Map<String, Object>> data = new ArrayList<Map<String,Object>>();
    private GridView gridView;
    public SimpleAdapter sa;
    public  Boolean GetResult;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addxzhz);
        RQ=findViewById(R.id.XZid);//容器
        RQ.setOnEditorActionListener(new TextView.OnEditorActionListener() {//回车事件
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId== EditorInfo.IME_ACTION_SEARCH){
                    System.out.println("监听扫描枪回车事件");
                    String id=v.getText().toString();
              /*      Thread thread=new IDCheckThread(id);
                    thread.start();
                    try{
                        thread.join();
                    }catch (InterruptedException e){

                    }*/
                    return  true;
                }
                if(actionId==5||actionId==6){
                    /*隐藏软键盘*/
                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (inputMethodManager.isActive()) {
                        inputMethodManager.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);
                    }
                    System.out.println("这里是监听手机的回车事件");

                    String id = v.getText().toString();
                  /*  //新建线程访问
                    Thread thread = new IDCheckThread(id);
                    thread.start();
                    try {
                        thread.join();
                    } catch (InterruptedException e) {
                        //e.printStackTrace();
                    }*/
                    return true;
                } else {
                    return false;}
            }
        });
        BarCode=findViewById(R.id.etBarCode);//条码
        BarCode.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId== EditorInfo.IME_ACTION_SEARCH){
                    System.out.println("监听扫描枪回车事件");
                    String id=v.getText().toString();
                 /*   Thread thread=new BoxActivity.IDCheckThread(id);
                    thread.start();
                    try{
                        thread.join();
                    }catch (InterruptedException e){

                    }
                    if (GetResult)
                        Insert2List(id);
                    else
                        AlterDialog.simple(AddxzhzActivity.this,"条码存在问题","请检查条码" + id + "是否正确");*/
                    return  true;
                }
                if(actionId==5||actionId==6){
                    /*隐藏软键盘*/
                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (inputMethodManager.isActive()) {
                        inputMethodManager.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);
                    }
                    System.out.println("这里是监听手机的回车事件");

                    String id = v.getText().toString();
                    //新建线程访问
                /*    Thread thread = new BoxActivity.IDCheckThread(id);
                    thread.start();
                    try {
                        thread.join();
                    } catch (InterruptedException e) {
                        //e.printStackTrace();
                    }
                    if (GetResult) {
                        Insert2List(id);
                    } else {
                        //progressDialog = ProgressDialog.show(BoxArchiveActivity.this, "条码存在问题", "请检查条码" + ajID + "是否正确", true);
                        AlterDialog.simple(AddxzhzActivity.this,"条码存在问题","请检查条码" + id + "是否正确");
                    }*/
                    return true;
                } else {
                    return false;}
            }
        });
        BindingGridview();
    }
    public  void BindingGridview()
    {
        HashMap<String, Object> title = new HashMap<String, Object>();
        title.put("row", "序号");
        title.put("ID", "案卷号");
        data.add(title);

        gridView =(GridView)findViewById(R.id.ajGrid);
          sa=new SimpleAdapter(
                AddxzhzActivity.this, //上下文环境
                data,    //数据源
                R.layout.activity_ajgrid_item,  //内容布局
                new String[]{"row","ID"},  //数据源的arrayName
                new int[]{R.id.text_item0,R.id.text_item1}  //装载数据的控件
        );
        gridView.setAdapter(sa);   //与gridview绑定
    }
    //服务器获取视图添加到显示的视图集合
    private void Insert2List(String ajID) {
        HashMap<String, Object> value = new HashMap<String, Object>();
            value.put("row", data.toArray().length);
            value.put("ID", ajID);
            data.add(value);
            //添加之后进行更新
            sa.notifyDataSetChanged();

    }
}

