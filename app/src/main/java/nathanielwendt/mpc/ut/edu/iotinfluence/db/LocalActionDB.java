package nathanielwendt.mpc.ut.edu.iotinfluence.db;

import android.content.Context;

import java.util.HashMap;
import java.util.Map;

import nathanielwendt.mpc.ut.edu.iotinfluence.misc.Location;
import nathanielwendt.mpc.ut.edu.iotinfluence.service.ProxyService;

/**
 * Created by nathanielwendt on 4/5/16.
 */
public class LocalActionDB {
    private InteractionHistory history;

    public LocalActionDB(Context ctx){
        if(ctx != null){
            history = new InteractionHistory(ctx);
        }
    }

    //key is actionId
    private Map<String, Action> actions = new HashMap<>();

    //key is index of (RequestId)
    private Map<String, PendingAction> pending = new HashMap<>();

    public void insert(String actionId, Action action){
        actions.put(actionId, action);
    }

    public Action getAction(String actionId){
        return actions.get(actionId);
    }

    public void insertPending(String requestId, Location refLoc){
        PendingAction pendingAction = new PendingAction(requestId, refLoc);
        pending.put(requestId, pendingAction);
    }

    public void populatePending(String requestId, String deviceId, Location deviceLoc){
        PendingAction pendingAction = pending.get(requestId);
        if(pendingAction == null){
            throw new RuntimeException("cannot populate empty pending request");
        }
        pendingAction.addDevice(deviceId, deviceLoc);
    }

    //always marked as successful, a help! plan will overwrite the entry to be unsuccessful
    public void completePending(String requestId, String deviceId, String actionId, ActionType actionType){
        PendingAction pendingAction = pending.get(requestId);
        if(pendingAction == null){
            //TODO: log that client is trying to populate empty pending request
            //This is no longer an exception because undo actions will call completePending on nonexistent pending records
            return;
        }

        if(ProxyService.isProxyId(deviceId)){
            //TODO:
            //lookup actual device id from initiated proxy table
        }

        Action action = Action.newDefault(deviceId, pendingAction.getRefLoc(),
                        pendingAction.getDevLoc(deviceId), true);
        action.type = actionType;
        action.id = actionId;
        this.insert(actionId, action);
        //no longer remove because pending is just a template from which many actions may be copied
        //pending.remove(requestId);
    }

    public void update(String actionId, boolean successful){
        Action action = actions.get(actionId);
        action.successful = successful;
    }

    public void clear(){
        actions.clear();
        pending.clear();
    }

    public void flushToHistory(){
        for(Action action : actions.values()){
            history.insert(action);
        }
    }

//    public int size(){
//        return actions.size();
//    }

//    public List<Action> query(Location r, Location d, double range){
//        List<Action> res = new ArrayList<>();
//        for(Action action : actions.values()){
//            double distToDev = Location.distance(action.devLocation, d);
//            double distToRef = Location.distance(action.refLocation, r);
//            if(distToDev <= range && distToRef <= range){
//                res.add(action);
//            }
//        }
//        return res;
//    }
//
//
//    public List<Action> query(Location r, double range){
//        List<Action> res = new ArrayList<>();
//        for(Action action : actions.values()){
//            double distToRef = Location.distance(action.refLocation, r);
//            if(distToRef <= range){
//                res.add(action);
//            }
//        }
//        return res;
//    }

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

