package com.fuzheng.archivewms;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.fuzheng.archivewms.Util.AlterDialog;
import com.fuzheng.archivewms.Util.HttpHelper;
import com.fuzheng.archivewms.Util.StringHelper;

import java.util.ArrayList;
import java.util.HashMap;

public class DataoutActivity extends AppCompatActivity implements AdapterView.OnItemLongClickListener {
    private ArrayList<HashMap<String, Object>> data = new ArrayList<HashMap<String,Object>>();
    private GridViewAdapter_Box adapter;
    private GridView gridView;
    public Button btnOut;
    public String txtJsonResult;
    private ProgressDialog progressDialog = null;//进度条
    public EditText etBarCode;
    private Boolean isShowDelete = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dataout);

        btnOut=findViewById(R.id.btnSavePrintxz);
        btnOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Out();
            }
        });
        etBarCode=findViewById(R.id.etBarCode);
        etBarCode.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId== EditorInfo.IME_ACTION_SEARCH){
                    System.out.println("监听扫描枪回车事件");
                    String id=v.getText().toString();
                        Insert2List(id);
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

                        Insert2List(id);
                    return true;
                } else {
                    return false;}
            }
        });
        BindingGridview();
    }
    //传入条码添加到显示的视图集合
    private void Insert2List(String ajID) {
        HashMap<String, Object> value = new HashMap<String, Object>();
        boolean Repeat=false;
        for (int i=1;i<data.size();i++){
            if (ajID.equals(data.get(i).get("ID"))){
                Repeat=true;
                new AlertDialog.Builder(this)
                        .setTitle("出库重复")
                        .setMessage("此条形码为已经在出库列表中了")
                        .setPositiveButton("确定", null)
                        .show();
                break;
            }
        }if (Repeat==false){
            value.put("row", data.toArray().length);
            value.put("ID", ajID);
            data.add(value);
            //添加之后进行更新
            adapter.notifyDataSetChanged();
        }

    }
    public void BindingGridview()
    {
        HashMap<String, Object> title = new HashMap<String, Object>();
        title.put("row", "序号");
        title.put("ID", "条码");
        data.add(title);

        gridView =(GridView)findViewById(R.id.ajGrid);
        adapter = new GridViewAdapter_Box(
                DataoutActivity.this, //上下文环境
                data  //装载数据的控件
        );
        gridView.setOnItemLongClickListener(this);//监听长按事件
        gridView.setAdapter(adapter);   //与gridview绑定
    }
    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        if (position == 0)
            return false;
        System.out.println("长按事件");
        if (isShowDelete) {
            isShowDelete = false;

        } else {
            isShowDelete = true;
            adapter.setIsShowDelete(isShowDelete);
            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                    delete(position);//删除选中项
                    System.out.println("删除事件");
                    adapter = new GridViewAdapter_Box(DataoutActivity.this, data);//重新绑定一次adapter
                    gridView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();//刷新gridview

                }

            });
        }
        adapter.setIsShowDelete(isShowDelete);//setIsShowDelete()方法用于传递isShowDelete值

        return true;
    }
    private void delete(int position) {//删除选中项方法
        ArrayList<HashMap<String, Object>> newList = new ArrayList<HashMap<String, Object>>();
        if (isShowDelete) {
            data.remove(position);
            isShowDelete = false;
            //更新序号
            for (int i = 1; i < data.size(); i++) {
                data.get(i).put("row", i);
            }
        }
        newList.addAll(data);
        data.clear();
        data.addAll(newList);
    }
    private void Out() {
        btnOut.setClickable(false);
        progressDialog = ProgressDialog.show(DataoutActivity.this, "请稍等...", "正在出库...", true);
        Thread thread = new Thread(new Runnable() {
            public void run() {
                ArrayList<String> ss = new ArrayList<String>();
                for (int i = 0; i < data.size(); i++) {
                    if(i==0)
                        continue;
                    ss.add(data.get(i).get("ID").toString());
                }
                String str = StringHelper.StringJoin(",",ss);
                txtJsonResult = PostCK(null, str);
                handlerUserInfo.sendEmptyMessage(0);
            }
        });
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            //e.printStackTrace();
        }
        progressDialog.dismiss();
    }
    @SuppressLint("HandlerLeak")//在主线程用Handler处理消息出现时会有警告，提示你，这块有内存泄露的危险
            Handler handlerUserInfo = new Handler(){
        public void handleMessage() {
            if (txtJsonResult.indexOf("\"true\"") > 0) {
                //SaveSuccess();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(DataoutActivity.this, "出库成功", Toast.LENGTH_SHORT).show();
                    }
                });
            }//如果失败,Toast提示
            else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(DataoutActivity.this, "出库失败:请检查网络情况", Toast.LENGTH_SHORT).show();
                        btnOut.setClickable(true);
                    }
                });
            }
        }
    };
    //出库请求
    private String PostCK(String json, String AJCodes) {
        String url = HttpHelper.BASE_URL + "RemoveXZHZFromWareHouse?hzBarCodeOrXzBarCodes=" + AJCodes ;
        //String url = HttpHelper.BASE_URL + "ZX?xzBarCode=" + XZID.getText() + "&ajBarcodesOrHZBarCodes=" + AJCodes;
        return HttpHelper.Post(url, null);
    }




}
