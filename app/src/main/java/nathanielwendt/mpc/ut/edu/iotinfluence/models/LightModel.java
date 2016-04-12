package nathanielwendt.mpc.ut.edu.iotinfluence.models;


import nathanielwendt.mpc.ut.edu.iotinfluence.device.Device;
import nathanielwendt.mpc.ut.edu.iotinfluence.device.Light;
import nathanielwendt.mpc.ut.edu.iotinfluence.service.Service;

/**
 * Created by nathanielwendt on 3/25/16.
 */
public class LightModel extends DeviceModel {
    public Service service;
    public String name;
    public double brightness;

    public Device abs(String requestId){
        return service.light(this.id, requestId);
    }

    @Override
    public Class<? extends Device> type(){
        return Light.class;
    }

    @Override public String toString(){
        return super.toString() + " service: " + service + " name: " + name
                                + " brightness: " + brightness;
    }
}
