package nathanielwendt.mpc.ut.edu.iotinfluence.devicereqs;

/**
 * Created by nathanielwendt on 5/17/16.
 */
public class LOSReq extends DeviceReq {
    double tolerance;
    double heading;

    public LOSReq(double heading, double tolerance){
        this.tolerance = tolerance;
        this.heading = heading;
    }

    @Override
    ReqOperator operator() {
        return new LOSReqOperator(this);
    }

    @Override
    public String toSchema() {
        return null;
    }
}
