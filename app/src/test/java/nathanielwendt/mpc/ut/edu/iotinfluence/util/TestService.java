package nathanielwendt.mpc.ut.edu.iotinfluence.util;

import nathanielwendt.mpc.ut.edu.iotinfluence.db.LocalActionDB;
import nathanielwendt.mpc.ut.edu.iotinfluence.device.DeviceUnavailableException;
import nathanielwendt.mpc.ut.edu.iotinfluence.device.Light;
import nathanielwendt.mpc.ut.edu.iotinfluence.service.Service;

public class TestService implements Service {

    @Override
    public Light light(final String deviceId, String requestId, LocalActionDB db) {
        return new Light(deviceId, requestId, db){
            @Override
            public void brightness(int level) throws DeviceUnavailableException {
                super.brightness(level);
            }

            @Override
            public void off() throws DeviceUnavailableException {
                //super.off();
            }

            @Override
            public void on() throws DeviceUnavailableException {
                super.on();
            }
        };
    }

    @Override
    public void fetchDevices(FetchDevicesCallback callback) {

    }

    @Override
    public String id() {
        return "testservice";
    }
}