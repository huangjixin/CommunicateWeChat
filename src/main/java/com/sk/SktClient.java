package com.sk;

import java.io.*;
import java.net.Socket;

/**
 * 发送消息。
 * 黄记新
 */
public class SktClient {

    /**
     * @param message
     * @throws IOException
     */
    public static void startSktClient(String message) throws IOException {
        Socket socket = new Socket("127.0.0.1", 20034);
        OutputStream os = socket.getOutputStream();
        PrintWriter pw = new PrintWriter(os);
        pw.write(message);
        pw.flush();
        socket.shutdownOutput();

        InputStream is = socket.getInputStream();
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String info = null;
        while ((info = br.readLine()) != null) {
            System.out.println("服务端返回: " + info);
        }

        br.close();
        is.close();

        pw.close();
        os.close();
        socket.close();
    }

    public static void main (String args []) throws IOException{
        String msg = "来自安全域内的问候！";
        if(args.length>0){
            msg = args[0];
        }
        SktClient.startSktClient(msg);
    }
}
