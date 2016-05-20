package nathanielwendt.mpc.ut.edu.iotinfluence;

import android.Manifest;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import nathanielwendt.mpc.ut.edu.iotinfluence.device.DeviceUnavailableException;
import nathanielwendt.mpc.ut.edu.iotinfluence.device.Light;
import nathanielwendt.mpc.ut.edu.iotinfluence.devicereqs.DeviceReq;
import nathanielwendt.mpc.ut.edu.iotinfluence.devicereqs.SpatialReq;
import nathanielwendt.mpc.ut.edu.iotinfluence.devicereqs.TypeReq;
import nathanielwendt.mpc.ut.edu.iotinfluence.misc.Location;
import nathanielwendt.mpc.ut.edu.iotinfluence.service.DiscoverCallback;
import nathanielwendt.mpc.ut.edu.iotinfluence.service.Service;
import nathanielwendt.mpc.ut.edu.iotinfluence.service.ServiceManager;

public class BLEActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ble);

        this.requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 112);

        final List<DeviceReq> reqs = new ArrayList<DeviceReq>();
        Location currLoc = new Location(0,0);
        reqs.add(new SpatialReq(SpatialReq.Bound.CLOSEST, SpatialReq.Influence.AWARE, currLoc));
        reqs.add(new TypeReq(new TypeReq.Type[]{TypeReq.Type.LIGHT}));

        final Warble snapshot = new Warble(this, Warble.Discovery.ONDEMAND);

        snapshot.discover(new DiscoverCallback() {
            @Override
            public void onDiscover() {
                try {
                    Light light = snapshot.retrieve(Light.class, reqs);
                    light.off();
                } catch (DeviceUnavailableException e) {
                    e.printStackTrace();
                }
            }
        });




//        final List<DeviceReq> reqs = new ArrayList<>();
//        reqs.add(new SpatialReq(SpatialReq.Bound.CLOSEST, SpatialReq.Influence.AWARE,
//                new Location(0,0)));
//        //reqs.add(new TypeReq(new TypeReq.Type[]{TypeReq.Type.LIGHT}));
//
//        final Warble snapshot = new Warble(this, Warble.Discovery.ONDEMAND);
//        snapshot.discover();
////        snapshot.discover(new InitializedCallback() {
////            @Override
////            public void onInit() {
////                Light light = snapshot.retrieve(Light.class, reqs);
////                try {
////                    if (light != null) {
////                        System.out.println("turning light on!!");
////                        light.on();
////                    }
////                } catch (DeviceUnavailableException e) {
////                    e.printStackTrace();
////                }
////            }
////        });
//
//
//        snapshot.whenDiscovered(new DiscoverCallback() {
//            @Override
//            public void onDiscover() {
//                Light light = snapshot.retrieve(Light.class, reqs);
//                try {
//                    if (light != null) {
//                        System.out.println("turning light on!!");
//                        light.on();
//                    }
//                } catch (DeviceUnavailableException e) {
//                    e.printStackTrace();
//                }
//            }
//        });
//        System.out.println("here here");

    }

//    public void setCommand(String plan){
//        ParcelUuid pUuid = new ParcelUuid( UUID.fromString(BLE_UUID) );
//        data = new AdvertiseData.Builder()
//                .addServiceUuid(pUuid)
//                .addServiceData(pUuid, plan.getBytes(Charset.forName("UTF-8")))
//                .build();
//        advertiser.startAdvertising(settings, data, advertisingCallback);
//    }

    public void onClickScan(View v){
        ServiceManager manager = new ServiceManager(this);
        manager.scan(new ServiceManager.FindServiceCallback() {
            @Override
            public void onService(Service service) {
                System.out.println("Service received");
            }

            @Override
            public void done() {
                System.out.println("Done scanning");
            }
        });
    }

}
