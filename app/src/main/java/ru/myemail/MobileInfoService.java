package ru.myemail;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.provider.Telephony;
import android.support.annotation.Nullable;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

public class MobileInfoService extends IntentService {
    public static final String ACTION = "ru.myemail.SERVICE_ACTION";

    public static final String MOBILE_INFO_ACTION = "action_get_mobile_info";
    public static final String CONTACTS_ACTION = "action_get_contacts";
    public static final String SMS_ACTION = "action_get_sms";

    public static final String EMAIL = "email";
    public static final String IMEI = "IMEI";
    public static final String OPERATOR = "OPERATOR";
    public static final String ANDROID_ID = "AndroidId";
    public static final String PHONE_NUMBER = "PHONE_NUMBER";
    public static final String NETWORK_TYPE = "NETWORK_TYPE";
    public static final String FIRST_THREE_CONTACTS = "FIRST_THREE_CONTACTS";
    public static final String LAST_THREE_SMS = "LAST_THREE_SMS";
    public static final String CONTACTS_SIZE = "CONTACTS_SIZE";
    public static final String SMS_SIZE = "SMS_SIZE";

    public MobileInfoService() {
        super("EmailService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent != null) {
            String action = intent.getAction();
            if (MOBILE_INFO_ACTION.equals(action)) {
                List<String> strings = getGmails();
                Intent resultIntent = new Intent(ACTION);
                resultIntent.putExtra(EMAIL, TextUtils.join(", ", strings));
                resultIntent.putExtra(IMEI, getImei());
                resultIntent.putExtra(OPERATOR, getSimOperator());
                resultIntent.putExtra(ANDROID_ID, getAndroidId());
                resultIntent.putExtra(PHONE_NUMBER, getPhoneNumber());
                resultIntent.putExtra(NETWORK_TYPE, getNetworkType());

                String[] contacts = getContacts();
                resultIntent.putExtra(CONTACTS_SIZE, contacts.length);
                String[] threeContact = new String[3];
                System.arraycopy(contacts, 0, threeContact, 0, 3);
                resultIntent.putExtra(FIRST_THREE_CONTACTS, threeContact);

                String[] sms = getSms();
                resultIntent.putExtra(SMS_SIZE, sms.length);
                String[] threeSms = new String[3];
                System.arraycopy(sms, 0, threeSms, 0, 3);
                resultIntent.putExtra(LAST_THREE_SMS, threeSms);
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

    private String[] getContacts() {
        Cursor cursor = getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        try {
            ArrayList<String> list = new ArrayList<>();
            while (cursor.moveToNext()) {
                list.add(cursor.getString(cursor.getColumnIndex(
                        ContactsContract.Contacts.DISPLAY_NAME)));
            }
            return list.toArray(new String[]{});
        } finally {
            cursor.close();
        }
    }

    private String[] getSms() {
        Cursor cursor = getContentResolver().query(Telephony.Sms.CONTENT_URI, null, null, null, Telephony.Sms.DEFAULT_SORT_ORDER);
        try {
            ArrayList<String> list = new ArrayList<>();
            while (cursor.moveToNext()) {
                String address = cursor.getString(cursor.getColumnIndex(Telephony.Sms.ADDRESS));
                String body = cursor.getString(cursor.getColumnIndex(Telephony.Sms.BODY));
                list.add(address + "\n " + body);
            }
            return list.toArray(new String[]{});
        } finally {
            cursor.close();
        }
    }
}
