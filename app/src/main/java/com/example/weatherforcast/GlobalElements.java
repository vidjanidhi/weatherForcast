package com.example.weatherforcast;

import android.app.AlertDialog;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class GlobalElements extends Application {
    public static boolean isConnectingToInternet(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivity != null) {
            NetworkInfo info = connectivity.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

            if (info != null) {
                if (info.isConnected()) {
                    return true;
                } else {
                    NetworkInfo info1 = connectivity.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
                    if (info1.isConnected()) {
                        return true;
                    } else {
                        return false;
                    }
                }
            }
        }
        return false;
    }

    public static void showDialog(Context context) {
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        // Set Dialog Title
        alertDialog.setTitle("Internet Connection");
        // Set Dialog Message
        alertDialog.setMessage("Please check your internet connection ..");
        // Set OK Button
        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        // Show Alert Message
        alertDialog.show();
    }

}
