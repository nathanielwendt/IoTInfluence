package nathanielwendt.mpc.ut.edu.iotinfluence.device;

import java.util.Observable;

import nathanielwendt.mpc.ut.edu.iotinfluence.misc.Location;

/**
 * Created by nathanielwendt on 3/10/16.
 */
public class Observables {
    public static class SpatialObservable extends Observable {
        private Location loc;

        public void update(Location loc){
            this.loc = loc;
            this.notifyObservers(loc);
        }
    }
}
