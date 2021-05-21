package com.fuzheng.archivewms;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.fuzheng.archivewms.Util.HttpHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

public class LoginActivity extends AppCompatActivity {

    public Button btnlogin;
    public String txtJsonResult;
    public EditText etUserName;
    public EditText etPwd;
    public static  String  currentUserName;

    private ProgressDialog progressDialog = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        btnlogin = findViewById(R.id.btnLogin);
        btnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                btnlogin.setClickable(false);
                progressDialog = ProgressDialog.show(LoginActivity.this,"请稍等...","正在登录...",true);
                progressDialog.dismiss();
                new Thread(new Runnable() {
                    public void run() {
                        //Gson gson = new Gson();
                        txtJsonResult = PostRegistLogin(null);
                        progressDialog.dismiss();
                        handlerUserInfo.sendEmptyMessage(0);
                    }
                }).start();
            }
        });

       etUserName = findViewById(R.id.username);
       etPwd = findViewById(R.id.password);
    }

    public void loginSuccess()
    {
        currentUserName = etUserName.getText().toString();
        //实现跳转
        Intent intent = new Intent(LoginActivity.this,MainActivity.class);
        startActivity(intent);
    }

    private String PostRegistLogin(String json) {
        String url = HttpHelper.BASE_URL + "ValidateLogin?userName="+etUserName.getText()+"&pwd="+etPwd.getText();
        return HttpHelper.Post(url, json);
    }
    @SuppressLint("HandlerLeak")
    Handler handlerUserInfo = new Handler() {
        public void handleMessage(Message msg) {
            //以后如果做用户名密码判断就在这里判断
            if (txtJsonResult.indexOf("\"登录成功\"")>0){
                loginSuccess();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(LoginActivity.this,"登录成功",Toast.LENGTH_SHORT).show();
                    }
                });
                return;
            }//如果失败,Toast提示
            else
            {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(LoginActivity.this,"登录失败:请检查用户名或密码是否正确",Toast.LENGTH_SHORT).show();
                        btnlogin.setClickable(true);
                    }
                });
                return;
            }
        }
    };
}
