package OpencvTest;

import org.opencv.core.*;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;

public class WriteImgTest {

    public static void main(String[]args){
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        Mat img = new Mat();
        img = Imgcodecs.imread("D:/Test/test.jpg");
        Imgcodecs.imwrite("D:/Test/testjpg.jpg", img);
        HighGui.imshow("test", img);
        HighGui.waitKey();

        img = Imgcodecs.imread("D:/Test/testpng.png");
        HighGui.imshow("test1", img);
        HighGui.waitKey();
    }
}
