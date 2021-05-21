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

public class BoxArchiveActivity extends AppCompatActivity {
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
                if (actionId == 5) {
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

    public void BindingGridview()
    {
        HashMap<String, Object> title = new HashMap<String, Object>();
        title.put("row", "行号");
        title.put("address", "案卷号");
        data.add(title);

        gridView =(GridView)findViewById(R.id.list_AJ);
        adapter = new GridViewAdapter(
                BoxArchiveActivity.this, //上下文环境
                data  //装载数据的控件
        );
        gridView.setAdapter(adapter);   //与gridview绑定

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // 获取item对应的值
/*
                //String str = "ddd";
                //TextView txtView = findViewById(R.id.etBarCode);
                //txtView.setText(str+position);
                System.out.println("商品名称" +  position);
               CharSequence goods = ((TextView) gridView.getChildAt(position).findViewById(R.id.texdt)).getText();
                System.out.println("商品名称" + goods.toString() + position);

                //gridView中点击 item为选中状态(背景颜色)
                for(int i=0;i<parent.getCount();i++){
                    View item = gridView.getChildAt(i).findViewById(R.id.texdt);
                    if (position == i) {//当前选中的Item改变背景颜色
                        item.setBackgroundResource(R.color.zxing_custom_possible_result_points);
                    }
                }

*/
            }
        });
    }

    private class GridViewAdapter extends BaseAdapter {
        private ArrayList<HashMap<String, Object>> data;
        private Context mContext;
        private TextView tv1;
        private TextView tv2;
        private View deleteView;
        private boolean isShowDelete;// 根据这个变量来判断是否显示删除图标，true是显示，false是不显示

        public GridViewAdapter(Context mContext,
                               ArrayList<HashMap<String, Object>> data) {
            this.mContext = mContext;
            // this.names=names;
            this.data = data;
        }

        public void setIsShowDelete(boolean isShowDelete) {
            this.isShowDelete = isShowDelete;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {

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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.activity_subitem_gridview, null);
            tv1 = (TextView) convertView.findViewById(R.id.text_item0);
            tv2 = (TextView) convertView.findViewById(R.id.text_item1);
            deleteView = convertView.findViewById(R.id.delete_markView);
            deleteView.setVisibility(isShowDelete ? View.VISIBLE : View.GONE);// 设置删除按钮是否显示
            tv1.setText(data.get(position).get("row").toString());
            tv2.setText(data.get(position).get("address").toString());
            return convertView;
        }
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
        value.put("address", ajID);
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
