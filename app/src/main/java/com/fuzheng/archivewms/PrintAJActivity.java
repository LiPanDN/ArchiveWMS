package com.fuzheng.archivewms;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;

import com.fuzheng.archivewms.Util.AlterDialog;
import com.fuzheng.archivewms.Util.HttpHelper;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

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

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONException;
//import org.json.JSONObject;
//import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class PrintAJActivity extends AppCompatActivity implements AdapterView.OnItemLongClickListener {
    public Button btnSave;
    public Button btnPrint;
    public String txtJsonResult;
    public Boolean GetResult;
    public EditText etAJID;
    private ProgressDialog progressDialog = null;
    private ArrayList<HashMap<String, Object>> data = new ArrayList<HashMap<String, Object>>();
    private GridView gridView;
    private DynamicGridViewAdapter adapter;
    private Boolean isShowDelete = false;
    private Button btnSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_print_aj);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        btnPrint = findViewById(R.id.btnPrint);
        btnPrint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                PrintAll();
            }
        });

        btnSearch = findViewById(R.id.btnSearch);
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Thread thread = new PrintAJActivity.AJSearcher();
                thread.start();
                try {
                    thread.join();
                } catch (InterruptedException e) {
                    //e.printStackTrace();
                }
                BuildList();
            }
        });

        etAJID = findViewById(R.id.etAJID);
        etAJID.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    System.out.println("???????????????????????????????????????");
                    String ajID = v.getText().toString();
                    Thread thread = new PrintAJActivity.IDCheckThread(ajID);
                    thread.start();
                    try {
                        thread.join();
                    } catch (InterruptedException e) {
                        //e.printStackTrace();
                    }
                    if (GetResult) {
                        Insert2List(ajID);
                    } else {
                        //progressDialog = ProgressDialog.show(BoxArchiveActivity.this, "??????????????????", "???????????????" + ajID + "????????????", true);
                        AlterDialog.simple(PrintAJActivity.this, "??????????????????", "???????????????" + ajID + "????????????");
                    }
                    return true;
                }
                if (actionId == 5 || actionId == 6) {
                    /*???????????????*/
                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (inputMethodManager.isActive()) {
                        inputMethodManager.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);
                    }
                    System.out.println("????????????????????????????????????");

                    String ajID = v.getText().toString();
                    Thread thread = new PrintAJActivity.IDCheckThread(ajID);
                    thread.start();
                    try {
                        thread.join();
                    } catch (InterruptedException e) {
                        //e.printStackTrace();
                    }
                    if (GetResult) {
                        Insert2List(ajID);
                    } else {
                        //progressDialog = ProgressDialog.show(BoxArchiveActivity.this, "??????????????????", "???????????????" + ajID + "????????????", true);
                        AlterDialog.simple(PrintAJActivity.this, "??????????????????", "???????????????" + ajID + "????????????");
                    }
                    return true;
                } else {
                    return false;
                }
            }
        });
        BindingGridview();
    }

    public void PrintAll() {

    }

    public void BuildList() {
        try {
            JSONArray a = JSONArray.parseArray(txtJsonResult);
            if(a.equals(null))
                return;
            JSONObject jsonObject = a.getJSONObject(0);
            Map param = new HashMap();
            ArrayList<String> keys = new ArrayList<String>();
            for (Map.Entry entry : jsonObject.entrySet()) {
                String key = entry.getKey().toString();
                keys.add(key);
            }
            HashMap<String, Object> title = new HashMap<String, Object>();
            title.put("row", "??????");
            for (int i = 0; i < keys.size(); i++) {
                title.put("c" + i, keys.get(i));
            }
            data.add(title);
            adapter.notifyDataSetChanged();

            for (int i = 0; i < a.size(); i++) {
                JSONObject job = a.getJSONObject(i);
                HashMap<String, Object> value = new HashMap<String, Object>();
                value.put("row", data.toArray().length);
                for (int j = 0; j < keys.size(); j++) {
                    value.put("c" + j, job.get(keys.get(j)));
                }
                data.add(value);
                adapter.notifyDataSetChanged();
            }
        } catch (JSONException e) {

        }
    }

    public String PostForAJList(String AJID) {
        String url = HttpHelper.BASE_URL + "GetAJSearchResults?ajNo=" + AJID;
        return HttpHelper.Post(url, null);
    }

    public HashMap<String, Object> AnalysisJson(String Json) {
                HashMap<String, Object> map = new HashMap<String, Object>();

        //JSONObject jsonObject = new JSONObject(Json);
        return map;
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view,
                                   int position, long id) {
        if (position == 0)
            return false;
        System.out.println("????????????");
        if (isShowDelete) {
            isShowDelete = false;

        } else {
            isShowDelete = true;
            adapter.setIsShowDelete(isShowDelete);
            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                    delete(position);//???????????????
                    System.out.println("????????????");
                  //  adapter = new GridViewAdapter(PrintAJActivity.this, data);//??????????????????adapter
                    gridView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();//??????gridview

                }

            });
        }

        adapter.setIsShowDelete(isShowDelete);//setIsShowDelete()??????????????????isShowDelete???

        return true;
    }

    private void delete(int position) {//?????????????????????
        ArrayList<HashMap<String, Object>> newList = new ArrayList<HashMap<String, Object>>();
        if (isShowDelete) {
            data.remove(position);
            isShowDelete = false;
            //????????????
            for (int i = 1; i < data.size(); i++) {
                data.get(i).put("row", i);
            }
        }
        newList.addAll(data);
        data.clear();
        data.addAll(newList);
    }

    public void BindingGridview() {
        //??????

        gridView = (GridView) findViewById(R.id.list_AJ);
      /*  adapter = new GridViewAdapter(
                PrintAJActivity.this, //???????????????
                data  //?????????????????????
        );*/
        gridView.setOnItemLongClickListener(this);//??????????????????
        gridView.setAdapter(adapter);   //???gridview??????
    }

    //????????????Json????????????
    private String BuildJson() {
        //Gson gson = new Gson();
        return new String();
    }

    //AJ????????????
    private Boolean GetChecking(String id) {
        String url = HttpHelper.BASE_URL + "ValidateAJHZXZBarCodeIsValid?barCode=" + id;
        //return ("true".equals(HttpHelper.Get(url)));
        return true;
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
            //??????????????????????????????????????????????????????
            if (txtJsonResult.indexOf("\"????????????\"") > 0) {
                SaveSuccess();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(PrintAJActivity.this, "????????????", Toast.LENGTH_SHORT).show();
                    }
                });
            }//????????????,Toast??????
            else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(PrintAJActivity.this, "????????????:?????????????????????", Toast.LENGTH_SHORT).show();
                        btnSave.setClickable(true);
                        btnPrint.setClickable(true);
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

    private class AJSearcher extends Thread {
        public void run() {
            String JsonResult = PostForAJList(etAJID.getText().toString());
            txtJsonResult = JsonResult;
        }
    }

}
