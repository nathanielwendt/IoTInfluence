package nathanielwendt.mpc.ut.edu.iotinfluence.devicereqs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import nathanielwendt.mpc.ut.edu.iotinfluence.db.Action;
import nathanielwendt.mpc.ut.edu.iotinfluence.db.LocalActionDB;
import nathanielwendt.mpc.ut.edu.iotinfluence.misc.Location;
import nathanielwendt.mpc.ut.edu.iotinfluence.models.DeviceModel;

/**
 * Created by nathanielwendt on 3/23/16.
 */
public class SpatialReqOperator extends AggregateReqOperator {
    private final static double REF_THRESH = 20;
    private final static double REF_WEIGHT = 1;
    private final static double DEV_THRESH = 20;
    private final static double DEV_WEIGHT = 1;

    private final SpatialReq req;

    public SpatialReqOperator(SpatialReq req){
        this.req = req;
    }

    @Override
    public List<DeviceModel> resolve(List<DeviceModel> candidates) {
        List<Action> actions;
        List<RelevancePoint> relPoints = new ArrayList<RelevancePoint>();
        double knowledge;
        for(DeviceModel candidate : candidates){
            knowledge = 0.0;
            if(candidate.location() != null){
                actions = LocalActionDB.query(this.req.requesterLoc, candidate.location());
                knowledge = 0.0;
                for(Action action: actions){
                    double refDist = Location.distance(action.refLocation(), this.req.requesterLoc);
                    double devDist = Location.distance(action.devLocation(), candidate.location());
                    double sign = (action.isSuccessful()) ? 1 : -1;
                    knowledge += sign * (   (REF_WEIGHT * (REF_THRESH - refDist)) +
                                            (DEV_WEIGHT * (DEV_THRESH - devDist))
                                        );
                }
                relPoints.add(new RelevancePoint(knowledge, Location.distance(this.req.requesterLoc, candidate.location())));
            } else {
                actions = LocalActionDB.query(this.req.requesterLoc);
                knowledge = 0.0;
                for(Action action: actions){
                    //FIXX*****
                    double refDist = Location.distance(action.refLocation(), this.req.requesterLoc);
                    double sign = (action.isSuccessful()) ? 1 : -1;
                    knowledge += sign * (   (REF_WEIGHT * (REF_THRESH - refDist)) );
                }
                relPoints.add(new RelevancePoint(knowledge));
            }
        }

        Collections.sort(relPoints);

        return candidates;
    }

    private static class RelevancePoint implements Comparable<RelevancePoint> {
        private static final double RELEVANCE_THRESH = 100.0;
        private static final double NO_REF_DIST = -1.0;
        private final double knowledge;
        private final double refDist;

        public RelevancePoint(double knowledge){
            this.knowledge = knowledge;
            this.refDist = NO_REF_DIST;
        }

        public RelevancePoint(double knowledge, double refDist){
            this.knowledge = knowledge;
            this.refDist = refDist;
        }

        @Override
        public int compareTo(RelevancePoint other) {
            if(this.refDist == NO_REF_DIST){
                return (int) (this.knowledge - other.knowledge);
            } else {
                double thisRel = this.knowledge * (RELEVANCE_THRESH - this.refDist);
                double otherRel = other.knowledge * (RELEVANCE_THRESH - other.refDist);
                return (int) (thisRel - otherRel);
            }
        }
    }
}
