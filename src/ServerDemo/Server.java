package ServerDemo;

import java.lang.String;

import java.io.*;
import java.net.*;

/**
 * 服务器的功能是接收从一个客户端发送过来的的数据, 然后转发给另一个客户端.
 */

public class Server extends Thread{
    private int port1;
    private int port2;

    private ServerSocket serverSocket1;
    private ServerSocket serverSocket2;

    private Socket socket1;
    private Socket socket2;

    private DataInputStream dataInputStream = null;
    private DataOutputStream dataOutputStream = null;
    private int linkedNumber = 0;

    /**
     * Server construct
     * @No param
     */
    public Server(){
        port1 = 1056;
        port2 = 1057;
        try{
            System.out.println("等待客户端1(端口: " + port1 + ") 和客户端2(端口: " + port2 + ") 的连接.");
            serverSocket1 = new ServerSocket(port1);
            serverSocket2 = new ServerSocket(port2);
            initializePort();
        }catch (IOException err){
            System.out.println("Server error! [1]");
        }catch(Exception err){
            System.out.println("Server error! [2]");
        }
    }

    public Server(int count1, int count2){
        port1 = count1;
        port2 = count2;
        try{
            System.out.println("等待客户端1(端口: " + port1 + ") 和客户端2(端口: " + port2 + ") 的连接.");
            serverSocket1 = new ServerSocket(port1);
            serverSocket2 = new ServerSocket(port2);
            initializePort();
        }catch (IOException err){
            System.out.println("Server error! [1]");
        }catch(Exception err){
            System.out.println("Server error! [2]");
        }
    }

    /**
     * 分配两个线程初始化端口
     */
    public void initializePort(){
        new Thread(){
            public void run(){
                try {
                    socket1 = serverSocket1.accept();
                    System.out.println("客户端1 " + port1 + " 连接成功!");
                    linkedNumber++;
                }catch(IOException err){}
            }
        }.start();

        new Thread(){
            public void run(){
                try {
                    socket2 = serverSocket2.accept();
                    System.out.println("客户端2 " + port2 + " 连接成功!");
                    linkedNumber++;
                }catch(IOException err){}
            }
        }.start();
    }

    /**
     * 测试端口是否被占用
     * @param port
     * @return
     */
    public boolean isPortAvailable(int port){
        ServerSocket test = null;
        try{
            test = new ServerSocket(port);
        }catch(IOException err){
            return false;
        }
        if(test != null)
            try {
                test.close();
            }catch(IOException err){}
        return true;

    }

    /**
     * 接收并传输字节
     */
    public void receiveAndTransportDate(){
        int choice = 1;
        while(true) {
            if (choice == 1) {
                try {
                    dataInputStream = new DataInputStream(socket1.getInputStream());
                    dataOutputStream = new DataOutputStream(socket2.getOutputStream());
                    System.out.println("Initialize choice1.");
                } catch (IOException err) {
                    System.out.println("Server receive data failed!");
                    err.printStackTrace();
                }
            }else {
                try {
                    dataInputStream = new DataInputStream(socket2.getInputStream());
                    dataOutputStream = new DataOutputStream(socket1.getOutputStream());
                    System.out.println("Initialize choice2.");
                } catch (IOException err) {
                    System.out.println("Server receive data failed!");
                    err.printStackTrace();
                }
            }
            if (++choice > 2)
                choice = 1;
            int length = 0;
            int progress = 0;

            try {
                System.out.println("Serversocket ready to receive bytes!");
                byte[] bytes = new byte[1024 * 100];

                //read会造成线程阻塞
                while ((length = dataInputStream.read(bytes, 0, bytes.length)) != -1) {
                    progress += length;
                    System.out.println("Serversocket totally received " + progress + "bytes!");
                    dataOutputStream.write(bytes, 0, length);
                    dataOutputStream.flush();
                    if(length <= 2 || (bytes[length - 2] == -1 && bytes[length - 1] == -39))
                        break;
                }
            } catch (IOException err) {
                System.out.println("Server transport bytes failed!");
                err.printStackTrace();
            }catch (Exception err){
                System.out.println("Server timeout!");
            }
        }
    }

    /**
     * Thread run()
     */
    public void run(){
        while(linkedNumber < 2){}
        receiveAndTransportDate();
    }

    public static void main(String[]args){
        Server myServer = new Server();
        myServer.start();
    }
}
