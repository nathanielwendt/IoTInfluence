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

        boolean equals(String identifier){
            return this.identifier.equals(identifier);
        }

        static Type newInstance(String value){
            for(Type type : Type.values()){
                if(type.equals(value)){
                    return type;
                }
            }
            throw new RuntimeException("Could not detect type from new instance value");
        }
    }

    ReqOperator operator(){
        return new TypeReqOperator(this);
    }

    @Override
    public String toSchema() {
        String val = "";
        String delim = "";
        for(Type type: types){
            val += delim + type.identifier;
            delim = ":";
        }
        return val;
    }

    public static TypeReq fromSchema(String schema){
        String[] portions = schema.split(":");
        Type[] types = new Type[portions.length];
        int i = 0;
        for(String portion: portions){
            types[i++] = Type.newInstance(portion);
        }
        return new TypeReq(types);
    }
}

