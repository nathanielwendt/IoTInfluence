package nathanielwendt.mpc.ut.edu.iotinfluence;

import android.content.Context;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import nathanielwendt.mpc.ut.edu.iotinfluence.db.Action;
import nathanielwendt.mpc.ut.edu.iotinfluence.db.LocalActionDB;
import nathanielwendt.mpc.ut.edu.iotinfluence.device.Device;
import nathanielwendt.mpc.ut.edu.iotinfluence.device.DeviceCommand;
import nathanielwendt.mpc.ut.edu.iotinfluence.device.DeviceManager;
import nathanielwendt.mpc.ut.edu.iotinfluence.device.DevicePlan;
import nathanielwendt.mpc.ut.edu.iotinfluence.device.DeviceUnavailableException;
import nathanielwendt.mpc.ut.edu.iotinfluence.device.Light;
import nathanielwendt.mpc.ut.edu.iotinfluence.device.LocalDeviceManager;
import nathanielwendt.mpc.ut.edu.iotinfluence.devicereqs.AggregateReqOperator;
import nathanielwendt.mpc.ut.edu.iotinfluence.devicereqs.DeviceReq;
import nathanielwendt.mpc.ut.edu.iotinfluence.devicereqs.ItemwiseReqOperator;
import nathanielwendt.mpc.ut.edu.iotinfluence.devicereqs.ReqOperator;
import nathanielwendt.mpc.ut.edu.iotinfluence.devicereqs.SpatialReq;
import nathanielwendt.mpc.ut.edu.iotinfluence.misc.Location;
import nathanielwendt.mpc.ut.edu.iotinfluence.models.DeviceModel;
import nathanielwendt.mpc.ut.edu.iotinfluence.service.DiscoverCallback;
import nathanielwendt.mpc.ut.edu.iotinfluence.util.InitializedCallback;
import nathanielwendt.mpc.ut.edu.iotinfluence.util.Util;

/**
 * Created by nathanielwendt on 3/9/16.
 *
 * Requires external synchronization
 */
public class Warble {
    Device lastDevice = null;
    DeviceManager devManager;
    Context ctx;
    Observable initObserver;
    Discovery discovery;

    public enum Discovery {
        ONDEMAND(0), ACTIVE(10000), HYPER(1000);

        //ms
        private int scanInterval;
        Discovery(int scanInterval){
            this.scanInterval = scanInterval;
        }
    }

    public Warble(Context ctx, Discovery discovery){
        //scan for services
        //populate services and retrieve tables
        this.ctx = ctx;
        this.discovery = discovery;

        //TODO: handle automatic discovery

        initObserver = new Observable(){
            @Override
            public void addObserver(Observer observer) {
                super.addObserver(observer);
                this.setChanged();
            }
        };
    }

    public void setDevManager(DeviceManager devManager){
        this.devManager = devManager;
    }

    public void discover(){
        if(devManager == null){
            devManager = new LocalDeviceManager(ctx);
        }
        devManager.scan(new InitializedCallback(){
            @Override public void onInit(){
                initObserver.notifyObservers();
            }
        });
    }

    public void discover(final DiscoverCallback callback){
        if(devManager == null){
            devManager = new LocalDeviceManager(ctx);
        }

        devManager.scan(new InitializedCallback(){
            @Override public void onInit(){
                callback.onDiscover();
                initObserver.notifyObservers();
            }
        });
    }

    public void whenDiscovered(final DiscoverCallback callback){
        if(hasDiscovered()){
            callback.onDiscover();
        } else {
            initObserver.addObserver(new Observer() {
                @Override
                public void update(Observable observable, Object data) {
                    callback.onDiscover();
                }
            });
        }
    }

    public boolean hasDiscovered(){
        if(devManager == null){
            return false;
        }
        return devManager.initialized();
    }

    public <D extends Device> List<D> retrieve(Class<D> clazz, List<DeviceReq> reqs, int N){
        if (!hasDiscovered()) {
            throw new IllegalStateException("Warble is not hasDiscovered");
        }
        List<Device> finalDevices = this.retrieve(reqs, N);

        //transform to actual device rather than model version
        //cast to specific <D> class
        String requestId = Util.getUUID();

        List<D> retList = new ArrayList<>();
        for(Device device : finalDevices){
            retList.add(clazz.cast(device));
        }

        return retList;
    }

    public List<Device> retrieve(List<DeviceReq> reqs, int N){
        if (!hasDiscovered()) {
            throw new IllegalStateException("Warble is not hasDiscovered");
        }
        String requestId = Util.getUUID();
        return retrieveHelper(null, reqs, N, requestId);
    }

    public <D extends Device> D retrieve(Class<D> clazz, List<DeviceReq> reqs){
        List<D> items = retrieve(clazz, reqs, 1);
        if(items.size() > 0){
            return items.get(0);
        } else {
            return null;
        }
    }

    public <D extends Device> void act(final Class<D> clazz, final List<DeviceReq> reqs, final int N,
                        final DeviceCommand command){
        this.whenDiscovered(new DiscoverCallback() {
            @Override
            public void onDiscover() {
                List<D> devices = Warble.this.retrieve(clazz, reqs, N);
                for(D device : devices){
                    try {
                        command.onBind(device);
                    } catch (DeviceUnavailableException e){
                        //TODO: generate warning to client
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private <D extends Device> List<Device> retrieveHelper(Class<D> clazz, List<DeviceReq> reqs, int N, String requestId) {
        List<DeviceModel> finalDevices = retrieveCore(clazz, reqs, N, requestId);
        List<Device> ret = new ArrayList<>();
        for (DeviceModel device : finalDevices) {
            Device temp = device.abs(requestId);
            ret.add(temp);
        }
        return ret;
    }

    <D extends Device> List<DeviceModel> retrieveCore(Class<D> clazz, List<DeviceReq> reqs, int N, String requestId){
        //query to get the list of all available retrieve
        List<DeviceModel> devices;
        if (clazz != null) {
            devices = devManager.fetchDevices(clazz);
        } else {
            devices = devManager.fetchDevices();
        }

        List<AggregateReqOperator> aggregateOperators = new ArrayList<AggregateReqOperator>();
        List<ItemwiseReqOperator> itemwiseOperators = new ArrayList<ItemwiseReqOperator>();

        //separate req types to be applied differently
        for (DeviceReq req : reqs) {
            ReqOperator operator = ReqOperator.newInstance(req);
            if (operator instanceof AggregateReqOperator) {
                aggregateOperators.add((AggregateReqOperator) operator);
            } else {
                itemwiseOperators.add((ItemwiseReqOperator) operator);
            }
        }

        //apply item-wise reqs per device
        Iterator<DeviceModel> devIter = devices.iterator();
        while (devIter.hasNext()) {
            DeviceModel device = devIter.next();
            for (ItemwiseReqOperator operator : itemwiseOperators) {
                if (!operator.match(device)) {
                    devIter.remove();
                    break;
                }
            }
        }

        //apply aggregate reqs to device collection
        for (AggregateReqOperator operator : aggregateOperators) {
            devices = operator.resolve(devices);
        }

        List<DeviceModel> finalDevices;
        if (devices.size() >= N) {
            finalDevices = devices.subList(0, N);
        } else {
            finalDevices = devices;
        }

        Location refLoc = null;
        for (DeviceReq req : reqs) {
            if (req instanceof SpatialReq) {
                refLoc = ((SpatialReq) reqs.get(0)).loc();
            }
        }

        //List<Device> ret = new ArrayList<>();
        LocalActionDB.insertPending(requestId, refLoc);
        for (DeviceModel device : finalDevices) {
            //populate local histories
            //Device temp = device.abs(requestId);
            //ret.add(temp);
            LocalActionDB.populatePending(requestId, device.id, device.location());
        }
        return finalDevices;
    }

    //TODO: trigger a device scan on a help request
    //@Convenience method for calling help on an object
    public void help(Device device){
        String requestId = device.requestId();
        String deviceId = device.deviceId();
        this.help(requestId, deviceId);
    }

    public void help(String requestId, String deviceId){
        //update previous entry, indicating it failed
        LocalActionDB.update(requestId, deviceId, false);

        //rollback action
        Action badAction = LocalActionDB.getAction(requestId, deviceId);
        try {
            badAction.type.undo();
        } catch (DeviceUnavailableException e) {
            e.printStackTrace();
        }
    }



    public static class Commands {
        public static DeviceCommand lightBinary = new DeviceCommand() {
            @Override
            public void onBind(Device device) throws DeviceUnavailableException {
                ((Light) device).on();
            }
        };
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
