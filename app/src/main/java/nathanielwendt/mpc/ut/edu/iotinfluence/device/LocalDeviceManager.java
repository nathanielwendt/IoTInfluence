package nathanielwendt.mpc.ut.edu.iotinfluence.device;

import android.content.Context;
import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.List;

import nathanielwendt.mpc.ut.edu.iotinfluence.models.DeviceModel;
import nathanielwendt.mpc.ut.edu.iotinfluence.service.Service;
import nathanielwendt.mpc.ut.edu.iotinfluence.service.ServiceManager;
import nathanielwendt.mpc.ut.edu.iotinfluence.util.InitializedCallback;

/**
 * Created by nathanielwendt on 3/25/16.
 */
public class LocalDeviceManager implements DeviceManager {
    List<DeviceModel> devices = new ArrayList<>();
    List<Service> services = new ArrayList<>();
    Context ctx;
    ServiceManager serviceManager;
    volatile boolean initialized = false;

    public LocalDeviceManager(Context ctx){
        this.ctx = ctx;
        serviceManager = new ServiceManager(ctx);
    }

    private class ScanTask extends AsyncTask<InitializedCallback, Void, Void> {

        @Override
        protected Void doInBackground(final InitializedCallback... params) {
            serviceManager.scan(new ServiceManager.FindServiceCallback(){
                @Override public void onService(Service service){
                    services.add(service);
                    service.fetchDevices(new Service.FetchDevicesCallback(){
                        @Override public void onFetch(List<DeviceModel> fetchedDevices){
                            devices.addAll(fetchedDevices);
                        }
                    });
                }

                @Override public void done(){
                    initialized = true;
                    //should be single param, but need loop to access varargs
                    for(InitializedCallback cb : params){ cb.onInit(); }
                }
            });
            return null;
        }
    }

    @Override
    public void scan() {
        ScanTask scanTask = new ScanTask();
        scanTask.execute();
    }

    @Override
    public void scan(final InitializedCallback callback){
        ScanTask scanTask = new ScanTask();
        scanTask.execute(callback);
//
//
//        serviceManager.scan(new ServiceManager.FindServiceCallback(){
//            @Override public void onService(Service service){
//                services.add(service);
//                service.fetchDevices(new Service.FetchDevicesCallback(){
//                    @Override public void onFetch(List<DeviceModel> fetchedDevices){
//                        devices.addAll(fetchedDevices);
//                        done();
//                    }
//                });
//            }
//
//            @Override public void done(){
//                initialized = true;
//                callback.onInit();
//            }
//        });
    }

    public boolean isInitialized(){
        return initialized;
    }

//    private <D extends Device> String getClazzIdentifier(Class<D> clazz){
//        try {
//            Field idField = clazz.getField(Device.identifierField);
//            idField.setAccessible(true);
//            return (String) idField.get(clazz);
//        } catch (NoSuchFieldException | IllegalAccessException e) {
//            //class may not have indicated identifier field
//            e.printStackTrace();
//            return ""; //to-do better error visibility for client
//        }
//    }

    @Override
    public <D extends Device> List<DeviceModel> fetchDevices(Class<D> clazz) {
        List<DeviceModel> retList = new ArrayList<>();

        for(DeviceModel device : devices){
            if(device.type() == clazz){
                retList.add(device);
            }
        }
        return retList;
    }

    @Override
    public List<DeviceModel> fetchDevices() {
        return devices;
    }
}
