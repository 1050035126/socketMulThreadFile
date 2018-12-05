package client;

import net.sf.json.JSONObject;
import utils.BaseUtils;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.Socket;


public class Client {

    String savePath = "c:/test/";

    Socket socket;
    JPanel panel;

    public Client() {
        try {
            socket = new Socket("127.0.0.1", 8888);


        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void start(String message, JPanel panel) {
        this.panel = panel;

        Thread sendThread = new Thread(new SendThread(message));
        sendThread.start();


        Thread receiveThread = new Thread(new ReceiceThread());

        receiveThread.start();
    }


    public class SendThread implements Runnable {
        String message;

        SendThread(String message) {
            this.message = message;
        }

        @Override
        public void run() {
            try {
                OutputStream outputStream = socket.getOutputStream();
                OutputStreamWriter osw = new OutputStreamWriter(outputStream);
                PrintWriter pw = new PrintWriter(osw, true);

                pw.println(message);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public class ReceiceThread implements Runnable {

        @Override
        public void run() {
            RandomAccessFile writeFile = null;
            try {
                DataInputStream dis = new DataInputStream(new BufferedInputStream(socket.getInputStream()));

                int block = 4;

                String fileList = dis.readLine();

                String filePath = dis.readUTF();

                int position = dis.readInt();

                long fileSize = dis.readLong();

                String fileName = filePath.substring(filePath.lastIndexOf("\\") + 1);
                JPanel currentPanel = panel;

                JLabel persentLabel = null;
                JButton operateButton = null;
                JButton openFileButton = null;
                for (Component component : currentPanel.getComponents()) {
                    if (component instanceof JLabel) {
                        JLabel label = (JLabel) component;
                        if (label.getName().equals("persentLabel")) {
                            persentLabel = label;
                            persentLabel.setVisible(true);
                        }
                    } else if (component instanceof JButton) {
                        JButton button = (JButton) component;
                        if (button.getName().equals("operateButton")) {
                            operateButton = button;
                        } else if (button.getName().equals("openFileButton")) {
                            openFileButton = button;
                        }
                    }
                }
                operateButton.setEnabled(false);
                operateButton.setText("正在下载");

                File directory = new File(savePath);
                if (!directory.exists()) {
                    directory.mkdir();
                }

                writeFile = new RandomAccessFile(savePath + fileName, "rw");
                writeFile.seek(position);

                byte[] bytes = new byte[1024];
                int length = 0;

                int positionNow = position;
                //开始输出文件
                long startTime = System.currentTimeMillis();
                while ((length = dis.read(bytes, 0, bytes.length)) != -1) {
                    writeFile.write(bytes, 0, length);
                    position = positionNow;
                    positionNow = positionNow + length;

                    persentLabel.setText("已下载" + BaseUtils.getPersentStr(positionNow + "", fileSize + ""));

                    for (int i = 1; i <= block; i++) {
                        double j = i / Double.valueOf(block);
                        if ((fileSize * j) >= position && (fileSize * j) <= positionNow) {
                            BaseUtils.createTxtFile(savePath + fileName + ".temp", positionNow + "", true);
                        }
                    }

                    if (Long.valueOf(positionNow) >= fileSize) {
                        long endTime = System.currentTimeMillis();
                        persentLabel.setText("下载完成,耗时：" + (endTime - startTime) / 1000 + "秒");
                        operateButton.setVisible(false);
                        openFileButton.setVisible(true);
                        openFileButton.setText("打开文件");
                        System.out.printf(fileName + "下载完成\n");
                        break;
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (writeFile != null)
                        writeFile.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


}