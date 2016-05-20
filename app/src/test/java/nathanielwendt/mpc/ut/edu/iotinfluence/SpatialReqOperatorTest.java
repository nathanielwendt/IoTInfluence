package nathanielwendt.mpc.ut.edu.iotinfluence;

import junit.framework.Assert;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import nathanielwendt.mpc.ut.edu.iotinfluence.db.Action;
import nathanielwendt.mpc.ut.edu.iotinfluence.db.LocalActionDB;
import nathanielwendt.mpc.ut.edu.iotinfluence.devicereqs.SpatialReq;
import nathanielwendt.mpc.ut.edu.iotinfluence.devicereqs.SpatialReqOperator;
import nathanielwendt.mpc.ut.edu.iotinfluence.misc.Location;
import nathanielwendt.mpc.ut.edu.iotinfluence.models.DeviceModel;
import nathanielwendt.mpc.ut.edu.iotinfluence.models.LightModel;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class SpatialReqOperatorTest {
    List<DeviceModel> candidates;
    LightModel[] lightModels;
    LocalActionDB localActionDB = new LocalActionDB(null);

    public void buildBasicGrid(){
        candidates = new ArrayList<DeviceModel>();
        lightModels = new LightModel[4];
        lightModels[0] = new LightModel();
        lightModels[0].id = "0";
        lightModels[0].location = new Location(0,0);
        lightModels[1] = new LightModel();
        lightModels[1].id = "1";
        lightModels[1].location = new Location(0,10);
        lightModels[2] = new LightModel();
        lightModels[2].id = "2";
        lightModels[2].location = new Location(10,10);
        lightModels[3] = new LightModel();
        lightModels[3].id = "3";
        lightModels[3].location = new Location(10,0);

        candidates.addAll(Arrays.asList(lightModels));
    }

    private void buildSinglePartitionSparse(){
        insertAction(0, new Location(2,4), true);
        insertAction(1, new Location(1,6), false);
        insertAction(1, new Location(4,6), false);
        insertAction(1, new Location(2,8), true);
        insertAction(1, new Location(2,9), true);
    }

    private static int count = 0;

    private void insertAction(int lmIndex, Location loc, boolean successful){
        String requestId = "req" + String.valueOf(count++);

        String deviceId = lightModels[lmIndex].id;
        localActionDB.insert(requestId, Action.newDefault(deviceId, loc,
                lightModels[lmIndex].location, successful));
    }

    @Test
    public void basicLocationTest() throws Exception {
        buildBasicGrid();
        executeSingleLocation(new Location(9.9,9), new String[]{"2","3","1","0"});
        executeSingleLocation(new Location(8.9, 9), new String[]{"2", "1", "3", "0"});
    }

    @Test
    public void singlePartitionSparseTest() throws Exception {
        buildBasicGrid();
        buildSinglePartitionSparse();
        executeSingleLocation(new Location(2,2), new String[]{"0","3"});
        executeSingleLocation(new Location(0,6), new String[]{"0"});
        executeSingleLocation(new Location(0,8), new String[]{"1"});
        executeSingleLocation(new Location(0,9), new String[]{"1"});

        //incorrect result, but expected, algorithm learns and corrects
        executeSingleLocation(new Location(0,7.1), new String[]{"0"});
        insertAction(0, new Location(0,7.1), false);
        executeSingleLocation(new Location(0,7.5), new String[]{"1"});
    }

    //Need to pass interaction history (global) to resolve method, just passed null to bypass compilation errors
    private void executeSingleLocation(Location loc, String[] expected){
        SpatialReq spatialReq = new SpatialReq(SpatialReq.Bound.CLOSEST,
                SpatialReq.Influence.AWARE, loc);
        SpatialReqOperator spatialReqOp = new SpatialReqOperator(spatialReq);

        checkSequence(spatialReqOp.resolve(candidates, null), expected);
    }

    private void checkSequence(List<DeviceModel> res, String[] desiredIdList){
        for(int i = 0; i < desiredIdList.length; i++) {
            Assert.assertEquals(desiredIdList[i], res.get(i).id);
        }
    }
}