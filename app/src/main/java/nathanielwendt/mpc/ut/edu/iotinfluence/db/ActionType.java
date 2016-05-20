package nathanielwendt.mpc.ut.edu.iotinfluence.db;

import nathanielwendt.mpc.ut.edu.iotinfluence.device.DeviceUnavailableException;

/**
 * Created by nathanielwendt on 4/30/16.
 */
public interface ActionType extends DBStorable {
    String val();
    void undo() throws DeviceUnavailableException;
}
