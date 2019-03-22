package ClientDemo;

import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.highgui.HighGui;
import org.opencv.videoio.VideoCapture;

import java.io.*;
import java.net.*;

public class Client2 extends Thread{
    private Socket socket = null;
    private FileOutputStream fileOutputStream = null;
    private FileInputStream fileInputStream = null;
    private DataInputStream dataInputStream = null;
    private DataOutputStream dataOutputStream = null;

    private Mat img = null;

    private int port;
    private String host;

    private boolean flag = false;

    private String writePath = "D:/Test/test.jpg";
    private String readPath = "D:/TransportTest/Client.jpg";

    private byte[] bytes = new byte[1024 * 100];
    private byte[] receiveBytes = new byte[1024 * 100];

    static{System.loadLibrary(Core.NATIVE_LIBRARY_NAME);}

    /**
     * 初始化客户端, 连接上服务器的一个端口
     */
    public Client2(){
        port = 1056;
        host = "127.0.0.1";
        try{
            socket = new Socket(host, port);
            System.out.println("Client initialize success! port: " + port);
            socket.setSoTimeout(60000);
        }catch (IOException err){
            System.out.println("Client initialize failed!");
        }
    }

    public Client2(int temp){
        port = temp;
        host = "127.0.0.1";
        try{
            socket = new Socket(host, port);
            System.out.println("Client initialize success! port: " + port);
            socket.setSoTimeout(60000);
        }catch (IOException err){
            System.out.println("Client initialize failed!");
        }
    }

    /**
     * 判断服务器的指定端口是否可以连接
     */
    public boolean isHostAvailable(String host, int temp){
        Socket test = new Socket();
        try{
            System.out.println("Try to connect " + host + ":" + temp);
            test.connect(new InetSocketAddress(host, temp));
        }catch(IOException err){
            return false;
        }finally {
            try{
                test.close();
            }catch(IOException error){
                ;
            }
        }
        return true;
    }

    /**
     * 打开摄像头
     * @param temp
     * @return void
     */
    public void openVideo(VideoCapture temp){
        temp.open(0);
        if(!temp.isOpened()) {
            System.out.println("Open video failed!");
            System.exit(1);
        }
    }

    /**
     * 客户端套接字的操作, 先传输后接收
     * @param
     * @return void
     */
    public void clientSocket(){
        try {
            dataInputStream = new DataInputStream(socket.getInputStream());
            dataOutputStream = new DataOutputStream(socket.getOutputStream());

            /**
             * 打开摄像头
             */
            VideoCapture myVideo = new VideoCapture();
            openVideo(myVideo);
            img = new Mat();

            /**
             * 自己的摄像头为一个单独的线程
             */
            new Thread(){
                public void run(){
                    Mat myImg = new Mat();
                    while(true) {
                        myVideo.read(myImg);
                        Imgcodecs.imwrite(writePath, myImg);
                        HighGui.imshow("MyVideo", myImg);
                        HighGui.waitKey(5);
                    }
                }
            }.start();

            /**
             * opencv捕获一帧, 存入硬盘, 传输, 接收服务器发送的图片, 显示
             */
            while (true) {
                //File初始化
                File file = new File(writePath);
                if (!file.exists())
                    return;

                //socket输出端口初始化
                fileInputStream = new FileInputStream(file);

                int length = 0;
                int progress = 0;

                /**
                 * 给服务器传输图片, 每次最多传输100KB.
                 */
                while ((length = fileInputStream.read(bytes, 0, bytes.length)) != -1) {
                    dataOutputStream.write(bytes, 0, length);
                    dataOutputStream.flush();
                    progress += length;
                    System.out.println("jpg文件大小: " + progress);
                }
                System.out.println("图片传输完成!");

                /**
                 * 接收服务器传输的图片并显示
                 */
                fileOutputStream = new FileOutputStream(new File(readPath));
                System.out.println("接收数据中...");
                int receiveLength = 0;
                int receiveProgress = 0;

                while((receiveLength = dataInputStream.read(receiveBytes, 0, receiveBytes.length)) != -1){
                    receiveProgress += receiveLength;
                    System.out.println("Receive: " + receiveProgress);

                    fileOutputStream.write(receiveBytes, 0, receiveLength);
                    fileOutputStream.flush();

                    //接收完成, 写入硬盘上的图片, 并显示
                    if(receiveLength <= 2 || (receiveBytes[receiveLength - 2] == -1 && receiveBytes[receiveLength - 1] == -39)){
                        flag = true;
                        try{
                            img = Imgcodecs.imread(readPath);
                            if(!img.empty()){
                                HighGui.imshow("Video", img);
                                HighGui.waitKey(5);
                            }else System.out.println("Img empty!");
                        }catch(Exception err){
                            System.out.println("Receive picture failed!");
                            err.printStackTrace();
                        }finally {
                            receiveProgress = 0;
                        }
                    }
                    if(flag){
                        flag = false;
                        System.out.println("Picture has received, show and break!");
                        break;
                    }
                }
            }
        }catch(IOException err){
            System.out.println("Server transport img failed! [1]");
            err.printStackTrace();
        }catch(Exception err){
            System.out.println("Server transport img failed! [2]");
            err.printStackTrace();
        }
    }

    /**
     * 线程方法
     */
    public void run() {
        clientSocket();
    }

    public static void main(String[]args){
        Client2 myClient= new Client2(1057);
        myClient.start();
    }
}
