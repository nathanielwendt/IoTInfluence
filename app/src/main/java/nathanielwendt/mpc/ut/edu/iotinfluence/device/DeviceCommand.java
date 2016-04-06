package nathanielwendt.mpc.ut.edu.iotinfluence.device;

import nathanielwendt.mpc.ut.edu.iotinfluence.device.Device;
import nathanielwendt.mpc.ut.edu.iotinfluence.device.DeviceUnavailableException;

/**
 * Created by nathanielwendt on 3/9/16.
 */
public interface DeviceCommand {
    public void onBind(Device device) throws DeviceUnavailableException;
    public void onUnbind(Device device) throws DeviceUnavailableException;
}
