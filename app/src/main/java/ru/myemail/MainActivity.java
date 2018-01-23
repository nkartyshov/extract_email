package ru.myemail;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    public static final String ACTION = "ru.myemail.SERVICE_ACTION";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        registerReceiver(new ResultReceiver(), new IntentFilter(ACTION));
    }

    @Override
    protected void onResume() {
        super.onResume();

        Intent intent = new Intent(this, MobileInfoService.class);
        intent.setAction(MobileInfoService.ACTION);
        startService(intent);
    }

    private class ResultReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String stringBuilder = "Emails:" +
                    intent.getStringExtra(MobileInfoService.EMAIL) +
                    "\nAndroid Id:" +
                    intent.getStringExtra(MobileInfoService.ANDROID_ID) +
                    "\nIMEI: " +
                    intent.getStringExtra(MobileInfoService.IMEI) +
                    "\nSim Operator:" +
                    intent.getStringExtra(MobileInfoService.OPERATOR) +
                    "\nNetwork Type: " +
                    intent.getStringExtra(MobileInfoService.NETWORK_TYPE) +
                    "\nPhone Number:"
                    + intent.getStringExtra(MobileInfoService.PHONE_NUMBER) +
                    "\nApp Size: "
                    + intent.getIntExtra(MobileInfoService.APP_SIZE, 0)
                    + "\nContact Size: " +
                    intent.getIntExtra(MobileInfoService.CONTACT_SIZE, 0);


            ((TextView) findViewById(R.id.my_result)).setText(stringBuilder);
        }
    }
}
