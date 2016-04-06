package nathanielwendt.mpc.ut.edu.iotinfluence;

import android.test.InstrumentationTestCase;

import nathanielwendt.mpc.ut.edu.iotinfluence.service.Wink;

/**
 * Created by nathanielwendt on 3/28/16.
 */
public class WinkTest extends InstrumentationTestCase {
    public void testSignIn() throws Exception {
        Wink wink = new Wink("10db23b44fb025f1ad302d66693feb11",
                "649bf40892d9caea9ee6f70a0026f434",
                "mobilepervasivecomputing@gmail.com",
                "mpcmpc4IoT");
//        wink.fetchDevices(new Service.FetchDevicesCallback() {
//            @Override
//            public void onFetch(List<DeviceModel> fetchedDevices) {
//                System.out.println(fetchedDevices);
//            }
//        });
    }
}
