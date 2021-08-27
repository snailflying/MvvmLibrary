package com.theone.framework.util

import android.content.Context
import java.io.ByteArrayOutputStream

import java.io.File
import java.io.InputStream

/**
 * @Author zhiqiang
 * @Email liuzhiqiang@theone.com
 * @Date 2019-06-18
 * @Description 文件管理 工具类
 */
object FileUtil {
    private val CACHE_PATH = "cache"
    private val FILE_PATH = "file"


    //获取文件夹目录 by:zhiqiang [start]


    fun getCachePath(context: Context): String {
        return getDiskCacheDir(
            context,
            CACHE_PATH
        )!!.toString() + File.separator
    }

    fun getFilePath(context: Context): String {
        return getDiskFileDir(
            context,
            FILE_PATH
        )!!.toString() + File.separator
    }

    fun getCachePath(context: Context, path: String): String {
        return getDiskCacheDir(context, path)!!.toString() + File.separator
    }

    fun getFilePath(context: Context, path: String): String {
        return getDiskFileDir(context, path)!!.toString() + File.separator
    }

    @JvmOverloads
    fun getDiskFileDir(context: Context, fileDir: String? = FILE_PATH): File? {

        var directory = context.getExternalFilesDir(null)

        if (directory == null) {
            directory = context.filesDir
        }
        if (fileDir != null) {
            val file = File(directory, fileDir)
            return if (!file.exists() && !file.mkdir()) {
                directory
            } else {
                file
            }
        }
        return directory
    }

    @JvmOverloads
    fun getDiskCacheDir(context: Context, fileDir: String? = CACHE_PATH): File? {

        var cacheDirectory = context.externalCacheDir

        if (cacheDirectory == null) {
            cacheDirectory = context.cacheDir
        }
        if (fileDir != null) {
            val file = File(cacheDirectory, fileDir)
            return if (!file.exists() && !file.mkdir()) {
                cacheDirectory
            } else {
                file
            }
        }
        return cacheDirectory
    }
    //获取文件夹目录 [end]

    fun inputStreamToString(inputStream: InputStream): String {
        val result = ByteArrayOutputStream()
        val buffer = ByteArray(1024)
        var length: Int
        while (inputStream.read(buffer).also { length = it } != -1) {
            result.write(buffer, 0, length)
        }
        return result.toString("UTF-8")
    }

}
