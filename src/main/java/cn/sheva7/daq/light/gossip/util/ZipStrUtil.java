/*********************************************************************************
 * Copyright (c) 2019 中电健康云科技有限公司
 * 版本      DATE                 BY             REMARKS
 * ----  -----------  ---------------  ------------------------------------------
 * 1.0    2019-03-22      zhoubin           init.
 ********************************************************************************/
package src.main.java.cn.sheva7.daq.light.gossip.util;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * @Description:
 * @Author: zhoubin
 * @Date: 2019-03-22 15:50
 */
public class ZipStrUtil {

    /**
     * 字符串的压缩
     *
     * @param str 待压缩的字符串
     * @return 返回压缩后的字符串
     * @throws IOException
     */
    public static String compress(String str) throws IOException {
        if (null == str || str.length() <= 0) {
            return str;
        }
        try (// 创建一个新的 byte 数组输出流
             ByteArrayOutputStream out = new ByteArrayOutputStream();
             // 使用默认缓冲区大小创建新的输出流
             GZIPOutputStream gzip = new GZIPOutputStream(out)
        ) {
            // 将 b.length 个字节写入此输出流
            gzip.write(str.getBytes());
            gzip.close();
            // 使用指定的 charsetName，通过解码字节将缓冲区内容转换为字符串
            return out.toString("ISO-8859-1");
        }

    }

    /**
     * 字符串的解压
     *
     * @param str 对字符串解压
     * @return 返回解压缩后的字符串
     * @throws IOException
     */
    public static String unCompress(String str) throws IOException {
        if (null == str || str.length() <= 0) {
            return str;
        }
        try (// 创建一个新的 byte 数组输出流
             ByteArrayOutputStream out = new ByteArrayOutputStream();
             // 创建一个 ByteArrayInputStream，使用 buf 作为其缓冲区数组
             ByteArrayInputStream in = new ByteArrayInputStream(str
                     .getBytes("ISO-8859-1"));
             // 使用默认缓冲区大小创建新的输入流
             GZIPInputStream gzip = new GZIPInputStream(in)

        ) {
            byte[] buffer = new byte[256];
            int n = 0;
            // 将未压缩数据读入字节数组
            while ((n = gzip.read(buffer)) >= 0) {
                // 将指定 byte 数组中从偏移量 off 开始的 len 个字节写入此 byte数组输出流
                out.write(buffer, 0, n);
            }
            // 使用指定的 charsetName，通过解码字节将缓冲区内容转换为字符串
            return out.toString("utf-8");
        }
    }

//    public static void main(String[] args) throws IOException{
//        String a = "zdfdzfzszfdsds发顺丰第三方232423*%%……（）%*（*？？《#！dfggdd 方法  fff    !@#!@#@$@#$%$#%()~````1`1！！`111！！！！!!!!!";
//        System.out.println(a.length());
//        String b = compress(compress(a));
//        System.out.println(b);
//        String c = unCompress(unCompress(b));
//        System.out.println(c);
//        System.err.println(a.equals(c));
//    }
}