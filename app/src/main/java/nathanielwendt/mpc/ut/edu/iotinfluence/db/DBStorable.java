package nathanielwendt.mpc.ut.edu.iotinfluence.db;

/**
 * Created by nathanielwendt on 5/14/16.
 * facility for very simple ORM scheme (quick and dirty)
 */
public interface DBStorable {
    String toSchema();
    //static method
    //Object fromSchema(String schema);
}
