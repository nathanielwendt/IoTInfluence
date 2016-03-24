package nathanielwendt.mpc.ut.edu.iotinfluence;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import nathanielwendt.mpc.ut.edu.iotinfluence.devicereqs.AggregateReqOperator;
import nathanielwendt.mpc.ut.edu.iotinfluence.devicereqs.DeviceReq;
import nathanielwendt.mpc.ut.edu.iotinfluence.devicereqs.ItemwiseReqOperator;
import nathanielwendt.mpc.ut.edu.iotinfluence.devicereqs.ReqOperator;
import nathanielwendt.mpc.ut.edu.iotinfluence.devices.Device;
import nathanielwendt.mpc.ut.edu.iotinfluence.devices.Light;

/**
 * Created by nathanielwendt on 3/9/16.
 *
 * Requires external synchronization
 */
public class Warble {
    Device lastDevice = null;

    private List<Device> fetchAllDevices(){
        return new ArrayList<Device>();
    }

    //default, don't scan for new devices
    public <D> List<D> retrieve(Class<D> clazz, List<DeviceReq> reqs, int N){
        try {
            Field idField = clazz.getField(Device.identifierField);
            idField.setAccessible(true);
            String identifier = (String) idField.get(clazz);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            //class may not have indicated identifier field
            e.printStackTrace();
            return null; //to-do better error visibility for client
        }

        //query to get the list of all available devices
        List<Device> devices = fetchAllDevices();

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
        Iterator<Device> devIter = devices.iterator();
        while (devIter.hasNext()) {
            Device device = devIter.next();
            for(ItemwiseReqOperator operator : itemwiseOperators){
                if(!operator.match(device)){
                    devIter.remove();
                    break;
                }
            }
        }

        //apply aggregate reqs to device collection
        for(AggregateReqOperator operator : aggregateOperators){
            operator.resolve(devices);
        }

        List<Device> finalDevices = devices.subList(0, N);
        List<D> retList = new ArrayList<>();
        for(Device device : devices){
            retList.add(clazz.cast(device));
        }
        return retList;

        //SQL Query approach, tabled for now
//        for(DeviceReq req: reqs){
//            query += req.getAllQueryConstraints();
//        }
//
//        query += "LIMIT " + String.valueOf(N);
//
//        //execute the query
//        //*(*****
//
//        List<Device> devices = new ArrayList<Device>();
//        List<D> retList = new ArrayList<D>();
//        for(Device device : devices){
//            retList.add(clazz.cast(device));
//        }
//        return retList;
    }

    public <D> D retrieve(Class<D> clazz, List<DeviceReq> reqs){
        List<D> items = retrieve(clazz, reqs, 1);
        return items.get(0);
    }

    public List<Device> retrieveMixed(List<DeviceReq> reqs){
        return null;
    }

    public <D> void act(Class<D> clazz, List<DeviceReq> reqs, int N,
                           DeviceCommand comm){

    }

    public static class CommandPlans {
        public static DeviceCommand lightBinary = new DeviceCommand() {
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
