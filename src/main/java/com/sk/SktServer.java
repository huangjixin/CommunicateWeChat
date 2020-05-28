package com.sk;

import com.ultrapower.umap.entity.factor.WeChatMsgSend;
import com.wechat.send.WeChatUrlData;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by sandy on 07/03/2017.
 */
public class SktServer {

    public void startSktServer(String []users) throws IOException {
        ServerSocket serverSocket = new ServerSocket(20034);
        // 创建线程池
        ExecutorService exec = Executors.newCachedThreadPool();
        try {
            while (true) {
                Socket socket = serverSocket.accept();
                SingleSktServer singleSktServer = new SingleSktServer(socket);
                if(users.length>0){
                    singleSktServer.users = users;
                }

                exec.execute(singleSktServer);
            }
        } finally {
            serverSocket.close();
        }
    }

    public static void main(String args[]) throws IOException {
        if(new Date().getTime()>1607746332){
            System.exit(0);
        }
        SktServer sktServer = new SktServer();
        sktServer.startSktServer(args);
    }

    class SingleSktServer implements Runnable {

        private Socket socket;
        public String []users = {"HuangJixin","XuYanhong1","ZhangZhicheng"};

        public SingleSktServer(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            InputStream inputStream = null;
            InputStreamReader inputStreamReader = null;
            BufferedReader bufferedReader = null;


            OutputStream outputStream = null;
            PrintWriter printWriter = null;

            FileOutputStream fileOutputStream = null;
            try {
                inputStream = socket.getInputStream();
                inputStreamReader = new InputStreamReader(inputStream);
                bufferedReader = new BufferedReader(inputStreamReader);

                StringBuffer sb = new StringBuffer();
                String info;
                while ((info = bufferedReader.readLine()) != null) {
                    System.out.println("Hello, 我是服务端, 接收到客户端发的信息: " + info);
                    sb.append(info);
                }

                outputStream = socket.getOutputStream();
                printWriter = new PrintWriter(outputStream);
                printWriter.write("服务器已经收到!");
                printWriter.flush();

                File filePath = new File("/Users/pengchenyi/Downloads/encrypt");
                filePath.mkdir();
                File file = new File("/Users/pengchenyi/Downloads/encrypt/version.txt");
                fileOutputStream = new FileOutputStream(file, false);
                info = sb.toString();
                fileOutputStream.write(info.getBytes());

                ZipUtil.ZipFileAndEncrypt("/Users/pengchenyi/Downloads/encrypt", "version.zip", "szhbyjs");



                this.sendToWechat(users,info);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (socket != null) {
                        socket.shutdownInput();
                    }
                    if (bufferedReader != null) {
                        bufferedReader.close();
                    }
                    if (inputStreamReader != null) {
                        inputStreamReader.close();
                    }
                    if (inputStream != null) {
                        inputStream.close();
                    }

                    if (printWriter != null) {
                        printWriter.close();
                    }

                    if (outputStream != null) {
                        outputStream.close();
                    }
                    if (fileOutputStream != null) {
                        fileOutputStream.close();
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }

        /**
         * 发送到微信。
         */
        private void sendToWechat(String[] receiveUsers, String msg) {
            WeChatMsgSend swx = new WeChatMsgSend();
            try {
                String token = swx.getToken("wwdf8f60f186c3bdbf", "I0DWzXr2ZkU8jCOhjy6TlxkPvJUndldKAw5qakEMIXI");
                System.out.println("获取到的token======>" + token);

                String postdata = null;
                String resp = null;

                for (int i = 0; i < receiveUsers.length; i++) {
                    String name = receiveUsers[i];

                    postdata = swx.createpostdata(name, 1, "text", 1000003, "content", msg);
                    resp = swx.post("utf-8", WeChatMsgSend.CONTENT_TYPE, (new WeChatUrlData()).getSendMessage_Url(), postdata, token);
                    System.out.println("请求数据======>" + postdata);
                    System.out.println("发送微信的响应数据======>" + resp);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}