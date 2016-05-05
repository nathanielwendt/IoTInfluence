package nathanielwendt.mpc.ut.edu.iotinfluence;

import android.app.Activity;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseSettings;
import android.os.AsyncTask;
import android.os.Bundle;

import java.util.List;

import nathanielwendt.mpc.ut.edu.iotinfluence.device.DeviceUnavailableException;
import nathanielwendt.mpc.ut.edu.iotinfluence.device.Light;
import nathanielwendt.mpc.ut.edu.iotinfluence.devicereqs.DeviceReq;
import nathanielwendt.mpc.ut.edu.iotinfluence.devicereqs.TypeReq;
import nathanielwendt.mpc.ut.edu.iotinfluence.models.DeviceModel;
import nathanielwendt.mpc.ut.edu.iotinfluence.service.BLEService;
import nathanielwendt.mpc.ut.edu.iotinfluence.service.Service;
import nathanielwendt.mpc.ut.edu.iotinfluence.service.ServiceManager;
import nathanielwendt.mpc.ut.edu.iotinfluence.service.Wink;

/**
 * Created by nathanielwendt on 3/28/16.
 */
public class TestActivity extends Activity {
    Warble snapshot;
    List<DeviceReq> reqs;

    Wink wink;

    public void evalBLEScan(){

        //begin profiling
        ServiceManager serviceManager = new ServiceManager(this);
        serviceManager.scan(new ServiceManager.FindServiceCallback() {
            @Override
            public void onService(Service service) {

            }

            @Override
            public void done() {
                //end profiling
            }
        });
    }

    public void evalCloudScan(){

        wink = new Wink("10db23b44fb025f1ad302d66693feb11",
                "649bf40892d9caea9ee6f70a0026f434",
                "mobilepervasivecomputing@gmail.com",
                "mpcmpc4IoT");

        wink.signIn(new Wink.SignInCallback() {
            @Override
            public void signedIn() {
                //begin profiling
                wink.fetchDevices(new Service.FetchDevicesCallback() {
                    @Override
                    public void onFetch(List<DeviceModel> fetchedDevices) {
                        //end profiling
                    }
                });
            }
        });
    }

    public void evalCloudAction(){
        wink = new Wink("10db23b44fb025f1ad302d66693feb11",
                "649bf40892d9caea9ee6f70a0026f434",
                "mobilepervasivecomputing@gmail.com",
                "mpcmpc4IoT");

        //begin profiling
        wink.signIn(new Wink.SignInCallback() {
            @Override
            public void signedIn() {
                //end profiling
            }
        });
    }

    public void evalBLEAction(){
        BLEService bleService = new BLEService("bledevice001", TypeReq.Type.LIGHT);
        BLEService.BLEHandler bleHandler = bleService.new BLEHandler();

        //start profiling
        bleHandler.advertise("light-on", 3, new AdvertiseCallback() {
            @Override
            public void onStartSuccess(AdvertiseSettings settingsInEffect) {

            }

            @Override
            public void onStartFailure(int errorCode) {
                //end profiling
            }
        });
    }


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


        //Personalization ------------


        //Standard

        //alternative standard
//        HttpRequest auth = new HttpRequest(Params ...);
//        auth.post(new HttpRequestCallback(){
//            @Override public void onResult(JSONObject result){
//                HttpRequest retrieve = new HttpRequest(Params ...);
//                String deviceName = findClosest(retrieve, getCurrLocation());
//                HttpRequest onReq = new HttpRequest(Params ..., deviceName, "device: on");
//            }
//        });
//        //BLE
//        //
//
//
//        //Enterprise
//        final List<Device> retrieve = fetchDevices();
//        Location userLoc = new Location(0,0);
//        Device closestDevice = retrieve.get(0);
//        double currMin = Double.MAX_VALUE;
//        for(Device device: retrieve){
//            if(Location.distance(device.location(), closestDevice.location()) < currMin){
//                closestDevice = device;
//            }
//        }
//        closestDevice.on();


        //Warble
//        reqs = new ArrayList<DeviceReq>();
//        reqs.add(new SpatialReq(SpatialReq.Bound.CLOSEST, SpatialReq.Influence.AWARE,
//                new Location(0,0)));
//        //reqs.add(new TypeReq(new TypeReq.Type[]{TypeReq.Type.LIGHT}));
//
//        snapshot = new Warble(this);
//        while(!snapshot.hasDiscovered()){}
//        Light light = snapshot.retrieve(Light.class, reqs);
//        try {
//            light.on();
//        } catch (DeviceUnavailableException e) {
//            e.printStackTrace();
//        }
//

        //Abstraction ---------------
        //1) Sign in to wink with RAw HTTP
        //2) Get a list of retrieve Raw HTTP
        //3) Go through list of retrieve, find closest
        //4) Turn device on


        //-----------------

//
//        reqs = new ArrayList<DeviceReq>();
//        reqs.add(new SpatialReq(SpatialReq.Bound.CLOSEST, SpatialReq.Influence.AWARE,
//                                new Location(0,0)));
//        //no effect for static binding since type token is passed into methods
//        //only can effect static binding if type given here is different from type token (will return nothing)
//        reqs.add(new TypeReq(new TypeReq.Type[]{TypeReq.Type.LIGHT}));
//
//        snapshot = new Warble(this);
//
//        //background initialization, polling at use to wait until hasDiscovered
//        snapshot.discover();
//        while(!snapshot.hasDiscovered()){}
//        act();

        //initialization with callback
//        snapshot.discover(new InitializedCallback(){
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
            while(!snapshot.hasDiscovered()) {
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
