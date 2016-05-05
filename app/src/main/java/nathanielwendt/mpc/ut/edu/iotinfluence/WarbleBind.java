package nathanielwendt.mpc.ut.edu.iotinfluence;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

import nathanielwendt.mpc.ut.edu.iotinfluence.device.Device;
import nathanielwendt.mpc.ut.edu.iotinfluence.device.DevicePlan;
import nathanielwendt.mpc.ut.edu.iotinfluence.devicereqs.DeviceReq;
import nathanielwendt.mpc.ut.edu.iotinfluence.models.DeviceModel;
import nathanielwendt.mpc.ut.edu.iotinfluence.service.DiscoverCallback;
import nathanielwendt.mpc.ut.edu.iotinfluence.service.ProxyService;
import nathanielwendt.mpc.ut.edu.iotinfluence.util.Util;

/**
 * Created by nathanielwendt on 3/21/16.
 */
public class WarbleBind {
    private final int N;
    private final DevicePlan plan;
    private final Warble.Discovery discovery;
    private final Warble warble;
    private final Context ctx;
    private List<DeviceReq> reqs;
    private List<DeviceModel> activeDevices;
    private String requestId;
    private ProxyService proxyService;

    private WarbleBind(Builder builder){
        if(builder.reqs == null){
            throw new IllegalArgumentException("No Device Requirements included in Warble Request");
        }

        if(builder.plan == null){
            throw new IllegalArgumentException("No Device Requirements included in Warble Request");
        }

        if(builder.obs == null){
            throw new IllegalArgumentException("No Observable included in Warble Request");
        }

        if(builder.ctx == null){
            throw new IllegalArgumentException("No context included in Warble Request");
        }

        this.ctx = builder.ctx;
        this.discovery = builder.discovery;
        this.warble = new Warble(this.ctx, this.discovery);
        this.N = builder.N;
        this.reqs = builder.reqs;
        this.plan = builder.plan;

        proxyService = new ProxyService(activeDevices, requestId);

        this.requestId = Util.getUUID();
        populateActiveDevices();
//        this.obs.addObserver(new Observer(){
//            @Override
//            public void update(Observable observable, Object data) {
//                Location newLocation = (Location) data;
//                //act
//                //List<Device> retrieve = WarbleBind.this.warble.retrieve()
//                populateActiveDevices();
//            }
//        });
    }

    private void populateActiveDevices(){
        //TODO: compare new devices with old
        //call onUbind on old devices not in new list
        //call onBind on new devices not in old list
        activeDevices = warble.retrieveCore(null, reqs, N, requestId);
        proxyService.setDevices(activeDevices);
    }

    public void discover(DiscoverCallback discoverCallback){
        warble.discover(discoverCallback);
    }

    public void trigger(List<DeviceReq> reqs){
        this.reqs = reqs;
        populateActiveDevices();
    }


    public void help(Device device){
        String requestId = device.requestId();
        String deviceId = device.deviceId();
        int index = ProxyService.idToIndex(deviceId);
        DeviceModel deviceModel = activeDevices.get(index);
        warble.help(requestId, deviceModel.id);
    }


    public List<Device> retrieve(){
        List<Device> ret = new ArrayList<>();
        int index = 0;
        for (DeviceModel device : activeDevices) {
            String proxyId = ProxyService.indexToId(index++);
            Device temp = device.proxy(proxyService, requestId, proxyId);
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
        private Observable obs;
        private Warble.Discovery discovery;
        private Context ctx;

        public Builder(){}

        public Builder num(int num){
            this.N = num;
            return this;
        }

        public Builder reqs(List<DeviceReq> reqs){
            this.reqs = reqs;
            return this;
        }

        public Builder command(DevicePlan plan){
            this.plan = plan;
            return this;
        }

        public Builder discovery(Warble.Discovery discovery){
            this.discovery = discovery;
            return this;
        }

        public Builder ctx(Context ctx){
            this.ctx = ctx;
            return this;
        }

        public WarbleBind build(){
            return new WarbleBind(this);
        }
    }


}

