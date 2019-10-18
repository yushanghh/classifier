package classifier;

import java.io.Serializable;
import java.util.List;

import com.intel.analytics.zoo.pipeline.inference.AbstractInferenceModel;

public class ExtendedInferenceModel extends AbstractInferenceModel implements Serializable {
    @Override
    public void doLoadTF(byte[] savedModelBytes, int[] inputShape, boolean ifReverseInputChannels, float[] meanValues, float scale, String input) {
        super.doLoadTF(savedModelBytes, inputShape, ifReverseInputChannels, meanValues, scale, input);
    }
}
