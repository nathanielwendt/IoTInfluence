package nathanielwendt.mpc.ut.edu.iotinfluence.devicereqs;

import nathanielwendt.mpc.ut.edu.iotinfluence.devices.Device;

/**
 * Created by nathanielwendt on 3/23/16.
 */
public abstract class ItemwiseReqOperator extends ReqOperator {
    public abstract boolean match(Device device);
}
