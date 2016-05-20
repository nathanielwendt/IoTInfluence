package nathanielwendt.mpc.ut.edu.iotinfluence;

import android.app.Activity;
import android.os.Bundle;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import nathanielwendt.mpc.ut.edu.iotinfluence.misc.Location;
import nathanielwendt.mpc.ut.edu.iotinfluence.models.DeviceModel;

public class NativeActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Location currLoc = new Location(0,0);
        final AsyncHttpClient client = new AsyncHttpClient();
        client.get("https://api.wink.com", null, new JsonHttpResponseHandler() {
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
                            String locStr = (String) deviceData.get("lat_lng");
                            double x = Double.valueOf(locStr.split(",")[0]);
                            double y = Double.valueOf(locStr.split(",")[1]);
                            Location location = new Location(x, y);
                            double distance = Location.distance(location, currLoc);
                            if (distance < minDistance) {
                                minDistance = distance;
                                minId = id;
                            }
                        }
                    }

                    client.get("https://api.wink.com/devices/" + minId, null, new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            //light has hopfully been turned on
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                            super.onFailure(statusCode, headers, throwable, errorResponse);
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
