package nathanielwendt.mpc.ut.edu.iotinfluence.device;

import nathanielwendt.mpc.ut.edu.iotinfluence.db.ActionType;
import nathanielwendt.mpc.ut.edu.iotinfluence.db.LocalActionDB;
import nathanielwendt.mpc.ut.edu.iotinfluence.util.Util;

/**
 * Created by nathanielwendt on 3/8/16.
 */
public abstract class Light extends Device {
    public static String identifier = "Light";

    public enum LightAction implements ActionType {
        BRIGHTNESS("brightness"){
            @Override public void undo() throws DeviceUnavailableException {
                this.light.brightness(this.prevBrightness);
            }
        },
        OFF("off") {
            @Override public void undo() throws DeviceUnavailableException {
                this.light.on();
            }
        },
        ON("on") {
            @Override public void undo() throws DeviceUnavailableException {
                this.light.off();
            }
        };

        protected final String val;
        protected Light light;
        protected int prevBrightness;
        protected int nextBrightness;
        LightAction(String val){
            this.val = val;
        }

        public String val(){
            return this.val;
        }

        public void setObj(Light light){
            this.light = light;
        }

        public void setBrightness(int prevBrightness, int nextBrightness){
            this.prevBrightness = prevBrightness;
            this.nextBrightness = nextBrightness;
        }

        @Override
        public String toSchema() {
            return this.val;
        }

        public static LightAction fromSchema(String schema) {
            for(LightAction lAction: LightAction.values()){
                if(lAction.val.equals(schema)){
                    return lAction;
                }
            }
            throw new RuntimeException("could not find new instance for schema value: " + schema);
        }
    }

    public Light(String deviceId, String requestId, LocalActionDB localActionDB) {
        super(deviceId, requestId, localActionDB);
    }

    //included to allow extending class to provide brightness details so the undo action can set to previous value
    protected void brightness(int prevLevel, int nextLevel){
        LightAction lightAction = LightAction.BRIGHTNESS;
        lightAction.setBrightness(prevLevel, nextLevel);
        lightAction.setObj(this);
        String actionId = Util.getUUID();
        this.actionIds.add(actionId);
        localActionDB.completePending(this.requestId(), this.deviceId(), actionId, lightAction);
    }

    public void brightness(int level) throws DeviceUnavailableException {
        LightAction lightAction = LightAction.BRIGHTNESS;
        lightAction.setBrightness(0, level);
        lightAction.setObj(this);
        String actionId = Util.getUUID();
        this.actionIds.add(actionId);
        localActionDB.completePending(this.requestId(), this.deviceId(), actionId, lightAction);
    }

    public void off() throws DeviceUnavailableException {
        LightAction lightAction = LightAction.OFF;
        lightAction.setObj(this);
        String actionId = Util.getUUID();
        this.actionIds.add(actionId);
        localActionDB.completePending(this.requestId(), this.deviceId(), actionId, lightAction);
    }

    public void on() throws DeviceUnavailableException {
        LightAction lightAction = LightAction.ON;
        lightAction.setObj(this);
        String actionId = Util.getUUID();
        this.actionIds.add(actionId);
        localActionDB.completePending(this.requestId(), this.deviceId(), actionId, lightAction);
    }
}
