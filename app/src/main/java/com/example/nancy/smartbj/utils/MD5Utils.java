package com.example.nancy.smartbj.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Nancy on 2016/3/16.
 */
public class MD5Utils {
    public static String getMD5(String str) {
        StringBuilder sb = new StringBuilder();
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] bytes = str.getBytes();
            byte[] digest = md.digest(bytes);
            for (byte b : digest) {
                String hexString = Integer.toHexString(b & 0xff);
                if (hexString.length() == 1) {
                    hexString = "0" + hexString;
                }
                sb.append(hexString);
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return sb.toString();
    }

    /**
     * @param path 文件路径
     * @return 大写的MD5值(这里不注意大小写的话，后面查询数据库会有困难
     *todo 也可以用sql的语句 select * from atable where upper(某个字段名) =  upper('ABC')
     */
    public static String getMD5FromFile(String path) {
        int len;
        StringBuilder sb = new StringBuilder();
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            FileInputStream fis = new FileInputStream(new File(path));
            byte[] buffer = new byte[fis.available()];
            while ((len = fis.read(buffer)) != -1) {
                md.update(buffer, 0, len);
            }

            byte[] digest = md.digest();

            for (byte b : digest) {
                String hexString = Integer.toHexString(b & 0xff);
                if (hexString.length() == 1) {
                    hexString = "0" + hexString;
                }
                sb.append(hexString);
            }
        } catch (IOException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        //返回大写形式
        return sb.toString().toUpperCase();
    }


}
