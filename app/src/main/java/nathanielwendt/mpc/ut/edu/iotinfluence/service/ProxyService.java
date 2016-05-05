package nathanielwendt.mpc.ut.edu.iotinfluence.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nathanielwendt.mpc.ut.edu.iotinfluence.device.DeviceUnavailableException;
import nathanielwendt.mpc.ut.edu.iotinfluence.device.Light;
import nathanielwendt.mpc.ut.edu.iotinfluence.models.DeviceModel;

/**
 * Created by nathanielwendt on 5/3/16.
 */
public class ProxyService implements Service {
    private static final String PROXY_DELIM = "Proxy:";

    private Map<String, DeviceModel> deviceMap = new HashMap<>();
    private String requestId;

    public ProxyService(List<DeviceModel> devices, String requestId){
        this.requestId = requestId;
        setDevices(devices);
    }

    public String getKey(String deviceId, String requestId){
        return requestId + deviceId;
    }

    public void setDevices(List<DeviceModel> devices){
        for(DeviceModel device : devices) {
            deviceMap.put(getKey(device.id, requestId), device);
        }
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
    public Light light(final String deviceId, final String requestId) {
        return new Light(deviceId, requestId){

            private Light resolveDevice(){
                DeviceModel deviceModel = deviceMap.get(getKey(deviceId, requestId));
                return (Light) deviceModel.abs(requestId);
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
