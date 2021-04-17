package com.theone.framework.encrypt

import javax.crypto.Cipher

/**
 * @Author zhiqiang
 * @Date 2019-05-14
 * @Email liuzhiqiang@theone.com
 * @Description 加解密策接口
 */
interface IEncrypt {

    @Throws(Exception::class)
    fun getEncryptCipher(pwd: String? = null): Cipher

    @Throws(Exception::class)
    fun getDecryptCipher(pwd: String? = null): Cipher

    @Throws(Exception::class)
    fun encrypt(pwd: String?=null, plainText: String): String

    @Throws(Exception::class)
    fun decrypt(pwd: String?=null, encryptedText: String): String
}
