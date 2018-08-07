package com.CLD.dataAnonymization.util.deidentifier.algorithm;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * @description:该类对信息进行*3DES加密处理
 * @Author CLD
 * @Date 2018/8/6 8:40
 */
public class Encrypt {

    //定义加密算法，有DES、DESede(即3DES)、Blowfish
    private static final String Algorithm = "DESede";


    public static Boolean encryptHandle(ArrayList<ArrayList<String>> data,
                                        ArrayList<Integer> col,
                                        String password,
                                        ArrayList<ArrayList<HashMap<String,String>>> proInfo){
        for(int Column : col){
            for(int i=0;i<data.get(0).size();i++){
                HashMap<String,String> info=new HashMap<String,String>();
                String O=data.get(Column).get(i);
                String E=encryptMode(data.get(Column).get(i),password);
                info.put(O,E);
                proInfo.get(i).add(info);
                data.get(Column).set(i,E);
            }
        }
        return true;
    }

    public static Boolean decryptHandle(ArrayList<ArrayList<String>> data,
                                        ArrayList<Integer> col,
                                        String password){
        for(int Column : col){
            for(int i=0;i<data.get(0).size();i++){
                data.get(Column).set(i,decryptMode(data.get(Column).get(i),password));
            }
        }
        return true;
    }


    /**
     * 加密方法
     * @param src 源数据
     * @return 16进制密文
     */
    private static String encryptMode(String src,String password) {
        try {
            SecretKey deskey = new SecretKeySpec(build3DesKey(password), Algorithm);    //生成密钥
            Cipher c1 = Cipher.getInstance(Algorithm);    //实例化负责加密/解密的Cipher工具类
            c1.init(Cipher.ENCRYPT_MODE, deskey);    //初始化为加密模式
            return byteArrayToHexStr(c1.doFinal(src.getBytes()));
        } catch (java.security.NoSuchAlgorithmException e1) {
            e1.printStackTrace();
        } catch (javax.crypto.NoSuchPaddingException e2) {
            e2.printStackTrace();
        } catch (java.lang.Exception e3) {
            e3.printStackTrace();
        }
        return null;
    }

    /**
     * 解密方法
     * @param src 16进制密文
     * @return    明文
     */
    private static String decryptMode(String src,String password) {
        try {
            SecretKey deskey = new SecretKeySpec(build3DesKey(password), Algorithm);
            Cipher c1 = Cipher.getInstance(Algorithm);
            c1.init(Cipher.DECRYPT_MODE, deskey);    //初始化为解密模式
            return new String(c1.doFinal(hexStrToByteArray(src)));
        } catch (java.security.NoSuchAlgorithmException e1) {
            e1.printStackTrace();
        } catch (javax.crypto.NoSuchPaddingException e2) {
            e2.printStackTrace();
        } catch (java.lang.Exception e3) {
            e3.printStackTrace();
        }
        return null;
    }

    /**
     * 根据字符串生成密钥字节数组
     * @param keyStr 密钥字符串
     * @return
     * @throws UnsupportedEncodingException
     */
    private static byte[] build3DesKey(String keyStr) throws UnsupportedEncodingException {
        byte[] key = new byte[24];    //24位字节密码
        byte[] temp = keyStr.getBytes("UTF-8");
        if(key.length > temp.length){
            //如果temp不够24位，则拷贝temp数组整个长度的内容到key数组中
            System.arraycopy(temp, 0, key, 0, temp.length);
        }else{
            //如果temp大于24位，则拷贝temp数组24个长度的内容到key数组中
            System.arraycopy(temp, 0, key, 0, key.length);
        }return key;
    }

    /**
     * 将字节数组转为16位字符串
     * @param byteArray 字节数组
     * @return
     */
    private static String byteArrayToHexStr(byte[] byteArray) {
        if (byteArray == null){
            return null;
        }
        char[] hexArray = "0123456789ABCDEF".toCharArray();
        char[] hexChars = new char[byteArray.length * 2];
        for (int j = 0; j < byteArray.length; j++) {
            int v = byteArray[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    /**
     * 将16位字符串转为字节数组
     * @param str 16位字符串
     * @return
     */
    private static byte[] hexStrToByteArray(String str)
    {
        if (str == null) {
            return null;
        }
        if (str.length() == 0) {
            return new byte[0];
        }
        byte[] byteArray = new byte[str.length() / 2];
        for (int i = 0; i < byteArray.length; i++){
            String subStr = str.substring(2 * i, 2 * i + 2);
            byteArray[i] = ((byte)Integer.parseInt(subStr, 16));
        }
        return byteArray;
    }
}
