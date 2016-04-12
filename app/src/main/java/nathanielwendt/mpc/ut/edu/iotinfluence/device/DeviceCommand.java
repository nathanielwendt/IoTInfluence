package nathanielwendt.mpc.ut.edu.iotinfluence.device;

/**
 * Created by nathanielwendt on 3/9/16.
 */
public interface DeviceCommand {
    void onBind(Device device) throws DeviceUnavailableException;
}
