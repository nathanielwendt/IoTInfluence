package nathanielwendt.mpc.ut.edu.iotinfluence.devicereqs;

import java.util.Comparator;

import nathanielwendt.mpc.ut.edu.iotinfluence.misc.Location;
import static nathanielwendt.mpc.ut.edu.iotinfluence.devicereqs.SpatialReqOperator.*;

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

    public Location loc(){
        return requesterLoc;
    }

    public enum Bound implements Comparator<RelevancePoint> {
        CLOSEST {
            @Override public int compare(RelevancePoint p1, RelevancePoint p2){
                if(p2.relevance() > p1.relevance()){
                    return 1;
                } else if(p2.relevance() == p1.relevance()){
                    return 0;
                } else {
                    return - 1;
                }
            }
        },
        FARTHEST {
            @Override public int compare(RelevancePoint p1, RelevancePoint p2){
                if(p1.relevance() > p2.relevance()){
                    return 1;
                } else if(p1.relevance() == p2.relevance()){
                    return 0;
                } else {
                    return - 1;
                }
            }
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
