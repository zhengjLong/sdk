package com.library.base.utils

import android.Manifest
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.webkit.MimeTypeMap
import com.library.base.BuildConfig
import java.io.*

/**
 *
 * @Author: jerome
 * @Date: 2018-01-26
 */

object FileUtil {


    val PROJECT_FILE_PATH = Environment
            .getExternalStorageDirectory().path + File.separator + BuildConfig.APP_NAME + File.separator // 项目路径

    val CHAT_DATA = PROJECT_FILE_PATH + "chatData.txt"


    /**
     * 初始化文件目录
     */
    fun init() {
        val projectDir = File(PROJECT_FILE_PATH)
        if (!projectDir.exists()) {
            projectDir.mkdirs()
        }
    }

    /**
     * 保存对象为文件
     * @param object
     */
    fun saveFile(`object`: Any, path: String): Boolean {
        var fos: FileOutputStream? = null
        var oos: ObjectOutputStream? = null
        val f = File(PROJECT_FILE_PATH)
        if (!f.exists()) {
            f.mkdirs()
        }
        return try {
            fos = FileOutputStream(path)
            oos = ObjectOutputStream(fos)
            oos.writeObject(`object`)
            oos.close()
            fos.close()
            true
        } catch (e: FileNotFoundException) {
            Logcat.e(e.message)
            false
        } catch (e: IOException) {
            Logcat.e(e.message)
            false
        }

    }

    /**
     * 读取对象文件
     * @param path
     * *
     * @return
     */
    fun readObjectFile(path: String): Any? {
        var fis: FileInputStream? = null
        var ois: ObjectInputStream? = null
        val f = File(path)
        if (!f.exists()) {
            return null
        }
        try {
            fis = FileInputStream(f)
            ois = ObjectInputStream(fis)
            val `object` = ois.readObject()
            ois.close()
            fis.close()
            return `object`
        } catch (e: FileNotFoundException) {
            Logcat.e(e.message)
        } catch (e: IOException) {
            Logcat.e(e.message)
        } catch (e: ClassNotFoundException) {
            Logcat.e(e.message)
        }

        return null
    }

    /**
     * 删除单个文件
     */
    fun deleteFile(sPath: String): Boolean {
        var flag = false
        val file = File(sPath)
        if (file.isFile && file.exists()) {
            file.delete()
            flag = true
        }
        return flag
    }

    /**
     * 删除文件夹
     *
     * @param file
     */
    fun deleteFolder(file: File) {
        if (file.exists() && file.isDirectory) {//判断是文件还是目录
            if (file.listFiles()!!.isNotEmpty()) {//若目录下没有文件则直接删除
                //若有则把文件放进数组，并判断是否有下级目录
                val delFile = file.listFiles()
                val i = file.listFiles()!!.size
                for (j in 0 until i) {
                    if (delFile!![j].isDirectory) {
                        deleteFolder(delFile[j])//递归调用del方法并取得子目录路径
                    }
                    delFile[j].delete()//删除文件
                }
            }
            file.delete()
        }
    }

    /**
     * 将一个InputStream里面的数据写入到SD卡中
     */
    fun saveInputStream(path: String, input: InputStream): File? {
        var file: File? = null
        var output: OutputStream? = null
        try {
            file = File(PROJECT_FILE_PATH)
            if (!file.exists()) {
                file.mkdirs()
            }
            output = FileOutputStream(path)
            val buffer = ByteArray(4 * 1024)
            var length = 0
            length = input.read(buffer)
            while (length != -1) {
                output.write(buffer, 0, length)
                length = input.read(buffer)
            }
            output.flush()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            closeIO(output)
        }
        return file
    }

    /**
     * 保存文本
     */
    fun saveString(content: String, path: String) {
        try {
            val file = File(PROJECT_FILE_PATH)
            if (!file.exists()) {
                file.mkdirs()
            }
            val buf = BufferedWriter(FileWriter(File(path), true))
            buf.append(content)
            buf.close()
        } catch (e: IOException) {
            Logcat.e(e.message)
        }

    }


    /**
     * 先质量压缩到指定百分比（0% ~ 90%），再把bitmap保存到sd卡上
     *
     * @param filePath
     * @param bm
     * @param quality
     * @return
     */
    fun saveBitmap(filePath: String, bm: Bitmap, quality: Int): Boolean {
        var file: File? = null
        var out: FileOutputStream? = null
        return try {
            file = File(filePath)
            if (!file.exists()) {
                file.createNewFile()
            }
            out = FileOutputStream(file.path)
            bm.compress(Bitmap.CompressFormat.JPEG, quality, out)
            true
        } catch (e: Exception) {
            Logcat.e(e.message)
            false
        } finally {
            closeIO(out)
        }
    }

    /**
     * 调用系统打开文件
     */
    fun openFile(context: Context, path: String) {
        val intent = Intent()
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.action = Intent.ACTION_VIEW
        val type = getMimeType(path)
        intent.setDataAndType(Uri.fromFile(File(path)), type)
        context.startActivity(intent)
    }

    private fun getMimeType(uri: String): String? {
        var type: String? = null
        val extension = MimeTypeMap.getFileExtensionFromUrl(uri)
        if (extension != null) {
            val mime = MimeTypeMap.getSingleton()
            type = mime.getMimeTypeFromExtension(extension)
        }
        return type
    }


    /**
     * 获取单个文件的大小or获取目录包括包含的文件的总大小
     * @param directory 文件获取目录
     * @return
     */
    fun getDirectorySize(directory: File): Long {
        if (!directory.exists()) return 0
        return if (directory.isDirectory) {
            val directorySize: Long = directory.listFiles()!!
                    .map { getDirectorySize(it) }
                    .sum()
            directorySize
        } else {
            directory.length()
        }
    }

    /**
     * 清空某个目录下的所有文件和文件夹
     * @param directory
     */
    fun clearDirectory(directory: File) {
        if (directory.exists() && directory.isDirectory) {
            for (file in directory.listFiles()!!) {
                if (file.exists() && file.isFile) {
                    file.delete()
                } else if (file.exists() && file.isDirectory) {
                    clearDirectory(file)
                    file.delete()
                }
            }
        }
    }

    /**
     * 从文件中读取文本
     *
     * @param filePath
     * @return
     */
    fun readFile(filePath: String): String? {
        val resultSb: StringBuilder? = null
        var `is`: InputStream? = null
        try {
            `is` = FileInputStream(filePath)
        } catch (e: Exception) {
            Logcat.e(e.message)
        }

        return inputStream2String(`is`)
    }

    /**
     * 从assets中读取文本
     *
     * @param name
     * @return
     */
    fun readFileFromAssets(context: Context, name: String): String? {
        var `is`: InputStream? = null
        try {
            `is` = context.resources.assets.open(name)
        } catch (e: Exception) {
            Logcat.e(e.message)
        }

        return inputStream2String(`is`)

    }

    fun inputStream2String(`is`: InputStream?): String? {
        if (null == `is`) {
            return null
        }
        var resultSb: StringBuilder? = null
        try {
            val br = BufferedReader(InputStreamReader(`is`))

            resultSb = StringBuilder()
            var len: String?
            len = br.readLine()
            while (null != (len)) {
                resultSb.append(len)
                len = br.readLine()
            }
        } catch (ex: Exception) {
            Logcat.e(ex.message)
        } finally {
            closeIO(`is`)
        }
        return if (null == resultSb) null else resultSb.toString()
    }


    /**
     * 关闭流
     *
     * @param closeables
     */
    fun closeIO(vararg closeables: Closeable?) {
        if (closeables.isEmpty()) {
            return
        }
        for (cb in closeables) {
            try {
                if (null == cb) {
                    continue
                }
                cb.close()
            } catch (e: IOException) {
                Logcat.e("close IO ERROR..." + e.message)
            }

        }
    }

    /**
     * 扫描目录（扫描后可以及时在图库中看到）
     * @param context
     * @param path
     */
    fun notifyFileSystemChanged(context: Context, path: String?) {
        if (path == null)
            return
        val f = File(path)
        if (Build.VERSION.SDK_INT >= 19 /*Build.VERSION_CODES.KITKAT*/) { //添加此判断，判断SDK版本是不是4.4或者高于4.4
            var paths: Array<String>? = arrayOf(path)

            if (paths == null)
                paths = arrayOf(Environment.getExternalStorageDirectory().toString())

            MediaScannerConnection.scanFile(context, paths, null, null)
        } else {
            val intent: Intent
            if (f.isDirectory) {
                intent = Intent(Intent.ACTION_MEDIA_MOUNTED)
                intent.setClassName("com.android.providers.media", "com.android.providers.media.MediaScannerReceiver")
                intent.data = Uri.fromFile(Environment.getExternalStorageDirectory())
            } else {
                intent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
                intent.data = Uri.fromFile(File(path))
                Logcat.e("file changed, send uri : " + intent.data!!)
                Logcat.e("file changed, send broadcast:" + intent.toString())
            }
            context.sendBroadcast(intent)
        }
    }
}
