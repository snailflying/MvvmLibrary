package com.theone.framework.ext

/**
 * @Author zhiqiang
 * @Date 2019-06-19
 * @Email liuzhiqiang@theone.com
 * @Description https://github.com/fengzhizi715/SAF-Kotlin-Utils/blob/master/saf-kotlin-ext/src/main/java/com/safframework/ext/Boolean%2BExtension.kt
 */
infix fun <T> Boolean.then(value: T?) = if (this) value else null

fun <T> Boolean.then(value: T?, default: T) = if (this) value else default

inline fun <T> Boolean.then(function: () -> T, default: T) = if (this) function() else default

inline fun <T> Boolean.then(function: () -> T, default: () -> T) = if (this) function() else default()

inline infix fun <reified T> Boolean.then(function: () -> T) = if (this) function() else null