package ru.myemail;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    public static final String ACTION = "ru.myemail.EMAIL_ACTION";
    public static final String EMAIL = "email";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        registerReceiver(new ResultReceiver(), new IntentFilter(ACTION));

        Intent intent = new Intent(this, EmailService.class);
        intent.setAction(EmailService.EMAIL_ACTION);
        startService(intent);
    }

    private class ResultReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            ((TextView)findViewById(R.id.my_email)).setText(intent.getStringExtra(EMAIL));
        }
    }
}
