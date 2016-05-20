package nathanielwendt.mpc.ut.edu.iotinfluence.devicereqs;

import nathanielwendt.mpc.ut.edu.iotinfluence.db.InteractionHistory;
import nathanielwendt.mpc.ut.edu.iotinfluence.models.DeviceModel;

/**
 * Created by nathanielwendt on 3/23/16.
 */
public class TypeReqOperator extends ItemwiseReqOperator {
    final TypeReq req;

    public TypeReqOperator(TypeReq req){
        this.req = req;
    }

    @Override
    public boolean match(DeviceModel device, InteractionHistory history) {
        for(TypeReq.Type type: req.types){
            if(type == device.type) return true;
        }
        return false;
    }
}
