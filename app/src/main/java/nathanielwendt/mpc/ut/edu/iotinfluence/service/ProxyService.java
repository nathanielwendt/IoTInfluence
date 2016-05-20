package nathanielwendt.mpc.ut.edu.iotinfluence.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nathanielwendt.mpc.ut.edu.iotinfluence.db.LocalActionDB;
import nathanielwendt.mpc.ut.edu.iotinfluence.device.Device;
import nathanielwendt.mpc.ut.edu.iotinfluence.device.DeviceUnavailableException;
import nathanielwendt.mpc.ut.edu.iotinfluence.device.Light;
import nathanielwendt.mpc.ut.edu.iotinfluence.models.DeviceModel;

/**
 * Created by nathanielwendt on 5/3/16.
 */
public class ProxyService implements Service {
    private static final String PROXY_DELIM = "Proxy:";

    private Map<String, DeviceModel> deviceModelMap = new HashMap<>();
    private Map<String, Device> deviceMap = new HashMap<>();
    private String requestId;

    public ProxyService(List<DeviceModel> devices, String requestId){
        this.requestId = requestId;
        setDevices(devices);
    }

    public String id(){
        return this.requestId;
    }

    public String getKey(String deviceId, String requestId){
        return requestId + deviceId;
    }

    public void setDevices(List<DeviceModel> devices){
        for(DeviceModel device : devices) {
            deviceModelMap.put(getKey(device.id, requestId), device);
        }
    }

    public Device getActingDevice(Device device){
        String key = getKey(device.deviceId(), device.requestId());
        return deviceMap.get(key);
    }

    public static boolean isProxyId(String id){
        return id.contains(PROXY_DELIM);
    }

    public static String indexToId(int index){
        return PROXY_DELIM + index;
    }

    public static int idToIndex(String id){
        return Integer.valueOf(id.split(PROXY_DELIM)[1]);
    }


    @Override
    public Light light(final String deviceId, final String requestId, final LocalActionDB localActionDB) {
        return new Light(deviceId, requestId, localActionDB){

            private Light resolveDevice(){
                DeviceModel deviceModel = deviceModelMap.get(getKey(deviceId, requestId));
                Light light = (Light) deviceMap.get(getKey(deviceId, requestId));
                if(light == null){
                    light = (Light) deviceModel.abs(requestId, localActionDB);
                    deviceMap.put(getKey(deviceId, requestId), light);
                }
                return light;
            }

            @Override
            protected void brightness(int prevLevel, int nextLevel) {
                throw new RuntimeException("Not implemented fully yet");
//                Light light = resolveDevice();
//                light.brightness(prevLevel, nextLevel);
            }

            @Override
            public void brightness(int level) throws DeviceUnavailableException {
                Light light = resolveDevice();
                light.brightness(level);
            }

            @Override
            public void off() throws DeviceUnavailableException {
                Light light = resolveDevice();
                light.off();
            }

            @Override
            public void on() throws DeviceUnavailableException {
                Light light = resolveDevice();
                light.on();
            }
        };
    }

    @Override
    public void fetchDevices(FetchDevicesCallback callback) {
        throw new RuntimeException("Proxy Service should never fetch retrieve");
    }


}
