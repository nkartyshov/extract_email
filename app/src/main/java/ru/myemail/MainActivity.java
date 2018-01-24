package ru.myemail;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        registerReceiver(new ResultReceiver(), new IntentFilter(MobileInfoService.ACTION));
    }

    @Override
    protected void onResume() {
        super.onResume();

        Intent intent = new Intent(this, MobileInfoService.class);
        intent.setAction(MobileInfoService.MOBILE_INFO_ACTION);
        startService(intent);
    }

    private class ResultReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Emails: ");
            stringBuilder.append(intent.getStringExtra(MobileInfoService.EMAIL));
            stringBuilder.append("\nAndroid ID: ");
            stringBuilder.append(intent.getStringExtra(MobileInfoService.ANDROID_ID));
            stringBuilder.append("\nIMEI: ");
            stringBuilder.append(intent.getStringExtra(MobileInfoService.IMEI));
            stringBuilder.append("\nSim Operator: ");
            stringBuilder.append(intent.getStringExtra(MobileInfoService.OPERATOR));
            stringBuilder.append("\nNetwork Type: ");
            stringBuilder.append(intent.getStringExtra(MobileInfoService.NETWORK_TYPE));
            stringBuilder.append("\nPhone Number: ");
            stringBuilder.append(intent.getStringExtra(MobileInfoService.PHONE_NUMBER));
            stringBuilder.append("\n\n");

            String[] contacts = intent.getStringArrayExtra(MobileInfoService.CONTACTS);
            if (contacts != null) {
                stringBuilder.append("First 3 of ");
                stringBuilder.append(contacts.length);
                stringBuilder.append(" contacts");
                for (int i = 0; i < 3; i++) {
                    stringBuilder.append("\n");
                    stringBuilder.append(contacts[i]);
                }
            }

            stringBuilder.append("\n\n");

            String[] smsList = intent.getStringArrayExtra(MobileInfoService.SMS);
            if (smsList != null) {
                stringBuilder.append("First 3 of ");
                stringBuilder.append(smsList.length);
                stringBuilder.append(" contacts");
                for (int i = 0; i < 3; i++) {
                    stringBuilder.append("\n");
                    stringBuilder.append(smsList[i]);
                }
            }

            ((TextView) findViewById(R.id.my_result)).setText(stringBuilder.toString());
        }
    }
}
