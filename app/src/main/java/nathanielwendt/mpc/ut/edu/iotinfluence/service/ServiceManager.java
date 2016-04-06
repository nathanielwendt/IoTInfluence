package nathanielwendt.mpc.ut.edu.iotinfluence.service;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.BluetoothLeScanner;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.widget.Toast;

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

    public ServiceManager(Context ctx) {
        this.ctx = ctx;

        mHandler = new Handler();

        // Use this check to determine whether BLE is supported on the device.  Then you can
        // selectively disable BLE-related features.
        if (!this.ctx.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(ctx, R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
        }
        // Initializes a Bluetooth adapter.  For API level 18 and above, get a reference to
        // BluetoothAdapter through BluetoothManager.
        final android.bluetooth.BluetoothManager bluetoothManager =
                (android.bluetooth.BluetoothManager) this.ctx.getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        // Checks if Bluetooth is supported on the device.
        if (mBluetoothAdapter == null) {
            Toast.makeText(this.ctx, R.string.error_bluetooth_not_supported, Toast.LENGTH_SHORT).show();
            return;
        }

        mBluetoothScanner = mBluetoothAdapter.getBluetoothLeScanner();
    }

    public void scan(final FindServiceCallback findCallback){
        findCallback.onService(ServiceLookup.lookup("63:3353:5363:2324"));
        findCallback.done();
//        final ScanCallback scanCallback = new ScanCallback() {
//            @Override
//            public void onScanResult(int callbackType, ScanResult result) {
//                super.onScanResult(callbackType, result);
//                Service service = ServiceLookup.lookup(result.getDevice().getAddress());
//                findCallback.onService(service);
//            }
//
//            @Override
//            public void onBatchScanResults(List<ScanResult> results) {
//                super.onBatchScanResults(results);
//
//            }
//
//            @Override
//            public void onScanFailed(int errorCode) {
//                super.onScanFailed(errorCode);
//            }
//        };
//
//        mBluetoothScanner.startScan(scanCallback);
//        mHandler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                mBluetoothScanner.stopScan(scanCallback);
//                findCallback.done();
//            }
//        }, SCAN_PERIOD);
    }

//    public List<Service> scan(){
//        List<ScanResult> scanResults = scan(SCAN_PERIOD);
//        List<Service> services = new ArrayList<Service>();
//        for(ScanResult result : scanResults){
//            Service service = ServiceLookup.lookup(result.getDevice().getAddress());
//            services.add(service);
//        }
//        return services;
//    }
//
//
//    private List<ScanResult> scan(int ms) {
//        final AtomicReference<List<ScanResult>> notifier = new AtomicReference<List<ScanResult>>();
//
//        final ScanCallback callback = new ScanCallback() {
//            @Override
//            public void onScanResult(int callbackType, ScanResult result) {
//                super.onScanResult(callbackType, result);
//                synchronized (notifier) {
//                    List<ScanResult> results = new ArrayList<>();
//                    results.add(result);
//                    notifier.set(results);
//                    notifier.notify();
//                }
//            }
//
//            @Override
//            public void onBatchScanResults(List<ScanResult> results) {
//                super.onBatchScanResults(results);
//                synchronized (notifier) {
//                    notifier.set(results);
//                    notifier.notify();
//                }
//            }
//
//            @Override
//            public void onScanFailed(int errorCode) {
//                super.onScanFailed(errorCode);
//            }
//        };
//
//        mBluetoothScanner.startScan(callback);
//        mHandler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                mBluetoothScanner.stopScan(callback);
//            }
//        }, ms);
//
//        synchronized (notifier) {
//            while (notifier.get() == null)
//                try {
//                    notifier.wait();
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//        }
//        return notifier.get();
//    }

    public interface FindServiceCallback {
        void onService(Service service);
        void done();
    }

}
