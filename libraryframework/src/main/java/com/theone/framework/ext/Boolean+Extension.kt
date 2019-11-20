package com.theone.framework.ext

/**
 * @Author zhiqiang
 * @Date 2019-06-19
 * @Email liuzhiqiang@theone.com
 * @Description Stream流式编程，替代if(...){...}else if(...){...}else{...}
 * 参考：https://github.com/fengzhizi715/SAF-Kotlin-Utils/blob/master/saf-kotlin-ext/src/main/java/com/safframework/ext/Boolean%2BExtension.kt
 */
infix fun <R> Boolean.thenReturn(value: R?): R? = if (this) value else null

infix fun <R> Boolean.thenReturn(function: () -> R?): R? = if (this) function() else null

fun <R> Boolean.thenReturn(value: R, default: R): R = if (this) value else default

inline fun <R> Boolean.thenReturn(function: () -> R, default: R): R = if (this) function() else default

inline fun <R> Boolean.thenReturn(function: () -> R, default: () -> R): R = if (this) function() else default()

/**
 * if(...){...}
 * @receiver Boolean
 * @param function Function0<R>
 * @return Boolean
 */
inline infix fun <reified R> Boolean.then(function: () -> R): Boolean {
    if (this) {
        function()
        return true
    }
    return false
}

/**
 * else if(...){...}
 * @receiver Boolean
 * @param condition Boolean
 * @param function Function0<R>
 * @return Boolean
 */
inline fun <reified R> Boolean.thenIf(condition: Boolean, function: () -> R): Boolean {
    if (!this && condition) {
        function()
        return true
    }
    return false
}

/**
 * else{...}
 * @receiver Boolean
 * @param function Function0<R>
 * @return Boolean
 */
inline infix fun <reified R> Boolean.thenElse(function: () -> R) {
    if (!this) {
        function()
    }
}
