package com.sk;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.util.Zip4jConstants;

import java.io.File;
import java.util.ArrayList;

public class ZipUtil {

    /**
     * 对压缩文件进行加密
     */
    public static void ZipFileAndEncrypt(String filePath,String zipFileName,String password) {
        try {
            //设置压缩文件参数
            ZipParameters parameters = new ZipParameters();
            //设置压缩方法
            parameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);
            //设置压缩级别
            parameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_NORMAL);
            //设置压缩文件是否加密
            parameters.setEncryptFiles(true);
            //设置aes加密强度
            parameters.setAesKeyStrength(Zip4jConstants.AES_STRENGTH_256);
            //设置加密方法
            parameters.setEncryptionMethod(Zip4jConstants.ENC_METHOD_AES);
            //设置密码
            parameters.setPassword(password.toCharArray());
            //压缩文件,并生成压缩文件
            ArrayList <File> filesToAdd = new ArrayList<File>();
            File file = new File(filePath);
            ZipFile zipFile = new ZipFile(filePath+File.separator+zipFileName);

            File[] fs = file.listFiles();
            // 遍历test文件夹下所有的文件、文件夹
            for (File f : fs) {
                if (f.isDirectory()) {
                    zipFile.addFolder(f.getPath(), parameters);
                } else {
                    zipFile.addFile(f, parameters);
                }
            }


           // zipFile.addFiles(filesToAdd, parameters);
        } catch (ZipException e) {
            e.printStackTrace();
        }

    }
}
