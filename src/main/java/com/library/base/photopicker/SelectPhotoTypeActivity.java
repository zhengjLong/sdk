package com.library.base.photopicker;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.eightbitlab.rxbus.Bus;
import com.library.base.R;
import com.library.base.photopicker.beans.SelectImageEvent;
import com.library.base.photopicker.beans.SelectPhotoEvent;
import com.library.base.photopicker.utils.PhotoUtils;
import com.library.base.utils.SdkUtil;
import com.yalantis.ucrop.UCrop;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;


public class SelectPhotoTypeActivity extends FragmentActivity implements View.OnClickListener {
    public static final int REQUEST_CODE_FROM_CAMERA = 150;
    public static final int REQUEST_CODE_FROM_ALBUM = 151;
    public static final int REQUEST_CODE_CROP_PHOTO = 152;//裁剪照片
    private static final String TAG = SelectPhotoTypeActivity.class.getSimpleName();
    private static final String SAMPLE_CROPPED_IMAGE_NAME = "Image.jpeg";
    private boolean requestCrop;
    public final static int CALL_BACK_EVENT_BUS = 0;
    public final static int CALL_BACK_BUS = 1;
    private String flag;
    private int callBackType;
    private boolean isOval;
    private int width;
    private int height;
    private Uri mDestinationUri;
    SelectPhotoEvent selectPhotoEvent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_dialog);
        TextView takePhoto = (TextView) findViewById(R.id.photo_tv_goto_camera);
        TextView select = (TextView) findViewById(R.id.photo_tv_goto_album);
        TextView cancle = (TextView) findViewById(R.id.photo_tv_cancel);
        takePhoto.setOnClickListener(this);
        select.setOnClickListener(this);
        cancle.setOnClickListener(this);
//        getWindow().setGravity(Gravity.BOTTOM);
//        getWindow().setWindowAnimations(R.style.dialog_bottom_up_style);// 效果

        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        getWindow().setAttributes(params);
        getWindow().setGravity(Gravity.BOTTOM) ;
        getWindow().setWindowAnimations(R.style.dialog_bottom_up_style); // 添加动画

        requestCrop = getIntent().getBooleanExtra("requestCrop", false);
        isOval = getIntent().getBooleanExtra("isOval", false);
        width = getIntent().getIntExtra("width", PhotoUtils.getDeviceWidth(this));
        height = getIntent().getIntExtra("height", PhotoUtils.getDeviceWidth(this));
        flag = getIntent().getStringExtra("flag");
        callBackType = getIntent().getIntExtra("callBackType",CALL_BACK_BUS);
        mDestinationUri = Uri.fromFile(new File(getCacheDir(), System.currentTimeMillis()+SAMPLE_CROPPED_IMAGE_NAME));
        selectPhotoEvent = new SelectPhotoEvent();
        selectPhotoEvent.flag = flag;
    }
    public static void start(Context context, String flag,int callBackType) {
        Intent starter = new Intent(context, SelectPhotoTypeActivity.class);
        starter.putExtra("flag", flag);
        starter.putExtra("requestCrop", false);
        starter.putExtra("callBackType", callBackType);
        context.startActivity(starter);
    }

    /**
     *  启动并裁剪
     * @param context
     * @param flag
     */
    public static void startAndCrop(Context context, String flag,int width,int height) {
        Intent starter = new Intent(context, SelectPhotoTypeActivity.class);
        starter.putExtra("flag", flag);
        starter.putExtra("width", width);
        starter.putExtra("height", height);
        starter.putExtra("isOval", false);
        starter.putExtra("requestCrop", true);
        context.startActivity(starter);
    }
    public static void startAndCropOval(Context context, String flag,int width,int height) {
        Intent starter = new Intent(context, SelectPhotoTypeActivity.class);
        starter.putExtra("flag", flag);
        starter.putExtra("width", width);
        starter.putExtra("height", height);
        starter.putExtra("isOval", true);
        starter.putExtra("requestCrop", true);
        context.startActivity(starter);
    }
    public static void startAndCrop(Context context, String flag,int width,int height,boolean isOval,UCrop.Options options) {
        Intent starter = new Intent(context, SelectPhotoTypeActivity.class);
        starter.putExtra("flag", flag);
        starter.putExtra("width", width);
        starter.putExtra("height", height);
        starter.putExtra("isOval", isOval);
        starter.putExtra("requestCrop", true);
//        starter.putExtras(options.getOptionBundle());
        context.startActivity(starter);
    }
    public static UCrop.Options getDefaultOption(){
        UCrop.Options options=new UCrop.Options();
        options.setCompressionFormat(Bitmap.CompressFormat.JPEG);
        return options;
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CODE_FROM_CAMERA && data != null) {//照相机图片
                Uri uri = data.getData();
                if (uri != null) {//获取到了uri
                    if (requestCrop) {//需要裁剪
                        startCropActivity(uri);//去裁剪图片
                    } else {//不需要裁剪
                        Bitmap bitmap = null;
                        try {
                            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        sendResultFromBitmap(bitmap);//发送结果
                    }
                } else {//没有获取到uri，需要自己生成
                    Bundle bundle = data.getExtras();
                    Bitmap bitmap = (Bitmap) bundle.get("data");//bitmap；
                    if(requestCrop){
                        File file = savePhoto(bitmap);
                        if(file==null){
                            sendResultFail();
                            return;
                        }
                        startCropActivity(Uri.fromFile(file));//去裁剪图片
                    }else{
                        sendResultFromBitmap(bitmap);
                    }
                }
            }
            if (requestCode == REQUEST_CODE_FROM_ALBUM && data != null) {//相册图片
                Uri uri = data.getData();//视手机情况而定，有的手机为空，有的手机不为空
                if (requestCrop) {
                    startCropActivity(uri);
                } else {
                    Bitmap bitmap = null;
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    sendResultFromBitmap(bitmap);//发送结果
                }
            }
            if (requestCode == UCrop.REQUEST_CROP) {
                sendResultFromUri(UCrop.getOutput(data));
            }
        }else if (resultCode == UCrop.RESULT_ERROR) {
            sendResultFail();
        }else{
            selectPhotoEvent.status= SelectImageEvent.STATUS_CANCEL;
            selectPhotoEvent.imagePath=null;
            if (callBackType == CALL_BACK_EVENT_BUS)
            EventBus.getDefault().post(selectPhotoEvent);//发送取消
            else
            Bus.INSTANCE.send(selectPhotoEvent);
            finish();
        }
    }
    private void sendResultFromUri(Uri uri){
        if(uri==null){
            sendResultFail();
            return;
        }
        selectPhotoEvent.status = SelectPhotoEvent.STATUS_OK;
        selectPhotoEvent.imagePath = uri.getPath();
        if (callBackType == CALL_BACK_EVENT_BUS)
        EventBus.getDefault().post(selectPhotoEvent);//发送通知图片选择成功
        else
        Bus.INSTANCE.send(selectPhotoEvent);
        finish();
    }
    private void sendResultFromBitmap(Bitmap bitmap) {
        if (bitmap == null) {
            sendResultFail();
            return;
        }
        try {
            File file = savePhoto(bitmap);
            if (file != null) {
                selectPhotoEvent.status = SelectImageEvent.STATUS_OK;
            } else {
                selectPhotoEvent.status = SelectImageEvent.STATUS_FAIL;
            }
            selectPhotoEvent.imagePath = file.getAbsolutePath();
            if (callBackType == CALL_BACK_EVENT_BUS)
            EventBus.getDefault().post(selectPhotoEvent);//发送通知图片选择成功
            else
            Bus.INSTANCE.send(selectPhotoEvent);
            finish();
        } catch (Exception e) {
            e.printStackTrace();
            sendResultFail();
        }
    }

    /**
     * 通知图片选择失败
     */
    private void sendResultFail() {
        selectPhotoEvent.status = SelectImageEvent.STATUS_FAIL;
        selectPhotoEvent.imagePath = null;
        if (callBackType == CALL_BACK_EVENT_BUS)
        EventBus.getDefault().post(selectPhotoEvent);//发送通知图片选择成功
        else
        Bus.INSTANCE.send(selectPhotoEvent);
        finish();
    }

    private File savePhoto(Bitmap bitmap) {
        //进行图片保存
        try {
            bitmap = ThumbnailUtils.extractThumbnail(bitmap, bitmap.getWidth(), bitmap.getHeight(),
                    ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
            File file = new File(getCacheDir(), System.currentTimeMillis()+"temp.jpg");
            file.createNewFile();
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
            return file;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void onClick(View v) {
        if(v.getId()== R.id.photo_tv_goto_camera){
            SdkUtil.getUserPermission(new SdkUtil.PermissionCallBack() {
                @Override
                public void isSuccess(boolean isSuccess) {
                    if(isSuccess){
                        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                        startActivityForResult(intent, REQUEST_CODE_FROM_CAMERA);
                    }else{
                        Toast.makeText(SelectPhotoTypeActivity.this,"请在设置里打开相机权限！",Toast.LENGTH_SHORT).show();
                    }
                }
            },this,Manifest.permission.CAMERA);
        }else if(v.getId()== R.id.photo_tv_goto_album){
            Intent intentPic = new Intent(Intent.ACTION_PICK);
            intentPic.setType("image/*");
            startActivityForResult(intentPic, REQUEST_CODE_FROM_ALBUM);
        }else if(v.getId()== R.id.photo_tv_cancel){
            finish();
        }
    }

    /**
     * 启动裁剪
     * @param uri
     */
    private void startCropActivity(@NonNull Uri uri) {
        Bundle extras = getIntent().getExtras();
        UCrop uCrop = UCrop.of(uri, mDestinationUri);
        uCrop.withAspectRatio(width, height);
        //构造新的option 加入传来的参数
        UCrop.Options options=getDefaultOption();
//        options.setOvalDimmedLayer(isOval);
//        options.getOptionBundle().putAll(extras);
        //构造新的ucrop
        uCrop=uCrop.withOptions(options);
        uCrop.start(this);
//        this.overridePendingTransition(R.anim.dialog_enter_bottom_to_up, R.anim.static_anim);
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(0, R.anim.dialog_exit_top_to_bottom);
    }
}
