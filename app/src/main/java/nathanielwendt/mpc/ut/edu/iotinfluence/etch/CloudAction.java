package nathanielwendt.mpc.ut.edu.iotinfluence.etch;

import android.content.Context;

import com.ut.mpc.etch.Eval;
import com.ut.mpc.etch.MultiProfiler;
import com.ut.mpc.etch.Stabilizer;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import nathanielwendt.mpc.ut.edu.iotinfluence.models.DeviceModel;
import nathanielwendt.mpc.ut.edu.iotinfluence.service.Service;
import nathanielwendt.mpc.ut.edu.iotinfluence.service.Wink;

/**
 * Created by nathanielwendt on 5/4/16.
 */
public class CloudAction implements Eval {
    int iterations;
    int i;
    boolean stop;
    Object lock = new Object();
    boolean done = false;
    long initTime;

    @Override
    public void execute(final Context ctx, JSONObject options) throws JSONException {
        final Stabilizer stabFunc = null;

        final String dbName = options.getString("dbName");
        iterations = Integer.valueOf(options.getString("iterations"));

        MultiProfiler.init(this, ctx);


        i = 0;
        final Wink wink = new Wink("10db23b44fb025f1ad302d66693feb11",
                "649bf40892d9caea9ee6f70a0026f434",
                "mobilepervasivecomputing@gmail.com",
                "mpcmpc4IoT");


//        wink.signIn(new Wink.SignInCallback() {
//            @Override
//            public void signedIn() {
//                MultiProfiler.startProfiling(dbName);
//                MultiProfiler.startMark(stabFunc, null, "First");
//                initTime = System.nanoTime();
//                request(ctx, wink);
//            }
//        });

        MultiProfiler.startProfiling(dbName);
        MultiProfiler.startMark(stabFunc, null, "First");
        initTime = System.nanoTime();
        request(ctx, wink);
    }

    public void request(final Context ctx, final Wink wink){
        wink.sendReq(new Service.FetchDevicesCallback() {
            @Override
            public void onFetch(List<DeviceModel> fetchedDevices) {
                if (i == iterations - 1) {
                    System.out.println((System.nanoTime() - initTime) / 1000000);
                    MultiProfiler.endMark("First");
                    MultiProfiler.stopProfiling();
                    System.out.println("all requests complete");
                } else {
                    request(ctx, wink);
                }
                i++;
            }
        });
    }
}
