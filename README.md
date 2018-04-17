# AndroidSdk

# 功能介绍：

android开发中的快速集成库,以下为所含基本功能：

1：基类的封装如Activity,Fragment,Dialog,Application等。

2：线程池调用封封。

3：基于MVP设计模式的Demo。

4：基于OKHttp网络请求框架的封装。

5：基于Glide图片查看器的使用。

6：常用工具类的封装。

7：常用自定义View。

8：图片轮播的使用。

# 关键调用：

1：SdkUtil：library 全局调用类，引用了常用的功能。initSdk（）方法初始化于BaseApplication 类，用于library 库的初始化。

2：BasePresenterActivity： 基于MVP Activity的基类，同步了Activity 与Presenter 的生命周期。

3：LoadingDialog： 加载框与对话框的封装。

4：PromptDialog ： 自定义信息提示框。

5：BaseApi： 对于网络请求api的调用。

6：AppRoute ： 对Activity实例的存储与移除，实现于 BaseApplication。

7：build.gradle: 添加有对library 全局配置的修改。





