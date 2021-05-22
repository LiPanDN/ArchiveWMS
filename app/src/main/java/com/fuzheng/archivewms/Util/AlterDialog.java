package com.fuzheng.archivewms.Util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.widget.Toast;

import com.fuzheng.archivewms.R;

public class AlterDialog {
    public static void simple(Context context,String Title,String TXT){
        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setTitle(Title)//设置对话框 标题
                //.setIcon(R.drawable.seek02)//设置图标
                .setMessage(TXT);

        setPositiveButton(builder)//add 'yes' Button to AlertDialog
        //setNegativeButton(builder)//add 'no' Button to AlertDialog
                .create()
                .show();
    }
    private static AlertDialog.Builder setPositiveButton(AlertDialog.Builder builder){
        // use 'setPositiveButton' method to add 'yes' Button
        return builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Toast.makeText(MainActivity.mMainActivity,"you click 'yes' button ",Toast.LENGTH_SHORT).show();
            }
        });
    }

    private static AlertDialog.Builder setNegativeButton(AlertDialog.Builder builder){
        // use 'setPositiveButton' method to add 'yes' Button
        return builder.setNegativeButton("no", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Toast.makeText(MainActivity.mMainActivity,"you click 'no' button ",Toast.LENGTH_SHORT).show();
            }
        });
    }

}
