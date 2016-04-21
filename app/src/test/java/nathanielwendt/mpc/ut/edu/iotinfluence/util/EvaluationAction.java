package nathanielwendt.mpc.ut.edu.iotinfluence.util;

public interface EvaluationAction {
    SampleResult act(double x, double y);
    SampleResult onError(SampleResult expected, SampleResult actual);
    SampleResult onSuccess(SampleResult actual);
}
