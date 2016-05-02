package nathanielwendt.mpc.ut.edu.iotinfluence.util;


import java.util.ArrayList;
import java.util.List;

import nathanielwendt.mpc.ut.edu.iotinfluence.device.Device;
import nathanielwendt.mpc.ut.edu.iotinfluence.device.DeviceManager;
import nathanielwendt.mpc.ut.edu.iotinfluence.models.DeviceModel;
import nathanielwendt.mpc.ut.edu.iotinfluence.models.LightModel;

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

    public void removeLocations(){
        List<DeviceModel> newDevices = new ArrayList<>();
        for(DeviceModel device: devices){
            LightModel newDevice = new LightModel();
            LightModel oldDevice = (LightModel) device;
            newDevice.id = oldDevice.id;
            newDevice.service = oldDevice.service;
            newDevice.location = null;
            newDevices.add(newDevice);
        }
        this.devices = newDevices;
    }

    @Override
    public boolean initialized() {
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