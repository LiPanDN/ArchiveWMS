package com.fuzheng.archivewms;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;

import com.fuzheng.archivewms.Util.AlterDialog;
import com.fuzheng.archivewms.Util.HttpHelper;
import com.fuzheng.archivewms.Util.StringHelper;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

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

import java.util.ArrayList;
import java.util.HashMap;

//boxing
public class BoxActivity extends AppCompatActivity implements AdapterView.OnItemLongClickListener {
    public Button btnSave;
    public Button btnSavePrint;
    public String txtJsonResult;
    public  EditText XZID;
    public  EditText etBarCode;
    public  Boolean GetResult;
    private GridViewAdapter_Box adapter;
    private GridView gridView;
    private Boolean isShowDelete = false;
    private ProgressDialog progressDialog = null;//进度条
    private ArrayList<HashMap<String, Object>> data = new ArrayList<HashMap<String,Object>>();

    //箱号请求
    private String GetXZID() {
        //return "XZ-001";
        String url = HttpHelper.BASE_URL + "NewOneXZCode";
        return HttpHelper.Get(url).replace("\"", "");
    }
    //保存请求
    private String PostSave(String json, String AJCodes) {
        String url = HttpHelper.BASE_URL + "ZX?xzBarCode=" + XZID.getText() + "&ajBarcodesOrHZBarCodes=" + AJCodes;
        return HttpHelper.Post(url, null);
    }

    //检查请求
    private Boolean GetChecking(String id) {
        String url = HttpHelper.BASE_URL + "ValidateAJHZXZBarCodeIsValid?barCode=" + id;
        //return ("true".equals(HttpHelper.Get(url)));
        return true;
    }
    //传入条码添加到显示的视图集合
    private void Insert2List(String ajID) {
        HashMap<String, Object> value = new HashMap<String, Object>();
        boolean Repeat=false;
        for (int i=1;i<data.size();i++){
            if (ajID.equals(data.get(i).get("ID"))){
                Repeat=true;
                new AlertDialog.Builder(this)
                        .setTitle("输入重复")
                        .setMessage("此条形码为重复，请检查是否已录入")
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

    private class IDCheckThread extends Thread {
        private String name;

        private IDCheckThread(String name) {
            this.name = name;
        }

        public void run() {
            GetResult = GetChecking(name);
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_box);

        XZID=findViewById(R.id.XZid);
        new Thread(new Runnable() {
            @Override
            public void run() {
                XZID.setText(GetXZID());
            }
        }).start();
        btnSave = findViewById(R.id.btnSavexz);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Save();
            }
        });

        btnSavePrint = findViewById(R.id.btnSavePrintxz);
        btnSavePrint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Save();
                //待写入的打印方法
            }
        });

        etBarCode=findViewById(R.id.etBarCode);
        etBarCode.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId== EditorInfo.IME_ACTION_SEARCH){
                    System.out.println("监听扫描枪回车事件");
                    String id=v.getText().toString();
                    Thread thread=new IDCheckThread(id);
                    thread.start();
                    try{
                        thread.join();
                    }catch (InterruptedException e){

                    }
                    if (GetResult)
                        Insert2List(id);
                    else
                        AlterDialog.simple(BoxActivity.this,"条码存在问题","请检查条码" + id + "是否正确");
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
                    Thread thread = new IDCheckThread(id);
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
                        AlterDialog.simple(BoxActivity.this,"条码存在问题","请检查条码" + id + "是否正确");
                    }
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
        adapter = new GridViewAdapter_Box(
                BoxActivity.this, //上下文环境
                data  //装载数据的控件
        );
        gridView.setOnItemLongClickListener(this);//监听长按事件
        gridView.setAdapter(adapter);   //与gridview绑定
}
    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view,
                                   int position, long id) {
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
                    adapter = new GridViewAdapter_Box(BoxActivity.this, data);//重新绑定一次adapter
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
    private void Save() {
        btnSave.setClickable(false);
        btnSavePrint.setClickable(false);
        progressDialog = ProgressDialog.show(BoxActivity.this, "请稍等...", "正在保存...", true);
        Thread thread = new Thread(new Runnable() {
            public void run() {
                ArrayList<String> ss = new ArrayList<String>();
                for (int i = 0; i < data.size(); i++) {
                    if(i==0)
                        continue;
                    ss.add(data.get(i).get("ID").toString());
                }
                String str = StringHelper.StringJoin(",",ss);
                txtJsonResult = PostSave(null, str);
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
            if (txtJsonResult.indexOf("\"保存成功\"") > 0) {
                //SaveSuccess();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(BoxActivity.this, "保存成功", Toast.LENGTH_SHORT).show();
                    }
                });
            }//如果失败,Toast提示
            else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(BoxActivity.this, "保存失败:请检查网络情况", Toast.LENGTH_SHORT).show();
                        btnSave.setClickable(true);
                        btnSavePrint.setClickable(true);
                    }
                });
            }
        }
    };
}
