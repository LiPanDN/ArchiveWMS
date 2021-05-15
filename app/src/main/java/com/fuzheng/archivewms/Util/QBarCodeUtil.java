package com.fuzheng.archivewms.Util;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

public class QBarCodeUtil {

    //字体大小
    private  static final int TXT_SIZE = 80;
    //画布偏离顶端多远
    private  static final int y_offset = 10;

    public static Bitmap CreateOneDCode(String content, int w, int h) throws WriterException {
        // 生成一维条码,编码时指定大小,不要生成了图片以后再进行缩放,这样会模糊导致识别失败
        int imgWidth = w;//this.getResources().getDisplayMetrics().widthPixels - 40;
        int imgHeight = h;//imgWidth / 5 * 2;

        BitMatrix matrix = new MultiFormatWriter().encode(content,
                BarcodeFormat.CODE_128, imgWidth, imgHeight);
        int width = matrix.getWidth();
        int height = matrix.getHeight();
        int[] pixels = new int[width * height];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (matrix.get(x, y)) {
                    pixels[y * width + x] = 0xff000000;
                }
            }
        }

        Bitmap bitmap = Bitmap.createBitmap(width, height,
                Bitmap.Config.ARGB_8888);
        // 通过像素数组生成bitmap,具体参考api
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        return bitmap;
    }

    /**
     * 将字符串转成图片
     * @param text
     * @param width
     * @param height
     * @return
     */

    public static Bitmap StringToBitmap(String text, int width, int height) {
        Bitmap newBitmap = Bitmap.createBitmap(width,height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(newBitmap);
        canvas.drawBitmap(newBitmap, 0, 0, null);
        TextPaint textPaint = new TextPaint();
        textPaint.setAntiAlias(true);
        textPaint.setTextSize(TXT_SIZE);
        textPaint.setColor(Color.BLACK);
        StaticLayout sl= new StaticLayout(text, textPaint, newBitmap.getWidth()-8, Layout.Alignment.ALIGN_CENTER, 1.0f, 0.0f, false);
        canvas.translate(0, y_offset);
        canvas.drawColor(Color.WHITE);
        sl.draw(canvas);
        return newBitmap;
    }


    /**
     * 连接两个bitmap
     *  以第一个图为准
     * 优化算法  1.图片不需要铺满，只需要以统一合适的宽度。然后让imageview自己去铺满，不然长图合成长图会崩溃，这里以第一张图为例
     *2.只缩放不相等宽度的图片。已经缩放过的不需要再次缩放
     * @param bit1
     * @param bit2
     * @return
     */
    public static Bitmap CombBitmap(Bitmap bit1, Bitmap bit2) {
        Bitmap newBit = null;
        int width = bit1.getWidth();
        if (bit2.getWidth() != width) {
            int h2 = bit2.getHeight() * width / bit2.getWidth();
            newBit = Bitmap.createBitmap(width, bit1.getHeight() + h2, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(newBit);
            Bitmap newSizeBitmap2 = getNewSizeBitmap(bit2, width, h2);
            canvas.drawBitmap(bit1, 0, 0, null);
            canvas.drawBitmap(newSizeBitmap2, 0, bit1.getHeight(), null);
        } else {
            newBit = Bitmap.createBitmap(width, bit1.getHeight() + bit2.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(newBit);
            canvas.drawBitmap(bit1, 0, 0, null);
            canvas.drawBitmap(bit2, 0, bit1.getHeight(), null);
        }
        return newBit;
    }

    /**
     * 获取新的bitmap
     * @param bitmap
     * @param newWidth
     * @param newHeight
     * @return
     */
    public static Bitmap getNewSizeBitmap(Bitmap bitmap, int newWidth, int newHeight) {
        float scaleWidth = ((float) newWidth) / bitmap.getWidth();
        float scaleHeight = ((float) newHeight) / bitmap.getHeight();
        // 取得想要缩放的matrix参数
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        // 得到新的图片
        Bitmap bit1Scale = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix,
                true);
        return bit1Scale;
    }


    /**
     *  创建带文字的一维码
     * @param content
     * @param w
     * @param h
     * @param stringPrefix
     * @return
     * @throws WriterException
     */
    public static Bitmap CreateOneDCodeAndString(String content, int w, int h, String stringPrefix) throws WriterException {
        return CombBitmap(CreateOneDCode(content, w, h-TXT_SIZE-y_offset),
                StringToBitmap(stringPrefix + content, w, TXT_SIZE + y_offset));
    }
}
