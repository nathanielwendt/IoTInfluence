package nathanielwendt.mpc.ut.edu.iotinfluence.util;

/**
 * Created by nathanielwendt on 4/20/16.
 */
public interface TrainingAction {
    SampleResult act(double x, double y);
    SampleResult onError(SampleResult expected, SampleResult actual);
}
