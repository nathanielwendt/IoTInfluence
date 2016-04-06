package nathanielwendt.mpc.ut.edu.iotinfluence.service;

import java.util.List;

import nathanielwendt.mpc.ut.edu.iotinfluence.device.Light;
import nathanielwendt.mpc.ut.edu.iotinfluence.models.DeviceModel;

/**
 * Created by nathanielwendt on 3/8/16.
 */
public interface Service {
    Light light(String deviceId, String requestId);
    public void fetchDevices(FetchDevicesCallback callback);


    public interface FetchDevicesCallback {
        void onFetch(List<DeviceModel> fetchedDevices);
    }
}
