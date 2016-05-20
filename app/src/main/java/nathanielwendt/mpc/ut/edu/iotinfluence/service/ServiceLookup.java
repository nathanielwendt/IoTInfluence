package nathanielwendt.mpc.ut.edu.iotinfluence.service;

import java.util.HashMap;
import java.util.Map;

import nathanielwendt.mpc.ut.edu.iotinfluence.devicereqs.TypeReq;

/**
 * Created by nathanielwendt on 3/26/16.
 */
public class ServiceLookup {
    private static Map<String, Service> addresses = new HashMap<String, Service>();

    static {
        addresses.put("78:A5:04:8C:1F:10", new WinkMock("10db23b44fb025f1ad302d66693feb11",
                                                    "649bf40892d9caea9ee6f70a0026f434",
                                                    "mobilepervasivecomputing@gmail.com",
                                                    "mpcmpc4IoT"));
        addresses.put("f0f816b3-f6de-48b9-9c5d-ce6eb4a59c97", new BLEService("f0f816b3-f6de-48b9-9c5d-ce6eb4a59c97", TypeReq.Type.LIGHT));
    }

    public static Service lookup(String address){
        Service service = addresses.get(address);
//        if(service == null){
//            service = new WinkMock("10db23b44fb025f1ad302d66693feb11",
//                    "649bf40892d9caea9ee6f70a0026f434",
//                    "mobilepervasivecomputing@gmail.com",
//                    "mpcmpc4IoT");
//        }
        return service;
    }
}
