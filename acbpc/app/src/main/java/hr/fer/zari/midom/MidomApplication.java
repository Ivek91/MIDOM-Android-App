package hr.fer.zari.midom;

import android.app.Application;
import android.util.Log;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;

public class MidomApplication extends Application {

    private CookieManager cookieManager;

    @Override
    public void onCreate()
    {
        super.onCreate();

        cookieManager = new CookieManager();
        cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
        CookieHandler.setDefault(cookieManager);

        Log.d(this.getClass().getName(), "onCreate");
    }

    public CookieManager getCookieManager() {
        return cookieManager;
    }
}
