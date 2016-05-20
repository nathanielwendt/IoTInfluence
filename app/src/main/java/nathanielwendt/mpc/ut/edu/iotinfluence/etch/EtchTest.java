package nathanielwendt.mpc.ut.edu.iotinfluence.etch;

import android.content.Context;

import com.ut.mpc.etch.Eval;
import com.ut.mpc.etch.MultiProfiler;
import com.ut.mpc.etch.Stabilizer;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by nathanielwendt on 5/4/16.
 */
public class EtchTest implements Eval {


    @Override
    public void execute(Context ctx, JSONObject options) throws JSONException {
        Stabilizer stabFunc = null;

        String dbName = options.getString("dbName");
        int iterations = Integer.valueOf(options.getString("iterations"));

        MultiProfiler.init(this, ctx);
        MultiProfiler.startProfiling(dbName);
        MultiProfiler.startMark(stabFunc, null, "First");


        MultiProfiler.endMark("First");



        MultiProfiler.stopProfiling();
    }
}
