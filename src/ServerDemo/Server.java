package ServerDemo;

import ImgOperation.PictureOperation;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.videoio.VideoCapture;

import java.io.*;
import java.net.*;


public class Server extends PictureOperation{
    /**
     * Server construct
     * @No param
     */
    public Server(){
        try{
            /**
             * ServerSocket initialize
             */
            serverSocket = new ServerSocket(1056);
            System.out.println("ServerSocket initialize...");
            System.out.println("Listening...");

            socket = serverSocket.accept();
            System.out.println("Connect to Client!");
        }catch (IOException err){
            System.out.println("Server error! [1]");
        }catch(Exception err){
            System.out.println("Server error! [2]");
        }
    }

    /**
     * Server transport picture
     * @No param
     */
    public void writeSocket(){
        try {

            /**
             * 先打开摄像头, 捕获一帧, 写入硬盘, 传输
             */
            System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
            VideoCapture myVideo = new VideoCapture();
            myVideo.open(0);
            if (!myVideo.isOpened()) {
                System.out.println("Open video failed!");
                return;
            }
            Mat img = new Mat();

            /**
             * opencv捕获一帧, 然后存入硬盘, 再传输
             */
            while (true) {
                //opencv捕获一帧
                myVideo.read(img);
                Imgcodecs.imwrite("D:/Test/test.jpg", img);

                //File初始化
                File file = new File("D:/Test/test.jpg");
                if (!file.exists())
                    return;

                //socket输出端口初始化
                fileInputStream = new FileInputStream(file);
                dataOutputStream = new DataOutputStream(socket.getOutputStream());

                byte[] bytes = new byte[1024];
                int length = 0;
                int progress = 0;

                /**
                 * transport jpg picture
                 */
                while ((length = fileInputStream.read(bytes, 0, bytes.length)) != -1) {
                    dataOutputStream.write(bytes, 0, length);
                    dataOutputStream.flush();
                    progress += length;
                    System.out.println(progress);

                    System.out.println("begin!!!");
                    System.out.println(bytes[bytes.length - 5]);
                    System.out.println(bytes[bytes.length - 4]);
                    System.out.println(bytes[bytes.length - 3]);
                    System.out.println(bytes[bytes.length - 2]);
                    System.out.println(bytes[bytes.length - 1]);
                    System.out.println("end!!!");
                }
                System.out.println("Transport picture successful!");
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
     * Threan run()
     * @No param
     */
    public void run(){
        writeSocket();
    }

    public static void main(String[]args){
        Server myServer = new Server();
        myServer.start();
    }
}
