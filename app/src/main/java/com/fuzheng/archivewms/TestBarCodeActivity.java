package com.fuzheng.archivewms;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.os.Bundle;

import com.fuzheng.archivewms.Util.QBarCodeUtil;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

public class TestBarCodeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_bar_code);
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
    }


    //
    public void onGeneratedCode(View v)
    {
        try
        {
            int w = this.getResources().getDisplayMetrics().widthPixels - 40;
            int h = w / 5 * 2;
            Bitmap qrCodeBitmap = QBarCodeUtil.CreateOneDCodeAndString("AXF21212124",w,h,"");//CreateOneDCode("Hello");
            ImageView imageView = findViewById(R.id.imageView);
            imageView.setImageBitmap(qrCodeBitmap);
            //ivQrImage.setImageBitmap(qrCodeBitmap);
        }
        catch (WriterException ex)
        {
            Log.i("ansen","发生异常:"+ex.getMessage());
        }
    }





}
