package com.library.base.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 位图转化
 *
 * @Author jerome
 * @Date 2017/5/17
 */

public class BitmapUtil {

    /**
     * 获得原大小的位图
     *
     * @param filePath
     * @return
     */
    public static Bitmap decodeBitmap(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            return null;
        }
        Bitmap bitmap = null;
        try {
            FileInputStream inputStream = new FileInputStream(file);
            FileDescriptor fileDescriptor = inputStream.getFD();
            bitmap = decodeBitmap(fileDescriptor);
            inputStream.close();
        } catch (FileNotFoundException e) {
            Logcat.INSTANCE.e(filePath + "not found");
        } catch (IOException e) {
            Logcat.INSTANCE.e(filePath + "read error");
        }
        return bitmap;
    }


    /**
     * 图片读取
     *
     * @param desc
     * @return
     */
    private static Bitmap decodeBitmap(FileDescriptor desc) {
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inSampleSize = 1;
        try {
            opts.inPurgeable = true;
            opts.inInputShareable = true;
            opts.inPreferredConfig = Bitmap.Config.RGB_565;
            return BitmapFactory.decodeFileDescriptor(desc, null, opts);
        } catch (Exception e) {
            Logcat.INSTANCE.e(e.getMessage());
        }
        return null;
    }


    /**
     * bitmap转为base64
     * 注：位图应先压缩
     *
     * @param bitmap
     * @return
     */
    public static String bitmapToBase64(Bitmap bitmap) {

        String result = null;
        ByteArrayOutputStream baos = null;
        try {
            if (bitmap != null) {
                baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);

                baos.flush();
                baos.close();

                byte[] bitmapBytes = baos.toByteArray();
                result = Base64.encodeToString(bitmapBytes, Base64.DEFAULT);
            }
        } catch (IOException e) {
            Logcat.INSTANCE.e(e.getMessage());
        } finally {
            try {
                if (baos != null) {
                    baos.flush();
                    baos.close();
                }
            } catch (IOException e) {
                Logcat.INSTANCE.e(e.getMessage());
            }
        }
        return result;
    }

    /**
     * 压缩到指定大小容量
     *
     * @param image
     * @param size 单位KB
     * @return
     */
    public static Bitmap compressImage(Bitmap image, int size) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 100;
        while (baos.toByteArray().length / 1024 > size) {    //循环判断如果压缩后图片是否大于100kb,大于继续压缩
            baos.reset();//重置baos即清空baos
            options -= 10;//每次都减少10
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//把压缩后的数据baos存放到ByteArrayInputStream中
        // 把ByteArrayInputStream数据生成图片
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inSampleSize = 1;
        opts.inPreferredConfig = Bitmap.Config.RGB_565;
        return BitmapFactory.decodeStream(isBm, null, opts);
    }

    /**
     * 对图片进行圆角处理
     *
     * @param bitmap  要处理的Bitmap对象
     * @param roundPx 圆角半径设置
     * @return Bitmap对象
     */
    public static Bitmap roundedCornerBitmap(Bitmap bitmap, float roundPx) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        Bitmap output = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, w, h);
        final RectF rectF = new RectF(rect);
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }

    /**
     * 图片加投影 羽化效果
     *
     * @param originalBitmap
     * @return
     */
    public static Bitmap shadowBitmap(Bitmap originalBitmap) {
        BlurMaskFilter blurFilter = new BlurMaskFilter(10, BlurMaskFilter.Blur.OUTER);
        Paint shadowPaint = new Paint();
        shadowPaint.setMaskFilter(blurFilter);
        final int w = originalBitmap.getWidth();
        final int h = originalBitmap.getHeight();
        Bitmap bmp = Bitmap.createBitmap(w + 20, h + 20, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bmp);
        c.drawBitmap(originalBitmap, 10, 10, shadowPaint);
        c.drawBitmap(originalBitmap, 10, 10, null);
        return bmp;
    }

    /**
     * 对图片进行倒影处理
     *
     * @param bitmap
     * @return
     */
    public static Bitmap reflectionImage(Bitmap bitmap) {
        final int reflectionGap = 4;
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        Matrix matrix = new Matrix();
        matrix.preScale(1, -1);

        Bitmap reflectionImage = Bitmap.createBitmap(bitmap, 0, h / 2, w,
                h / 2, matrix, false);

        Bitmap bitmapWithReflection = Bitmap.createBitmap(w, (h + h / 2),
                Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmapWithReflection);
        canvas.drawBitmap(bitmap, 0, 0, null);
        Paint deafalutPaint = new Paint();
        canvas.drawRect(0, h, w, h + reflectionGap, deafalutPaint);

        canvas.drawBitmap(reflectionImage, 0, h + reflectionGap, null);

        Paint paint = new Paint();
        LinearGradient shader = new LinearGradient(0, bitmap.getHeight(), 0,
                bitmapWithReflection.getHeight() + reflectionGap, 0x70ffffff,
                0x00ffffff, Shader.TileMode.CLAMP);
        paint.setShader(shader);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        canvas.drawRect(0, h, w, bitmapWithReflection.getHeight()
                + reflectionGap, paint);

        return bitmapWithReflection;
    }

    /**
     * 放缩图片处理
     *
     * @param bitmap 要放缩的Bitmap对象
     * @param width  放缩后的宽度
     * @param height 放缩后的高度
     * @return 放缩后的Bitmap对象
     * @author com.tiantian
     */
    public static Bitmap zoomBitmap(Bitmap bitmap, int width, int height) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        Matrix matrix = new Matrix();
        float scaleWidth = ((float) width / w);
        float scaleHeight = ((float) height / h);
        matrix.postScale(scaleWidth, scaleHeight);
        return Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, true);
    }

    /**
     * 将Bitmap转化为Drawable
     *
     * @param bitmap
     * @return
     * @author com.tiantian
     */
    public static Drawable bitmap2Drawable(Context context,Bitmap bitmap) {
        return new BitmapDrawable(context.getResources(),bitmap);
    }

    /**
     * 将Drawable转化为Bitmap
     *
     * @param drawable
     * @return
     */
    public static Bitmap drawable2Bitmap(Drawable drawable) {
        // 取 drawable 的长宽
        int w = drawable.getIntrinsicWidth();
        int h = drawable.getIntrinsicHeight();

        // 取 drawable 的颜色格式
        Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                : Bitmap.Config.RGB_565;
        // 建立对应 bitmap
        Bitmap bitmap = Bitmap.createBitmap(w, h, config);
        // 建立对应 bitmap 的画布
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, w, h);
        // 把 drawable 内容画到画布中
        drawable.draw(canvas);
        return bitmap;
    }

    /**
     * 获得旋转角度正确的照片
     *
     * @param path 用于获取原图的信息
     * @return 原图的bitmap（可以是被压缩过的）
     */
    public static Bitmap formatCameraPictureOriginal(String path, Bitmap bitmap) {
         //获取图片的旋转角度，有些系统把拍照的图片旋转了，有的没有旋转
        if (null == bitmap ||TextUtils.isEmpty(path))return null;
        int degree = readPictureDegree(path);
        if (0 == degree) {
            return bitmap;
        }
         //把图片旋转为正的方向
        Bitmap newBitmap = rotaingImage(degree, bitmap);
        recycleBitmap(bitmap);
        return newBitmap;
    }

    /**
     * 回收bitmap
     * @param bitmap
     */
    private static void recycleBitmap(Bitmap bitmap) {
        if (!bitmap.isRecycled()) {
            bitmap.recycle();
        }
    }

    /**
  * 旋转图片
  * @param angle
  * @param bitmap
  * @return Bitmap
  */
    private static Bitmap rotaingImage(int angle, Bitmap bitmap) {
        //旋转图片 动作
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        // 创建新的图片
        Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        recycleBitmap(bitmap);
        return resizedBitmap;
    }

    /**
     * 读取图片属性：旋转的角度
     *
     * @param path 图片绝对路径
     * @return degree旋转的角度
     */
    private static int readPictureDegree(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            Logcat.INSTANCE.e(e.getMessage());
        }
        return degree;
    }


    /**
     * 保存图片到文件
     *
     * @param path
     * @param bitmap
     */
    public static void savePicture(String path, Bitmap bitmap) {
        if (bitmap == null || TextUtils.isEmpty(path)) {
            return;
        }
        File f = new File(path);
        BufferedOutputStream bos;
        try {
            bos = new BufferedOutputStream(new FileOutputStream(f));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            bos.flush();
            bos.close();
        } catch (Exception e) {
            Logcat.INSTANCE.e("save picture is error : " + e.getMessage());
        }
    }

    /**
     * 获取bitmap宽高数组
     * @return 0：宽 1：高
     */
    public static int[] getBitmapWidthAndHeight(String file){
        int [] size = new int[2];
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(file,options);
        size[0] = options.outWidth;
        size[1] = options.outHeight;
        options.inJustDecodeBounds = false;
        recycleBitmap(bitmap);
        return size;
    }

    /**
     * 转换View到Bitmap
     *
     * @param v
     * @return
     */
    public static Bitmap convertViewToBitmap(View v) {
        v.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        v.layout(0, 0, v.getMeasuredWidth(), v.getMeasuredHeight());
        v.buildDrawingCache();
        return v.getDrawingCache();
    }

    /**
     * 将多个Bitmap合并成一个图片。
     *
     * @param columns 将多个图合成多少列
     * @param bitmaps
     *            ... 要合成的图片
     * @return
     */
    public static Bitmap combineBitmaps(int columns, Bitmap... bitmaps) {
        if (columns <= 0 || bitmaps == null || bitmaps.length == 0) {
            throw new IllegalArgumentException(
                    "Wrong parameters: columns must > 0 and bitmaps.length must > 0.");
        }
        int maxWidthPerImage = 20;
        int maxHeightPerImage = 20;
        for (Bitmap b : bitmaps) {
            maxWidthPerImage = maxWidthPerImage > b.getWidth() ? maxWidthPerImage
                    : b.getWidth();
            maxHeightPerImage = maxHeightPerImage > b.getHeight() ? maxHeightPerImage
                    : b.getHeight();
        }
        Logcat.INSTANCE.e("maxWidthPerImage=>" + maxWidthPerImage
                + ";maxHeightPerImage=>" + maxHeightPerImage);
        int rows = 0;
        if (columns >= bitmaps.length) {
            rows = 1;
            columns = bitmaps.length;
        } else {
            rows = bitmaps.length % columns == 0 ? bitmaps.length / columns
                    : bitmaps.length / columns + 1;
        }
        Bitmap newBitmap = Bitmap.createBitmap(columns * maxWidthPerImage, rows
                * maxHeightPerImage, Bitmap.Config.ARGB_8888);
        Logcat.INSTANCE.e("newBitmap=>" + newBitmap.getWidth() + ","
                + newBitmap.getHeight());
        for (int x = 0; x < rows; x++) {
            for (int y = 0; y < columns; y++) {
                int index = x * columns + y;
                if (index >= bitmaps.length)
                    break;
                Logcat.INSTANCE.e("y=>" + y + " * maxWidthPerImage=>"
                        + maxWidthPerImage + " = " + (y * maxWidthPerImage));
                Logcat.INSTANCE.e("x=>" + x + " * maxHeightPerImage=>"
                        + maxHeightPerImage + " = " + (x * maxHeightPerImage));
                newBitmap = mixtureBitmap(newBitmap, bitmaps[index],
                        new PointF(y * maxWidthPerImage, x * maxHeightPerImage));
            }
        }
        return newBitmap;
    }

    /**
     * Mix two Bitmap as one.
     *
     * @param first
     * @param second
     * @param fromPoint
     *            where the second bitmap is painted.
     * @return
     */
    private static Bitmap mixtureBitmap(Bitmap first, Bitmap second,PointF fromPoint) {
        if (first == null || second == null || fromPoint == null) {
            return null;
        }
        Bitmap newBitmap = Bitmap.createBitmap(first.getWidth(), first.getHeight(), Bitmap.Config.RGB_565);
        Canvas cv = new Canvas(newBitmap);
        cv.drawBitmap(first, 0, 0, null);
        cv.drawBitmap(second, fromPoint.x, fromPoint.y, null);
        cv.save(Canvas.ALL_SAVE_FLAG);
        cv.restore();
        return newBitmap;
    }

}
