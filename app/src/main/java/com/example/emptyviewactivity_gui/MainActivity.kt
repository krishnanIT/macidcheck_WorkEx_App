package com.example.emptyviewactivity_gui

import android.Manifest
import android.Manifest.permission
import android.Manifest.permission.BLUETOOTH_SCAN
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothAdapter
import android.bluetooth.le.AdvertiseCallback
import android.bluetooth.le.AdvertiseData
import android.bluetooth.le.AdvertiseSettings
import android.content.Context
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.pm.PackageManager
import androidx.annotation.RequiresPermission
import androidx.core.app.ActivityCompat
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothProfile
import android.bluetooth.le.ScanSettings
import android.os.Looper
import androidx.core.os.postDelayed
import android.os.Handler
import android.util.Log
import java.util.UUID
import android.os.ParcelUuid


//private val MainActivity.bluetoothLeScanner: Any
//private val MainActivity.bluetoothLeScanner: Any

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets }

        Toast.makeText(this, "Activity is starting!", Toast.LENGTH_SHORT).show()

        //declear the variable's
        val scanning_button = findViewById<Button>(R.id.scanning_button)
        val macid = findViewById<TextView>(R.id.macid_view)
        val serialnumber = findViewById<TextView>(R.id.serialnumber_view)
        val acceptbutton = findViewById<Button>(R.id.accept_button)
        val rejectbutton = findViewById<Button>(R.id.reject_button)


        //initilize bluetooth adapter and scanner
        val bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        val bluetoothAdapter = bluetoothManager.adapter


        //write app code. scanning button code
        scanning_button.setOnClickListener @androidx.annotation.RequiresPermission(android.Manifest.permission.BLUETOOTH_SCAN) {
            //bluetooth support or not checking code
            val bluetoothManager_check: BluetoothManager = getSystemService(BluetoothManager::class.java)
            val bluetoothAdapter: BluetoothAdapter? = bluetoothManager_check.getAdapter()
            if (bluetoothAdapter == null) {
                // Device doesn't support Bluetooth
                Toast.makeText(this,"Bluetooth NOT Support... :(",Toast.LENGTH_LONG).show()
            }

            if (bluetoothAdapter?.isEnabled == false) {
                Toast.makeText(this,"ENABLE THE BLUETOOTH...  :(",Toast.LENGTH_LONG).show()
                
            }
            
            


            




        //bluetooth button ending
        }





    }



//Main activity coding start
// Define a common UUID for the service you are advertising (Example: Eddystone/Beacon)
private val SERVICE_UUID: UUID = UUID.fromString("0000FEAA-0000-1000-8000-00805F9B34FB")

    class BleAdvertiser(context: Context) {

        private val TAG = "BleAdvertiser"

        // Get the BluetoothAdapter and Advertiser
        private val bluetoothAdapter: BluetoothAdapter? =
            (context.getSystemService(Context.BLUETOOTH_SERVICE) as? android.bluetooth.BluetoothManager)?.adapter

        // The main object used to start and stop BLE advertising
        private val advertiser = bluetoothAdapter?.bluetoothLeAdvertiser

        // 1. Define the AdvertiseCallback to monitor the advertising process
        private val advertiseCallback = object : AdvertiseCallback() {
            override fun onStartSuccess(settingsInEffect: AdvertiseSettings?) {
                Log.i(TAG, "BLE Advertising started successfully!")
            }

            override fun onStartFailure(errorCode: Int) {
                // Check errorCode constants (e.g., ADVERTISE_FAILED_ALREADY_STARTED, ADVERTISE_FAILED_INTERNAL_ERROR)
                Log.e(TAG, "BLE Advertising failed with error code: $errorCode")
            }
        }

        /**
         * Starts the BLE advertising process.
         */
        fun startAdvertising() {
            // Basic checks: is Bluetooth available and is the advertiser supported?
            if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled || advertiser == null) {
                Log.e(TAG, "Bluetooth not available, not enabled, or Advertiser not supported on this device.")
                return
            }

            // --- 2. Build AdvertiseSettings (How to advertise) ---
            val settings = AdvertiseSettings.Builder()
                // High frequency and low latency for quick discovery
                .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_LATENCY)
                // Allows other devices to form a GATT connection
                .setConnectable(true)
                // 0 means advertising continues until stopAdvertising() is called
                .setTimeout(0)
                // Set transmit power level
                .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_MEDIUM)
                .build()

            // --- 3. Build AdvertiseData (What to advertise) ---
            val SERVICE_UUID = null
            val data = AdvertiseData.Builder()
                // Include the device's friendly Bluetooth name in the advertisement packet
                .setIncludeDeviceName(true)
                // Add a Service UUID to let scanners know what kind of service you offer
                .addServiceUuid(ParcelUuid(SERVICE_UUID))
                // Add manufacturer-specific data (e.g., beacon payload)
                .addManufacturerData(
                    0x004C, // Example: Apple company ID
                    byteArrayOf(0x02, 0x15, 0x01, 0x02, 0x03, 0x04) // Example custom payload
                )
                .build()

            // --- 4. Call the startAdvertising method ---
            // This requires the BLUETOOTH_ADVERTISE permission
            advertiser.startAdvertising(settings, data, advertiseCallback)
        }

        /**
         * Stops the BLE advertising process.
         */
        fun stopAdvertising() {
            // This requires the BLUETOOTH_ADVERTISE permission
            advertiser?.stopAdvertising(advertiseCallback)
            Log.i(TAG, "BLE Advertising stopped.")
        }
    }
    //main activity ending

}



