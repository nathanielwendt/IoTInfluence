package nathanielwendt.mpc.ut.edu.iotinfluence.util;

import java.lang.reflect.Field;
import java.util.UUID;

/**
 * Created by nathanielwendt on 3/28/16.
 */
public class Util {
    public static String ObjFieldsToString(Object obj){
        StringBuilder result = new StringBuilder();
        String newLine = System.getProperty("line.separator");

        result.append(obj.getClass().getName());
        result.append( " Object {" );
        result.append(newLine);

        //determine fields declared in this class only (no fields of superclass)
        Field[] fields = obj.getClass().getDeclaredFields();

        //print field names paired with their values
        for ( Field field : fields  ) {
            result.append("  ");
            try {
                result.append( field.getName() );
                result.append(": ");
                //requires access to private field:
                result.append( field.get(obj) );
            } catch ( IllegalAccessException ex ) {
                System.out.println(ex);
            }
            result.append(newLine);
        }
        result.append("}");

        return result.toString();
    }

    public static String getUUID(){
        UUID uuid = UUID.randomUUID();
        return uuid.toString();
    }
}
