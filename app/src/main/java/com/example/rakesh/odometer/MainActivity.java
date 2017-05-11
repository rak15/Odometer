package com.example.rakesh.odometer;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private OdometerService odometerService;
    private boolean bound=false;            //to store whether activity is bound to service or not

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent=new Intent(this,OdometerService.class);   //intent directed to the OdometerService
        bindService(intent,connection, Context.BIND_AUTO_CREATE);
        watchMileage();         // to display the distance in textView
    }
    private ServiceConnection connection=new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            OdometerService.OdometerBinder odometerBinder=(OdometerService.OdometerBinder)binder;
            odometerService= odometerBinder.getBinder();     //Cast the Binder to an OdometerBinder, then use to get a reference to the OdometerService.
            bound=true;   //when service is connected, st bound = true
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            bound=false;     ////when service is disconnected, st bound = false
        }
    };

    @Override
    protected void onStart() {      //to bind activity to service on activity start
        super.onStart();
        //Intent intent=new Intent(this,OdometerService.class);   //intent directed to the OdometerService
       // bindService(intent,connection, Context.BIND_AUTO_CREATE);   //to bind activity to the service
    }

    @Override
    protected void onStop() {       ////to unbind activity from service on activity stop
        super.onStop();
        if (bound){
            unbindService(connection);
            bound=false;
        }
    }
    private void watchMileage() {
        final TextView distanceView = (TextView)findViewById(R.id.textView);
        final Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                double distance = 0.0;
                if (odometerService != null) {
                    distance = odometerService.getDistanceinmeter();    //called method is in OdometerService class
                }
                String distanceStr = String.format("%1$,.2f meters", distance);
                distanceView.setText(distanceStr);
                handler.postDelayed(this, 1000);  //to update view every 1 second
            }
        });
    }
}
