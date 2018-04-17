package com.library.base.mvpDemo.model

import android.content.Context
import com.library.base.net.HttpCallback

/**
 * 日志上传
 * @author : jerome
 */

class UploadModelImpl(context: Context) : BaseModelImpl(context) {


    /**
     * 上传日志
     *
     * @param listener
     */
    fun uploadErrorData(content: String, listener: BaseResponseListener<String>) {
        api.uploadErrorData(content, object : HttpCallback<String> {
            override fun onSuccess(data: String) {
                listener.onSuccess(data)
            }

            override fun onFailure(errorCode: Int, message: String) {
                listener.onFailure(errorCode, message)
            }
        })
    }
}


