package nathanielwendt.mpc.ut.edu.iotinfluence.device;

/**
 * Created by nathanielwendt on 3/8/16.
 */
public abstract class Light extends Device {
    public static String identifier = "Light";


    public Light(String deviceId, String requestId) {
        super(deviceId, requestId);
    }

    public abstract void brightness(int level) throws DeviceUnavailableException;
    public abstract void off() throws DeviceUnavailableException;
    public abstract void on() throws DeviceUnavailableException;
}
