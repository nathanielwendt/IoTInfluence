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
import nathanielwendt.mpc.ut.edu.iotinfluence.device.Observables;
import nathanielwendt.mpc.ut.edu.iotinfluence.devicereqs.DeviceReq;
import nathanielwendt.mpc.ut.edu.iotinfluence.devicereqs.SpatialReq;
import nathanielwendt.mpc.ut.edu.iotinfluence.devicereqs.TypeReq;
import nathanielwendt.mpc.ut.edu.iotinfluence.misc.Location;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        List<DeviceReq> reqs = new ArrayList<DeviceReq>();
        Location currLoc = new Location(0,0);
        reqs.add(new SpatialReq(SpatialReq.Bound.CLOSEST, SpatialReq.Influence.AWARE, currLoc));
        //no effect for static binding since type token is passed into methods
        //only can effect static binding if type given here is different from type token (will return nothing)
        reqs.add(new TypeReq(new TypeReq.Type[]{TypeReq.Type.LIGHT}));

        Warble snapshot = new Warble(this);

        //STATIC BIND
        //decoupled
        List<Light> devices = snapshot.retrieve(Light.class, reqs, 1);
        Light light = snapshot.retrieve(Light.class, reqs);

        try {
            devices.get(0).on();
            light.on();
        } catch (DeviceUnavailableException e) {
            e.printStackTrace();
        }

        //Use this requestId to report Help! commands
        String requestId = light.requestId();
        String deviceId = light.deviceId();

        //STATIC BIND
        //coupled
        snapshot.act(Light.class, reqs, 1, Warble.Commands.lightBinary);
        snapshot.act(Light.class, reqs, 1, new DeviceCommand() {
            @Override
            public void onBind(Device device) throws DeviceUnavailableException {
                ((Light) device).on();
            }
        });

        //DYNAMIC BIND
        Observables.SpatialObservable spatialObservable = new Observables.SpatialObservable();
        WarbleBind warbleBinding = new WarbleBind.Builder().reqs(reqs).num(1).ctx(this)
                .fluidity(WarbleBind.Fluidity.FIXED)
                .command(Warble.Plans.lightBinary)
                .bind(spatialObservable);

        //update
        Location loc = new Location(0,0);
        spatialObservable.update(loc);


        //unbind
        warbleBinding.unbind();

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
