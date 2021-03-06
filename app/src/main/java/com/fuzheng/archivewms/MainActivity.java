package com.fuzheng.archivewms;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;

import com.fuzheng.archivewms.Util.HttpHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;


import com.google.gson.Gson;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.apache.http.client.utils.URLEncodedUtils;


public class MainActivity extends AppCompatActivity {

    public ImageButton btnFolder;
    public String txtJsonResult;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        btnFolder = findViewById(R.id.btnAJPrint);
        btnFolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                btnFolder.setClickable(false);

                new Thread(new Runnable() {
                    public void run() {
                        //Gson gson = new Gson();
                        txtJsonResult = PostRegistLogin(null);
                        //progressDialog.dismiss();
                        handlerUserInfo.sendEmptyMessage(0);
                    }
                }).start();
            }
        });

    }

    private String PostRegistLogin(String json) {
        String url = HttpHelper.BASE_URL + "ValidateLogin?userName=2121&pwd=2121";
        return HttpHelper.Post(url, json);
    }
    @SuppressLint("HandlerLeak")
    Handler handlerUserInfo = new Handler() {
        public void handleMessage(Message msg) {
            //??????????????????????????????????????????????????????
            if (txtJsonResult.equals("\"????????????\"")){
                //loginSuccess();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //Toast.makeText(Login.this,"????????????",Toast.LENGTH_SHORT).show();
                    }
                });
                return;
            }//????????????,Toast??????
            else
            {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //Toast.makeText(Login.this,"????????????:???????????????????????????????????????",Toast.LENGTH_SHORT).show();
                        btnFolder.setClickable(true);
                    }
                });
                return;
            }
        }
    };


    //??????
    public void onFoldering(View v)
    {
        Intent intent=new Intent(MainActivity.this,FolderActivity.class);
        startActivity(intent);
    }

    //??????
    public void OnBoxing(View v)
    {
        Intent intent=new Intent(MainActivity.this,BoxActivity.class);
        startActivity(intent);
    }

    public void onScanBarcode(View v){
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ONE_D_CODE_TYPES);
        integrator.setPrompt("???????????????");
        integrator.setCameraId(0);
        integrator.setBeepEnabled(false);
        integrator.initiateScan();
    }

    public void onScanQrcode(View v){
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
        integrator.setPrompt("???????????????");
        integrator.setCameraId(0);
        integrator.setBeepEnabled(false);
        integrator.initiateScan();
    }

    public void onGeneratedCode(View v)
    {
        //qrCodeBitmap = CreateQRBitmp.createQRCodeBitmap(contentString, portrait);
        //ivQrImage.setImageBitmap(qrCodeBitmap);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            if(result.getContents() == null) {
                Toast.makeText(this, "???????????????", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "????????????????????????: " + result.getContents(), Toast.LENGTH_LONG).show();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
