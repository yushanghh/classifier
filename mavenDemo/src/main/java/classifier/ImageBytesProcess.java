package classifier;

import com.alibaba.tianchi.garbage_image_util.ImageData;
import com.intel.analytics.bigdl.transform.vision.image.BytesToMat;
import com.intel.analytics.bigdl.transform.vision.image.ImageFeature;
import com.intel.analytics.bigdl.transform.vision.image.ImageFrame;
import com.intel.analytics.zoo.feature.image.ImageMatToFloats;
import com.intel.analytics.zoo.pipeline.inference.JTensor;
import ij.ImageJ;
import ij.ImagePlus;
import ij.plugin.filter.GaussianBlur;
import ij.process.ByteProcessor;
import ij.process.ImageProcessor;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.CLAHE;
import org.opencv.imgproc.Imgproc;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class ImageBytesProcess {
	static JTensor process(ImageData imageDataInput) throws Exception {
//	    return processByOpenCV(imageDataInput.getImage());
		return processByImageJ(imageDataInput);
	}

	public static JTensor processByImageJ(ImageData imageDataInput) throws Exception{
        byte[] image = imageDataInput.getImage();
        Image originImage = Toolkit.getDefaultToolkit().createImage(image);
        ImagePlus imagePlus = new ImagePlus(imageDataInput.getId(), originImage);
        ImageProcessor imageProcessor = imagePlus.getProcessor();
        ImageProcessor resizeProcessor =  imageProcessor.resize(224, 224);

//        GaussianBlur gaussianBlur = new GaussianBlur();
//        gaussianBlur.blur(resizeProcessor, 0.1);
//        gaussianBlur.run(resizeProcessor);
        imagePlus.setProcessor(resizeProcessor);
//        imagePlus.updateAndDraw();
        BufferedImage newBuffered = imagePlus.getBufferedImage();

        List<Float> imageData = new ArrayList<>();
        for(int k = 0; k < 3; k++){
            for(int i = 0; i < 224; i++){
                for(int j = 0; j < 224; j++){
                    int rgb = newBuffered.getRGB(j, i);
//			    System.out.println("RGBVALUE: " + String.valueOf(rgb));
                    switch (k){
                        case 2: imageData.add((float)((rgb & 0xff0000) >> 16));  //r
                            break;
                        case 1: imageData.add((float)((rgb & 0xff00) >> 8));  //g
                            break;
                        case 0: imageData.add((float)(rgb & 0xff));  //b
                            break;
                    }
                }
            }
        }

        int[] shape = {1, 224, 224, 3};
        JTensor jTensor = new JTensor(imageData, shape);
        return jTensor;
    }

	public static JTensor processByOpenCV(byte[] image) throws Exception{

	    System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

//        nu.pattern.OpenCV.loadLibrary();
	    InputStream inputStream = new ByteArrayInputStream(image);
        BufferedImage bufferedImage = ImageIO.read(inputStream);
        Mat srcMat = new Mat(bufferedImage.getHeight(), bufferedImage.getWidth(), CvType.CV_8UC3);
        srcMat.put(0, 0, image);

        Mat resizedMat = new Mat();
        Imgproc.resize(srcMat, resizedMat, new Size(224, 224), 0, 0, Imgproc.INTER_AREA);

        Mat imgLab = new Mat();
        Imgproc.cvtColor(resizedMat, imgLab, Imgproc.COLOR_BGR2Lab);
        List<Mat> lab = new ArrayList<>();
        Core.split(resizedMat, lab);

        CLAHE clahe = Imgproc.createCLAHE();
        Mat claheMat = new Mat();
        clahe.apply(lab.get(0), claheMat);
        lab.get(0).setTo(claheMat);
        Mat claheImage = new Mat();
        Core.merge(lab, claheImage);
        Mat newClaheImage = new Mat();
        Imgproc.cvtColor(claheImage, newClaheImage, Imgproc.COLOR_Lab2BGR);

        Imgcodecs.imwrite("/home/cm/Desktop/\" + String.valueOf((Math.random() * 10)) + \".jpg", newClaheImage);

        for(int i = 0; i < newClaheImage.rows(); i++){
            for(int j = 0; j < newClaheImage.cols(); j++){
                System.out.println(newClaheImage.get(j, i)[0]);   //blue
                System.out.println(newClaheImage.get(j, i)[1]);   //green
                System.out.println(newClaheImage.get(j, i)[2]);   //red
            }
        }


	    JTensor jTensor = new JTensor();
	    return jTensor;
    }
}
