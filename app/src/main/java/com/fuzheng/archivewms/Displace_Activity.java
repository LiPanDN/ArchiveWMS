package com.fuzheng.archivewms;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.fuzheng.archivewms.Util.HttpHelper;
import com.fuzheng.archivewms.Util.StringHelper;

import java.util.ArrayList;
//换位
public class Displace_Activity extends AppCompatActivity {
    public  EditText Barcode;
    public  EditText position;
    public String txtJsonResult;
    private ProgressDialog progressDialog = null;//进度条
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_displace);
        Button qd=findViewById(R.id.btnHW);
         Barcode=findViewById(R.id.Barcode);
         position=findViewById(R.id.position);
        qd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                QR();
            }
        });
    }
    private void QR() {

        progressDialog = ProgressDialog.show(Displace_Activity.this, "请稍等...", "正在换位...", true);
        Thread thread = new Thread(new Runnable() {
            public void run() {
                txtJsonResult = PostHW(null);
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
                        Toast.makeText(Displace_Activity.this, "换位成功", Toast.LENGTH_SHORT).show();
                    }
                });
            }//如果失败,Toast提示
            else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(Displace_Activity.this, "换位失败:请检查网络情况", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    };
    //换位请求
    private String PostHW(String json) {
        String url = HttpHelper.BASE_URL + "SwitchPosition?changingdBarCode=" + Barcode.getText().toString().replaceAll(" ","")+
                "&destinationBarCode=" + position.getText().toString().replaceAll(" ","") ;
        //String url = HttpHelper.BASE_URL + "ZX?xzBarCode=" + XZID.getText() + "&ajBarcodesOrHZBarCodes=" + AJCodes;
        return HttpHelper.Post(url, null);
    }
}

