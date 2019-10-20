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
		boolean ifReverseInputChannels = true;
		int[] inputShape = {1, 224, 224, 3};
		float[] meanValues = {15.68f, 8.78f, 5.94f};
		float scale = 1.0f;
		String input = "input_1";

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

	}

}
