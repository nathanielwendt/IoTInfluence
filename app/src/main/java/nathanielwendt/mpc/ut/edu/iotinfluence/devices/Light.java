package nathanielwendt.mpc.ut.edu.iotinfluence.devices;

import nathanielwendt.mpc.ut.edu.iotinfluence.DeviceUnavailableException;

/**
 * Created by nathanielwendt on 3/8/16.
 */
public interface Light extends Device {
    public String identifier = "Light";
    //0-100 brightness
    public void brightness(int level) throws DeviceUnavailableException;
    public void off() throws DeviceUnavailableException;
    public void on() throws DeviceUnavailableException;
}
