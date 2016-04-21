package nathanielwendt.mpc.ut.edu.iotinfluence;

import android.app.Activity;

import org.junit.Test;
import org.tc33.jheatchart.HeatChart;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import nathanielwendt.mpc.ut.edu.iotinfluence.device.DeviceUnavailableException;
import nathanielwendt.mpc.ut.edu.iotinfluence.device.Light;
import nathanielwendt.mpc.ut.edu.iotinfluence.devicereqs.DeviceReq;
import nathanielwendt.mpc.ut.edu.iotinfluence.devicereqs.SpatialReq;
import nathanielwendt.mpc.ut.edu.iotinfluence.misc.Location;
import nathanielwendt.mpc.ut.edu.iotinfluence.models.LightModel;
import nathanielwendt.mpc.ut.edu.iotinfluence.service.Service;
import nathanielwendt.mpc.ut.edu.iotinfluence.util.EvaluationAction;
import nathanielwendt.mpc.ut.edu.iotinfluence.util.Geometry;
import nathanielwendt.mpc.ut.edu.iotinfluence.util.Grid;
import nathanielwendt.mpc.ut.edu.iotinfluence.util.SampleResult;
import nathanielwendt.mpc.ut.edu.iotinfluence.util.TestService;
import nathanielwendt.mpc.ut.edu.iotinfluence.util.TrainingAction;

/**
 * Created by nathanielwendt on 4/20/16.
 */
public class AdvancedEvaluationRunner {


    public static class StudioGrid extends Grid {
        public StudioGrid(double step) {
            this.step = step;
            this.width = 20;
            this.height = 14;

            Location[] lightLocs = new Location[]{new Location(2, 2), new Location(5, 11),
                    new Location(11.5, 11), new Location(15, 13),
                    new Location(12, 7), new Location(19, 7)};

            Service testService = new TestService();
            int count = 0;
            for (Location loc : lightLocs) {
                LightModel dev = new LightModel();
                dev.id = String.valueOf(count++);
                dev.service = testService;
                dev.location = loc;
                devices.add(dev);
            }

            partitions.add(new Geometry.LineSegment(10, 8, 10, 14));
            partitions.add(new Geometry.LineSegment(10, 8, 20, 8));
            partitions.add(new Geometry.LineSegment(13, 8, 13, 14));
        }
    }

    @Test
    public void testBasic(){

        final Warble warble = new Warble(new Activity());

        StudioGrid studioGrid = new StudioGrid(1);

        List<SampleResult> trainErrors = studioGrid.train(new Location[]{}, false, new TrainingAction() {
            @Override
            public SampleResult act(double x, double y) {
                SpatialReq spatialReq = new SpatialReq(SpatialReq.Bound.CLOSEST,
                        SpatialReq.Influence.AWARE, new Location(x,y));
                List<DeviceReq> reqs = new ArrayList<>();
                reqs.add(spatialReq);
                List<Light> lights = warble.retrieve(Light.class, reqs, 1);
                Light warbleLight = lights.get(0);
                try {
                    warbleLight.on();
                } catch (DeviceUnavailableException e) {
                    e.printStackTrace();
                }
                return new SampleResult(warbleLight.deviceId());
            }

            @Override
            public SampleResult onError(SampleResult expected, SampleResult actual) {
                return new SampleResult("0");
            }
        });

        SampleResult[][] evalResults = studioGrid.evaluate(new EvaluationAction() {
            @Override
            public SampleResult act(double x, double y) {
                SpatialReq spatialReq = new SpatialReq(SpatialReq.Bound.CLOSEST,
                        SpatialReq.Influence.AWARE, new Location(x,y));
                List<DeviceReq> reqs = new ArrayList<>();
                reqs.add(spatialReq);
                List<Light> lights = warble.retrieve(Light.class, reqs, 1);
                Light warbleLight = lights.get(0);
                try {
                    warbleLight.on();
                } catch (DeviceUnavailableException e) {
                    e.printStackTrace();
                }
                return new SampleResult(warbleLight.deviceId());
            }

            @Override
            public SampleResult onError(SampleResult expected, SampleResult actual){
                return new SampleResult("0");
            }

            @Override
            public SampleResult onSuccess(SampleResult actual){
                return new SampleResult("1");
            }
        });

//        // Create some dummy data.
//        double[][] data = new double[][]{{3,2,3,4,5,6},
//                {2,3,4,5,6,7},
//                {3,4,5,6,7,6},
//                {4,5,6,7,6,5}};



        // Step 1: Create our heat map chart using our data.
        HeatChart map = new HeatChart(SampleResult.toDoubleArr(evalResults));

        // Step 2: Customise the chart.
        map.setTitle("Saweeet");
        map.setXAxisLabel("X Axis");
        map.setYAxisLabel("Y Axis");


        // Step 3: Output the chart to a file.
        try {
            map.saveToFile(new File("java-heat-chart.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}