/*********************************************************************************
 * Copyright (c) 2019 中电健康云科技有限公司
 * 版本      DATE                 BY             REMARKS
 * ----  -----------  ---------------  ------------------------------------------
 * 1.0    2019-04-29      zhoubin           init.
 ********************************************************************************/
package src.main.java.cn.sheva7.daq.light.gossip.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

/**
 * @Description:
 * @Author: zhoubin
 * @Date: 2019-04-29 11:18
 */
public class FileUtil {

    private static final String ENCODING = "UTF-8";

    public static void write(String data, String path) throws IOException {
        FileUtils.write(new File(path), data, ENCODING);
    }

    public static String readFileToString(File file) throws IOException {
        return FileUtils.readFileToString(file, ENCODING);
    }

    public static String readFileToString(String path) throws IOException {
        return FileUtils.readFileToString(new File(path), ENCODING);
    }

    public static JSONArray readFileToJSONArray(File file) throws IOException, JSONException {
        return JSONArray.parseArray(readFileToString(file));
    }

    public static JSONObject readFileToJSONObject(File file) throws IOException, JSONException {
        return JSONObject.parseObject(readFileToString(file));
    }

    public static JSONArray readFileToJSONArray(String path) throws IOException, JSONException {
        return JSONArray.parseArray(readFileToString(path));
    }

    public static JSONObject readFileToJSONObject(String path) throws IOException, JSONException {
        return JSONObject.parseObject(readFileToString(path));
    }

    public static void deleteDirectory(String directory) throws IOException {
        FileUtils.deleteDirectory(new File(directory));
    }

    public static void deleteDirectory(File directory) throws IOException {
        FileUtils.deleteDirectory(directory);
    }

    public static void deleteFile(String file) throws IOException {
        FileUtils.forceDelete(new File(file));
    }

    public static void deleteFile(File file) throws IOException {
        FileUtils.forceDelete(file);
    }

    /**
     * 拼接路径，使用File.separator分隔符
     * @param path
     * @return
     */
    public static String appendPath(String... path){
        return String.join(File.separator, path);
    }

}
