package nathanielwendt.mpc.ut.edu.iotinfluence.db;

import java.util.List;

import nathanielwendt.mpc.ut.edu.iotinfluence.devicereqs.DeviceReq;
import nathanielwendt.mpc.ut.edu.iotinfluence.misc.Location;

/**
 * Created by nathanielwendt on 4/5/16.
 */
public class Action {
    private String id;
    private Location refLocation;
    private Location devLocation;
    private List<DeviceReq> reqs;
    private String deviceId;
    private String type; //e.q. Light-On, Key-Unlock, Video-Pan
    private boolean succesful;
}
