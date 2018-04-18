package com.example.eylee.bluetoothsetting;

import android.content.Context;
import android.widget.Toast;

public class Utils {

    private static Toast toast;

    public static void toast(Context context, String message){
        if(toast == null){
            toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
        }else{
            toast.setText(message);
        }
        toast.show();
    }
}
