package nathanielwendt.mpc.ut.edu.iotinfluence.device;

import android.content.Context;

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
    private final Object lock = new Object();

    public LocalDeviceManager(Context ctx){
        this.ctx = ctx;
        serviceManager = new ServiceManager(ctx);
    }

    public boolean initialized(){
        synchronized(lock){
            return initialized;
        }
    }

    public void setInitialized(boolean initialized){
        synchronized(lock){
            this.initialized = initialized;
        }
    }

//    private class ScanTask extends AsyncTask<InitializedCallback, Void, Void> {
//
//        @Override
//        protected Void doInBackground(final InitializedCallback... params) {
//            serviceManager.scan(new ServiceManager.FindServiceCallback(){
//                @Override public void onService(Service service){
//                    services.add(service);
//                    service.fetchDevices(new Service.FetchDevicesCallback(){
//                        @Override public void onFetch(List<DeviceModel> fetchedDevices){
//                            retrieve.addAll(fetchedDevices);
//                        }
//                    });
//                }
//
//                @Override public void done(){
//                    setInitialized(true);
//                    //should be single param, but need loop to access varargs
//                    for(InitializedCallback cb : params){ cb.onInit(); }
//                }
//            });
//            return null;
//        }
//    }

    @Override
    public void scan() {
        throw new RuntimeException("not implemented yet");
    }

    @Override
    public void scan(final InitializedCallback callback){
        //ToDo: perform cloud update scans as well

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
                setInitialized(true);
                callback.onInit();
            }
        });
    }

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
