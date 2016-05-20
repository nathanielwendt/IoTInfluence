package nathanielwendt.mpc.ut.edu.iotinfluence.devicereqs;

import nathanielwendt.mpc.ut.edu.iotinfluence.db.InteractionHistory;
import nathanielwendt.mpc.ut.edu.iotinfluence.models.DeviceModel;

/**
 * Created by nathanielwendt on 3/23/16.
 */
public abstract class ItemwiseReqOperator extends ReqOperator {
    public abstract boolean match(DeviceModel device, InteractionHistory history);
}
