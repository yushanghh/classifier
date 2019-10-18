package classifier;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.flink.api.common.functions.RichFlatMapFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.util.Collector;

import com.alibaba.tianchi.garbage_image_util.IdLabel;
import com.alibaba.tianchi.garbage_image_util.ImageData;
import com.intel.analytics.zoo.pipeline.inference.InferenceModel;
import com.intel.analytics.zoo.pipeline.inference.JTensor;
import org.mortbay.log.Log;

public class PredictionMapFunction extends RichFlatMapFunction<Tuple2<String, JTensor>, IdLabel> {

	private byte[] savedModelTarBytes;
	private int[] inputShape;
	private boolean ifReverseInputChannels;
	private float[] meanValues;
	private float scale;
	private String input;
	private ExtendedInferenceModel model;
//	private Map<Integer, String> className;
	
	public PredictionMapFunction(byte[] savedModelTarBytes, int[] inputShape, boolean ifReverseInputChannels,
			float[] meanValues, float scale, String input) {
		this.savedModelTarBytes = savedModelTarBytes;
		this.inputShape = inputShape;
		this.ifReverseInputChannels = ifReverseInputChannels;
		this.meanValues = meanValues;
		this.scale = scale;
		this.input = input;
	}

	public PredictionMapFunction(int[] inputShape, boolean ifReverseInputChannels,
								 float[] meanValues, float scale, String input) {
		this.savedModelTarBytes = null;
		this.inputShape = inputShape;
		this.ifReverseInputChannels = ifReverseInputChannels;
		this.meanValues = meanValues;
		this.scale = scale;
		this.input = input;
	}

	@Override
	public void flatMap(Tuple2<String, JTensor> value, Collector<IdLabel> out) throws Exception {
		List<JTensor> data = Arrays.asList(value.f1);
		List<List<JTensor>> inputs = new ArrayList<>();
		inputs.add(data);
		float[] outputData = model.doPredict(inputs).get(0).get(0).getData();
		int index = Util.indexOfMax(outputData);
		String label = Util.name[index];
		IdLabel idLabel = new IdLabel(value.f0, label);
		out.collect(idLabel);
		
		System.out.println("result: " + value.f0 + " " + String.valueOf(index) + " " + label);
	}
	
	@Override
	public void open(Configuration parameters) throws Exception {
//		System.out.println("read class name");
//		className = Util.readClassName();
		model = new ExtendedInferenceModel();

		if(this.savedModelTarBytes == null){
//			model.doLoadTF("/home/cm/workspace/classifier/Flink_export_20190812/", inputShape, ifReverseInputChannels,
//				meanValues, scale, input);
		} else {
			model.doLoadTF(savedModelTarBytes, inputShape, ifReverseInputChannels,
					meanValues, scale, input);
		}
	}

	@Override
	public void close() throws Exception {
		model.release();
	}


}
