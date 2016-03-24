package nathanielwendt.mpc.ut.edu.iotinfluence.devices;

import android.location.Location;

import java.util.Observable;

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
