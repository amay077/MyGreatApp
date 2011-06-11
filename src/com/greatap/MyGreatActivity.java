package com.greatap;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.greatap.webapi.FacebookWebApiClient;
import com.greatap.webapi.FacebookWebApiClient.AuthorizationCallback;
import com.greatap.webapi.FacebookWebApiClient.PlaceCallback;
import com.greatap.webapi.SpotJsonMessage;

public class MyGreatActivity extends Activity {

    protected static final String TAG = "MyGreatActivity";
	
	private FacebookWebApiClient fbClient = new FacebookWebApiClient();
	private ListView spotListView = null;
	private ArrayAdapter<SpotJsonMessage> spotAdapter = null;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); 
        setContentView(R.layout.main);
        
        spotListView = (ListView)findViewById(R.id.listView1);
        spotAdapter = new ArrayAdapter<SpotJsonMessage>(this, android.R.layout.simple_list_item_1);
        spotListView.setAdapter(spotAdapter);
        
        
        fbClient.authorize(this, new AuthorizationCallback() {
			
			@Override
			public void onComplete() {
			}
			
			@Override
			public void onError(Throwable error) {
			}

			@Override
			public void onCancel() {
			}
		});
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        fbClient.authorizeCallback(requestCode, resultCode, data);
    }
    
    public void onButtonClick(View v) {
		Location center = new Location("");
		center.setLatitude(34.7257);
		center.setLongitude(137.4387);
		fbClient.searchPlaces("", center, 5000, new PlaceCallback() {
			
			@Override
			public void onReceive(SpotJsonMessage spot) {
				Log.d(TAG, spot.name);
				spotAdapter.add(spot);
				spotAdapter.notifyDataSetChanged();
			}
			
			@Override
			public void onError(Throwable error) {
				showToast(error.getMessage());
			}
		});
    }
    
    protected void showToast(String text) {
    	Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }
}
