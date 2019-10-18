package classifier;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.alibaba.tianchi.garbage_image_util.*;
import ij.ImagePlus;
import ij.process.ImageProcessor;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import org.apache.hadoop.fs.*;
import org.apache.hadoop.hdfs.DistributedFileSystem;

import javax.imageio.ImageIO;

public class RunMain {

	public static void main(String[] args) throws Exception {

		String savedModelTarPath = System.getenv(ConfigConstant.IMAGE_MODEL_PACKAGE_PATH);
//		String savedModelTarPath = "/home/cm/workspace/classifier/model.tar.gz";
		boolean ifReverseInputChannels = true;
		int[] inputShape = {1, 224, 224, 3};
		float[] meanValues = {15.68f, 8.78f, 5.94f};
		float scale = 1.0f;
		String input = "input_1";

//		String imagePath = "/home/cm/workspace/classifier/figures/baozhi/";
//		List<LocatedFileStatus> currentFileList;
//		currentFileList = new ArrayList();
//		Path imageRoot = new Path(imagePath);
//		org.apache.hadoop.conf.Configuration hadoopConfig = new org.apache.hadoop.conf.Configuration();
//		hadoopConfig.set("fs.hdfs.impl", DistributedFileSystem.class.getName());
//		hadoopConfig.set("fs.file.impl", LocalFileSystem.class.getName());
//		FileSystem fileSystem = FileSystem.get(new URI(imagePath), hadoopConfig);
//		RemoteIterator<LocatedFileStatus> it = fileSystem.listFiles(imageRoot, true);
//		int i = 0;
//		while(it.hasNext()) {
//			LocatedFileStatus locatedFileStatus = (LocatedFileStatus)it.next();
//			if (!locatedFileStatus.getPath().getName().startsWith(".")) {
//				currentFileList.add(locatedFileStatus);
//				++i;
//			}
//		}
//
//		Iterator var2 = currentFileList.iterator();
//		while(var2.hasNext()) {
//			LocatedFileStatus fileStatus = (LocatedFileStatus)var2.next();
//			ImageData imageLabel = new ImageData();
//			imageLabel.setId(fileStatus.getPath().getName());
//			long fileLength = fileStatus.getLen();
//			FSDataInputStream in = fileSystem.open(fileStatus.getPath());
//			byte[] buffer = new byte[(int)fileLength];
//			in.readFully(buffer);
//			in.close();
//			imageLabel.setImage(buffer);
//			Image originImage = Toolkit.getDefaultToolkit().createImage(imageLabel.getImage());
//			ImagePlus imagePlus = new ImagePlus(imageLabel.getId(), originImage);
//			ImageProcessor imageProcessor = imagePlus.getProcessor();
//			ImageProcessor resizeProcessor =  imageProcessor.resize(224, 224);
//			imagePlus.setProcessor(resizeProcessor);
//			BufferedImage newBuffered = imagePlus.getBufferedImage();
//			ImageIO.write(newBuffered, "JPG", new File("/home/cm/Desktop/" + String.valueOf((Math.random() * 10)) + ".jpg"));
//		}








		long fileSize = new File(savedModelTarPath).length();
		InputStream inputStream = new FileInputStream(savedModelTarPath);
		byte[] savedModelTarBytes = new byte[(int)fileSize];
		inputStream.read(savedModelTarBytes);
		
		StreamExecutionEnvironment flinkEnv = StreamExecutionEnvironment.getExecutionEnvironment();
		flinkEnv.setParallelism(1);

		ImageDirSource source = new ImageDirSource();
		
//		flinkEnv.disableOperatorChaining();
		
		flinkEnv.addSource(source).setParallelism(1)
				.flatMap(new ProcessingFlatMap()).setParallelism(1)
				.flatMap(new PredictionMapFunction(savedModelTarBytes, inputShape, ifReverseInputChannels,
						meanValues, scale, input)).setParallelism(1)
				.addSink(new ImageClassSink()).setParallelism(1);
		flinkEnv.execute();

//		flinkEnv.addSource(source).setParallelism(1)
//				.flatMap(new DebugFlatMap()).setParallelism(4)
//				.addSink(new ImageClassSink()).setParallelism(1);
//		flinkEnv.execute();

	}

}
