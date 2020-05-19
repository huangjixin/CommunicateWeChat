package com.sk;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by sandy on 07/03/2017.
 */
public class SktServer {

    public void startSktServer() throws IOException {
        ServerSocket serverSocket = new ServerSocket(20034);
        // 创建线程池
        ExecutorService exec = Executors.newCachedThreadPool();
        try {
            while (true) {
                Socket socket = serverSocket.accept();
                exec.execute(new SingleSktServer(socket));
            }
        } finally {
            serverSocket.close();
        }
    }

    public static void main(String args[]) throws IOException {
        SktServer sktServer = new SktServer();
        sktServer.startSktServer();
    }

    class SingleSktServer implements Runnable {

        private Socket socket;

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
    }
}