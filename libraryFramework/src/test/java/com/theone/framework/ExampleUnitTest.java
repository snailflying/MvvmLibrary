package com.theone.framework;

import androidx.annotation.NonNull;

import org.junit.Test;

import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.Cipher;

import static org.junit.Assert.assertEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    private static String RSA = "RSA";

    @Test
    public void addition_isCorrect() {
        String pattern_str = "acs/v1/ac_shows/(.*)/get_encrypt";
        String url = "https://www.lalala.com/acs/v1/ac_shows/13liu3456789/get_encrypt?key=vall";
        Pattern pattern = Pattern.compile(pattern_str);
        Matcher matcher = pattern.matcher(url);
        if (matcher.find()) {
            String showId = matcher.group(1);
            System.out.print(showId);
            assertEquals(showId, "13liu3456789");
        } else {
            System.out.print("not match");
        }

    }

    @Test
    public void testCombineSplit() {
        StringBuilder result = new StringBuilder();
        ArrayList<String> projects = new ArrayList<String>();
        projects.add("a");
        projects.add("b");
        for (String project : projects) {
            result.append(project).append(",");
        }
        String resultStr = result.toString().substring(0, result.length() - 1);
        assertEquals(resultStr, "a,b");

        StringBuilder result1 = new StringBuilder();
        ArrayList<String> projects1 = new ArrayList<String>();
        projects1.add("a");
        for (String project : projects1) {
            result1.append(project).append(",");
        }
        String resultStr1 = result1.toString().substring(0, result1.length() - 1);
        assertEquals(resultStr1, "a");
    }

    @Test
    public void testSplit() {

        List<String> input1 = Arrays.asList("a".split(","));
        List<String> result1 = new ArrayList<>();
        result1.add("a");
        assertEquals(input1, result1);

        List<String> input2 = Arrays.asList("a,b".split(","));
        List<String> result2 = new ArrayList<>();
        result2.add("a");
        result2.add("b");
        assertEquals(input2, result2);

        List<String> input3 = Arrays.asList("a,".split(","));
        List<String> result3 = new ArrayList<>();
        result3.add("a");
        assertEquals(input3, result3);
    }

    @Test
    public void testEncryptByPrivateKey() {
        String privateKey = "MIIBVQIBADANBgkqhkiG9w0BAQEFAASCAT8wggE7AgEAAkEAhzJJKH3r7qDTaQvdTn8bJScTiVECxRUOtuNylICmd+vq6N9DUk8N6NAIXiqIHMrEdoywQUSjCnajlzwtLEs4DQIDAQABAkBdXv9jtcPSJMSdkhIf+mz29cvqVEbDck2dReyGX2uY+hamAYwoxhff/gbRonj5OqR179fK34pYfPis+3JoRdlBAiEAyLrVqSpiEmFyRQfOKO7CE/s2e6OVY3unQXRqy+qYEfkCIQCsbBf0wFRqP9SLfgklhlB3Gr4YgHVV7sL6mSmX/DtbtQIhAIcw5nQPuoucm+SIND53R7lDaVduPlAJWQWJjeAW+SKpAiEAhmkpb6my5LTnqupQlQkUlxSo1g7l6VxccOCPNSTy3PUCIGrG25fPjS9sC7jhrBEa7LxA99mUadUVomxGHmq4QYIo";
        String data = "6006447057d98e46b2a58741|shshshshs";
        String result = encryptByPrivateKey(data, privateKey);
        System.out.print("result:" + result);
    }

    @Test
    public void testDecryptByPublicKey() {
        String publicKey = "MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBAIcySSh96+6g02kL3U5/GyUnE4lRAsUVDrbjcpSApnfr6ujfQ1JPDejQCF4qiBzKxHaMsEFEowp2o5c8LSxLOA0CAwEAAQ==";
        String data = "AYQdopjcLKgNfgM/X3xj9ULFzAgXgd3m8BsiuxTF8KAn1L4/QP4Ya44LiJ/v+/ZlTjbTA2fVJVKzcVjQLict5w==";
        String result = decryptByPublicKey(data, publicKey);
        System.out.print("result:" + result);
    }

    /**
     * RSA 使用私钥进行解密
     * 如果返回空则表明解密错误
     */
    public static String decryptByPublicKey(@NonNull String data, @NonNull String publicKeyBase64) {
        try {
            //base64编码的私钥
            byte[] decoded = Base64Util.INSTANCE.decode(publicKeyBase64);
            RSAPublicKey pubKey = (RSAPublicKey) KeyFactory.getInstance(RSA).generatePublic(new X509EncodedKeySpec(decoded));
            //64位解码加密后的字符串
            byte[] inputByte = Base64Util.INSTANCE.decode(data);
            //RSA解密
            Cipher cipher = Cipher.getInstance(RSA);
            cipher.init(Cipher.DECRYPT_MODE, pubKey);
            return new String(cipher.doFinal(inputByte));
        } catch (Exception e) {
            System.out.print("解密失败，src：{" + data + "}, publicKeyBase64:{" + publicKeyBase64 + "+}" + e);
            return null;
        }
    }

    /**
     * 执行加密
     */
    public static String encryptByPrivateKey(String src, String privateKey) {
        try {
            byte[] decoded = Base64Util.INSTANCE.decode(privateKey);
            RSAPrivateKey rsaPrivateKey = (RSAPrivateKey) KeyFactory.getInstance(RSA)
                    .generatePrivate(new PKCS8EncodedKeySpec(decoded));
            Cipher cipher = Cipher.getInstance(RSA);
            cipher.init(Cipher.ENCRYPT_MODE, rsaPrivateKey);
            byte[] srcBytes = src.getBytes();
            byte[] resultBytes = cipher.doFinal(srcBytes);
            return Base64Util.INSTANCE.encode(resultBytes);
        } catch (Exception exp) {
            System.out.print("加密失败，src：{" + src + "}, privateKey:{" + privateKey + "+}" + exp);
            return null;
        }
    }

}