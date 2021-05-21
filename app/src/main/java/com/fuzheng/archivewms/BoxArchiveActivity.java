package com.fuzheng.archivewms;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Button;
import android.app.ProgressDialog;
import android.widget.GridView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import android.widget.TextView;
import android.widget.ArrayAdapter;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.view.LayoutInflater;
import android.widget.BaseAdapter;
import android.view.ViewGroup;

import com.fuzheng.archivewms.Util.HttpHelper;

public class BoxArchiveActivity extends AppCompatActivity implements AdapterView.OnItemLongClickListener {
    public Button btnSave;
    public Button btnSavePrint;
    public String txtJsonResult;
    public Boolean GetResult;
    public EditText etBoxID;
    public EditText etAJID;
    private ProgressDialog progressDialog = null;
    private ArrayList<HashMap<String, Object>> data = new ArrayList<HashMap<String,Object>>();
    private GridView gridView;
    private GridViewAdapter adapter;
    private Boolean isShowDelete = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_box_archive);

        etBoxID = findViewById(R.id.etHZID);
        new Thread(new Runnable() {
            public void run() {
                etBoxID.setText(GetHZID());
            }
        }).start();

        btnSave = findViewById(R.id.btnSave);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Save();
            }
        });

        btnSavePrint = findViewById(R.id.btnSavePrint);
        btnSavePrint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Save();
                //待写入的打印方法
            }
        });

        etAJID = findViewById(R.id.etAJID);
        etAJID.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    System.out.println("这里是监听扫码枪的回车事件");
                    String ajID = v.getText().toString();
                    Thread thread = new IDCheckThread(ajID);
                    thread.start();
                    try {
                        thread.join();
                    } catch (InterruptedException e) {
                        //e.printStackTrace();
                    }
                    if (GetResult) {
                        Insert2List(ajID);
                    } else {
                        progressDialog = ProgressDialog.show(BoxArchiveActivity.this, "条码存在问题", "请检查条码" + ajID + "是否正确", true);
                    }
                    return true;
                }
                if (actionId == 5 || actionId == 6) {
                    /*隐藏软键盘*/
                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (inputMethodManager.isActive()) {
                        inputMethodManager.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);
                    }
                    System.out.println("这里是监听手机的回车事件");

                    String ajID = v.getText().toString();
                    Thread thread = new IDCheckThread(ajID);
                    thread.start();
                    try {
                        thread.join();
                    } catch (InterruptedException e) {
                        //e.printStackTrace();
                    }
                    if (GetResult) {
                        Insert2List(ajID);
                    } else {
                        progressDialog = ProgressDialog.show(BoxArchiveActivity.this, "条码存在问题", "请检查条码" + ajID + "是否正确", true);
                        progressDialog.dismiss();
                    }
                    return true;
                } else {
                    return false;
                }
            }
        });
        BindingGridview();
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view,
                                   int position, long id) {
        if(position == 0)
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
                    adapter = new GridViewAdapter(BoxArchiveActivity.this, data);//重新绑定一次adapter
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
            for (int i = 1;i<data.size();i++)
            {
                data.get(i).put("row",i);
            }
        }
        newList.addAll(data);
        data.clear();
        data.addAll(newList);
    }

    public void BindingGridview()
    {
        //表头
        HashMap<String, Object> title = new HashMap<String, Object>();
        title.put("row", "序号");
        title.put("ID", "案卷号");
        data.add(title);

        gridView =(GridView)findViewById(R.id.list_AJ);
        adapter = new GridViewAdapter(
                BoxArchiveActivity.this, //上下文环境
                data  //装载数据的控件
        );
        gridView.setOnItemLongClickListener(this);//监听长按事件
        gridView.setAdapter(adapter);   //与gridview绑定
    }




    private void Save() {
        btnSave.setClickable(false);
        btnSavePrint.setClickable(false);
        progressDialog = ProgressDialog.show(BoxArchiveActivity.this, "请稍等...", "正在保存...", true);
        progressDialog.dismiss();
        new Thread(new Runnable() {
            public void run() {
                String JsonStr = BuildJson();
                txtJsonResult = PostSave(JsonStr);
                progressDialog.dismiss();
                handlerUserInfo.sendEmptyMessage(0);
            }
        }).start();
    }

    //未完成的Json构建方法
    private String BuildJson() {
        //Gson gson = new Gson();
        return new String();
    }

    //未完成的保存请求
    private String PostSave(String json) {
        String url = HttpHelper.BASE_URL + "Validate";
        return HttpHelper.Post(url, json);
    }

    //未完成的检查请求
    private Boolean GetChecking(String id) {
        //String url = HttpHelper.BASE_URL + "Validate";
        //return ("true".equals(HttpHelper.Get(url)));
        return true;
    }

    //未完成的盒号请求
    private String GetHZID() {
        return "HZ-001";
        //String url = HttpHelper.BASE_URL + "Validate";
        //return HttpHelper.Get(url);
    }

    private void Insert2List(String ajID) {
        HashMap<String, Object> value = new HashMap<String, Object>();
        value.put("row", data.toArray().length);
        value.put("ID", ajID);
        data.add(value);
        adapter.notifyDataSetChanged();
    }

    @SuppressLint("HandlerLeak")
    Handler handlerUserInfo = new Handler() {
        public void handleMessage() {
            //以后如果做用户名密码判断就在这里判断
            if (txtJsonResult.indexOf("\"保存成功\"") > 0) {
                SaveSuccess();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(BoxArchiveActivity.this, "保存成功", Toast.LENGTH_SHORT).show();
                    }
                });
            }//如果失败,Toast提示
            else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(BoxArchiveActivity.this, "保存失败:请检查网络情况", Toast.LENGTH_SHORT).show();
                        btnSave.setClickable(true);
                        btnSavePrint.setClickable(true);
                    }
                });
            }
        }
    };

    public void SaveSuccess() {

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
}
