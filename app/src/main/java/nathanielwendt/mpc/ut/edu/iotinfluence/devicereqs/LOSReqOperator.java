package nathanielwendt.mpc.ut.edu.iotinfluence.devicereqs;

import java.util.List;

import nathanielwendt.mpc.ut.edu.iotinfluence.db.InteractionHistory;
import nathanielwendt.mpc.ut.edu.iotinfluence.models.DeviceModel;

/**
 * Created by nathanielwendt on 5/17/16.
 */
public class LOSReqOperator extends AggregateReqOperator {
    LOSReq req;

    public LOSReqOperator(LOSReq req){
        this.req = req;
    }

    @Override
    public List<DeviceModel> resolve(List<DeviceModel> candidates, InteractionHistory history) {
        //resolve the line of sight

        return null;
    }
}
