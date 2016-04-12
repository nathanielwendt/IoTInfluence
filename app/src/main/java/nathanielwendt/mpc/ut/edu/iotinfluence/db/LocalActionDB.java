package nathanielwendt.mpc.ut.edu.iotinfluence.db;

import java.util.ArrayList;
import java.util.List;

import nathanielwendt.mpc.ut.edu.iotinfluence.misc.Location;

/**
 * Created by nathanielwendt on 4/5/16.
 */
public class LocalActionDB {
    private static List<Action> actions = new ArrayList<>();

    public static void insert(Action action){
        actions.add(action);
    }

    public static List<Action> query(Location r, Location d, double range){
        List<Action> res = new ArrayList<>();
        for(Action action : actions){
            double distToDev = Location.distance(action.devLocation, d);
            double distToRef = Location.distance(action.refLocation, r);
            if(distToDev <= range && distToRef <= range){
                res.add(action);
            }
        }
        return res;
    }

    public static List<Action> query(Location r, double range){

        return new ArrayList<Action>();
    }
}

