package nathanielwendt.mpc.ut.edu.iotinfluence.device;

import java.util.List;

import nathanielwendt.mpc.ut.edu.iotinfluence.models.DeviceModel;
import nathanielwendt.mpc.ut.edu.iotinfluence.util.InitializedCallback;

/**
 * Created by nathanielwendt on 3/25/16.
 */
public interface DeviceManager {

    public void scan();
    public void scan(InitializedCallback callback);
    public boolean initialized();

    public <D extends Device> List<DeviceModel> fetchDevices(Class<D> clazz);
    public List<DeviceModel> fetchDevices();

}
