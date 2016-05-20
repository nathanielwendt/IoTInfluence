package nathanielwendt.mpc.ut.edu.iotinfluence.etch;

import android.content.Context;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.ut.mpc.etch.Eval;
import com.ut.mpc.etch.MultiProfiler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import nathanielwendt.mpc.ut.edu.iotinfluence.Warble;
import nathanielwendt.mpc.ut.edu.iotinfluence.db.InteractionHistory;
import nathanielwendt.mpc.ut.edu.iotinfluence.device.DeviceUnavailableException;
import nathanielwendt.mpc.ut.edu.iotinfluence.device.Light;
import nathanielwendt.mpc.ut.edu.iotinfluence.devicereqs.DeviceReq;
import nathanielwendt.mpc.ut.edu.iotinfluence.devicereqs.SpatialReq;
import nathanielwendt.mpc.ut.edu.iotinfluence.devicereqs.TypeReq;
import nathanielwendt.mpc.ut.edu.iotinfluence.misc.Location;
import nathanielwendt.mpc.ut.edu.iotinfluence.models.DeviceModel;
import nathanielwendt.mpc.ut.edu.iotinfluence.service.DiscoverCallback;

/**
 * Created by nathanielwendt on 5/5/16.
 */
public class NativeEval implements Eval {
    int count = 0;
    int iterations;
    Warble snapshot;
    List<DeviceReq> reqs;
    boolean warbleFetchUpdate;

    @Override
    public void execute(final Context ctx, JSONObject options) throws JSONException {
        final String dbName = options.getString("dbName");
        iterations = Integer.valueOf(options.getString("iterations"));
        warbleFetchUpdate = Boolean.valueOf(options.getString("fetchupdate"));


        InteractionHistory history = new InteractionHistory(ctx);
        history.clear();

        MultiProfiler.init(this, ctx);

        reqs = new ArrayList<DeviceReq>();
        reqs.add(new SpatialReq(SpatialReq.Bound.CLOSEST, SpatialReq.Influence.UNAWARE, new Location(0,0)));
        reqs.add(new TypeReq(new TypeReq.Type[]{TypeReq.Type.LIGHT}));
        snapshot = new Warble(ctx, Warble.Discovery.ONDEMAND);
        snapshot.discover(new DiscoverCallback() {
            @Override
            public void onDiscover() {
                MultiProfiler.startProfiling(dbName);
                MultiProfiler.startMark(null, null, "Warble");
                warbleExecute(ctx);
            }
        });
    }

    public void warbleExecute(final Context ctx){
        Light light = snapshot.retrieve(Light.class, reqs);
        System.out.println("lights retrieved");
        try {
            light.on();
            System.out.println("light on");
            if (count >= iterations - 1) {
                MultiProfiler.endMark("Warble");
                count = 0;
                System.out.println("starting native");
                MultiProfiler.startMark(null, null, "Native");
                nativeExecute();
            } else {
                count++;
                warbleExecute(ctx);
            }
        } catch (DeviceUnavailableException e) {
            e.printStackTrace();
        }
    }

    public void nativeExecute(){
        final Location currLoc = new Location(0,0);
        final AsyncHttpClient client = new AsyncHttpClient();
        client.get("http://pacobackend.appspot.com/?n=2", null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                List<DeviceModel> devices = new ArrayList<DeviceModel>();
                try {
                    JSONArray data = (JSONArray) response.get("data");
                    double minDistance = Double.MAX_VALUE;
                    String minId = "";
                    for (int i = 0; i < data.length(); i++) {
                        JSONObject deviceData = data.getJSONObject(i);
                        if (deviceData.has("light_bulb_id")) {
                            String id = (String) deviceData.get("light_bulb_id");
//                            String locStr = (String) deviceData.get("lat_lng");
//                            double x = Double.valueOf(locStr.split(",")[0]);
//                            double y = Double.valueOf(locStr.split(",")[1]);
                            Location location = new Location(2, 3);
                            double distance = Location.distance(location, currLoc);
                            if (distance < minDistance) {
                                minDistance = distance;
                                minId = id;
                            }
                        }
                    }

                    client.get("http://pacobackend.appspot.com/?n=1", null, new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            //light has hopfully been turned on
                            System.out.println("light on");
                            if(count >= iterations - 1){
                                MultiProfiler.endMark("Native");
                                MultiProfiler.stopProfiling();
                            } else {
                                count++;
                                nativeExecute();
                            }
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                            System.out.println(responseString);
                            super.onFailure(statusCode, headers, responseString, throwable);
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                System.out.println(responseString);
                super.onFailure(statusCode, headers, responseString, throwable);
            }
        });
    }

}
