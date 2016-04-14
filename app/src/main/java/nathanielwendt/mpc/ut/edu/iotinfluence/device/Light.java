package nathanielwendt.mpc.ut.edu.iotinfluence.device;

import nathanielwendt.mpc.ut.edu.iotinfluence.db.LocalActionDB;

/**
 * Created by nathanielwendt on 3/8/16.
 */
public abstract class Light extends Device {
    public static String identifier = "Light";

    public enum ActionTypes {
        BRIGHTNESS("brightness"),
        OFF("off"),
        ON("on");

        private final String val;
        ActionTypes(String val){
            this.val = val;
        }

        public String val(){
            return this.val;
        }
    }

    public Light(String deviceId, String requestId) {
        super(deviceId, requestId);
    }

    public void brightness(int level) throws DeviceUnavailableException {
        LocalActionDB.completePending(this.requestId(), this.deviceId(), ActionTypes.BRIGHTNESS.val());
    }
    public void off() throws DeviceUnavailableException {
        LocalActionDB.completePending(this.requestId(), this.deviceId(), ActionTypes.OFF.val());
    }
    public void on() throws DeviceUnavailableException {
        LocalActionDB.completePending(this.requestId(), this.deviceId(), ActionTypes.ON.val());
    }
}
