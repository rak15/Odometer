package com.example.rakesh.odometer;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;

public class OdometerService extends Service {  //When you create a bound service, you need to provide a Binder implementation.
    private static double distanceinmeter;
    private static Location lastLocation=null;
    public class OdometerBinder extends Binder{   // here is the binder implementation
        OdometerService getBinder(){   //activity will use this method to get instance of this service
            return OdometerService.this;  //returns the instance of this service
        }
    }
    private final IBinder iBinder=new OdometerBinder();  //upper class uses its instance reference variable to hold inner class's object
    public OdometerService() {  //constructor
    }

    @Override
    public IBinder onBind(Intent intent) {    //firstly onBind method will be called
        return iBinder;  //The onBind() method returns an OdometerBinder object from which activity will get getBinder method & use it to get object of this service
    }

    @Override
    public void onCreate() { //this service needs to get location the whole time so we'll put location listener in service's onCreate method
        super.onCreate();
        LocationListener listener=new LocationListener() {  //creating location listener to get location from system
            @Override
            public void onLocationChanged(Location location) {  //here location argument is the current location provided by system
                if (lastLocation==null){
                    lastLocation=location;
                }
                distanceinmeter+=location.distanceTo(lastLocation);
                lastLocation=location;
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {   //get called if GPS is enabled

            }

            @Override
            public void onProviderDisabled(String provider) {  //get called if GPS is disabled

            }
        };
        LocationManager locmanager=(LocationManager)getSystemService(Context.LOCATION_SERVICE);  //manager to access system's location service
        try {
            locmanager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, listener); //registering listener with location service
        }catch (SecurityException e){      //try block to handle condition where user doesn't give location permission
            e.printStackTrace();
        }
    }
    public double getDistanceinmeter(){   //activity will call this method to get travelled distance
        return distanceinmeter;
    }
}
