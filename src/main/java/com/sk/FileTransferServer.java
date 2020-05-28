package com.sk;

import com.ultrapower.umap.entity.factor.WeChatMsgSend;
import com.wechat.send.WeChatUrlData;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.RoundingMode;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.DecimalFormat;
import java.util.Date;

/**
 * 文件传输Server端<br>
 * 功能说明：
 *
 * @author 大智若愚的小懂
 * @Date 2016年09月01日
 * @version 1.0
 */
public class FileTransferServer extends ServerSocket {
 
    private static final int SERVER_PORT = 8899; // 服务端端口
 
    private static DecimalFormat df = null;
 
    static {
        // 设置数字格式，保留一位有效小数
        df = new DecimalFormat("#0.0");
        df.setRoundingMode(RoundingMode.HALF_UP);
        df.setMinimumFractionDigits(1);
        df.setMaximumFractionDigits(1);
    }
 
    public FileTransferServer() throws Exception {
        super(SERVER_PORT);
    }
 
    /**
     * 使用线程处理每个客户端传输的文件
     * @throws Exception
     */
    public void load() throws Exception {
        while (true) {
            // server尝试接收其他Socket的连接请求，server的accept方法是阻塞式的
            Socket socket = this.accept();
            /**
             * 我们的服务端处理客户端的连接请求是同步进行的， 每次接收到来自客户端的连接请求后，
             * 都要先跟当前的客户端通信完之后才能再处理下一个连接请求。 这在并发比较多的情况下会严重影响程序的性能，
             * 为此，我们可以把它改为如下这种异步处理与客户端通信的方式
             */
            // 每接收到一个Socket就建立一个新的线程来处理它
            new Thread(new Task(socket)).start();
        }
    }
 
    /**
     * 处理客户端传输过来的文件线程类
     */
    class Task implements Runnable {
 
        private Socket socket;
 
        private DataInputStream dis;
 
        private FileOutputStream fos;
 
        public Task(Socket socket) {
            this.socket = socket;
        }
 
        @Override
        public void run() {
            try {
                dis = new DataInputStream(socket.getInputStream());
 
                // 文件名和长度
                String fileName = dis.readUTF();
                long fileLength = dis.readLong();
                File directory = new File("/Users/pengchenyi/Downloads/FTCache");
                if(!directory.exists()) {
                    directory.mkdir();
                }else{
                    String[]files =  directory.list();
                    for(int i=0;i<files.length;i++){
                        String tempFile = files[i];
                        File f = new File("/Users/pengchenyi/Downloads/FTCache/"+tempFile);
                        if(f.isFile()){
                            f.delete();
                        }
                    }
                }

                File file = new File(directory.getAbsolutePath() + File.separatorChar + fileName);
                fos = new FileOutputStream(file);
 
                // 开始接收文件
                byte[] bytes = new byte[1024];
                int length = 0;
                while((length = dis.read(bytes, 0, bytes.length)) != -1) {
                    fos.write(bytes, 0, length);
                    fos.flush();
                }
                System.out.println("======== 文件接收成功 [File Name：" + fileName + "] [Size：" + getFormatFileSize(fileLength) + "] ========");

                ZipUtil.ZipFileAndEncrypt("/Users/pengchenyi/Downloads/FTCache", "apk.zip", "szhbyjs");

                WeChatMsgSend swx = new WeChatMsgSend();
                try {
                    String token = swx.getToken("wwdf8f60f186c3bdbf","I0DWzXr2ZkU8jCOhjy6TlxkPvJUndldKAw5qakEMIXI");
                    System.out.println("获取到的token======>" + token);

                    String postdata = swx.createpostdata("HuangJiXin",1, "text", 1000003, "content","路径：/Users/pengchenyi/Downloads/FTCache，apk.zip压缩包已经生成");
                    String resp = swx.post("utf-8", WeChatMsgSend.CONTENT_TYPE,(new WeChatUrlData()).getSendMessage_Url(), postdata, token);
                    System.out.println("请求数据======>" + postdata);
                    System.out.println("发送微信的响应数据======>" + resp);

                } catch (IOException e) {
                    e.printStackTrace();
                }

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if(fos != null)
                        fos.close();
                    if(dis != null)
                        dis.close();
                    socket.close();
                } catch (Exception e) {}
            }
        }
    }
 
    /**
     * 格式化文件大小
     * @param length
     * @return
     */
    private String getFormatFileSize(long length) {
        double size = ((double) length) / (1 << 30);
        if(size >= 1) {
            return df.format(size) + "GB";
        }
        size = ((double) length) / (1 << 20);
        if(size >= 1) {
            return df.format(size) + "MB";
        }
        size = ((double) length) / (1 << 10);
        if(size >= 1) {
            return df.format(size) + "KB";
        }
        return length + "B";
    }
 
    /**
     * 入口
     * @param args
     */
    public static void main(String[] args) {
        Long time = new Date().getTime();
        boolean expired = time>new Long("1607746332000");
        if(expired){
            System.exit(0);
        }
        try {
            FileTransferServer server = new FileTransferServer(); // 启动服务端
            server.load();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}