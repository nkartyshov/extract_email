package ru.myemail;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

public class MobileInfoService extends IntentService {
    public static final String ACTION = "action_get__mobile_info";

    public static final String EMAIL = "email";
    public static final String IMEI = "IMEI";
    public static final String OPERATOR = "OPERATOR";
    public static final String ANDROID_ID = "AndroidId";
    public static final String PHONE_NUMBER = "PHONE_NUMBER";
    public static final String NETWORK_TYPE = "NETWORK_TYPE";
    public static final String APP_SIZE = "APP_SIZE";
    public static final String CONTACT_SIZE = "CONTACT_SIZE";

    public MobileInfoService() {
        super("EmailService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent != null) {
            String action = intent.getAction();
            if (ACTION.equals(action)) {
                List<String> strings = getGmails();
                Intent resultIntent = new Intent(MainActivity.ACTION);
                resultIntent.putExtra(EMAIL, TextUtils.join(", ", strings));
                resultIntent.putExtra(IMEI, getImei());
                resultIntent.putExtra(OPERATOR, getSimOperator());
                resultIntent.putExtra(ANDROID_ID, getAndroidId());
                resultIntent.putExtra(PHONE_NUMBER, getPhoneNumber());
                resultIntent.putExtra(NETWORK_TYPE, getNetworkType());
                resultIntent.putExtra(APP_SIZE, getInstalledAppsSize());
                resultIntent.putExtra(CONTACT_SIZE, getContactSize());
                sendBroadcast(resultIntent);
            }
        }
    }

    private String getPhoneNumber() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP_MR1) {
            SubscriptionManager subscriptionManager = (SubscriptionManager) getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE);

            List<SubscriptionInfo> list = subscriptionManager.getActiveSubscriptionInfoList();
            if (list != null) {
                List<String> results = new ArrayList<>();
                for (SubscriptionInfo subscriptionInfo : list) {
                    results.add(subscriptionInfo.getNumber());
                }

                return TextUtils.join(", ", results);
            }
        }

        return ((TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE)).getLine1Number();
    }

    private String getSimOperator() {
        return ((TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE)).getSimOperator();
    }

    private String getImei() {
        return ((TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
    }

    private String getAndroidId() {
        return Settings.Secure.getString(getContentResolver(), "android_id");
    }

    private List<String> getGmails() {
        List<String> mGmail = new ArrayList<>();
        AccountManager accountManager = AccountManager.get(getBaseContext());
        Account[] accounts = accountManager.getAccountsByType("com.google");
        for (Account account : accounts) {
            String name = account.name;
            if (!(TextUtils.isEmpty(name) || mGmail.contains(name))) {
                mGmail.add(name);
            }
        }

        return mGmail;
    }

    private String getNetworkType() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
                return activeNetworkInfo.getTypeName();
            }
        }

        return "";
    }

    private int getInstalledAppsSize() {
        List<ApplicationInfo> apps = getPackageManager().getInstalledApplications(PackageManager.GET_META_DATA);
        if (apps != null && !apps.isEmpty()) {
            return apps.size();
        }

        return 0;
    }

    private int getContactSize() {
        Cursor query = getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        try {
            return query.getCount();
        } finally {
            query.close();
        }
    }
}
