package nathanielwendt.mpc.ut.edu.iotinfluence.util;


import java.util.List;

import nathanielwendt.mpc.ut.edu.iotinfluence.device.Device;
import nathanielwendt.mpc.ut.edu.iotinfluence.device.DeviceManager;
import nathanielwendt.mpc.ut.edu.iotinfluence.models.DeviceModel;

public class TestDevManager implements DeviceManager {
    private List<DeviceModel> devices;
    private boolean initialized;

    public TestDevManager(List<DeviceModel> devices){
        this.devices = devices;
    }

    @Override
    public void scan() {
        initialized = true;
    }

    @Override
    public void scan(InitializedCallback callback) {
        initialized = true;
        callback.onInit();
    }

    @Override
    public boolean isInitialized() {
        return initialized;
    }

    @Override
    public <D extends Device> List<DeviceModel> fetchDevices(Class<D> clazz) {
        return this.devices;
    }

    @Override
    public List<DeviceModel> fetchDevices() {
        return this.devices;
    }
}