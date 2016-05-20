package nathanielwendt.mpc.ut.edu.iotinfluence.etch;

import android.content.Context;

import com.ut.mpc.etch.Eval;
import com.ut.mpc.etch.MultiProfiler;
import com.ut.mpc.etch.Stabilizer;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by nathanielwendt on 5/5/16.
 */
public class Wait implements Eval {

    @Override
    public void execute(Context ctx, JSONObject options) throws JSONException {
        Stabilizer stabFunc = new Stabilizer(){
            Object obj = new Object();

            @Override
            public void task(Object data) {

            }
        };

        String dbName = options.getString("dbName");
        int iterations = Integer.valueOf(options.getString("iterations"));

        MultiProfiler.init(this, ctx);
        MultiProfiler.startProfiling(dbName);
        MultiProfiler.startMark(stabFunc, null, "First");

        try {
            Thread.sleep(iterations);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        MultiProfiler.endMark("First");
        MultiProfiler.stopProfiling();
    }
}
