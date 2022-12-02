package com.doSomething;

import java.io.*;

public class CleanText {

    public static void main(String[] args) {
        File fromFile = new File("E:\\cleanText\\From\\西方哲学史-罗素.txt");
        File toFile = new File("E:\\cleanText\\To\\西方哲学史-罗素.txt");
        deletePageNum(fromFile, toFile);
        System.out.println("执行完毕！");
    }

    private static void deletePageNum(File formFile, File toFile){
        int lines = 0;
        String fileCharset = getFilecharset(formFile);

        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(formFile), fileCharset));
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(toFile),fileCharset));
            String s = null;
            String s1 = null;
            String temp = null;
            while (( s = br.readLine()) != null || true){
                if (s != null && s != "" ){
                    System.out.println(s);
                    if (s.length() > 3 && s.substring(0,2).equals("--")){
                        for (int i = 0; i < 4; i++){
                           s1 = br.readLine();
                        }
                        temp = temp.replace("\r\n|\\n|\\\\n", "");
                        temp += s1;
                        System.out.println("lines:"+ ++lines);
                        continue;
                    }else if (s.equals("ends")){
                        bw.write(temp);
                        bw.newLine();
                        break;
                    }else {
                        if (temp !=null && temp != ""){
                            bw.write(temp);
                            bw.newLine();
                        }
                        temp = s;
                        System.out.println("lines:"+ ++lines);
                        continue;
                    }
                }else {
                    continue;
                }
            }
            br.close();
            bw.flush();
            bw.close();
            System.out.println("fileCharset:"+fileCharset);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //判断编码格式方法
    private static  String getFilecharset(File sourceFile) {
        String charset = "GBK";
        byte[] first3Bytes = new byte[3];
        try {
            boolean checked = false;
            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(sourceFile));
            bis.mark(0);
            int read = bis.read(first3Bytes, 0, 3);
            if (read == -1) {
                return charset; //文件编码为 ANSI
            } else if (first3Bytes[0] == (byte) 0xFF
                    && first3Bytes[1] == (byte) 0xFE) {
                charset = "UTF-16LE"; //文件编码为 Unicode
                checked = true;
            } else if (first3Bytes[0] == (byte) 0xFE
                    && first3Bytes[1] == (byte) 0xFF) {
                charset = "UTF-16BE"; //文件编码为 Unicode big endian
                checked = true;
            } else if (first3Bytes[0] == (byte) 0xEF
                    && first3Bytes[1] == (byte) 0xBB
                    && first3Bytes[2] == (byte) 0xBF) {
                charset = "UTF-8"; //文件编码为 UTF-8
                checked = true;
            }
            bis.reset();
            if (!checked) {
                int loc = 0;
                while ((read = bis.read()) != -1) {
                    loc++;
                    if (read >= 0xF0)
                        break;
                    if (0x80 <= read && read <= 0xBF) // 单独出现BF以下的，也算是GBK
                        break;
                    if (0xC0 <= read && read <= 0xDF) {
                        read = bis.read();
                        if (0x80 <= read && read <= 0xBF) // 双字节 (0xC0 - 0xDF)
                            // (0x80
                            // - 0xBF),也可能在GB编码内
                            continue;
                        else
                            break;
                    } else if (0xE0 <= read && read <= 0xEF) {// 也有可能出错，但是几率较小
                        read = bis.read();
                        if (0x80 <= read && read <= 0xBF) {
                            read = bis.read();
                            if (0x80 <= read && read <= 0xBF) {
                                charset = "UTF-8";
                                break;
                            } else
                                break;
                        } else
                            break;
                    }
                }
            }
            bis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return charset;
    }
}
