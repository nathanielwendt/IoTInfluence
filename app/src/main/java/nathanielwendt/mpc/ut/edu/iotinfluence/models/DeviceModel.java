package nathanielwendt.mpc.ut.edu.iotinfluence.models;

import nathanielwendt.mpc.ut.edu.iotinfluence.db.LocalActionDB;
import nathanielwendt.mpc.ut.edu.iotinfluence.device.Device;
import nathanielwendt.mpc.ut.edu.iotinfluence.devicereqs.TypeReq;
import nathanielwendt.mpc.ut.edu.iotinfluence.misc.Location;
import nathanielwendt.mpc.ut.edu.iotinfluence.service.Service;

/**
 * Created by nathanielwendt on 3/25/16.
 */
public abstract class DeviceModel {
    public Service service;
    public String id;
    public String manufacturer;
    public String model;
    public String hubId;
    public String hubManufacturer;
    public String radioType;
    public TypeReq.Type type;
    public long createdAt;
    public boolean powered;
    public Location location;

    public abstract Device abs(String requestId, LocalActionDB localActionDB);
    public abstract Device proxy(Service proxyService, String requestId, String proxyId, LocalActionDB localActionDB);
    public abstract Class<? extends Device> type();
    public Location location(){
        return location;
    }

    @Override public String toString(){
        return "id: " + id + " manufacturer: " + manufacturer + " hubId: " + hubId +
                " hubManufacturer: " + hubManufacturer + " radioType: " + radioType +
                " createdAt: " + createdAt + " powered: " + powered;
    }
}
