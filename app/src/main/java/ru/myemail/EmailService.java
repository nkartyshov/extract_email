package ru.myemail;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class EmailService extends IntentService {
    public static final String EMAIL_ACTION = "action_get_gmail";

    public EmailService() {
        super("EmailService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent != null) {
            String action = intent.getAction();
            if (EMAIL_ACTION.equals(action)) {
                List<String> strings = getGmail(getBaseContext());
                Intent resultIntent = new Intent(MainActivity.ACTION);
                resultIntent.putExtra(MainActivity.EMAIL, TextUtils.join(", ", strings));
                sendBroadcast(resultIntent);
            }
        }
    }

    private List<String> getGmail(Context context) {
        List<String> mGmail = new ArrayList();
        try {
            Class loadClass = context.getClassLoader().loadClass("android.accounts.AccountManager");
            Object invoke = loadClass.getDeclaredMethod("get", new Class[]{Context.class}).invoke(null, new Object[]{context});
            Object[] objArr = (Object[]) loadClass.getDeclaredMethod("getAccountsByType", new Class[]{String.class}).invoke(invoke, new Object[]{"com.google"});
            for (int i = 0; i < objArr.length; i++) {
                String str = (String) objArr[i].getClass().getDeclaredField("name").get(objArr[i]);
                if (!(TextUtils.isEmpty(str) || mGmail.contains(str))) {
                    mGmail.add(str);
                }
            }
        } catch (Exception e) {
            Log.e("Test", e.getMessage(), e);
        }

        return mGmail;
    }
}
