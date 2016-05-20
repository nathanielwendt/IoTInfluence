package nathanielwendt.mpc.ut.edu.iotinfluence;

import android.app.Activity;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

import nathanielwendt.mpc.ut.edu.iotinfluence.device.DeviceUnavailableException;
import nathanielwendt.mpc.ut.edu.iotinfluence.device.Light;
import nathanielwendt.mpc.ut.edu.iotinfluence.devicereqs.DeviceReq;
import nathanielwendt.mpc.ut.edu.iotinfluence.devicereqs.SpatialReq;
import nathanielwendt.mpc.ut.edu.iotinfluence.devicereqs.TypeReq;
import nathanielwendt.mpc.ut.edu.iotinfluence.misc.Location;
import nathanielwendt.mpc.ut.edu.iotinfluence.service.DiscoverCallback;

public class WarbleActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final List<DeviceReq> reqs = new ArrayList<DeviceReq>();
        reqs.add(new SpatialReq(SpatialReq.Bound.CLOSEST, SpatialReq.Influence.AWARE, new Location(0,0)));
        reqs.add(new TypeReq(new TypeReq.Type[]{TypeReq.Type.LIGHT}));
        final Warble snapshot = new Warble(this, Warble.Discovery.ONDEMAND);
        snapshot.discover(new DiscoverCallback() {
            @Override
            public void onDiscover() {
                Light light = snapshot.retrieve(Light.class, reqs);
                try {
                    light.on();
                } catch (DeviceUnavailableException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
