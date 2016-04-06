package nathanielwendt.mpc.ut.edu.iotinfluence;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

import nathanielwendt.mpc.ut.edu.iotinfluence.device.DeviceUnavailableException;
import nathanielwendt.mpc.ut.edu.iotinfluence.device.Light;
import nathanielwendt.mpc.ut.edu.iotinfluence.devicereqs.DeviceReq;
import nathanielwendt.mpc.ut.edu.iotinfluence.devicereqs.SpatialReq;
import nathanielwendt.mpc.ut.edu.iotinfluence.devicereqs.TypeReq;

/**
 * Created by nathanielwendt on 3/28/16.
 */
public class TestActivity extends Activity {
    Warble snapshot;
    List<DeviceReq> reqs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        final Wink wink = new Wink("10db23b44fb025f1ad302d66693feb11",
//                "649bf40892d9caea9ee6f70a0026f434",
//                "mobilepervasivecomputing@gmail.com",
//                "mpcmpc4IoT");
//
//        wink.fetchDevices(new Service.FetchDevicesCallback() {
//            @Override
//            public void onFetch(List<DeviceModel> fetchedDevices) {
//                System.out.println(fetchedDevices);
//            }
//        });

        reqs = new ArrayList<DeviceReq>();
        reqs.add(new SpatialReq(SpatialReq.Bound.CLOSEST, SpatialReq.Influence.AWARE));
        //no effect for static binding since type token is passed into methods
        //only can effect static binding if type given here is different from type token (will return nothing)
        reqs.add(new TypeReq(new TypeReq.Type[]{TypeReq.Type.LIGHT}));

        snapshot = new Warble(this);

        //background initialization, polling at use to wait until initialized
        snapshot.initialize();
        while(!snapshot.initialized()){}
        act();

        //initialization with callback
//        snapshot.initialize(new InitializedCallback(){
//           @Override public void onInit(){
//               act();
//           }
//        });

//        MyTask task = new MyTask();
//        task.execute();
    }

    public void act(){
        //STATIC BIND
        //decoupled
        List<Light> devices = snapshot.retrieve(Light.class, reqs, 2);
        //Light light = snapshot.retrieve(Light.class, reqs);
        try {
            devices.get(1).off();
            devices.get(0).off();
            //light.on();
        } catch (DeviceUnavailableException e) {
            e.printStackTrace();
        }
    }

    private class MyTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            while(!snapshot.initialized()) {
            }

            //STATIC BIND
            //decoupled
            List<Light> devices = snapshot.retrieve(Light.class, reqs, 2);
            //Light light = snapshot.retrieve(Light.class, reqs);
            try {
                devices.get(0).off();
                devices.get(1).on();
                //light.on();
            } catch (DeviceUnavailableException e) {
                e.printStackTrace();
            }

            return null;
        }
    }
}
