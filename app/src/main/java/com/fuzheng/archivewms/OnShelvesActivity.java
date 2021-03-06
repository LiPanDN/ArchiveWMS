package com.fuzheng.archivewms;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;

import com.fuzheng.archivewms.Util.AlterDialog;
import com.fuzheng.archivewms.Util.HttpHelper;
import com.fuzheng.archivewms.Util.StringHelper;

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



public class OnShelvesActivity extends AppCompatActivity implements AdapterView.OnItemLongClickListener {

    public Button btnSave;
    public Button btnSavePrint;
    public String txtJsonResult;
    public Boolean GetResult;
    public EditText etLibID;
    public EditText etAJID;
    private ProgressDialog progressDialog = null;
    private ArrayList<HashMap<String, Object>> data = new ArrayList<HashMap<String, Object>>();
    private GridView gridView;
    private GridViewAdapter adapter;
    private Boolean isShowDelete = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_box_archive);

//        etLibID = findViewById(R.id.etLBID);
//        Thread thread = new Thread(new Runnable() {
//            public void run() {
//                etLibID.setText(GetHZID());
//            }
//        });
//        thread.start();
//        try {
//            thread.join();
//        } catch (InterruptedException e) {
//            //e.printStackTrace();
//        }

        btnSave = findViewById(R.id.btnSave);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Save();
            }
        });

        btnSavePrint = findViewById(R.id.btnPrint);
        btnSavePrint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Save();
                //????????????????????????
            }
        });

        etAJID = findViewById(R.id.etAJID);
        etAJID.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    System.out.println("???????????????????????????????????????");
                    String ajID = v.getText().toString();
                    Thread thread = new OnShelvesActivity.IDCheckThread(ajID);
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
                        AlterDialog.simple(OnShelvesActivity.this, "??????????????????", "???????????????" + ajID + "????????????");
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
                    Thread thread = new OnShelvesActivity.IDCheckThread(ajID);
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
                        AlterDialog.simple(OnShelvesActivity.this, "??????????????????", "???????????????" + ajID + "????????????");
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
                    adapter = new GridViewAdapter(OnShelvesActivity.this, data);//??????????????????adapter
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
        HashMap<String, Object> title = new HashMap<String, Object>();
        title.put("row", "??????");
        title.put("ID", "??????");
        data.add(title);

        gridView = (GridView) findViewById(R.id.list_AJ);
        adapter = new GridViewAdapter(
                OnShelvesActivity.this, //???????????????
                data  //?????????????????????
        );
        gridView.setOnItemLongClickListener(this);//??????????????????
        gridView.setAdapter(adapter);   //???gridview??????
    }


    private void Save() {
        btnSave.setClickable(false);
        btnSavePrint.setClickable(false);
        progressDialog = ProgressDialog.show(OnShelvesActivity.this, "?????????...", "????????????...", true);
        Thread thread = new Thread(new Runnable() {
            public void run() {
                ArrayList<String> ss = new ArrayList<String>();
                for (int i = 0; i < data.size(); i++) {
                    if (i == 0)
                        continue;
                    ss.add(data.get(i).get("ID").toString());
                }
                String str = StringHelper.StringJoin(",", ss);
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

    //????????????Json????????????
    private String BuildJson() {
        //Gson gson = new Gson();
        return new String();
    }

    //????????????
    private String PostSave(String json, String AJCodes) {
        String url = HttpHelper.BASE_URL + "ZH?hzBarCode=" + etLibID.getText() + "&ajBarCodes=" + AJCodes;
        return HttpHelper.Post(url, null);
    }

    //AJ????????????
    private Boolean GetChecking(String id) {
        String url = HttpHelper.BASE_URL + "ValidateAJHZXZBarCodeIsValid?barCode=" + id;
        //return ("true".equals(HttpHelper.Get(url)));
        return true;
    }

    //????????????
    private String GetHZID() {
        //return "HZ-001";
        String url = HttpHelper.BASE_URL + "NewHZCode";
        return HttpHelper.Get(url).replace("\"", "");
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
                        Toast.makeText(OnShelvesActivity.this, "????????????", Toast.LENGTH_SHORT).show();
                    }
                });
            }//????????????,Toast??????
            else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(OnShelvesActivity.this, "????????????:?????????????????????", Toast.LENGTH_SHORT).show();
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


