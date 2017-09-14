package com.wiseweb.cat.task;

import java.io.*;

/**
 * Created by wiseweb on 2016/5/16.
 */
public class test {

    public static void main(String []args){
        test t = new test();
        t.write();
        t.t();
        t.add();
        t.t();
    }

    public void t(){
//        String path =Class.class.getClass().getResource("/").getPath();
//        System.out.print(path);
//        InputStream in = this.getClass().getResourceAsStream(path+"runningIds.json");
////        File file = new File("runningIds.json");
//        try {
////            InputStream in = new FileInputStream(file);
//            byte b[]=new byte[1024];     //创建合适文件大小的数组
//            in.read(b);
//            in.close();//读取文件中的内容到b[]数组
//            System.out.println(new String(b));
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        File file = new File("D:/runningIds.json");
        BufferedReader reader = null;
        try {
            System.out.println("以行为单位读取文件内容，一次读一整行：");
            reader = new BufferedReader(new FileReader(file));
            String tempString = null;
            // 一次读入一行，直到读入null为文件结束
            while ((tempString = reader.readLine()) != null) {
                System.out.println(tempString);
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }
        }
    }

    public void write(){
        String str = "亲爱的小南瓜！";
        byte bt[] = new byte[1024];
        bt = str.getBytes();
        try {
            FileOutputStream in = new FileOutputStream("D:/runningIds.json");
            try {
                in.write(bt, 0, bt.length);
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void add(){
        RandomAccessFile randomFile = null;
        try {
            // 打开一个随机访问文件流，按读写方式
            randomFile = new RandomAccessFile("D:/runningIds.json", "rw");
            // 文件长度，字节数
            long fileLength = randomFile.length();
            // 将写文件指针移到文件尾。
            randomFile.seek(fileLength);
            randomFile.writeBytes("hehe");
        } catch (IOException e) {
            e.printStackTrace();
        } finally{
            if(randomFile != null){
                try {
                    randomFile.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
