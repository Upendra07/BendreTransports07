package com.example.bendrenagarasarige.Service;

import com.example.bendrenagarasarige.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.Manifest;
import android.os.Build;
import android.os.IBinder;
//import android.support.v4.app.NotificationCompat;
//import android.support.v4.content.ContextCompat;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

public class TrackerService extends Service {


    private static final String TAG = TrackerService.class.getSimpleName();

    String bus,towards;

    @Override
    public IBinder onBind(Intent intent) {return null;}

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onCreate() {
        super.onCreate();

       SharedPreferences sp = getSharedPreferences("Tracker",Context.MODE_PRIVATE);
       bus = sp.getString("bus","");
       towards = sp.getString("towards","");

        requestLocationUpdates();
        buildNotification();
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private void buildNotification() {

        String stop = "stop";
        registerReceiver(stopReceiver, new IntentFilter(stop));
        PendingIntent broadcastIntent = PendingIntent.getBroadcast(
                this, 0, new Intent(stop), PendingIntent.FLAG_UPDATE_CURRENT);
        // Create the persistent notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this,createNotificationChannel("my_id","My_cool_channel"))
                .setContentTitle(getString(R.string.bendre_transports))
                .setContentText(getString(R.string.notification_text))
                .setOngoing(true)
                .setContentIntent(broadcastIntent)
                .setSmallIcon(R.drawable.ic_tracker);
        startForeground(1, builder.build());

    }


    @RequiresApi(Build.VERSION_CODES.O)
    private String createNotificationChannel(String channelId ,String channelName){
        NotificationChannel chan = new NotificationChannel(channelId,
                channelName, NotificationManager.IMPORTANCE_NONE);
        chan.setLightColor(Color.BLUE);
        chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert manager != null;
        manager.createNotificationChannel(chan);
        return channelId;
    }


    protected BroadcastReceiver stopReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            DatabaseReference ref = FirebaseDatabase.getInstance().getReference(towards+"/"+bus);
            ref.removeValue().addOnCompleteListener(
                    task -> {

                        if (task.isSuccessful()){

                            stopSelf();
                            unregisterReceiver(stopReceiver);
                            int id = android.os.Process.myPid();
                            android.os.Process.killProcess(id);

                        }

                    }
            );

            Log.d(TAG, "received stop broadcast");
            // Stop the service when the notification is tapped
            Log.d(TAG,towards);
            Log.d(TAG,bus);

        }
    };


    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void requestLocationUpdates() {
        LocationRequest request = new LocationRequest();
        request.setInterval(10000);
        request.setFastestInterval(5000);
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        FusedLocationProviderClient client = LocationServices.getFusedLocationProviderClient(this);


        int permission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        if (permission == PackageManager.PERMISSION_GRANTED) {
            // Request location updates and when an update is
            // received, store the location in Firebase
            client.requestLocationUpdates(request, new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference(towards + "/" + bus);
                    Location location = locationResult.getLastLocation();

                    if (location != null) {

                        Log.d(TAG,towards);
                        Log.d(TAG,bus);
                        Log.d(TAG, "location update " + location);
                        ref.setValue(location);
                    }
                }
            }, null);
        }
    }

}
