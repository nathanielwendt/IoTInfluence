package nathanielwendt.mpc.ut.edu.iotinfluence.devicereqs;

import java.util.List;

import nathanielwendt.mpc.ut.edu.iotinfluence.db.InteractionHistory;
import nathanielwendt.mpc.ut.edu.iotinfluence.models.DeviceModel;

/**
 * Created by nathanielwendt on 3/23/16.
 */
public abstract class AggregateReqOperator extends ReqOperator {
    public abstract List<DeviceModel> resolve(List<DeviceModel> candidates, InteractionHistory history);
}
