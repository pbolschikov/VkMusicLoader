package com.home.vkmusicloader;

import roboguice.activity.RoboFragmentActivity;
import roboguice.inject.ContentView;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.vk.sdk.VKSdk;
import com.vk.sdk.VKSdkListener;
import com.vk.sdk.VKUIHelper;
import com.vk.sdk.util.VKUtil;


@ContentView(R.layout.activity_login)
public class LoginActivity extends RoboFragmentActivity implements ILoginActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        VKUIHelper.onCreate(this);
        VKSdk.initialize(sdkListener, getString(R.string.vk_app_id));
        if (VKSdk.wakeUpSession()) {
        	showMainActivity();
            return;
        }

        String[] fingerprint = VKUtil.getCertificateFingerprint(this, this.getPackageName());
        Log.d("Fingerprint", fingerprint[0]);
    }
    public void showLogout() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, new LogoutFragment())
                .commit();
    }
    public void showLogin() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, new LoginFragment())
                .commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        VKUIHelper.onResume(this);
        if (VKSdk.isLoggedIn()) {
            showLogout();
        } else {
            showLogin();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        VKUIHelper.onDestroy(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        VKUIHelper.onActivityResult(this, requestCode, resultCode, data);
    }

    private final VKSdkListener sdkListener = new CustomVKSdkListener(this);
    
    public void showMainActivity() {
    	startActivity(new Intent(this, MainActivity.class));
    }
}
