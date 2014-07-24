package com.home.vkmusicloader;

import android.app.AlertDialog;

import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKCaptchaDialog;
import com.vk.sdk.VKScope;
import com.vk.sdk.VKSdk;
import com.vk.sdk.VKSdkListener;
import com.vk.sdk.VKUIHelper;
import com.vk.sdk.api.VKError;

public class CustomVKSdkListener extends VKSdkListener {
	
	private final ILoginActivity mLoginActivity;
	
	public CustomVKSdkListener(ILoginActivity loginActivity)
	{
		mLoginActivity = loginActivity;
	}
	
	public static final String[] sMyScope = new String[] {
        VKScope.FRIENDS,
        VKScope.WALL,
        VKScope.PHOTOS,
        VKScope.NOHTTPS,
        VKScope.AUDIO
};
	
	    @Override
        public void onCaptchaError(VKError captchaError) {
            new VKCaptchaDialog(captchaError).show();
        }

        @Override
        public void onTokenExpired(VKAccessToken expiredToken) {
            VKSdk.authorize(sMyScope);
        }

        @Override
        public void onAccessDenied(final VKError authorizationError) {
        	new AlertDialog.Builder(VKUIHelper.getTopActivity())
                    .setMessage(authorizationError.toString())
                    .show();
        }

        @Override
        public void onReceiveNewToken(VKAccessToken newToken) {
        	mLoginActivity.showMainActivity();
        }

        @Override
        public void onAcceptUserToken(VKAccessToken token) {
        	mLoginActivity.showMainActivity();
        }
}

