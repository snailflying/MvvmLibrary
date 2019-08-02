package com.theone.framework.ext

import android.content.Context
import android.content.SharedPreferences
import com.themone.core.base.impl.CoreApp
import com.theone.framework.encrypt.AesRsaEncrypt

/**
 * @Author zhiqiang
 * @Date 2019-06-22
 * @Email liuzhiqiang@theone.com
 * @Description
 */

fun SharedPreferences.getEncryptString(key: String, defValue: String = "", pwd: String? = "l1r2z3o2q1n"): String {
    val encryptValue = this.getString(encryptPreference(plainText = key, pwd = pwd), null)
    return if (encryptValue == null) defValue else decryptPreference(cipherText = encryptValue, pwd = pwd)
}

fun SharedPreferences.Editor.putEncryptString(
    key: String,
    value: String,
    pwd: String? = "l1r2z3o2q1n"
): SharedPreferences.Editor {
    this.putString(encryptPreference(plainText = key, pwd = pwd), encryptPreference(plainText = value, pwd = pwd))
    return this
}


fun SharedPreferences.getEncryptInt(key: String, defValue: Int = 0, pwd: String? = "l1r2z3o2q1n"): Int {
    val encryptValue = this.getString(encryptPreference(plainText = key, pwd = pwd), null)

    return if (encryptValue == null) defValue else decryptPreference(cipherText = encryptValue, pwd = pwd).toInt()
}

fun SharedPreferences.Editor.putEncryptInt(
    key: String,
    value: Int,
    pwd: String? = "l1r2z3o2q1n"
): SharedPreferences.Editor {
    this.putString(
        encryptPreference(plainText = key, pwd = pwd),
        encryptPreference(plainText = value.toString(), pwd = pwd)
    )
    return this
}


fun SharedPreferences.getEncryptLong(key: String, defValue: Long = 0, pwd: String? = "l1r2z3o2q1n"): Long {
    val encryptValue = this.getString(encryptPreference(plainText = key, pwd = pwd), null)
    return if (encryptValue == null) defValue else decryptPreference(cipherText = encryptValue, pwd = pwd).toLong()
}

fun SharedPreferences.Editor.putEncryptLong(
    key: String,
    value: Long,
    pwd: String? = "l1r2z3o2q1n"
): SharedPreferences.Editor {
    this.putString(
        encryptPreference(plainText = key, pwd = pwd),
        encryptPreference(plainText = value.toString(), pwd = pwd)
    )
    return this
}

fun SharedPreferences.getEncryptFloat(key: String, defValue: Float = 0F, pwd: String? = "l1r2z3o2q1n"): Float {
    val encryptValue = this.getString(encryptPreference(plainText = key, pwd = pwd), null)
    return if (encryptValue == null) defValue else decryptPreference(cipherText = encryptValue, pwd = pwd).toFloat()
}

fun SharedPreferences.Editor.putEncryptFloat(
    key: String,
    value: Float,
    pwd: String? = "l1r2z3o2q1n"
): SharedPreferences.Editor {
    this.putString(
        encryptPreference(plainText = key, pwd = pwd),
        encryptPreference(plainText = value.toString(), pwd = pwd)
    )
    return this
}

fun SharedPreferences.getEncryptBoolean(key: String, defValue: Boolean = false, pwd: String? = "l1r2z3o2q1n"): Boolean {
    val encryptValue = this.getString(encryptPreference(plainText = key, pwd = pwd), null)
    return if (encryptValue == null) defValue else decryptPreference(cipherText = encryptValue, pwd = pwd).toBoolean()
}

fun SharedPreferences.Editor.putEncryptBoolean(
    key: String,
    value: Boolean,
    pwd: String? = "l1r2z3o2q1n"
): SharedPreferences.Editor {
    this.putString(
        encryptPreference(plainText = key, pwd = pwd),
        encryptPreference(plainText = value.toString(), pwd = pwd)
    )
    return this
}

fun SharedPreferences.getEncryptStringSet(
    key: String,
    defValues: Set<String> = HashSet(),
    pwd: String? = "l1r2z3o2q1n"
): Set<String> {
    val encryptSet = this.getStringSet(encryptPreference(plainText = key, pwd = pwd), null)
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
    pwd: String? = "l1r2z3o2q1n"
): SharedPreferences.Editor {
    val encryptSet = HashSet<String>()
    for (value in values) {
        encryptSet.add(encryptPreference(plainText = value, pwd = pwd))
    }
    this.putStringSet(encryptPreference(plainText = key, pwd = pwd), encryptSet)
    return this
}

/**
 * encrypt function
 * @return cipherText base64
 */
private fun encryptPreference(context: Context = CoreApp.APPLICATION, plainText: String, pwd: String? = null): String {

    return AesRsaEncrypt.getInstance(context).encrypt(pwd, plainText)
}

/**
 * decrypt function
 * @return plainText
 */
private fun decryptPreference(context: Context = CoreApp.APPLICATION, cipherText: String, pwd: String? = null): String {
    return AesRsaEncrypt.getInstance(context).decrypt(pwd, cipherText)
}