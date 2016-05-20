package nathanielwendt.mpc.ut.edu.iotinfluence;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;

import nathanielwendt.mpc.ut.edu.iotinfluence.device.Device;
import nathanielwendt.mpc.ut.edu.iotinfluence.device.DeviceCommand;
import nathanielwendt.mpc.ut.edu.iotinfluence.device.DeviceUnavailableException;
import nathanielwendt.mpc.ut.edu.iotinfluence.device.Light;
import nathanielwendt.mpc.ut.edu.iotinfluence.devicereqs.DeviceReq;
import nathanielwendt.mpc.ut.edu.iotinfluence.devicereqs.SpatialReq;
import nathanielwendt.mpc.ut.edu.iotinfluence.devicereqs.TypeReq;
import nathanielwendt.mpc.ut.edu.iotinfluence.misc.Location;
import nathanielwendt.mpc.ut.edu.iotinfluence.service.DiscoverCallback;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        List<DeviceReq> reqs = new ArrayList<DeviceReq>();
        Location currLoc = new Location(0,0);
        reqs.add(new SpatialReq(SpatialReq.Bound.CLOSEST, SpatialReq.Influence.AWARE, currLoc));
        reqs.add(new TypeReq(new TypeReq.Type[]{TypeReq.Type.LIGHT}));

        Warble snapshot = new Warble(this, Warble.Discovery.ACTIVE);
        List<Light> devices = snapshot.retrieve(Light.class, reqs, 1);
        Light light = snapshot.retrieve(Light.class, reqs);

        snapshot.discover();
        snapshot.hasDiscovered();

        snapshot.discover(new DiscoverCallback() {
            @Override
            public void onDiscover() {

            }
        });

        try {
            devices.get(0).on();
            light.on();
        } catch (DeviceUnavailableException e) {
            e.printStackTrace();
        }

        //Use this requestId to report Help! commands
        snapshot.help(light);

        //STATIC BIND
        //coupled
        snapshot.batch(Light.class, reqs, 1, Warble.Commands.lightBinary);
        snapshot.batch(Light.class, reqs, 1, new DeviceCommand() {
            @Override
            public void onBind(Device device) throws DeviceUnavailableException {
                ((Light) device).on();
            }
        });

        DynamicBinding dBinding = snapshot.dynamicBind(reqs, 1, DynamicBinding.Plans.lightBinary);
        dBinding.trigger(reqs);
        //discovery is still on the snapshot
        snapshot.discover(new DiscoverCallback() {
            @Override
            public void onDiscover() {

            }
        });

        List<Device> dDevices = dBinding.retrieve();
        try {
            Light dLight = (Light) dDevices.get(0);
            dLight.on();

            //time passes, new binding attached

            dLight.off();
            dBinding.help(dLight);


        } catch (DeviceUnavailableException e ){
            e.printStackTrace();
        }

        dBinding.unbind();


//        //DYNAMIC BIND
//        DynamicBinding warbleBinding = new DynamicBinding.Builder().reqs(reqs).num(1).ctx(this)
//                .discovery(Warble.Discovery.ONDEMAND)
//                .plan(Warble.Plans.lightBinary)
//                .build();
//
//        warbleBinding.discover(new DiscoverCallback(){
//            @Override public void onDiscover(){
//
//            }
//        });
//
//        warbleBinding.trigger(reqs);



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
