package com.footstrike.myapplication;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothProfile;
import android.content.Context;

import java.util.UUID;

@SuppressLint("MissingPermission")
public class GattHandler {

    //Services and characteristics
    static final UUID deviceServiceUuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    static final UUID deviceServiceCharacteristicUuid = UUID.fromString("00002101-0000-1000-8000-00805F9B34FB");
    static final String deviceMAC = "70:74:95:CF:0D:53";

    //BLE objects
    static BluetoothGattCharacteristic transChar;
    static BluetoothGatt mainGatt;

    public static ConnectionStatusChangeHandler connectionStatusChangeHandler;


    public static boolean isConnected = false;


    //Initialises connection
    public static void init(Context context) {


        new Thread(() ->
        {
            BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            BluetoothDevice device = bluetoothAdapter.getRemoteDevice(deviceMAC);
            device.connectGatt(context, false, gattCallback);

        }).start();


    }

    public static void write(int s){
        transChar.setValue(s,BluetoothGattCharacteristic.FORMAT_SINT32,0);
        mainGatt.writeCharacteristic(transChar);
    }

    private static final BluetoothGattCallback gattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);

            //Success
            if (newState == BluetoothProfile.STATE_CONNECTED && status == BluetoothGatt.GATT_SUCCESS) {
                isConnected = true;
                mainGatt = gatt;
                gatt.discoverServices();
            } else {
                isConnected = false;
                gatt.close();
            }

//            connectionStatusChangeHandler.onChange(isConnected);
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
        }


        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);

            //Setup
            transChar = gatt.getService(deviceServiceUuid).getCharacteristics().get(0);
            transChar.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT);

            //Setup notifications
            gatt.setCharacteristicNotification(transChar, true);
            BluetoothGattDescriptor desc = transChar.getDescriptors().get(0);
            desc.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);

            //Write to descriptor to seal the deal
            gatt.writeDescriptor(desc);

        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorWrite(gatt, descriptor, status);

        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);

        }


    };


    public interface ConnectionStatusChangeHandler {
        void onChange(boolean isConnected);
    }


}
