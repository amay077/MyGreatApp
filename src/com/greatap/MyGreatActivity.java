package com.greatap;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.facebook.android.*;
import com.facebook.android.Facebook.*;

public class MyGreatActivity extends Activity {

    protected static final String TAG = "MyGreatActivity";
	Facebook facebook = new Facebook("109362469155828");

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        facebook.authorize(this, new String[] { "email", "read_stream" },
        		new DialogListener() {
        	
            @Override
            public void onComplete(Bundle values) {
				Log.d(TAG, "onComplete");
            	try {
//            		Bundle param = new Bundle();
//            		param.putString("q", "ローソン");
//            		param.putString("type", "place");
//            		param.putString("center", "34.7257,137.4387");
//            		param.putString("distance", "2000");
//            		// =
//					String res = facebook.request("search", param);

					Bundle param = new Bundle();
					param.putString("sk", "deals");
					String res = facebook.request("189674171081087", param);
					Log.d(TAG, res);
				} catch (Exception e) {
					Log.e(TAG, "failed.", e);
				}   
            }

            @Override
            public void onFacebookError(FacebookError error) {
				Log.d(TAG, "onFacebookError:" + error.getMessage());
            }

            @Override
            public void onError(DialogError e) {
				Log.d(TAG, "onError");
            }

            @Override
            public void onCancel() {
				Log.d(TAG, "onCancel");
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        facebook.authorizeCallback(requestCode, resultCode, data);
    }
}
