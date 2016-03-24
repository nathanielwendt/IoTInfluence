package nathanielwendt.mpc.ut.edu.iotinfluence.devicereqs;

/**
 * Created by nathanielwendt on 3/23/16.
 */
public class TypeReq extends DeviceReq {
    final Type[] types;

    public TypeReq(Type[] types){
        this.types = types;
    }

    public enum Type {
        LIGHT("light"), CAMERA("camera"), LOCK("lock");

        private String identifier;

        public String getConstraint(){
            return "WHERE type = " + this.identifier;
        }

        Type(String identifier){
            this.identifier = identifier;
        }
    }

    ReqOperator operator(){
        return new TypeReqOperator(this);
    }
}

