package client;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import utils.BaseUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class Index {

    String savePath = "c:/test/";
    JFrame frame;

    public void init() throws Exception {
        frame = new JFrame("文件多线程下载");
        // Setting the width and height of frame
        frame.setSize(500, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        connectServer();

        // 设置界面可见
        frame.setVisible(true);
    }

    public void connectServer() {
        Socket socket = null;
        BufferedReader br = null;
        String str = "";
        try {
            socket = new Socket("127.0.0.1", 8888);
            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            str = br.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }


        JPanel jPanel;

        JSONArray jsonArray = JSONArray.fromObject(str);
        System.out.printf("可下载文件：\n");
        int i = 50;
        for (Object object : jsonArray) {
            JSONObject item = (JSONObject) object;
            String filePath = item.optString("filePath");
            String size = BaseUtils.byteToMB(item.optLong("fileSize"));
            String fileName = item.optString("fileName");

            jPanel = new JPanel();
            jPanel.setBounds(0, i, 500, 50);
            i += 50;

            JLabel labe2 = new JLabel(fileName);
            labe2.setName("fileNameLabel");
            JLabel labe3 = new JLabel(size);
            labe3.setName("fileSizeLabel");
            jPanel.add(labe2);
            jPanel.add(labe3);

            JLabel persentLabel = new JLabel("已下载：");
            persentLabel.setName("persentLabel");
            persentLabel.setVisible(false);
            jPanel.add(persentLabel);

            JButton operateButton = new JButton("下载");
            operateButton.setName("operateButton");
            operateButton.setVisible(false);

            JButton openFileButton = new JButton("打开文件");
            openFileButton.setName("openFileButton");
            openFileButton.setVisible(false);

            jPanel.add(operateButton);
            jPanel.add(openFileButton);


            String position = "0";
            String fileSize = item.optLong("fileSize") + "";
            try {
                String text = BaseUtils.getFileContentStr(savePath + fileName + ".temp");
                position = text;
                if ((item.optLong("fileSize") + "").equals(position)) {
                    operateButton.setVisible(false);
                    openFileButton.setVisible(true);
                } else {
                    persentLabel.setText("已下载：" + BaseUtils.getPersentStr(position, fileSize));
                    persentLabel.setVisible(true);

                    operateButton.setVisible(true);
                    operateButton.setText("继续下载");
                    openFileButton.setVisible(false);
                }
            } catch (Exception e2) {
                operateButton.setVisible(true);
                operateButton.setText("下载");
            }


            OperateListener operateListener = new OperateListener(jPanel, filePath, fileName, position, fileSize);
            operateButton.addActionListener(operateListener);

            openFileButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
                        Desktop.getDesktop().open(new File(savePath + fileName));
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            });


            System.out.printf("文件名：" + item.optString("fileName") + ";文件大小:" + size + "\n");
            frame.add(jPanel);
        }


    }

    private class OperateListener implements ActionListener {
        String filePath;
        String position;
        String fileSize;

        String fileName;
        JPanel panel;


        OperateListener(JPanel panel, String filePath, String fileName, String position, String fileSize) {
            this.panel = panel;
            this.filePath = filePath;
            this.fileName = fileName;
            this.position = position;
            this.fileSize = fileSize;
        }

        @Override
        public void actionPerformed(ActionEvent e) {

            System.out.printf("开始下载" + fileName + "\n");


            JSONObject jsonObject = new JSONObject();
            jsonObject.put("filePath", filePath);
            jsonObject.put("position", Long.valueOf(position));
            jsonObject.put("fileSize", Long.valueOf(fileSize));

            Client chatClient = new Client();
            chatClient.start(jsonObject.toString(), panel);

        }
    }


    public static void main(String[] args) throws Exception {
        new Index().init();
    }


}
