package nathanielwendt.mpc.ut.edu.iotinfluence.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import nathanielwendt.mpc.ut.edu.iotinfluence.device.Light;
import nathanielwendt.mpc.ut.edu.iotinfluence.devicereqs.DeviceReq;
import nathanielwendt.mpc.ut.edu.iotinfluence.devicereqs.SpatialReq;
import nathanielwendt.mpc.ut.edu.iotinfluence.devicereqs.TypeReq;
import nathanielwendt.mpc.ut.edu.iotinfluence.misc.Location;

/**
 * Created by nathanielwendt on 5/14/16.
 */
public class InteractionHistory {
    private SQLiteOpenHelper _openHelper;
    private static final String DB_NAME = "history.db";
    private static final String TAB_NAME = "interactions";

    /**
     * Construct a new database helper object
     * @param context The current context for the application or activity
     */
    public InteractionHistory(Context context) {
        _openHelper = new SimpleSQLiteOpenHelper(context);
    }

    /**
     * This is an internal class that handles the creation of all database tables
     */
    class SimpleSQLiteOpenHelper extends SQLiteOpenHelper {

        SimpleSQLiteOpenHelper(Context context) {
            super(context, DB_NAME, null, 1);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            //String requestId (Don't need?)
            //String deviceId

            //--Action--
            //public String id;
            //public Location refLocation;
            //public Location devLocation;
            //public List<DeviceReq> reqs;
            //public String deviceId;
            //public ActionType type; //e.q. Light-On, Key-Unlock, Video-Pan
            //public boolean successful;

            //_id and actionId are disjoint for now, shouldn't affect performance too much
            db.execSQL("create table " + TAB_NAME + " (_id integer primary key autoincrement, actionId text, " +
                    "                                  refX real, refY real, devX real, devY real, spatialReq text, typeReq text, deviceId text, " +
                    "                                   actionType text, successful integer)");
            db.execSQL("CREATE INDEX refXIndex ON " + TAB_NAME + " (refX);");
            db.execSQL("CREATE INDEX refYIndex ON " + TAB_NAME + " (refY);");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        }
    }

    public void clear(){
        SQLiteDatabase db = _openHelper.getWritableDatabase();
        db.execSQL("DROP TABLE " + TAB_NAME);
        db.execSQL("create table " + TAB_NAME + " (_id integer primary key autoincrement, actionId text, " +
                "                                  refX real, refY real, devX real, devY real, spatialReq text, typeReq text, deviceId text, " +
                "                                   actionType text, successful integer)");
        db.execSQL("CREATE INDEX refXIndex ON " + TAB_NAME + " (refX);");
        db.execSQL("CREATE INDEX refYIndex ON " + TAB_NAME + " (refY);");
        db.close();
    }

    public int size(){
        SQLiteDatabase db = _openHelper.getReadableDatabase();
        int size = (int) DatabaseUtils.longForQuery(db, "SELECT COUNT(*) FROM " + TAB_NAME, null);
        db.close();
        return size;
    }

    public List<Action> query(Location r, double range){
        SQLiteDatabase db = _openHelper.getReadableDatabase();
        if (db == null) {
            return null;
        }
        List<Action> actions = new ArrayList<>();
        String rminX = String.valueOf(r.x() - range);
        String rmaxX = String.valueOf(r.x() + range);
        String rminY = String.valueOf(r.y() - range);
        String rmaxY = String.valueOf(r.y() + range);

        String[] args = new String[]{rminX, rmaxX, rminY, rmaxY};
        Cursor cur = db.rawQuery("select actionId,refX,refY,devX,devY,spatialReq,typeReq,deviceId,actionType,successful from "
                                + TAB_NAME + " where (refX between ? AND ?) AND (refY between ? AND ?) ", args);
        if (cur.moveToNext()) {
            Action action = new Action();
            action.id = cur.getString(0);
            double refX = Double.valueOf(cur.getString(1));
            double refY = Double.valueOf(cur.getString(2));
            action.refLocation = new Location(refX, refY);
            double devX = Double.valueOf(cur.getString(3));
            double devY = Double.valueOf(cur.getString(4));
            action.devLocation = new Location(refX, refY);

            //Ugly, not extensible, needs fix.
            List<DeviceReq> reqs = new ArrayList<>();
            reqs.add(SpatialReq.fromSchema(cur.getString(5)));
            reqs.add(TypeReq.fromSchema(cur.getString(6)));
            action.reqs = reqs;
            action.deviceId = cur.getString(7);
            action.type = Light.LightAction.fromSchema(cur.getString(8));
            action.successful = Integer.valueOf(cur.getString(9)) == 1;
            actions.add(action);
        }
        cur.close();
        db.close();
        return actions;
    }


//    public List<Action> query(Location r, Location d, double range){
//        List<Action> res = new ArrayList<>();
//        for(Action action : actions.values()){
//            double distToRef = Location.distance(action.refLocation, r);
//            if(distToRef <= range){
//                res.add(action);
//            }
//        }
//        return res;
//    }

    public long insert(Action action){
        SQLiteDatabase db = _openHelper.getWritableDatabase();
        if (db == null) {
            return 0;
        }
        ContentValues row = new ContentValues();
        //row.put("requestId", requestId);
        row.put("actionId", action.id);
        row.put("refX", action.refLocation.x());
        row.put("refY", action.refLocation.y());
        if(action.devLocation != null){
            row.put("devX", action.devLocation.x());
            row.put("devY", action.devLocation.y());
        }
        if (action.reqs != null) {
            for(DeviceReq req : action.reqs){
                if(req instanceof TypeReq){
                    row.put("typeReq", req.toSchema());
                }
                else if(req instanceof SpatialReq){
                    row.put("spatialReq", req.toSchema());
                }
            }
        }
        row.put("deviceId", action.deviceId);
        if(action.type != null){
            row.put("actionType", action.type.toSchema());
        }
        if(action.successful){
            row.put("successful", 1);
        } else {
            row.put("successful", 0);
        }
        long id = db.insert(TAB_NAME, null, row);
        db.close();
        return id;
    }
}
