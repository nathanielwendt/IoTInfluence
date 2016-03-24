package nathanielwendt.mpc.ut.edu.iotinfluence.devicereqs;

import java.util.List;

import nathanielwendt.mpc.ut.edu.iotinfluence.devices.Device;

/**
 * Created by nathanielwendt on 3/23/16.
 */
public abstract class AggregateReqOperator extends ReqOperator {
    public abstract List<Device> resolve(List<Device> candidates);
}
