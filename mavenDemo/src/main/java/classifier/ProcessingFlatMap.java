package classifier;

import com.alibaba.tianchi.garbage_image_util.IdLabel;
import org.apache.flink.api.common.functions.RichFlatMapFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.util.Collector;

import com.alibaba.tianchi.garbage_image_util.ImageData;
import com.intel.analytics.zoo.pipeline.inference.JTensor;
import org.mortbay.log.Log;

import java.security.acl.LastOwnerException;

public class ProcessingFlatMap extends RichFlatMapFunction<ImageData, Tuple2<String, JTensor>> {

	@Override
	public void flatMap(ImageData value, Collector<Tuple2<String, JTensor>> out) throws Exception {
		// image processing
//		System.out.println("image size: " + String.valueOf(value.getImage().length));
		JTensor imageTensor = ImageBytesProcess.process(value);
		out.collect(new Tuple2<>(value.getId(), imageTensor));
	}

}
