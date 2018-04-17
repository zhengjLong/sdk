package com.library.base.mvpDemo.presenter.interfaces

import com.library.base.mvpDemo.view.IBaseView


/**
 * 上传日志
 *
 * @author : jerome
 */

interface IUploadPresenter : IBasePresenter {

//    上传日志
    fun uploadInfo(content: String)



    interface IPayView : IBaseView {
        //    上传成功
        fun uploadSuccess()

    }
}


