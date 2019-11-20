package com.theone.framework.ext

/**
 * @Author zhiqiang
 * @Date 2019-06-19
 * @Email liuzhiqiang@moretickets.com
 * @Description 拓展Standard.kt
 */


/**
 * 如果args全不为空，则调用function
 * @param args Array<out Any?>
 * @param function Function0<R>
 */
inline fun <R> isNotNull(vararg args: Any?, function: () -> R) {
    when {
        args.filterNotNull().size == args.size -> function()
    }
}

/**
 * 如果T为空，则通过default来获取T ,跟ELVIS用法相同
 *
 * @receiver T
 * @param function Function1<T, R>
 * @return Boolean
 */
fun <T> T?.elvis(default: T): T {
    return this ?: default
}

/**
 * 如果T为空，则通过function来获取T,拓展ELVIS
 *
 * @receiver T
 * @param function Function1<T, R>
 * @return Boolean
 */
inline fun <T> T?.elvis(function: () -> T): T {
    return this ?: function()
}

/**
 * 如果T不为空，将T通过function处理成R返回，否则返回default
 * 场景：类同于{@link runNotNull(function: (T) -> R, elvis: () -> R)}
 *
 * @receiver T
 * @param function Function1<T, R>
 * @return Boolean
 */
fun <T, R> T?.runNotNull(function: (T) -> R, elvis: R): R {
    return if (this != null) {
        function(this)
    } else {
        elvis
    }
}

/**
 * 如果T不为空，则调用function，否则调用orElse
 * 场景：不但可实现[alsoNotNull]与[thenNull]同时配合使用效果，而且有不同返回值
 *
 * @receiver T
 * @param function Function1<T, R>
 * @param orElse Function0<R>
 */
inline fun <T, R> T?.runNotNull(function: (T) -> R, elvis: () -> R): R {
    return if (this != null)
        function(this)
    else
        elvis()
}

/**
 * 如果T不为空，则调用function
 * 场景：多配合[thenNull]使用 例如：a.alsoNotNull{}.thenNull{}
 *
 * @receiver T
 * @param function Function1<T, R>
 * @return Boolean
 */
fun <T, R> T?.alsoNotNull(function: (T) -> R): T? {
    if (this != null) {
        function(this)
    }
    return this
}

/**
 * 作为Stream流的串接符,T为null时走function
 * 场景：多配合[alsoNotNull]使用  例如：a.alsoNotNull{}.thenNull{}
 *
 * @receiver T
 * @param function Function1<T, R>
 */
inline fun <T> T?.thenNull(function: () -> T): T? {
    if (this == null) {
        function()
    }
    return this
}
