package nathanielwendt.mpc.ut.edu.iotinfluence.db;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nathanielwendt.mpc.ut.edu.iotinfluence.misc.Location;

/**
 * Created by nathanielwendt on 4/5/16.
 */
public class LocalActionDB {
    //key is index of (RequestId + DeviceId)
    private static Map<String, Action> actions = new HashMap<>();

    //key is index of (RequestId)
    private static Map<String, PendingAction> pending = new HashMap<>();

    public static void insert(String requestId, String deviceId, Action action){
        actions.put(requestId + deviceId, action);
    }

    public static Action getAction(String requestId, String deviceId){
        return actions.get(requestId + deviceId);
    }

    public static int size(){
        return actions.size();
    }

    public static void insertPending(String requestId, Location refLoc){
        PendingAction pendingAction = new PendingAction(requestId, refLoc);
        pending.put(requestId, pendingAction);
    }

    public static void populatePending(String requestId, String deviceId, Location deviceLoc){
        PendingAction pendingAction = pending.get(requestId);
        if(pendingAction == null){
            throw new RuntimeException("cannot populate empty pending request");
        }
        pendingAction.addDevice(deviceId, deviceLoc);
    }

    //always marked as successful, a help! command will overwrite the entry to be unsuccessful
    public static void completePending(String requestId, String deviceId, ActionType actionType){
        PendingAction pendingAction = pending.get(requestId);
        if(pendingAction == null){
            //TODO: log that client is trying to populate empty pending request
            //This is no longer an exception because undo actions will call completePending on nonexistent pending records
            return;
        }
        Action action = Action.newDefault(deviceId, pendingAction.getRefLoc(),
                        pendingAction.getDevLoc(deviceId), true);
        action.type = actionType;
        LocalActionDB.insert(requestId, deviceId, action);
        pending.remove(requestId);
    }

    public static void update(String requestId, String deviceId, boolean successful){
        Action action = actions.get(requestId + deviceId);
        action.successful = successful;
    }

    public static List<Action> query(Location r, Location d, double range){
        List<Action> res = new ArrayList<>();
        for(Action action : actions.values()){
            double distToDev = Location.distance(action.devLocation, d);
            double distToRef = Location.distance(action.refLocation, r);
            if(distToDev <= range && distToRef <= range){
                res.add(action);
            }
        }
        return res;
    }

    public static void clear(){
        actions.clear();
        pending.clear();
    }

    public static List<Action> query(Location r, double range){
        List<Action> res = new ArrayList<>();
        for(Action action : actions.values()){
            double distToRef = Location.distance(action.refLocation, r);
            if(distToRef <= range){
                res.add(action);
            }
        }
        return res;
    }

    private static class PendingAction {
        private Map<String, Location> devLocs = new HashMap<>();
        private String requestId;
        private Location refLoc;

        public PendingAction(String requestId, Location refLoc){
            this.requestId = requestId;
            this.refLoc = refLoc;
        }

        public void addDevice(String deviceId, Location devLoc){
            devLocs.put(deviceId, devLoc);
        }

        public Location getRefLoc(){
            return refLoc;
        }

        public Location getDevLoc(String deviceId){
            return devLocs.get(deviceId);
        }

    }
}

