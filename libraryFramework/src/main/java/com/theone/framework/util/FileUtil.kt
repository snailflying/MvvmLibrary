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

    //获取文件夹目录 by:zhiqiang [start]

    /**
     * 删除文件
     */
    fun deleteDir(dir: File?): Boolean {
        if (dir != null && dir.isDirectory) {
            val children = dir.list() ?: emptyArray()
            for (i in children.indices) {
                val success = deleteDir(File(dir, children[i]))
                if (!success) {
                    return false
                }
            }
        }
        return dir!!.delete()
    }

    /**
     * 判断文件是否存在
     */
    fun fileIsExists(file: File?): Boolean {
        try {
            if (file?.exists() != true) {
                return false
            }
        } catch (e: Exception) {
            return false
        }
        return true
    }

    @JvmOverloads
    fun getCachePath(context: Context, fileDir: String? = null): String? {
        return getDiskCacheDir(context, fileDir)?.toString()
    }

    @JvmOverloads
    fun getFilePath(context: Context, path: String? = null): String? {
        return getDiskFileDir(context, path)?.toString()
    }

    /**
     * 优先获取getExternalFilesDir
     * 其次getFilesDir
     */
    fun getDiskFileDir(context: Context, fileDir: String?): File? {

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

    fun getDiskCacheDir(context: Context, fileDir: String?): File? {

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
