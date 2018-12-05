package utils;

import java.io.*;
import java.text.DecimalFormat;

public class BaseUtils {


    public static void createTxtFile(String filePath, String text, boolean ishide) throws IOException {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(new File(filePath));
            fos.write(text.getBytes());
            if (ishide == true) {
                String sets = "attrib \\" + filePath + "\\" + " +H ";
                Runtime.getRuntime().exec(sets);

            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            fos.close();
        }
    }

    public static void main(String[] args) throws Exception {
        String a = BaseUtils.getPersentStr("1","2");
        System.out.printf(a);
    }

    public static String getPersentStr(String top, String buttom) {
        double tempTop = Double.valueOf(top);
        double tempButtom = Double.valueOf(buttom);
        double result = (tempTop / tempButtom)*100;

        DecimalFormat df = new DecimalFormat("#");
        return df.format(result)+"%";
    }





    public static String getFileContentStr(InputStream inputStream) {
        InputStreamReader rd = null;
        String result = "";
        try {
            rd = new InputStreamReader(inputStream, "utf-8");
            int read = rd.read();
            while (read != -1) {
                result += (char) read;
                read = rd.read();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                rd.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }


    public static String getFileContentStr(String filePath) {
        InputStreamReader rd = null;
        String result = "";
        try {
            rd = new InputStreamReader(new FileInputStream(new File(filePath)), "utf-8");
            int read = rd.read();
            while (read != -1) {
                result += (char) read;
                read = rd.read();
            }
        } catch (Exception e) {

        } finally {
            try {
                rd.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    public static String byteToMB(long size) {
        if (size < 1024) {
            return String.valueOf(size) + "B";
        } else {
            size = size / 1024;
        }
        //如果原字节数除于1024之后，少于1024，则可以直接以KB作为单位
        //因为还没有到达要使用另一个单位的时候
        //接下去以此类推
        if (size < 1024) {
            return String.valueOf(size) + "KB";
        } else {
            size = size / 1024;
        }
        if (size < 1024) {
            //因为如果以MB为单位的话，要保留最后1位小数，
            //因此，把此数乘以100之后再取余
            size = size * 100;
            return String.valueOf((size / 100)) + "."
                    + String.valueOf((size % 100)) + "MB";
        } else {
            //否则如果要以GB为单位的，先除于1024再作同样的处理
            size = size * 100 / 1024;
            return String.valueOf((size / 100)) + "."
                    + String.valueOf((size % 100)) + "GB";
        }
    }

}
