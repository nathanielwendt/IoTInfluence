package nathanielwendt.mpc.ut.edu.iotinfluence.devicereqs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import nathanielwendt.mpc.ut.edu.iotinfluence.db.Action;
import nathanielwendt.mpc.ut.edu.iotinfluence.db.LocalActionDB;
import nathanielwendt.mpc.ut.edu.iotinfluence.misc.Location;
import nathanielwendt.mpc.ut.edu.iotinfluence.models.DeviceModel;

/**
 * Created by nathanielwendt on 3/23/16.
 */
public class SpatialReqOperator extends AggregateReqOperator {
    public static double DistToHistoryRatio = 9.5;


    private final static double INFLUENCE = 15;
    private final static double REF_THRESH = 50;
    private final static double REF_WEIGHT = 1;  //2
    private final static double DEV_THRESH = 50;
    private final static double DEV_WEIGHT = .2; //.3

    private final SpatialReq req;

    public SpatialReqOperator(SpatialReq req){
        this.req = req;
    }

    @Override
    //Do we want to trim candidates that have no (0.0) knowledge?
    //e.g. Influence.Unaware with a device that doesn't have a known location?
    public List<DeviceModel> resolve(List<DeviceModel> candidates) {
        List<Action> actions;
        List<RelevancePoint> relPoints = new ArrayList<RelevancePoint>();
        double knowledge;
        for(DeviceModel candidate : candidates) {
            knowledge = 0.0;
            int posCount = 0;
            int negCount = 0;
            double posKnowledge = 0.0;
            double negKnowledge = 0.0;
            if (this.req.influence == SpatialReq.Influence.AWARE) {
                if (candidate.location() != null) {
                    if(this.req.requesterLoc == null){ throw new RuntimeException("Requester Loc is Null and is required"); }
                    actions = LocalActionDB.query(this.req.requesterLoc,
                                                    candidate.location(),
                                                    INFLUENCE);
                } else {
                    actions = LocalActionDB.query(this.req.requesterLoc,
                                                    INFLUENCE);
                }

                for (Action action : actions) {
                    if (action.devLocation != null) {
                        double refDist = Location.distance(action.refLocation, this.req.requesterLoc, .25);
                        double devDist = Location.distance(action.devLocation, candidate.location(), .25);

                        if(action.successful){
                            double refValue = REF_THRESH - refDist; //0 - 50
                            double devValue = DEV_THRESH - devDist; //0 - 50
                            double adjRefValue = refValue * REF_WEIGHT; //0 - 50
                            double adjDevValue = refValue * DEV_WEIGHT; //0 - 10
                            posKnowledge += ((REF_WEIGHT * (REF_THRESH - refDist)) *
                                    (DEV_WEIGHT * (DEV_THRESH - devDist)));  //0 - 500
                            posCount++;
                        } else {
                            negKnowledge += ((REF_WEIGHT * (REF_THRESH - refDist)) *
                                    (DEV_WEIGHT * (DEV_THRESH - devDist)));
                            negCount++;
                        }

                    } else {
                        //TODO- not adapted for new algorithm!!!
                        throw new RuntimeException("Not implemented yet");
//                        double refDist = Location.distance(action.refLocation, this.req.requesterLoc);
//                        double sign = (action.successful) ? 1 : -1;
//                        knowledge += sign * ((REF_WEIGHT * (REF_THRESH - refDist)));
                    }
                }

                //knowledge = posKnowledge - negKnowledge;
                if (posCount != 0) {
                    knowledge += posKnowledge / (posCount);
                }

                if (negCount != 0) {
                    knowledge -= negKnowledge / (negCount) ;
                }
            }

            if (candidate.location() != null) {
                double dist = Location.distance(this.req.requesterLoc, candidate.location());
                relPoints.add(new RelevancePoint(candidate, knowledge, dist));
            } else {
                relPoints.add(new RelevancePoint(candidate, knowledge));
            }
        }
        Collections.sort(relPoints, this.req.bound);

        List<DeviceModel> res = new ArrayList<DeviceModel>();
        for(RelevancePoint point : relPoints){
            res.add(point.getDeviceModel());
        }

        return res;
    }

    public static class RelevancePoint {
        private static final double RELEVANCE_THRESH = 20.0;
        private final double knowledge;
        private final double refDist;
        private final DeviceModel dev;

        private final double relevance;

        public RelevancePoint(DeviceModel dev, double knowledge){
            this.knowledge = knowledge;
            this.dev = dev;
            this.refDist = -1;
            this.relevance = this.knowledge;
        }

        public RelevancePoint(DeviceModel dev, double knowledge, double refDist){
            this.knowledge = knowledge;
            this.refDist = refDist;
            this.dev = dev;
            //System.out.println(this.knowledge + " , " + (RELEVANCE_THRESH - this.refDist));
            this.relevance = this.knowledge + (RELEVANCE_THRESH - this.refDist) * DistToHistoryRatio;
        }

        public DeviceModel getDeviceModel(){
            return dev;
        }

        public double relevance(){
            return relevance;
        }
    }
}
