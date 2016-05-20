package nathanielwendt.mpc.ut.edu.iotinfluence;

import java.util.ArrayList;
import java.util.List;

import nathanielwendt.mpc.ut.edu.iotinfluence.device.Device;
import nathanielwendt.mpc.ut.edu.iotinfluence.device.DevicePlan;
import nathanielwendt.mpc.ut.edu.iotinfluence.device.DeviceUnavailableException;
import nathanielwendt.mpc.ut.edu.iotinfluence.device.Light;
import nathanielwendt.mpc.ut.edu.iotinfluence.devicereqs.DeviceReq;
import nathanielwendt.mpc.ut.edu.iotinfluence.models.DeviceModel;
import nathanielwendt.mpc.ut.edu.iotinfluence.service.ProxyService;
import nathanielwendt.mpc.ut.edu.iotinfluence.util.Util;

/**
 * Created by nathanielwendt on 3/21/16.
 */
public class DynamicBinding {
    private final int N;
    private final DevicePlan plan;
    private final Warble warble;
    private List<DeviceReq> reqs;
    private Class clazz;

    private List<DeviceModel> activeDevices;
    private String requestId;
    private ProxyService proxyService;

    private DynamicBinding(Builder builder){
        if(builder.warble == null){
            throw new IllegalArgumentException("Warble binding needs a non null reference to a Warble objet");
        }

        if(builder.reqs == null){
            throw new IllegalArgumentException("No Device Requirements included in Warble Request");
        }

        if(builder.plan == null){
            throw new IllegalArgumentException("No Device Requirements included in Warble Request");
        }

        this.warble = builder.warble;
        this.N = builder.N;
        this.reqs = builder.reqs;
        this.plan = builder.plan;
        this.clazz = builder.clazz;

        proxyService = new ProxyService(activeDevices, requestId);

        this.requestId = Util.getUUID();
        populateActiveDevices();
//        this.obs.addObserver(new Observer(){
//            @Override
//            public void update(Observable observable, Object data) {
//                Location newLocation = (Location) data;
//                //batch
//                //List<Device> retrieve = WarbleBind.this.warble.retrieve()
//                populateActiveDevices();
//            }
//        });
    }

    private void populateActiveDevices(){
        //TODO: compare new devices with old
        //call onUbind on old devices not in new list
        //call onBind on new devices not in old list

        activeDevices = warble.retrieveCore(clazz, reqs, N, requestId);
        proxyService.setDevices(activeDevices);
    }

//    public void discover(DiscoverCallback discoverCallback){
//        warble.discover(discoverCallback);
//    }

    public void trigger(List<DeviceReq> reqs){
        this.reqs = reqs;
        populateActiveDevices();
    }


    public void help(Device device){
        String requestId = device.requestId();
        String deviceId = device.deviceId();
        //int index = ProxyService.idToIndex(deviceId);
        //DeviceModel deviceModel = activeDevices.get(index);
        Device actingDevice = proxyService.getActingDevice(device);
        warble.help(actingDevice.getLastActionId());
    }


    public List<Device> retrieve(){
        List<Device> ret = new ArrayList<>();
        int index = 0;
        for (DeviceModel device : activeDevices) {
            String proxyId = ProxyService.indexToId(index++);
            Device temp = device.proxy(proxyService, requestId, proxyId, warble.localActionDB);
            ret.add(temp);
        }
        return ret;
    }

    public void unbind(){
        //TODO: implement unbinding cleanup
        //cleanup any histories
        //update database
        //object is done
    }

    public static class Builder {
        private int N = 1;
        private List<DeviceReq> reqs;
        private DevicePlan plan;
        private Warble warble;
        private Class clazz;

        public Builder(){}

        public Builder num(int num){
            this.N = num;
            return this;
        }

        public Builder reqs(List<DeviceReq> reqs){
            this.reqs = reqs;
            return this;
        }

        public Builder plan(DevicePlan plan){
            this.plan = plan;
            return this;
        }

        public <D extends Device> Builder clazz(Class<D> clazz){
            this.clazz = clazz;
            return this;
        }

        public Builder warble(Warble warble){
            this.warble = warble;
            return this;
        }

        public DynamicBinding build(){
            return new DynamicBinding(this);
        }
    }

    public static class Plans {
        public static DevicePlan lightBinary = new DevicePlan() {
            @Override
            public void onBind(Device device) throws DeviceUnavailableException {
                ((Light) device).on();
            }

            @Override
            public void onUnbind(Device device) throws DeviceUnavailableException {
                ((Light) device).off();
            }
        };
    }


}

