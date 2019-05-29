package pt.ipleiria.myapplication;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.os.Handler;
import android.os.ParcelUuid;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.IllegalFormatCodePointException;
import java.util.Random;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private BluetoothManager mBluetoothManager;
    private BluetoothLeAdvertiser mBluetoothLeAdvertiser;
    private boolean advertise_started = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mBluetoothManager = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
        BluetoothAdapter bluetoothAdapter = mBluetoothManager.getAdapter();



    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopAdvertise();
    }
    public void stopAdvertise() {
        BluetoothAdapter bluetoothAdapter = mBluetoothManager.getAdapter();
        if (bluetoothAdapter.isEnabled()) {
            mBluetoothLeAdvertiser.stopAdvertising(mAdvertiseCallback);
        }
    }

    public void btnAdvertise(View view) {
       // mBluetoothLeAdvertiser.
        if (!advertise_started){
            Random rand = new Random();
            int command = rand.nextInt(9);
            advertise(command);
            advertise_started = true;
            Log.i(TAG, "Staryted Advertise");
        } else {
            stopAdvertise();
            advertise_started = false;
            Log.i(TAG, "Stoped Advertise");
        }
       // Toast.makeText(this, "Advertise started", Toast.LENGTH_SHORT).show();
    }


    public void advertise(int command) {
       // BluetoothLeAdvertiser advertiser = BluetoothAdapter.getDefaultAdapter().getBluetoothLeAdvertiser();

        BluetoothAdapter bluetoothAdapter = mBluetoothManager.getAdapter();
        mBluetoothLeAdvertiser = bluetoothAdapter.getBluetoothLeAdvertiser();
        if (mBluetoothLeAdvertiser == null) {
            Log.w(TAG, "Failed to create advertiser");
            return;
        }

        AdvertiseSettings settings = new AdvertiseSettings.Builder()
                .setAdvertiseMode( AdvertiseSettings.ADVERTISE_MODE_BALANCED )
                .setTxPowerLevel( AdvertiseSettings.ADVERTISE_TX_POWER_MEDIUM )
                .setTimeout(0)
                .setConnectable( true )
                .build();

        ParcelUuid pUuid = new ParcelUuid( UUID.fromString( getString( R.string.ble_uuid ) ) );
        AdvertiseData data = new AdvertiseData.Builder()
                //.setIncludeDeviceName( true )
                //.addServiceUuid( pUuid )
                .setIncludeTxPowerLevel(false)
                .addServiceData( pUuid, "0".getBytes( Charset.forName( "UTF-8" ) ) )
                .build();


        mBluetoothLeAdvertiser.startAdvertising(settings, data, mAdvertiseCallback);

    }


    private AdvertiseCallback mAdvertiseCallback = new AdvertiseCallback() {
        @Override
        public void onStartSuccess(AdvertiseSettings settingsInEffect) {
            Log.i(TAG, "LE Advertise Started. "+ settingsInEffect.toString());

        }

        @Override
        public void onStartFailure(int errorCode) {
            if (errorCode == 1) {
                Log.w(TAG, "LE Exceeded limit service data!");
            } else if (errorCode == 3) {
                Log.w(TAG, "LE already advertised");
            } else{
                Log.w(TAG, "LE Advertise Failed: "+errorCode);
            }

        }
    };




}
