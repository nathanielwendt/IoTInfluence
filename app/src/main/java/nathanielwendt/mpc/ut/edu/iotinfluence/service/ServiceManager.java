package nathanielwendt.mpc.ut.edu.iotinfluence.service;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanRecord;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.ParcelUuid;
import android.util.Log;
import android.widget.Toast;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import nathanielwendt.mpc.ut.edu.iotinfluence.R;

/**
 * Created by nathanielwendt on 3/25/16.
 */
public class ServiceManager {
    private BluetoothLeScanner mBluetoothScanner;
    private BluetoothAdapter mBluetoothAdapter;
    private Handler mHandler;
    private Context ctx;
    private static final int SCAN_PERIOD = 5000;
    private ScanCallback scanCallback;

    final List<String> serviceIds = new ArrayList<>();

    public ServiceManager(Context ctx) {
        this.ctx = ctx;

        //setup scanning
        mHandler = new Handler();
        if (!this.ctx.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(ctx, R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
        }
        final android.bluetooth.BluetoothManager bluetoothManager =
                (android.bluetooth.BluetoothManager) this.ctx.getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        if (mBluetoothAdapter == null) {
            Toast.makeText(this.ctx, R.string.error_bluetooth_not_supported, Toast.LENGTH_SHORT).show();
            return;
        }
        mBluetoothScanner = mBluetoothAdapter.getBluetoothLeScanner();
    }

    private String extractCommand(ScanResult result){
        Map<ParcelUuid, byte[]> res = result.getScanRecord().getServiceData();
        for(Map.Entry<ParcelUuid, byte[]> item : res.entrySet()){
            return new String(item.getValue(), StandardCharsets.UTF_8);
        }
        return "";
    }

    public void scan(final FindServiceCallback findCallback){
        //findCallback.onService(ServiceLookup.lookup("63:3353:5363:2324"));
        //findCallback.done();

        scanCallback = new ScanCallback() {
            @Override
            public void onScanResult(int callbackType, ScanResult result) {
                super.onScanResult(callbackType, result);
                //System.out.println(result.toString());

                //check address from beacon first
                String serviceId = result.getDevice().getAddress();
                Service service = ServiceLookup.lookup(serviceId);

                //check id embedded in beacon payload
                if(service == null){
                    ScanRecord scanRecord = result.getScanRecord();
                    List<ParcelUuid> uuids = scanRecord.getServiceUuids();
                    if(uuids != null){
                        serviceId = uuids.get(0).toString();
                        service = ServiceLookup.lookup(serviceId);
                    }
                }

                if(service != null){
                    if(!serviceIds.contains(serviceId)){
                        Log.d("SERVICE", "found service: " + serviceId);
                        serviceIds.add(serviceId);
                        findCallback.onService(service);
                    }
                }
            }

            @Override
            public void onBatchScanResults(List<ScanResult> results) {
                super.onBatchScanResults(results);

            }

            @Override
            public void onScanFailed(int errorCode) {
                super.onScanFailed(errorCode);
            }
        };

        //TODO: add scan filters to only find appropriate devices
        mBluetoothScanner.startScan(scanCallback);

        mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Log.d("SERVICE", "stopping ble scanning");
                    mBluetoothScanner.stopScan(scanCallback);
                    findCallback.done();
                }
        }, SCAN_PERIOD);
    }

    public interface FindServiceCallback {
        void onService(Service service);
        void done();
    }

}
