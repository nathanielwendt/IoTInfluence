package nathanielwendt.mpc.ut.edu.iotinfluence.device;

/**
 * Created by nathanielwendt on 3/9/16.
 */
public abstract class Device {
    //public static String identifierField = "identifier";
    private String requestId;
    private String deviceId;

    public Device(String deviceId, String requestId){
        this.requestId = requestId;
        this.deviceId = deviceId;
    }

    public String deviceId(){
        return this.deviceId;
    }

    public String requestId(){
        return this.requestId;
    }
}
