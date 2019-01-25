package com.trotyzyq.util;

import org.apache.commons.codec.binary.Base64;
import org.springframework.util.StringUtils;

import javax.crypto.Cipher;
import java.io.*;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 加密验签工具
 * @author zyq
 */
public class RsaSignature {

    private static final String SIGN_TYPE_RSA                  = "RSA";
    private static final String SIGN_SHA256RSA_ALGORITHMS      = "SHA256WithRSA";
    private static final String CHARSET = "UTF-8";

    /** RSA最大加密明文大小  */
    private static final int MAX_ENCRYPT_BLOCK = 117;

    /**
     * RSA最大解密密文大小
     * 密钥长度修改为2048位为256，1024位时为128
     */
    private static final int MAX_DECRYPT_BLOCK = 256;

    /**
     *  rsa内容签名
     * @param content
     * @param privateKey
     * @return 成功返回字符串，失败null
     */
    public static String rsaSign(String content, String privateKey){
        return rsa256Sign(content, privateKey);
    }

    /**
     * sha256WithRsa 加签
     * @param content
     * @param privateKey 密钥长度2048位
     * @return 成功str,失败null
     */
    public static String rsa256Sign(String content, String privateKey){

        try {
            PrivateKey priKey = getPrivateKeyFromPKCS8(SIGN_TYPE_RSA,
                    new ByteArrayInputStream(privateKey.getBytes()));

            java.security.Signature signature = java.security.Signature
                    .getInstance(SIGN_SHA256RSA_ALGORITHMS);

            signature.initSign(priKey);

            if (StringUtil.isNotNull(CHARSET)) {
                signature.update(content.getBytes());
            } else {
                signature.update(content.getBytes(CHARSET));
            }

            byte[] signed = signature.sign();

            return new String(Base64.encodeBase64(signed));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 从证书中获取私钥
     * @param algorithm
     * @param ins
     * @return
     * @throws Exception
     */
    public static PrivateKey getPrivateKeyFromPKCS8(String algorithm,
                                                    InputStream ins) throws Exception {
        if (ins == null || StringUtil.isNull(algorithm)) {
            return null;
        }
        KeyFactory keyFactory = KeyFactory.getInstance(algorithm);
        byte[] encodedKey = StreamUtils.readText(ins).getBytes();
        encodedKey = Base64.decodeBase64(encodedKey);
        return keyFactory.generatePrivate(new PKCS8EncodedKeySpec(encodedKey));
    }

    /**
     * 通过公钥验签
     * @param params json字符串转换成map后的参数
     * @param publicKey 公钥
     * @return 验签成功 true,失败false
     */
    public static boolean rsaCheckV2(Map<String, String> params, String publicKey){
        String sign = params.get("sign");
        String content = getSignCheckContentV2(params);
        return rsa256CheckContent(content, sign, publicKey);
    }

    /**
     * 对map参数进行字符串长度组合后
     * @param params
     * @return String
     */
    public static String getSignCheckContentV2(Map<String, String> params) {
        if (params == null) {
            return null;
        }
        params.remove("sign");

        StringBuffer content = new StringBuffer();
        List<String> keys = new ArrayList<String>(params.keySet());
        Collections.sort(keys);

        for (int i = 0; i < keys.size(); i++) {
            String key = keys.get(i);
            String value = params.get(key);
            content.append((i == 0 ? "" : "&") + key + "=" + value);
        }

        return content.toString();
    }

    /**
     * 对map参数进行验签
     * @param content
     * @param sign
     * @param publicKey
     * @return
     */
    public static boolean rsa256CheckContent(String content, String sign, String publicKey){
        try {
            PublicKey pubKey = getPublicKeyFromX509("RSA",
                    new ByteArrayInputStream(publicKey.getBytes()));

            java.security.Signature signature = java.security.Signature
                    .getInstance(SIGN_SHA256RSA_ALGORITHMS);

            signature.initVerify(pubKey);

            if (StringUtils.isEmpty(CHARSET)) {
                signature.update(content.getBytes());
            } else {
                signature.update(content.getBytes(CHARSET));
            }

            return signature.verify(Base64.decodeBase64(sign.getBytes()));
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 从X509证书中获取公钥
     * @param algorithm
     * @param ins
     * @return
     * @throws Exception
     */
    public static PublicKey getPublicKeyFromX509(String algorithm,
                                                 InputStream ins) throws Exception {
        KeyFactory keyFactory = KeyFactory.getInstance(algorithm);

        StringWriter writer = new StringWriter();
        StreamUtils.io(new InputStreamReader(ins), writer);

        byte[] encodedKey = writer.toString().getBytes();

        encodedKey = Base64.decodeBase64(encodedKey);

        return keyFactory.generatePublic(new X509EncodedKeySpec(encodedKey));
    }


/**
     * 公钥加密 已验证
     * @param content
     * @param publicKey 秘钥长度2048位
     * @return
     */
    public static String rsaEncrypt(String content, String publicKey){
        try {
            PublicKey pubKey = getPublicKeyFromX509(SIGN_TYPE_RSA,
                    new ByteArrayInputStream(publicKey.getBytes()));
            Cipher cipher = Cipher.getInstance(SIGN_TYPE_RSA);
            cipher.init(Cipher.ENCRYPT_MODE, pubKey);
            byte[] data = content.getBytes(CHARSET);
            int inputLen = data.length;
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            int offSet = 0;
            byte[] cache;
            int i = 0;
            // 对数据分段加密
            while (inputLen - offSet > 0) {
                if (inputLen - offSet > MAX_ENCRYPT_BLOCK) {
                    cache = cipher.doFinal(data, offSet, MAX_ENCRYPT_BLOCK);
                } else {
                    cache = cipher.doFinal(data, offSet, inputLen - offSet);
                }
                out.write(cache, 0, cache.length);
                i++;
                offSet = i * MAX_ENCRYPT_BLOCK;
            }
            byte[] encryptedData = Base64.encodeBase64(out.toByteArray());
            out.close();

            return new String(encryptedData, CHARSET);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 私钥解密 已验证
     * @param content
     * @param privateKey 秘钥长度2048位
     * @return
     */
    public static String rsaDecrypt(String content, String privateKey){
        try {
            PrivateKey priKey = getPrivateKeyFromPKCS8(SIGN_TYPE_RSA,
                    new ByteArrayInputStream(privateKey.getBytes()));
            Cipher cipher = Cipher.getInstance(SIGN_TYPE_RSA);
            cipher.init(Cipher.DECRYPT_MODE, priKey);
            byte[] encryptedData = Base64.decodeBase64(content.getBytes(CHARSET));
            int inputLen = encryptedData.length;
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            int offSet = 0;
            byte[] cache;
            int i = 0;
            // 对数据分段解密
            while (inputLen - offSet > 0) {
                if (inputLen - offSet > MAX_DECRYPT_BLOCK) {
                    cache = cipher.doFinal(encryptedData, offSet, MAX_DECRYPT_BLOCK);
                } else {
                    cache = cipher.doFinal(encryptedData, offSet, inputLen - offSet);
                }
                out.write(cache, 0, cache.length);
                i++;
                offSet = i * MAX_DECRYPT_BLOCK;
            }
            byte[] decryptedData = out.toByteArray();
            out.close();

            return new String(decryptedData, CHARSET);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
