package nathanielwendt.mpc.ut.edu.iotinfluence.devicereqs;

import nathanielwendt.mpc.ut.edu.iotinfluence.db.DBStorable;

/**
 * Created by nathanielwendt on 3/9/16.
 */
public abstract class DeviceReq implements DBStorable {
    abstract ReqOperator operator();
}
