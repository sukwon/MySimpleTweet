package com.codepath.apps.restclienttemplate;

import android.app.Activity;
import android.widget.Toast;

import java.io.IOException;

public class Utils {
    private static boolean isOnline() {
        Runtime runtime = Runtime.getRuntime();
        try {
            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int     exitValue = ipProcess.waitFor();
            return (exitValue == 0);
        } catch (IOException e)          { e.printStackTrace(); }
        catch (InterruptedException e) { e.printStackTrace(); }
        return false;
    }

    public static void handleNetworkAvailability(Activity activity) {
        if (isOnline() == false) {
            Toast.makeText(activity, "Network is unavailable", Toast.LENGTH_SHORT).show();
        }
    }
}
