package nathanielwendt.mpc.ut.edu.iotinfluence.device;

/**
 * Created by nathanielwendt on 3/9/16.
 */
public interface DeviceCommand {
    <D extends Device> void onBind(D device) throws DeviceUnavailableException;
}
