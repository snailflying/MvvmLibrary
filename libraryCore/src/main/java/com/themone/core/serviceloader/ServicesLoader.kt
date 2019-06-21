package com.themone.core.serviceloader

import java.io.BufferedReader
import java.io.IOException
import java.net.URL
import java.util.HashMap
import java.util.ServiceConfigurationError

/**
 * @Author zhiqiang
 * @Email liuzhiqiang@theone.com
 * @Date 2019-06-06
 * @Description 接口隔离, 让Module可以反向获取App的实例。
 * @see java.util.ServiceLoader
 */
object ServicesLoader {
    private const val TAG = "MyServicesLoader"
    private val servers = HashMap<String, Any>()

    private var loadEntity: LoadEntity? = null
    private const val fullName = "assets/services/config"

    private val load: LoadEntity
        get() {
            if (null == loadEntity) {
                loadEntity = LoadEntity()
            }
            return loadEntity!!
        }

    fun <T> getService(clz: Class<T>): T? {
        return getService(clz, null)
    }

    fun <T> getService(clz: Class<T>, clzDefault: Class<out T>?): T? {
        val t = servers[clz.name] as T?
        return t ?: load.load(clz, clzDefault)
    }


    @Throws(ServiceConfigurationError::class)
    private fun parse(u: URL): Map<String, String> {
        val names = HashMap<String, String>()

        u.openStream()?.bufferedReader()?.use {
            var lc = 1
            do {
                lc = parseLine(it, lc, names)
            } while (lc>=0)
        }

        return names
    }

    @Throws(IOException::class, ServiceConfigurationError::class)
    private fun parseLine(r: BufferedReader, lc: Int,
                          names: MutableMap<String, String>): Int {
        var ln: String? = r.readLine() ?: return -1
        val ci = ln!!.indexOf('#')
        if (ci >= 0) {
            ln = ln.substring(0, ci)
        }
        ln = ln.trim { it <= ' ' }

        val lns = ln.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        if (lns.size == 2) {
            if (!isJavaIdentifier(lns[0]) || !isJavaIdentifier(lns[1])) {
                return -1
            }
            names[lns[0]] = lns[1]
        } else {
            return -1
        }
        return lc + 1
    }

    private fun isJavaIdentifier(ln: String): Boolean {
        val n = ln.length
        if (n != 0) {
            if (ln.indexOf(' ') >= 0 || ln.indexOf('\t') >= 0) {
                return false
            }
            var cp = ln.codePointAt(0)
            if (!Character.isJavaIdentifierStart(cp)) {
                return false
            }
            var i = Character.charCount(cp)
            while (i < n) {
                cp = ln.codePointAt(i)
                if (!Character.isJavaIdentifierPart(cp) && cp != '.'.toInt()) {
                    return false
                }
                i += Character.charCount(cp)
            }
        }
        return true
    }

    @Throws(ServiceConfigurationError::class)
    private fun fail(service: Class<*>, msg: String, cause: Throwable) {
        throw ServiceConfigurationError(service.name + ": " + msg,
                cause)
    }

    @Throws(ServiceConfigurationError::class)
    private fun fail(cause: Throwable) {
        throw ServiceConfigurationError("assets/services/config not found",
                cause)
    }

    internal class LoadEntity {

        var pending: Map<String, String>? = null
        var dzmLoad: Load

        init {
            dzmLoad = Load()
            initLoad()
        }

        private fun initLoad() {
            try {
                pending = parse(dzmLoad.initLoad(fullName))
            } catch (e: Exception) {
                fail(e)
            }

        }

        fun <T> load(server: Class<T>, clzDefault: Class<out T>?): T? {

            val cn = pending!![server.name]

            var c: Class<*>? = null
            if (cn != null) {
                try {
                    c = Class.forName(cn, false, dzmLoad.loader)
                } catch (x: ClassNotFoundException) {
                    if (clzDefault != null) {
                        c = clzDefault
                    } else {
                        fail(server,
                                "Provider $cn not found", x)
                    }
                }

            } else {
                if (clzDefault != null) {
                    c = clzDefault
                } else {
                    fail(server,
                            "Provider " + server.name + " not found", NullPointerException())
                }
            }

            if (!server.isAssignableFrom(c!!)) {
                val cce = ClassCastException(
                        "${server.canonicalName} is not assignable from ${c.canonicalName}")
                fail(server,
                        "Provider $cn not a subtype", cce)
            }
            try {
                val p = server.cast(c.newInstance())
                servers[server.name] = p as Any
                return p
            } catch (x: Throwable) {
                fail(server,
                        "Provider $cn could not be instantiated",
                        x)
            }

            throw Error()
        }
    }

}
