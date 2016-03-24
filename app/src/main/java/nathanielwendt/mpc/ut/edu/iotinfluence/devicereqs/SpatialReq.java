package nathanielwendt.mpc.ut.edu.iotinfluence.devicereqs;

/**
 * Created by nathanielwendt on 3/23/16.
 */
public class SpatialReq extends DeviceReq {
    private static final DeviceReq def = new SpatialReq(Bound.CLOSEST, Influence.AWARE);
    final Bound bound;
    final Influence influence;

    public SpatialReq(Bound bound, Influence influence){
        this.bound = bound;
        this.influence = influence;
    }

    public enum Bound {
        CLOSEST {

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

    public static DeviceReq def(){
        return def;
    }

    ReqOperator operator(){
        return new SpatialReqOperator(this);
    }
}
