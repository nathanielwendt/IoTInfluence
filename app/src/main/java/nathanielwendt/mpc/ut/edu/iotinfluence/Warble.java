package nathanielwendt.mpc.ut.edu.iotinfluence;

import android.content.Context;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.UUID;

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
import nathanielwendt.mpc.ut.edu.iotinfluence.util.InitializedCallback;

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


    public Warble(Context ctx){
        //scan for services
        //populate services and devices tables
        this.ctx = ctx;
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

    public void discover(final InitializedCallback callback){
        if(devManager == null){
            devManager = new LocalDeviceManager(ctx);
        }

        devManager.scan(new InitializedCallback(){
            @Override public void onInit(){
                callback.onInit();
                initObserver.notifyObservers();
            }
        });
    }

    public void whenDiscovered(final InitializedCallback callback){
        if(hasDiscovered()){
            callback.onInit();
        } else {
            initObserver.addObserver(new Observer() {
                @Override
                public void update(Observable observable, Object data) {
                    callback.onInit();
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
        List<Device> finalDevices = this.retrieve(reqs, N);

        //transform to actual device rather than model version
        //cast to specific <D> class
        String requestId = getNewRequestId();

        List<D> retList = new ArrayList<>();
        for(Device device : finalDevices){
            retList.add(clazz.cast(device));
        }

        return retList;
    }

    public <D extends Device> D retrieve(Class<D> clazz, List<DeviceReq> reqs){
        List<D> items = retrieve(clazz, reqs, 1);
        if(items.size() > 0){
            return items.get(0);
        } else {
            return null;
        }
    }

    public List<Device> retrieve(List<DeviceReq> reqs, int N){
        String requestId = getNewRequestId();
        List<Device> finalDevices = retrieveHelper(null, reqs, N, requestId);
        return finalDevices;
    }

    public <D extends Device> void act(final Class<D> clazz, final List<DeviceReq> reqs, final int N,
                        final DeviceCommand command){
        this.whenDiscovered(new InitializedCallback() {
            @Override
            public void onInit() {
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

    private <D extends Device> List<Device> retrieveHelper(Class<D> clazz, List<DeviceReq> reqs, int N, String requestId){
        if(!hasDiscovered()){ throw new IllegalStateException("Warble is not hasDiscovered"); }

        //query to get the list of all available devices
        List<DeviceModel> devices;
        if(clazz != null){
            devices = devManager.fetchDevices(clazz);
        } else {
            devices = devManager.fetchDevices();
        }

        List<AggregateReqOperator> aggregateOperators = new ArrayList<AggregateReqOperator>();
        List<ItemwiseReqOperator> itemwiseOperators = new ArrayList<ItemwiseReqOperator>();

        //separate req types to be applied differently
        for(DeviceReq req: reqs){
            ReqOperator operator = ReqOperator.newInstance(req);
            if(operator instanceof AggregateReqOperator){
                aggregateOperators.add((AggregateReqOperator) operator);
            } else {
                itemwiseOperators.add((ItemwiseReqOperator) operator);
            }
        }

        //apply item-wise reqs per device
        Iterator<DeviceModel> devIter = devices.iterator();
        while (devIter.hasNext()) {
            DeviceModel device = devIter.next();
            for(ItemwiseReqOperator operator : itemwiseOperators){
                if(!operator.match(device)){
                    devIter.remove();
                    break;
                }
            }
        }

        //apply aggregate reqs to device collection
        for(AggregateReqOperator operator : aggregateOperators){
            devices = operator.resolve(devices);
        }

        List<DeviceModel> finalDevices;
        if(devices.size() >= N){
            finalDevices = devices.subList(0, N);
        } else {
            finalDevices = devices;
        }

        Location refLoc = null;
        for(DeviceReq req : reqs){
            if(req instanceof SpatialReq){
                refLoc = ((SpatialReq) reqs.get(0)).loc();
            }
        }

        List<Device> ret = new ArrayList<>();
        LocalActionDB.insertPending(requestId, refLoc);
        for(DeviceModel device : finalDevices){
            //populate local histories
            Device temp = device.abs(requestId);
            ret.add(temp);
            LocalActionDB.populatePending(requestId, device.id, device.location());
        }

        return ret;
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

    private String getNewRequestId(){
        UUID uuid = UUID.randomUUID();
        return uuid.toString();
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
