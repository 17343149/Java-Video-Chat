package ClientDemo;

import ImgOperation.PictureOperation;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.highgui.HighGui;

import java.io.*;
import java.net.*;

public class Client extends PictureOperation{
    Socket socket = null;

    public Client(){
        try {
            socket = new Socket("127.0.0.1", 1056);
        }catch (IOException err){
            System.out.println("Client initialize failed!");
        }
    }

    /**
     * Client receive picture from server
     */
    public void receiveSocket() {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        Mat img = new Mat();
        try {
            while(true) {
                byte[] bytes = new byte[1024 * 100];
                int length = 0;
                int progress = 0;

                //输入端口初始化
                System.out.println("接收数据中...");
                dataInputStream = new DataInputStream(socket.getInputStream());

                //创建jpg文件
                fileOutputStream = new FileOutputStream(new File("D:/TransportTest/Client.jpg"));

                //输出端口初始化, 向Server发送是否接收完成的信号, 否两者速率不一致会报错
                dataOutputStream = new DataOutputStream(socket.getOutputStream());

                //接受图片的数据
                while ((length = dataInputStream.read(bytes, 0, bytes.length)) != -1) {
                    progress += length;
                    System.out.println(progress);
                    fileOutputStream.write(bytes, 0, length);
                    fileOutputStream.flush();

                    //一帧图片传输完成
                    if((bytes[length - 2] == -1 && bytes[length - 1] == -39) || progress > 102400){
                        try {
                            img = Imgcodecs.imread("D:/TransportTest/Client.jpg");
                            if(!img.empty()) {
                                HighGui.imshow("Video", img);
                                HighGui.waitKey(5);
                            }else System.out.println("Empty picture!");
                        }catch(Exception err){
                            System.out.println("empty!!!!!!");
                        }

                        //重新开始写入图片, File的对象创建后为0字节
                        fileOutputStream = new FileOutputStream(new File("D:/TransportTest/Client.jpg"));
                        progress = 0;
                    }

                }
            }
        }catch(IOException err){
            System.out.println("Client receive failed! [1]");
        }catch (Exception err) {
            System.out.println("Client receive failed! [2]");
        }
    }

    public void run(){
        receiveSocket();
    }

    public static void main(String[]args){
        Client myClient= new Client();
        myClient.start();
    }
}
