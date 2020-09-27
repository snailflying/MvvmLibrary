package com.theone.framework.ext

import android.content.Context
import android.content.SharedPreferences
import com.theone.framework.base.CoreApp
import com.theone.framework.encrypt.AesRsaEncrypt

/**
 * @Author zhiqiang
 * @Date 2019-06-22
 * @Email liuzhiqiang@theone.com
 * @Description 1.只加密value 2.加解密较为耗时，注意使用时机，放在子线程或者做内存缓存
 */


fun SharedPreferences.getEncryptString(key: String, defValue: String = "", pwd: String? = DEFAULT_PWD): String {
    val encryptValue = this.getString(key, null)
    return if (encryptValue == null) defValue else decryptPreference(cipherText = encryptValue, pwd = pwd)
}

fun SharedPreferences.Editor.putEncryptString(
    key: String,
    value: String,
    pwd: String? = DEFAULT_PWD
): SharedPreferences.Editor {
    this.putString(key, encryptPreference(plainText = value, pwd = pwd))
    return this
}


fun SharedPreferences.getEncryptInt(key: String, defValue: Int = 0, pwd: String? = DEFAULT_PWD): Int {
    val encryptValue = this.getString(key, null)

    return if (encryptValue == null) defValue else decryptPreference(cipherText = encryptValue, pwd = pwd).toInt()
}

fun SharedPreferences.Editor.putEncryptInt(
    key: String,
    value: Int,
    pwd: String? = DEFAULT_PWD
): SharedPreferences.Editor {
    this.putString(
        key,
        encryptPreference(plainText = value.toString(), pwd = pwd)
    )
    return this
}


fun SharedPreferences.getEncryptLong(key: String, defValue: Long = 0, pwd: String? = DEFAULT_PWD): Long {
    val encryptValue = this.getString(key, null)
    return if (encryptValue == null) defValue else decryptPreference(cipherText = encryptValue, pwd = pwd).toLong()
}

fun SharedPreferences.Editor.putEncryptLong(
    key: String,
    value: Long,
    pwd: String? = DEFAULT_PWD
): SharedPreferences.Editor {
    this.putString(
        key,
        encryptPreference(plainText = value.toString(), pwd = pwd)
    )
    return this
}

fun SharedPreferences.getEncryptFloat(key: String, defValue: Float = 0F, pwd: String? = DEFAULT_PWD): Float {
    val encryptValue = this.getString(key, null)
    return if (encryptValue == null) defValue else decryptPreference(cipherText = encryptValue, pwd = pwd).toFloat()
}

fun SharedPreferences.Editor.putEncryptFloat(
    key: String,
    value: Float,
    pwd: String? = DEFAULT_PWD
): SharedPreferences.Editor {
    this.putString(
        key,
        encryptPreference(plainText = value.toString(), pwd = pwd)
    )
    return this
}

fun SharedPreferences.getEncryptBoolean(key: String, defValue: Boolean = false, pwd: String? = DEFAULT_PWD): Boolean {
    val encryptValue = this.getString(key, null)
    return if (encryptValue == null) defValue else decryptPreference(cipherText = encryptValue, pwd = pwd).toBoolean()
}

fun SharedPreferences.Editor.putEncryptBoolean(
    key: String,
    value: Boolean,
    pwd: String? = DEFAULT_PWD
): SharedPreferences.Editor {
    this.putString(
        key,
        encryptPreference(plainText = value.toString(), pwd = pwd)
    )
    return this
}

fun SharedPreferences.getEncryptStringSet(
    key: String,
    defValues: Set<String> = HashSet(),
    pwd: String? = DEFAULT_PWD
): Set<String> {
    val encryptSet = this.getStringSet(key, null)
    return if (encryptSet == null) {
        defValues
    } else {
        val decryptSet = HashSet<String>()
        for (encryptValue in encryptSet) {
            decryptSet.add(decryptPreference(cipherText = encryptValue, pwd = pwd))
        }
        decryptSet
    }
}

fun SharedPreferences.Editor.putEncryptStringSet(
    key: String,
    values: Set<String>,
    pwd: String? = DEFAULT_PWD
): SharedPreferences.Editor {
    val encryptSet = HashSet<String>()
    for (value in values) {
        encryptSet.add(encryptPreference(plainText = value, pwd = pwd))
    }
    this.putStringSet(key, encryptSet)
    return this
}

/**
 * encrypt function
 * @return cipherText base64
 */
private fun encryptPreference(context: Context = CoreApp.application, plainText: String, pwd: String? = null): String {

    return AesRsaEncrypt.getInstance(context).encrypt(pwd, plainText)
}

/**
 * decrypt function
 * @return plainText
 */
private fun decryptPreference(context: Context = CoreApp.application, cipherText: String, pwd: String? = null): String {
    return AesRsaEncrypt.getInstance(context).decrypt(pwd, cipherText)
}

private const val DEFAULT_PWD = "l1r2z3o2q1n"
