package nathanielwendt.mpc.ut.edu.iotinfluence.services;

import nathanielwendt.mpc.ut.edu.iotinfluence.DeviceUnavailableException;
import nathanielwendt.mpc.ut.edu.iotinfluence.comm.APIRequest;
import nathanielwendt.mpc.ut.edu.iotinfluence.devices.Light;

/**
 * Created by nathanielwendt on 3/8/16.
 */
public class Wink implements Service {

    private static class WinkRequest extends APIRequest {

    }

    public Light light(final String id){
        return new Light(){
            @Override
            public void brightness(int level) throws DeviceUnavailableException {
                WinkRequest req = new WinkRequest();
                req.param("light_bulb_id", id);
                req.param("brightness", level);
                req.param("powered", true);
            }

            @Override
            public void off() {
                WinkRequest req = new WinkRequest();

            }

            @Override
            public void on() {

            }
        };
    }
}
