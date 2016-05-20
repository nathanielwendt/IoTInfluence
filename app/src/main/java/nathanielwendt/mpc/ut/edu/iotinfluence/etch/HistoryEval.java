package nathanielwendt.mpc.ut.edu.iotinfluence.etch;

import android.content.Context;

import com.ut.mpc.etch.Eval;
import com.ut.mpc.etch.MultiProfiler;
import com.ut.mpc.etch.Stabilizer;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import nathanielwendt.mpc.ut.edu.iotinfluence.db.Action;
import nathanielwendt.mpc.ut.edu.iotinfluence.db.InteractionHistory;
import nathanielwendt.mpc.ut.edu.iotinfluence.device.Light;
import nathanielwendt.mpc.ut.edu.iotinfluence.devicereqs.SpatialReq;
import nathanielwendt.mpc.ut.edu.iotinfluence.devicereqs.TypeReq;
import nathanielwendt.mpc.ut.edu.iotinfluence.misc.Location;

/**
 * Created by nathanielwendt on 5/14/16.
 */
public class HistoryEval implements Eval {

    @Override
    public void execute(Context ctx, JSONObject options) throws JSONException {
        Stabilizer stabFunc = null;

        InteractionHistory history = new InteractionHistory(ctx);
        String dbName = options.getString("dbName");
        int iterations = Integer.valueOf(options.getString("iterations"));
        int numQuery = Integer.valueOf(options.getString("numQuery"));
        int queryIterations = Integer.valueOf(options.getString("queryIterations"));

        history.clear();
        MultiProfiler.init(this, ctx);
        MultiProfiler.startProfiling(dbName);
        MultiProfiler.startMark(stabFunc, null, "Insert");

        Action action = Action.newDefault("dev001", new Location(22,23), new Location(24,25), true);
        action.reqs = new ArrayList<>();
        action.reqs.add(new TypeReq(new TypeReq.Type[]{TypeReq.Type.LOCK}));
        action.reqs.add(new SpatialReq(SpatialReq.Bound.CLOSEST, SpatialReq.Influence.UNAWARE, new Location(0,1)));
        action.type = Light.LightAction.ON;

        long initTime = System.nanoTime();
        for(int i = 0; i < iterations; i++){
            history.insert(action);
        }
        System.out.println("insert ms: " + (System.nanoTime() - initTime) / 1000000);
        MultiProfiler.endMark("Insert");

//        Action actionq = Action.newDefault("dev001", new Location(3,6), new Location(2,2), true);
//        actionq.reqs = new ArrayList<>();
//        actionq.reqs.add(new TypeReq(new TypeReq.Type[]{TypeReq.Type.LOCK}));
//        actionq.reqs.add(new SpatialReq(SpatialReq.Bound.CLOSEST, SpatialReq.Influence.UNAWARE, new Location(0, 1)));
//        actionq.type = Light.LightAction.ON;
//
//        for(int i = 0; i < numQuery; i++){
//            history.insert(actionq);
//        }
//
//        MultiProfiler.startMark(stabFunc, null, "Query");
//        initTime = System.nanoTime();
//        for(int i = 0; i < queryIterations; i++){
//            List<Action> actions = history.query(new Location(0,0), 10);
//        }
//        System.out.println("query ms: " + (System.nanoTime() - initTime) / 1000000);
//        MultiProfiler.endMark("Query");
        MultiProfiler.stopProfiling();
    }
}
