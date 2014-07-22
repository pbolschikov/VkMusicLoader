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
		
	public static final String[] sMyScope = new String[] {
        VKScope.FRIENDS,
        VKScope.WALL,
        VKScope.PHOTOS,
        VKScope.NOHTTPS
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
        	//new AlertDialog.Builder(VKUIHelper.getTopActivity())
            //        .setMessage(authorizationError.toString())
            //        .show();
        }

        @Override
        public void onReceiveNewToken(VKAccessToken newToken) {
            //startestActivity();
        }

        @Override
        public void onAcceptUserToken(VKAccessToken token) {
            //startTestActivity();
        }
}

