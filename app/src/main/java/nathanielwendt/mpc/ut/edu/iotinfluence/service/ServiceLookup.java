package nathanielwendt.mpc.ut.edu.iotinfluence.service;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by nathanielwendt on 3/26/16.
 */
public class ServiceLookup {
    private static Map<String, Service> addresses = new HashMap<String, Service>();

    static {
        addresses.put("63:3353:5363:2324", new Wink("10db23b44fb025f1ad302d66693feb11",
                                                    "649bf40892d9caea9ee6f70a0026f434",
                                                    "mobilepervasivecomputing@gmail.com",
                                                    "mpcmpc4IoT"));
    }

    public static Service lookup(String address){
        return addresses.get(address);
    }
}
