package nathanielwendt.mpc.ut.edu.iotinfluence.devicereqs;

import java.util.List;

import nathanielwendt.mpc.ut.edu.iotinfluence.devices.Device;

/**
 * Created by nathanielwendt on 3/23/16.
 */
public class SpatialReqOperator extends AggregateReqOperator {
    private final SpatialReq req;

    public SpatialReqOperator(SpatialReq req){
        this.req = req;
    }

    @Override
    public List<Device> resolve(List<Device> candidates){
        return null;
    }
}
