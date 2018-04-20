package com.example.eylee.bluetoothsetting;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends Activity {

    private String TAG = this.getClass().getSimpleName();
    private Context mContext;
    public static final int BLUETOOTH_REQUEST_CODE = 0x001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = getApplicationContext();

    }

    public void btnBluetoothSettingPage(View view){
//        Intent intentScan = new Intent(MainActivity.this  , MultiBluetoothSettingListActivity.class);
        Intent intentScan = new Intent(MainActivity.this  , MultiBluetoothSettingList2Activity.class);

        startActivityForResult(intentScan, BLUETOOTH_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case BLUETOOTH_REQUEST_CODE:{
                if(requestCode == Activity.RESULT_OK){
                    ToastUtils.toast(this, "RESULT_OK !!!");
                }else{
                    ToastUtils.toast(this, "RESULT_OK Not!!!");
                }
                break;
            }
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
