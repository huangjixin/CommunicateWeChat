package com.sk;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.plaf.basic.BasicInternalFrameTitlePane.MoveAction;

public class TcpSendFileServer {

    @SuppressWarnings("resource")
    public static void main(String[] args) {
        
        int port = 5555;
        ServerSocket server = null;
        Socket socket = null;
        try {
            server = new ServerSocket(port);
            System.out.println("======启动服务=======");
            
            String pathname = "/Users/pengchenyi/Downloads/FTCache";
            File d = new File(pathname);
            if(!d.exists()){
                d.mkdir();
            }
            
            //使用线程池
            ExecutorService threadPool = Executors.newFixedThreadPool(100);
            
            while(true){
                socket = server.accept();
                InputStream in = socket.getInputStream();
                DataInputStream dis = new DataInputStream(in);
                int fileNum = dis.readInt();
                System.out.println("传输的文件总个数:"+fileNum);
                String[] fileNames = new String[fileNum];
                long[] fileSizes = new long[fileNum];
                for (int i = 0; i < fileNum; i++) {
                    fileNames[i] = dis.readUTF();
                    fileSizes[i] = dis.readLong();
                    System.out.println("文件名:"+fileNames[i]);
                    System.out.println("文件大小:"+fileSizes[i]);
                }
                int byteNum = 1024;
                Runnable runnable = () -> {
                    FileOutputStream fos = null;
                    try {
                        byte[] bytes = new byte[byteNum]; 
                        //储存剩下的字节
                        byte[] surplusbytes = new byte[byteNum]; 
                        int length = 0;
                        //文件剩余大小
                        int leftLen = 0;
                        int writeLen  = 0;
                        boolean flog = true;
                        int num = 0;
                        long totalWriteLens = 0;
                        
                        //没用的变量
                        int testLen = 0;
                        int pointLen = 0;
                        while(((length = dis.read(bytes, 0, bytes.length)) != -1 || surplusbytes[0] != 0) && num < fileNum){
                            if(flog){
                                System.out.println("开始接受文件"+(num+1));
                                File file = new File(pathname+fileNames[num]);
                                fos = new FileOutputStream(file);
                                totalWriteLens = 0;
                                testLen = 0;
                                flog = false;   
                                if((num+1) == 6){
                                    System.out.println("=-----------------");
                                }
                            } 
                            if(length >= (int)fileSizes[num] || surplusbytes[0] != 0){
                                if(surplusbytes[0] != 0){
                                    testLen = surplusbytes.length - pointLen;
                                    if((int)fileSizes[num] >= testLen){
                                        fos.write(surplusbytes, 0, testLen);
                                        fos.flush();
                                        surplusbytes = new byte[byteNum];
                                        totalWriteLens += testLen;
                                    }else{
                                        fos.write(surplusbytes, 0, (int)fileSizes[num]);
                                        fos.flush();
                                        leftLen = surplusbytes.length - (int)fileSizes[num];
                                        move(surplusbytes, surplusbytes, (int)fileSizes[num] , leftLen);
                                        testLen = 0;
                                        flog = true;
                                    }
                                }
                                if(length >= (int)fileSizes[num] - testLen){
                                    fos.write(bytes, 0, (int)fileSizes[num] - testLen);
                                    fos.flush();
                                    pointLen = (int)fileSizes[num] - testLen;
                                    leftLen = bytes.length - (int)fileSizes[num];
                                    move(surplusbytes, bytes, (int)fileSizes[num] , leftLen);
                                }else{
                                    if(surplusbytes[0] != 0 && !flog){
                                        fos.write(surplusbytes, 0, (int)fileSizes[num]);
                                        surplusbytes = new byte[byteNum];
                                    }
                                    if(!flog){
                                        fos.write(bytes);
                                        surplusbytes = new byte[byteNum];
                                    }
                                    fos.flush();
                                    totalWriteLens += length;
                                }
                                if(surplusbytes[0] != 0 || num >= fileNum){
                                    flog = true;
                                    num ++;
                                }
                                
                            }else{
                                if((fileSizes[num] - totalWriteLens) / length < 1){
                                    length = (int)(fileSizes[num] - totalWriteLens);
                                }
                                if(surplusbytes[0] != 0){
                                    fos.write(surplusbytes, 0, leftLen);
                                    fos.flush();
                                    surplusbytes = new byte[byteNum];
                                }
                                fos.write(bytes, 0, length);
                                fos.flush();
                                leftLen = bytes.length - length;
                                pointLen = length;
                                move(surplusbytes, bytes, length, leftLen);
                                if(surplusbytes[0] != 0){
                                    flog = true;
                                    num ++;
                                }
                                totalWriteLens += length;
                            }
                        }
                        System.out.println("==========文件传输完毕===========");
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            if(fos != null){
                                fos.close();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                };
                threadPool.execute(runnable);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            
        }
    }
    
    
    /**
     * 
     * @param surplusbytes
     * @param bytes
     * @param length 已经使用的个数
     * @param leftLen 剩下需要转移的数组个数
     */
    public static void move(byte[] surplusbytes, byte[] bytes, int length, int leftLen){
        for (int i = 0; i < leftLen; i++) {
            if(bytes[length+i] != 0){
                surplusbytes[i] = bytes[length+i];
            }
        }
    }
}