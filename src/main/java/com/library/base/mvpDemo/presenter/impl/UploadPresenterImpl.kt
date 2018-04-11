package com.library.base.mvpDemo.presenter.impl

import android.content.Context
import com.library.base.mvpDemo.model.BaseResponseListener
import com.library.base.mvpDemo.model.UploadModelImpl
import com.library.base.mvpDemo.presenter.interfaces.IUploadPresenter

/**
 * 上传日志
 * @Author: jerome
 * @Date: 2018-01-30
 */

class UploadPresenterImpl(context: Context) : BasePresenterImpl<IUploadPresenter.IPayView>(context), IUploadPresenter {

    private val model: UploadModelImpl = UploadModelImpl(context)


    override fun start() {}

    override fun stop() {

    }


    /**
     * 上传日志
     */
    override fun uploadInfo(content: String) {
        view.showLoading()
        model.uploadErrorData(content,object :BaseResponseListener<String>{
            override fun onSuccess(data: String) {
                view.dismissLoading()
                view.uploadSuccess()
            }

            override fun onFailure(errorCode: Int, message: String?) {
                view.dismissLoading()
                view.showToast(message)
                view.requestError(errorCode,message)
            }

        })
    }
}

