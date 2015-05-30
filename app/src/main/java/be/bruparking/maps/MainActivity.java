package be.bruparking.maps;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;
import com.sromku.polygon.Polygon.Builder;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class MainActivity extends FragmentActivity implements OnMapLongClickListener {
	
	private GoogleMap map;
	private Circle homeCircle;
	private Marker homeMarker;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        try {
            setContentView(R.layout.activity_main);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // TODO: http://developer.android.com/google/play-services/setup.html#Setup
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        Log.println(1, "Status", String.valueOf(status));

        try {
            MapsInitializer.initialize(this);
            // Get a handle to the Map Fragment
            SupportMapFragment map_fragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

            map = map_fragment.getMap();

            // TODO Add check here if GPS is available. If gms is not initialized, apk will crash
            map.setMyLocationEnabled(true);

            // Set default location (Brussels, wide area)
            map.moveCamera( CameraUpdateFactory.newLatLngZoom(new LatLng(50.8199, 4.37265), 10.0f) );

            // Set event listener
            map.setOnMapLongClickListener(this);

            ArrayList<Zone> zones = get_all_zones();
            drawPolygons(this, map, zones);

            drawHomeCircle();

            // Check if current location is in homeCircle
            // TODO: If YES, then you should check only for red zones
            GPSTracker gps = new GPSTracker(this);
            if(gps.canGetLocation()) { // gps enabled} // return boolean true/false
                Double latitude = gps.getLatitude();
                Double longitude = gps.getLongitude();
                float[] distance = new float[2];
                Location.distanceBetween( latitude, longitude,
                        homeCircle.getCenter().latitude, homeCircle.getCenter().longitude, distance);

                    if( distance[0] > homeCircle.getRadius()  ){
                        displayDialogCurrentZone(map, zones);
                    } else {
                        displayDialogInsideHomeCircle();
                    }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

	private ArrayList<Zone> get_all_zones() {
		XMLZones xz = new XMLZones();
		ArrayList<Zone> zones = null;
		try {
			zones = xz.getZones(this);
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return zones;
	}
	
	private void displayDialogCurrentZone(GoogleMap map, ArrayList<Zone> zones) {
		//ArrayList<Zone> zones = get_all_zones();
		GPSTracker gps = new GPSTracker(this);
		if(gps.canGetLocation()) { // gps enabled} // return boolean true/false
			Double latitude = gps.getLatitude();
			Double longitude = gps.getLongitude();
			
			if (latitude != null && longitude != null) {
				//Dageraadstraat 7:
				//latitude = 50.8199
				//longitude = 4.37265
				//LatLng currentPosition = new LatLng(50.8199, 4.37265);
				LatLng currentPosition = new LatLng(latitude, longitude);
				
				//Zoom to current location
				CameraPosition cameraPosition = new CameraPosition.Builder()
				.target(currentPosition)		// Sets the center of the map to Mountain View
				.zoom(16)				   // Sets the zoom
				.bearing(0)					// Sets the orientation of the camera to the North
				.tilt(0)				   	// Sets the tilt of the camera to 0 degrees (bird-view)
				.build();				   // Creates a CameraPosition from the builder

				
				try {
					map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
				}
				catch (Exception animateException) {
					Log.println(BIND_DEBUG_UNBIND, ACTIVITY_SERVICE, "FATAL ERROR: CameraUpdateFactory failed on Animate!");
					try {
						//Alternatively, try to move camera without animation
						map.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
					}
					catch(Exception moveException) {
						Log.println(BIND_DEBUG_UNBIND, ACTIVITY_SERVICE, "FATAL ERROR: CameraUpdateFactory failed on Move!");
					}
				}
				
				// test for external polygon class
				for (Zone zone : zones) {
					if (pointTest(latitude, longitude, zone)) {
						DialogCurrentZone dialog = new DialogCurrentZone();
						Bundle b = new Bundle();
						
						// Get localized zone name
						if (zone.getZoneName().equals("RED")) {
							b.putString("CURRENT_ZONE", getResources().getString(R.string.zone_red));
						}
						else if(zone.getZoneName().equals("BLUE")) {
							b.putString("CURRENT_ZONE", getResources().getString(R.string.zone_blue));
						}
						else if(zone.getZoneName().equals("ORANGE")) {
							b.putString("CURRENT_ZONE", getResources().getString(R.string.zone_orange));
						}
						
						dialog.setArguments(b);
						dialog.show(getFragmentManager(), null);
					}
				}
			}
		}
		else {
			// TODO: Display message that current location is not available
		}
	}
	
	private void displayDialogCurrentZone() {
		// Get a handle to the Map Fragment
		SupportMapFragment map_fragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
		GoogleMap map = map_fragment.getMap();
		
		ArrayList<Zone> zones = get_all_zones();
		displayDialogCurrentZone(map, zones);
	}

	private boolean pointTest(Double latitude, Double longitude, Zone zone) {
		// Create a point that is going to be tested
		com.sromku.polygon.Point sromkuCurrentPoint = new com.sromku.polygon.Point(latitude.floatValue(), longitude.floatValue());

		// Create a polygon, based on zone coordinates 
		com.sromku.polygon.Polygon sromkuPolygon = null;
		Builder polygonBuilder = new Builder();
		for (Coordinate co : zone.coordinates) {
			polygonBuilder.addVertex(new com.sromku.polygon.Point(co.getLat().floatValue(), co.getLng().floatValue()));
		}
		sromkuPolygon = polygonBuilder.build();

		// Test point against polygon
		if (sromkuPolygon.contains(sromkuCurrentPoint)) {
			return true;
		}
		return false;
	}

	private void drawPolygons(MainActivity mainActivity, GoogleMap map, ArrayList<Zone> zones) {
		if (map != null) {
			for (Zone zone : zones) {
				PolygonOptions po = new PolygonOptions();
				for (Coordinate co : zone.coordinates) {
					po.add(new LatLng(co.getLat(), co.getLng()));
				}
				po.strokeWidth(1);
				po.strokeColor(zone.getStrokeColorInt());
				po.fillColor(zone.getZoneColorInt());
				po.visible(true);
				map.addPolygon(po);
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
// 			Disabled 
//			case R.id.action_settings:
//				Intent intent_settings = new Intent(this, SettingsActivity.class);
//				startActivity(intent_settings);
//				return true;
			case R.id.action_home_location:
				Intent intent_home_location = new Intent(this, HomeLocationActivity.class);
				startActivity(intent_home_location);
				return true;
			case R.id.action_help:
				Intent intent_help = new Intent(this, HelpActivity.class);
				startActivity(intent_help);
				return true;
			case R.id.action_refresh:
				this.drawHomeCircle();
				this.displayDialogCurrentZone();
				
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	public void drawHomeCircle() {
		SharedPreferences sharedPref = this.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
		Double lat = Double.parseDouble(sharedPref.getString(getString(R.string.pref_home_lat), "0"));
		Double lng = Double.parseDouble(sharedPref.getString(getString(R.string.pref_home_lng), "0"));
		
		if (lat!=0 && lng!=0) {
			if (this.homeCircle != null) {
				this.homeCircle.remove();
			}
			
			if (this.homeMarker != null) {
				this.homeMarker.remove();
			}
			
			CircleOptions circleOptions = new CircleOptions()
					.center(new LatLng(lat, lng))
					.radius(750); // In meters

			//Get back the mutable Circle
			this.homeCircle = map.addCircle(circleOptions);
			
			//Add home marker
			this.homeMarker = map.addMarker(new MarkerOptions().position(new LatLng(lat, lng)).title(getString(R.string.home_marker)));
		}
	}
	
	@Override
	public void onMapLongClick(LatLng point) {
		this.displayDialogSaveHomeLocation(point);
	}

	private void displayDialogSaveHomeLocation(LatLng point) {
		DialogSaveHomeLocation dialog = new DialogSaveHomeLocation(point);
		dialog.show(getFragmentManager(), null);
	}
	
	private void displayDialogInsideHomeCircle() {
		DialogInsideHomeCircle dialog = new DialogInsideHomeCircle();
		dialog.show(getFragmentManager(), null);
	}

}






