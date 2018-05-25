package com.elan.common.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class EncrypUtil {
    private Logger log = LoggerFactory.getLogger(EncrypUtil.class);
    private String defaultCharset = "UTF-8";

    //KeyGenerator 提供对称密钥生成器的功能，支持各种算法
    private KeyGenerator keygen;

    //SecretKey 负责保存对称密钥
    private SecretKey secretKey;

    //根据字节数组生成AES密钥
    private  SecretKeySpec keySpec;

    // 创建密码器,负责完成加密或解密工作
    private Cipher cipher;

    //加密算法名称
    private String secretName;

    public EncrypUtil(String secretName,String key,int keyLength) throws NoSuchAlgorithmException, NoSuchPaddingException {

        init(secretName,key,keyLength);
    }

    public EncrypUtil(String secretName,String key) throws NoSuchAlgorithmException, NoSuchPaddingException{
        init(secretName,key,128);
    }

    private void init(String secretName,String key,int keyLength)throws NoSuchAlgorithmException, NoSuchPaddingException{
        this.secretName = secretName;
        if(keyLength!=128 && keyLength!=192 && keyLength!=256){
            log.error("密钥长度必须为128、192或256！");
            return;
        }
        //1.构造密钥生成器，指定为AES算法,不区分大小写
        keygen = KeyGenerator.getInstance(secretName);

        //2.根据ecnodeRules规则初始化密钥生成器,生成一个128位的随机源,根据传入的字节数组
        keygen.init(keyLength, new SecureRandom(key.getBytes()));

        //3.产生原始对称密钥
        secretKey = keygen.generateKey();

        //4.获得原始对称密钥的字节数组
        byte[] enCodeFormat = secretKey.getEncoded();

        //5.根据字节数组生成AES密钥
        keySpec = new SecretKeySpec(enCodeFormat, secretName);

        //6.根据指定算法AES自成密码器,创建密码器
        cipher = Cipher.getInstance(secretName);
    }
    /**
     * 加密
     *
     * @param data 需要加密的内容
     * @return
     */
    public String encrypt(String data) throws IllegalBlockSizeException, BadPaddingException, InvalidKeyException, UnsupportedEncodingException {
        return doAES(data, Cipher.ENCRYPT_MODE);
    }

    /**
     * 解密
     *
     * @param data 待解密内容
     * @return
     */
    public String decrypt(String data) throws IllegalBlockSizeException, BadPaddingException, InvalidKeyException, UnsupportedEncodingException {
        return doAES(data, Cipher.DECRYPT_MODE);
    }

    /**
     * 加解密
     *
     * @param data 待处理数据
     * @param mode 加解密mode
     * @return
     */
    private String doAES(String data, int mode) throws InvalidKeyException, BadPaddingException, IllegalBlockSizeException, UnsupportedEncodingException {


            //判断是加密还是解密
            boolean encrypt = mode == Cipher.ENCRYPT_MODE;
            byte[] content;
            //true 加密内容 false 解密内容
            if (encrypt) {
                content = data.getBytes(defaultCharset);
            } else {
                content = parseHexStr2Byte(data);
            }

            //7.初始化密码器，第一个参数为加密(Encrypt_mode)或者解密解密(Decrypt_mode)操作，第二个参数为使用的KEY
            cipher.init(mode, keySpec);// 初始化
            byte[] result = cipher.doFinal(content);
            if (encrypt) {
                //将二进制转换成16进制
                return parseByte2HexStr(result);
            } else {
                return new String(result, defaultCharset);
            }

    }
    /**
     * 将二进制转换成16进制
     *
     * @param buf
     * @return
     */
    public static String parseByte2HexStr(byte buf[]) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < buf.length; i++) {
            String hex = Integer.toHexString(buf[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            sb.append(hex.toUpperCase());
        }
        return sb.toString();
    }
    /**
     * 将16进制转换为二进制
     *
     * @param hexStr
     * @return
     */
    public static byte[] parseHexStr2Byte(String hexStr) {
        if (hexStr.length() < 1) {
            return null;
        }
        byte[] result = new byte[hexStr.length() / 2];
        for (int i = 0; i < hexStr.length() / 2; i++) {
            int high = Integer.parseInt(hexStr.substring(i * 2, i * 2 + 1), 16);
            int low = Integer.parseInt(hexStr.substring(i * 2 + 1, i * 2 + 2), 16);
            result[i] = (byte) (high * 16 + low);
        }
        return result;
    }
    public static void main(String[] args) throws Exception {
        EncrypUtil encrypUtil = new EncrypUtil("AES","123",128);
        String content = "{'repairPhone':'18547854787','customPhone':'12365478965','captchav':'58m7'}";
        System.out.println("加密前：" + content);

        String encrypt = encrypUtil.encrypt(content);
        System.out.println("加密后：" + encrypt);
        String decrypt = encrypUtil.decrypt(encrypt);
        System.out.println("解密后：" + decrypt);
    }

}
