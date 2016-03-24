package nathanielwendt.mpc.ut.edu.iotinfluence;

import android.location.Location;
import android.os.Handler;

import java.util.ArrayList;
import java.util.List;

import nathanielwendt.mpc.ut.edu.iotinfluence.devicereqs.SpatialReq;
import nathanielwendt.mpc.ut.edu.iotinfluence.devicereqs.TypeReq;
import nathanielwendt.mpc.ut.edu.iotinfluence.devices.Device;
import nathanielwendt.mpc.ut.edu.iotinfluence.devicereqs.DeviceReq;
import nathanielwendt.mpc.ut.edu.iotinfluence.devices.Light;
import nathanielwendt.mpc.ut.edu.iotinfluence.devices.Observables;

import static nathanielwendt.mpc.ut.edu.iotinfluence.devicereqs.SpatialReq.*;
import static nathanielwendt.mpc.ut.edu.iotinfluence.devicereqs.TypeReq.*;

/**
 * Created by nathanielwendt on 3/9/16.
 */
public class Example {
    static Handler handler = new Handler();

    public static void main(String[] args){

        Runnable runnable = new Runnable() {
            @Override
            public void run() {


                List<DeviceReq> reqs = new ArrayList<DeviceReq>();
                reqs.add(new SpatialReq(Bound.CLOSEST, Influence.AWARE));
                //no effect for static binding since type token is passed into methods
                //only can effect static binding if type given here is different from type token (will return nothing)
                reqs.add(new TypeReq(new Type[]{Type.LIGHT}));

                Warble snapshot = new Warble();

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

                //STATIC BIND
                //coupled
                snapshot.act(Light.class, reqs, 1, Warble.CommandPlans.lightBinary);
                snapshot.act(Light.class, reqs, 1, new DeviceCommand() {
                    @Override
                    public void onBind(Device device) throws DeviceUnavailableException {
                        ((Light) device).on();
                    }

                    @Override
                    public void onUnbind(Device device) throws DeviceUnavailableException {
                        ((Light) device).off();
                    }
                });

                //DYNAMIC BIND
                Observables.SpatialObservable spatialObservable = new Observables.SpatialObservable();
                WarbleBind warbleBinding = new WarbleBind.Builder().reqs(reqs).num(1)
                                        .fluidity(WarbleBind.Fluidity.FIXED)
                                        .command(Warble.CommandPlans.lightBinary)
                                        .bind(spatialObservable);

                //update
                Location loc = new Location("");
                spatialObservable.update(loc);


                //unbind
                warbleBinding.unbind();

                /* and here comes the "trick" */
                handler.postDelayed(this, 100);
            }
        };

        handler.postDelayed(runnable, 100);

    }
}
