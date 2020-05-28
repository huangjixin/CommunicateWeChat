package com.sk;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.Socket;

public class TcpSendFileClient {

    public static int port = 5555;
    public static String host = "localhost";
    public static Socket socket = null;

    @SuppressWarnings("resource")
    public static void main(String[] args) {
        //创建socket连接
        createSocket();
        try {
            //发送文件的发放，需要文件价路径
            sendMultipleFile( "/Users/pengchenyi/Downloads/file");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //关闭socket连接
            closeSocket();
        }
    }

    public static void sendMultipleFile(String path) throws Exception{
        String[] fileNames = getFileName(path);
        sendSingleFile(path, fileNames);
    }

    public static String[] getFileName(String path){
        File file = new File(path);
        return file.list();
    }

    public static File[] createFile(String path, String[] pathname){
        File[] files = new File[pathname.length];
        try {
            for (int i = 0; i < pathname.length; i++) {
                File file = new File(path+"\\"+pathname[i]);
                if(file.exists() && file.isFile()){
                    file.createNewFile();
                    files[i] = file;
                }
            }
            return files;
            } catch (IOException e) {
            e.printStackTrace();
        }
        return files;
    }

    public static void sendSingleFile(String path, String[] pathName) throws Exception{
        File[] file = createFile(path, pathName);
        DataOutputStream dos = new DataOutputStream(socket.getOutputStream());

        dos.writeInt(file.length);
        dos.flush();
        //将文件名和文件长度先发送过去
        for (int i = 0; i < file.length; i++) {
            System.out.println(file.length+"=="+file[i].getName()+"=="+file[i].length());
            dos.writeUTF(file[i].getName());
            dos.flush();
            dos.writeLong(file[i].length());
            dos.flush();
        }
    
        System.out.println("开始发送文件...");

        byte[] bytes = new byte[1024];   
        int length = 0;
        //发送文件内容
        for (int i = 0; i < file.length; i++) {
            FileInputStream fis = new FileInputStream(file[i]);
            while((length = fis.read(bytes, 0, bytes.length)) != -1){
                dos.write(bytes, 0, length);
                dos.flush();
            }
        }
        System.out.println("文件发送完毕...");
    }

    public static void createSocket(){
        try {
            socket = new Socket(host, port);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void closeSocket(){
        try {
            if(socket != null){
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}