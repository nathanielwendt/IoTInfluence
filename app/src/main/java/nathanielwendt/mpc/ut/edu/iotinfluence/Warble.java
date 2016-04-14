package nathanielwendt.mpc.ut.edu.iotinfluence;

import android.content.Context;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
    private int reqCount = 0;

    public Warble(Context ctx){
        //scan for services
        //populate services and devices tables
        this.ctx = ctx;
    }

    public void setDevManager(DeviceManager devManager){
        this.devManager = devManager;
    }

    public void initialize(){
        if(devManager == null){
            devManager = new LocalDeviceManager(ctx);
        }
        devManager.scan();
    }

    public void initialize(final InitializedCallback callback){
        if(devManager == null){
            devManager = new LocalDeviceManager(ctx);
        }

        devManager.scan(new InitializedCallback(){
            @Override public void onInit(){
                callback.onInit();
            }
        });

//        InitializeTask initTask = new InitializeTask();
//        initTask.execute(callback);
    }

//    private class InitializeTask extends AsyncTask<InitializedCallback, Void, Void> {
//
//        @Override
//        protected Void doInBackground(final InitializedCallback... params) {
//            devManager.scan(new InitializedCallback() {
//                @Override
//                public void onInit() {
//                    for (InitializedCallback cb : params) {
//                        cb.onInit();
//                    }
//                }
//            });
//            return null;
//        }
//    }


    public synchronized boolean initialized(){
        return devManager.isInitialized();
    }

    //default, don't scan for new devices
    public <D extends Device> List<D> retrieve(Class<D> clazz, List<DeviceReq> reqs, int N){
        if(!initialized()){ throw new IllegalStateException("Warble is not initialized"); }

        //query to get the list of all available devices
        List<DeviceModel> devices = devManager.fetchDevices(clazz);

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

        List<DeviceModel> finalDevices = devices.subList(0, N);

        //transform to actual device rather than model version
        //cast to specific <D> class
        List<D> retList = new ArrayList<>();
        String requestId = getNewRequestId();
        Location refLoc = null;
        for(DeviceReq req : reqs){
            if(req instanceof SpatialReq){
                refLoc = ((SpatialReq) reqs.get(0)).loc();
               // requestingRefLocs.put(requestId, refLoc);
            }
        }

        LocalActionDB.insertPending(requestId, refLoc);
        for(DeviceModel device : finalDevices){
            Device temp = device.abs(requestId);
            retList.add(clazz.cast(temp));
            //populate local histories
            LocalActionDB.populatePending(requestId, device.id, device.location());
        }
        return retList;
    }

    public void help(String requestId, String deviceId){
        LocalActionDB.update(requestId, deviceId, false);
        //To-do rollback
    }

    public String getNewRequestId(){
        return "req" + String.valueOf(reqCount++);
    }

    public <D extends Device> D retrieve(Class<D> clazz, List<DeviceReq> reqs){
        List<D> items = retrieve(clazz, reqs, 1);
        return items.get(0);
    }

    public List<Device> retrieveMixed(List<DeviceReq> reqs){
        if(!initialized()){ throw new IllegalStateException("Warble is not initialized"); }
        return null;
    }

    public <D> void act(Class<D> clazz, List<DeviceReq> reqs, int N,
                           DeviceCommand command){
        //do we want to check for initialized here?
        //perhaps just give a warning?
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
