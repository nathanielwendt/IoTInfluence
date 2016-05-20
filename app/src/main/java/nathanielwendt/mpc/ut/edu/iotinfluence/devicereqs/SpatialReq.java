package nathanielwendt.mpc.ut.edu.iotinfluence.devicereqs;

import java.util.Comparator;

import nathanielwendt.mpc.ut.edu.iotinfluence.misc.Location;

import static nathanielwendt.mpc.ut.edu.iotinfluence.devicereqs.SpatialReqOperator.RelevancePoint;

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

    public enum Bound implements Comparator<RelevancePoint>  {
        CLOSEST("closest") {
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
        FARTHEST("farthest") {
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

        private String identifier;
        Bound(String identifier){
            this.identifier = identifier;
        }

        boolean equals(String identifier){
            return this.identifier.equals(identifier);
        }

        static Bound newInstance(String value){
            for(Bound bound : Bound.values()){
                if(bound.equals(value)){
                    return bound;
                }
            }
            throw new RuntimeException("Could not detect type from new instance value");
        }
    }

    public enum Influence {
        AWARE("aware") {
        },
        UNAWARE("unaware") {
        };

        private String identifier;
        Influence(String identifier){
            this.identifier = identifier;
        }

        boolean equals(String identifier){
            return this.identifier.equals(identifier);
        }

        static Influence newInstance(String value){
            for(Influence infl : Influence.values()){
                if(infl.equals(value)){
                    return infl;
                }
            }
            throw new RuntimeException("Could not detect type from new instance value");
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

    @Override
    //Bound:Influence
    public String toSchema() {
        String val = "";
        val += this.bound.identifier;
        val += ":" + this.influence.identifier;
        val += ":" + this.requesterLoc.toSchema();
        return val;
    }

    public static SpatialReq fromSchema(String schema){
        String[] portions = schema.split(":");

        Bound bound = Bound.newInstance(portions[0]);
        Influence influence = Influence.newInstance(portions[1]);
        Location location = Location.fromSchema(portions[2]);
        return new SpatialReq(bound, influence, location);
    }
}
