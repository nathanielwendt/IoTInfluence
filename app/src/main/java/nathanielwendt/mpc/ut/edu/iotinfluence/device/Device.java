package nathanielwendt.mpc.ut.edu.iotinfluence.device;

import java.util.ArrayList;
import java.util.List;

import nathanielwendt.mpc.ut.edu.iotinfluence.db.LocalActionDB;

/**
 * Created by nathanielwendt on 3/9/16.
 */
public abstract class Device {
    //public static String identifierField = "identifier";
    private String requestId;
    private String deviceId;
    final List<String> actionIds;
    final LocalActionDB localActionDB;

    public Device(String deviceId, String requestId, LocalActionDB localActionDB){
        this.requestId = requestId;
        this.deviceId = deviceId;
        this.localActionDB = localActionDB;
        actionIds = new ArrayList<>();
    }

    public String deviceId(){
        return this.deviceId;
    }

    public String requestId(){
        return this.requestId;
    }

    public String getLastActionId(){
        return actionIds.get(actionIds.size() - 1);
    }
}
