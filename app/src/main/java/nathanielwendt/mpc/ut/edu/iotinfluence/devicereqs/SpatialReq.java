package nathanielwendt.mpc.ut.edu.iotinfluence.devicereqs;

import java.util.Comparator;

import nathanielwendt.mpc.ut.edu.iotinfluence.misc.Location;
import nathanielwendt.mpc.ut.edu.iotinfluence.models.DeviceModel;

/**
 * Created by nathanielwendt on 3/23/16.
 */
public class SpatialReq extends DeviceReq {
    private static DeviceReq def;
    final Bound bound;
    final Influence influence;
    final Location requesterLoc;

    public SpatialReq(Bound bound, Influence influence, Location loc){
        this.bound = bound;
        this.influence = influence;
        this.requesterLoc = loc;
    }

    public enum Bound implements Comparator<DeviceModel> {
        CLOSEST {
            @Override public int compare(DeviceModel dev1, DeviceModel dev2){
                double d1;
                return 0;
            }
        },
        FARTHEST {

        };
    }

    public enum Influence {
        AWARE {
        },
        UNAWARE {
        }
    }

    public static DeviceReq def(Location loc){
        if(def == null){
            def = new SpatialReq(Bound.CLOSEST, Influence.AWARE, loc);
        }
        return def;
    }

    ReqOperator operator(){
        return new SpatialReqOperator(this);
    }
}
