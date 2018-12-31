package ImgOperation;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

import org.opencv.core.*;
import org.opencv.core.Mat;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.videoio.*;

public class PictureOperation extends Thread{
    public ServerSocket serverSocket = null;
    public Socket socket = null;
    public InputStream inputStream = null;
    public OutputStream outputStream = null;
    public FileOutputStream fileOutputStream = null;
    public FileInputStream fileInputStream = null;
    public DataInputStream dataInputStream = null;
    public DataOutputStream dataOutputStream = null;

    public Mat img = null;

    public void openVideo() {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        VideoCapture myVideo = new VideoCapture();
        myVideo.open(0);
        if (!myVideo.isOpened()) {
            System.out.println("Open video failed!");
            return;
        }
        img = new Mat();
        while (true) {
            myVideo.read(img);
            HighGui.imshow("Video", img);
            HighGui.waitKey(10);
        }
    }

    public static void main(String[]args){
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        try {
            Mat img = new Mat();
            img = Imgcodecs.imread("D:/TransportTest/Client.jpg");
            HighGui.imshow("test", img);
            HighGui.waitKey(10);
        }catch(Exception err){
            System.out.println("111");
        }
    }
}
