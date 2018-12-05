package server;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;


public class Server {

    String filePathList[] = {
            "C:\\Users\\10500\\Desktop\\test1.zip",
            "C:\\Users\\10500\\Desktop\\test1 - 副本.zip",
            "C:\\Users\\10500\\Desktop\\新建文本文档 (3).txt"
    };

    public void startServer() {
        try {
            ServerSocket serverSocket = new ServerSocket(8888);
            System.out.println("服务端已启动，等待客户端连接..");

            while (true) {
                Socket socket = serverSocket.accept();
                System.out.printf("有新连接\n");

                Thread receiveThread = new Thread(new ReceiceThread(socket));
                receiveThread.start();


                OutputStream outputStream = socket.getOutputStream();
                OutputStreamWriter osw = new OutputStreamWriter(outputStream);
                PrintWriter pw = new PrintWriter(osw, true);
                JSONArray jsonArray = new JSONArray();
                for (String filePath : filePathList) {
                    JSONObject jsonObject = new JSONObject();
                    File file = new File(filePath);
                    jsonObject.put("filePath", filePath);
                    jsonObject.put("fileName", file.getName());
                    jsonObject.put("fileSize", file.length());
                    jsonArray.add(jsonObject);
                }
                pw.println(jsonArray.toString());

//                Thread sendThread = new Thread(new ReceiceThread(socket));
//
//                sendThread.start();


            }


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendMessage(String message, Socket socket) {


    }


    public static void main(String[] args) {

        new Server().startServer();

    }


    public class ReceiceThread implements Runnable {
        Socket socket;

        ReceiceThread(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                while (true) {
                    BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                    String str = br.readLine();
                    JSONObject jasonObject = JSONObject.fromObject(str);
                    if (jasonObject.opt("filePath") == null) {
                        System.out.printf("客户端发送的消息：" + str);
                    } else {
                        String filePath = jasonObject.optString("filePath");
                        int position = jasonObject.optInt("position");
                        long fileSize = jasonObject.optLong("fileSize");
                        System.out.printf("客户端想要接收文件" + jasonObject.opt("filePath") + "\n");
                        System.out.printf("启动发送线程，开始发送文件\n");

                        Thread sendThread = new Thread(new SendThread(socket, filePath, position, fileSize));
                        sendThread.start();
                    }
                }
            } catch (IOException e) {
                System.out.printf("有连接关闭了");
            }


        }
    }

    public class SendThread implements Runnable {
        Socket socket;
        String filePath;
        int position;
        long fileSize;

        SendThread(Socket socket, String filePath, int position, long fileSize) {
            this.socket = socket;
            this.filePath = filePath;
            this.position = position;
            this.fileSize = fileSize;
        }

        @Override
        public void run() {
            RandomAccessFile readFile = null;
            try {
                DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));

                readFile = new RandomAccessFile(filePath, "rw");
                readFile.seek(position);

                dos.writeUTF(filePath);
                dos.flush();
                dos.writeInt(position);
                dos.flush();
                dos.writeLong(fileSize);
                dos.flush();


                byte[] bytes = new byte[1024];
                int length = 0;
                while ((length = readFile.read(bytes, 0, bytes.length)) != -1) {
                    dos.write(bytes, 0, length);
                    dos.flush();
                }
                readFile.close();
                System.out.println();

            } catch (Exception e) {
                e.printStackTrace();
            }


        }
    }


}


