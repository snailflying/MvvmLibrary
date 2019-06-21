package com.themone.core.serviceloader;

import androidx.annotation.NonNull;
import com.themone.core.util.LogUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ServiceConfigurationError;

/**
 * @Author zhiqiang
 * @Email liuzhiqiang@theone.com
 * @Date 2019-06-06
 * @Description 接口隔离, 让Module可以反向获取App的实例。
 * @see java.util.ServiceLoader
 */
public class ServicesLoader {
    private static final String TAG = "MyServicesLoader";
    private static HashMap<String, Object> servers = new HashMap<>();

    private static LoadEntity loadEntity;
    private final static String fullName = "assets/services/config";

    public static <T> T getService(Class<T> clz) {
        return getService(clz, null);
    }

    public static <T> T getService(@NonNull Class<T> clz, Class<? extends T> clzDefault) {
        T t = (T) servers.get(clz.getName());
        if (null != t) {
            return t;
        }
        return getLoad().load(clz, clzDefault);
    }

    private static LoadEntity getLoad() {
        if (null == loadEntity) {
            loadEntity = new LoadEntity();
        }
        return loadEntity;
    }


    private static Map<String, String> parse(URL u)
            throws ServiceConfigurationError {
        InputStream in = null;
        BufferedReader r = null;
        Map<String, String> names = new HashMap<>();
        try {
            in = u.openStream();
            r = new BufferedReader(new InputStreamReader(in, "utf-8"));
            int lc = 1;
            while ((lc = parseLine(r, lc, names)) >= 0) {
                ;
            }
        } catch (IOException x) {
        } finally {
            try {
                if (r != null) {
                    r.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException y) {
//                fail(service, "Error closing configuration file", y);
                LogUtil.d(TAG, "" + y);
            }
        }
        return names;
    }

    private static int parseLine(BufferedReader r, int lc,
                                 Map<String, String> names)
            throws IOException, ServiceConfigurationError {
        String ln = r.readLine();
        if (ln == null) {
            return -1;
        }
        int ci = ln.indexOf('#');
        if (ci >= 0) {
            ln = ln.substring(0, ci);
        }
        ln = ln.trim();

        String lns[] = ln.split(":");
        if (lns.length == 2) {
            if (!isJavaIdentifier(lns[0]) || !isJavaIdentifier(lns[1])) {
                return -1;
            }
            names.put(lns[0], lns[1]);
        } else {
            return -1;
        }
        return lc + 1;
    }

    private static boolean isJavaIdentifier(String ln) {
        int n = ln.length();
        if (n != 0) {
            if ((ln.indexOf(' ') >= 0) || (ln.indexOf('\t') >= 0)) {
                return false;
            }
            int cp = ln.codePointAt(0);
            if (!Character.isJavaIdentifierStart(cp)) {
                return false;
            }
            for (int i = Character.charCount(cp); i < n; i += Character.charCount(cp)) {
                cp = ln.codePointAt(i);
                if (!Character.isJavaIdentifierPart(cp) && (cp != '.')) {
                    return false;
                }
            }
        }
        return true;
    }

    private static void fail(Class<?> service, String msg, Throwable cause)
            throws ServiceConfigurationError {
        throw new ServiceConfigurationError(service.getName() + ": " + msg,
                cause);
    }

    private static void fail(Throwable cause)
            throws ServiceConfigurationError {
        throw new ServiceConfigurationError("assets/services/config not found",
                cause);
    }

    static class LoadEntity {

        Map<String, String> pending = null;
        Load dzmLoad;

        LoadEntity() {
            dzmLoad = new Load();
            initLoad();
        }

        private void initLoad() {
            try {
                pending = parse(dzmLoad.initLoad(fullName));
            } catch (Exception e) {
                fail(e);
            }
        }

        <T> T load(@NonNull Class<T> server, Class<? extends T> clzDefault) {

            String cn = pending.get(server.getName());

            Class<?> c = null;
            if (cn != null) {
                try {
                    c = Class.forName(cn, false, dzmLoad.getLoader());
                } catch (ClassNotFoundException x) {
                    if (clzDefault != null) {
                        c = clzDefault;
                    } else {
                        fail(server,
                                "Provider " + cn + " not found", x);
                    }
                }
            } else {
                if (clzDefault != null) {
                    c = clzDefault;
                } else {
                    fail(server,
                            "Provider " + server.getName() + " not found", new NullPointerException());
                }
            }

            if (!server.isAssignableFrom(c)) {
                ClassCastException cce = new ClassCastException(
                        server.getCanonicalName() + " is not assignable from " + c.getCanonicalName());
                fail(server,
                        "Provider " + cn + " not a subtype", cce);
            }
            try {
                T p = server.cast(c.newInstance());
                servers.put(server.getName(), p);
                return p;
            } catch (Throwable x) {
                fail(server,
                        "Provider " + cn + " could not be instantiated",
                        x);
            }
            throw new Error();
        }
    }

}
