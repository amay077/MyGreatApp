package com.greatap.webapi;

import net.arnx.jsonic.JSON;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.FacebookError;
import com.facebook.android.Facebook.DialogListener;

public class FacebookWebApiClient {
	static private final String TAG = "Client";
	static private final String KEY_EXPIRES_IN = "expires_in";
	static private final String KEY_ACCESS_TOKEN = "access_token";
	private Facebook facebook = new Facebook("109362469155828");
	private String[] permissions = new String[] { "email", "read_stream" };
	private String expiresIn = null;
	private String accessToken = null;
	private boolean isAuthorized = false;
	
	public interface AuthorizationCallback {
		void onComplete();
		void onCancel();
		void onError(Throwable error);
	}
	
	public interface PlaceCallback {
		void onReceive(SpotJsonMessage spot);
		void onError(Throwable error);
	}
	
	public void authorize(Activity activity, final AuthorizationCallback callback) {
		
		// 保存された Token が使えるなら使う。
		if (expiresIn != null) {
	        facebook.setAccessExpiresIn(expiresIn);
	        facebook.setAccessToken(accessToken);
	        if (facebook.isSessionValid()) {
	        	callback.onComplete();
	        	isAuthorized = true;
	        	return;
	        }
		}

    	expiresIn = null;
    	accessToken = null;
    	isAuthorized = false;

		// Token が使えないなら認証する。
        facebook.authorize(activity, permissions, new DialogListener() {
        	
            @Override
            public void onComplete(Bundle values) {
				Log.d(TAG, "onComplete");
	        	isAuthorized = true;
				callback.onComplete();
				
				// Token を保存しておく
				try {
					expiresIn = values.getString(KEY_EXPIRES_IN);
					accessToken = values.getString(KEY_ACCESS_TOKEN);
				} catch (Exception ignore) { } // 例外は使う必要ないので無視
            }

            @Override
            public void onFacebookError(FacebookError error) {
				Log.d(TAG, "onFacebookError:" + error.getMessage());
				callback.onError(error);
            }

            @Override
            public void onError(DialogError e) {
				Log.d(TAG, "onError");
				callback.onError(e);
            }

            @Override
            public void onCancel() {
				Log.d(TAG, "onCancel");
				callback.onCancel();
            }
        });
	}

	public void searchPlaces(String q, Location center, int distance, PlaceCallback callback) {
		if (!isAuthorized) {
			callback.onError(new UnAuthorizedException());
			return;
		}
		
		Bundle param = new Bundle();
		if (!TextUtils.isEmpty(q)) {
			param.putString("q", q);
		}
		param.putString("type", "place");
		param.putString("center", Location2String(center));
		param.putString("distance", String.valueOf(distance));
		try {
			String res = facebook.request("search", param);
			JSONObject jsonObj = new JSONObject(res);
			SpotJsonMessage[] spots = JSON.decode(jsonObj.getString("data"), SpotJsonMessage[].class);
			Log.d(TAG, "spots.size:" + String.valueOf(spots.length));
			for (SpotJsonMessage spot : spots) {
				callback.onReceive(spot);
			}
		} catch (Exception e) {
			Log.e(TAG, "failed.", e);
			callback.onError(e);
			return;
		}
	}
	
	private String Location2String(Location loc) {
		StringBuilder builder = new StringBuilder();
		builder.append(String.format("%.4f", loc.getLatitude()));
		builder.append(",");
		builder.append(String.format("%.4f", loc.getLongitude()));
		
		return builder.toString();
	}

	public void authorizeCallback(int requestCode, int resultCode, Intent data) {
		facebook.authorizeCallback(requestCode, resultCode, data);
	}
}
