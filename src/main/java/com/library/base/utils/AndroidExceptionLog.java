package com.library.base.utils;

import android.content.Context;
import android.os.Looper;
import android.util.Log;

import com.library.base.base.BaseApi;
import com.library.base.BuildConfig;
import com.library.base.dialog.LoadingDialog;
import com.library.base.executor.JobExecutor;
import com.library.base.net.HttpCallback;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 异常处理:Application调用init()
 */
public class AndroidExceptionLog implements Thread.UncaughtExceptionHandler {

    private Context mContext;
    private boolean mIsRuning = false;

    public AndroidExceptionLog(Context applicationContext) {
        mContext = applicationContext;
    }

    /**
     * 初始化
     *
     * @param context
     */
    public static void init(Context context) {
        // 正式环境不处理
        if (!BuildConfig.DEBUG) return;
        AndroidExceptionLog ex = new AndroidExceptionLog(context);
        Thread.setDefaultUncaughtExceptionHandler(ex);
    }


    @Override
    public void uncaughtException(Thread thread, Throwable throwable) {
        if (throwable == null) return;

        Logcat.INSTANCE.e("--------------- 程序出现异常了！ ---------------", throwable);

        // 记录日志
        saveToFile(mContext, Log.getStackTraceString(throwable));

        // 正式版，上报友盟错误统计
//        if (!BuildConfig.DEBUG) {
//            MobclickAgent.reportError(mContext, throwable);
//        }

        if (!mIsRuning) {
            JobExecutor.INSTANCE.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (mContext != null) {
                            mIsRuning = true;
                            Looper.prepare();
                            LoadingDialog.toast(mContext, "很抱歉，程序发生错误！");
                            upload(mContext, new HttpCallback<Boolean>() {
                                @Override
                                public void onSuccess(Boolean data) {
                                    LoadingDialog.dismiss();
                                    Logcat.INSTANCE.e(data ? "日志上传成功" : "日志上传失败");
                                }

                                @Override
                                public void onFailure(int errorCode, String message) {
                                    Logcat.INSTANCE.e(message);
                                }
                            });


                            Looper.loop();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    mIsRuning = false;
                }
            });
        }

        try {
            Thread.sleep(2000);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                // 杀死进程，保证友盟统计
//                MobclickAgent.onKillProcess(mContext);
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.exit(0);
        }

    }

    /**
     * 记录到文件中去
     *
     * @param context
     * @param content
     */
    public static void saveToFile(Context context, String content) {
        try {
            File dir = context.getExternalFilesDir("log");
            if (dir == null || !dir.exists() || !dir.canExecute()) {
                return;
            }

            boolean isSuccess = true;
            if (!dir.exists()) {
                isSuccess = dir.mkdirs();
            }
            if (!isSuccess) {
                return;
            }

            // 创建日志文件名:log-2016-0412-110033.log
            Date date = new Date();

            String fileName = String.format("log-%s.log", new SimpleDateFormat("yyyy-MMdd-HHmmss").format(date));

            File logFile = new File(dir.getPath() + File.separator + fileName);

            if (!logFile.exists() && !logFile.createNewFile()) {
                return;
            }

            FileWriter out = new FileWriter(logFile, true);
            BufferedWriter writer = new BufferedWriter(out);
            writer.write(content);
            writer.close();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 上传错误日志
     * @param context
     * @param callback
     */
    public static void upload(Context context, final HttpCallback<Boolean> callback) {
        final File file = context.getExternalFilesDir("log");
        if (file == null || !file.exists() || file.listFiles().length <= 0) {
            callback.onFailure(0, "没有日志需要上传");
            return;
        }

        final File[] files = file.listFiles();

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < files.length; i++) {
            File log = files[i];
            try {
                BufferedReader reader = new BufferedReader(new FileReader(log));
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                    sb.append("\r\n");
                }
                sb.append("\r\n\r\n");
                sb.append("------------------------- 华丽的分割线 --------------------------");
                sb.append("\r\n\r\n");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (sb.length() <= 0) {
            return;
        }

        BaseApi.getInstance(context).uploadErrorData(sb.toString(), new HttpCallback<String>() {
            @Override
            public void onSuccess(String data) {
                callback.onSuccess(true);
                for (File log : files) {
                    try {
                        log.delete();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(int errorCode, String message) {
                callback.onSuccess(false);
            }
        });
    }
}
