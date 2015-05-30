package be.bruparking.maps;

import android.app.Activity;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;

public class XMLZones {
	ArrayList<Zone> zones = new ArrayList<Zone>();
	
	public ArrayList<Zone> getZones(Activity activity) throws XmlPullParserException, IOException {
		
		ArrayList<String> file_names = new ArrayList<String>();
		file_names.add("elsene_east");
		file_names.add("elsene_west");
		
		Resources res = activity.getResources();
		XmlResourceParser xpp;
		
		for (String file_name : file_names) {
			int file_resource = res.getIdentifier(file_name, "xml", "be.bruparking.maps");
			xpp = res.getXml(file_resource);
		
			xpp.next();
			int eventType = xpp.getEventType();
			
			Zone currentZone = null;
			Coordinate currentCoordinate = null;
			
			String currentTag = "";
			while (eventType != XmlPullParser.END_DOCUMENT) {
				
				if(eventType == XmlPullParser.START_TAG) {
					if (xpp.getName().equals("zone")) {
						currentZone = new Zone();
					}
					if (xpp.getName().equals("coordinates")) {
						currentTag = "coordinates";
					}
					if (xpp.getName().equals("id")) {
						currentTag = "id";
					}
					if (xpp.getName().equals("scribble_key")) {
						currentTag = "scribble_key";
					}
					if (xpp.getName().equals("strokeColor")) {
						currentTag = "strokeColor";
					}
					if (xpp.getName().equals("zoneColor")) {
						currentTag = "zoneColor";
					}
					if (xpp.getName().equals("zoneName")) {
						currentTag = "zoneName";
					}
				}
				if(eventType == XmlPullParser.TEXT) {
					if (currentTag.equals("id")) {
						try {
							currentZone.setId(Integer.parseInt(xpp.getText()));
						}
						catch(Exception e) {
							Log.v("PARSE EXCEPTION", e.getMessage());
						}
					}
					if (currentTag.equals("strokeColor")) {
						currentZone.setStrokeColor(xpp.getText());
					}
					if (currentTag.equals("zoneColor")) {
						currentZone.setZoneColor(xpp.getText());
					}
					if (currentTag.equals("zoneName")) {
						currentZone.setZoneName(xpp.getText());
					}
					if (currentTag.equals("coordinates")) {
						
						String[] coords = xpp.getText().split("\\r?\\n");
						for (String lnglat : coords) {
							String[] lnglat_array = lnglat.split(",");
							String lng = lnglat_array[0]; 
							String lat = lnglat_array[1];
							currentCoordinate = new Coordinate();
							currentCoordinate.setLng(Double.parseDouble(lng));
							currentCoordinate.setLat(Double.parseDouble(lat));
							currentZone.addCoordinate(currentCoordinate);
						}
					}
				}
				if(eventType == XmlPullParser.END_TAG) {
					if (xpp.getName().equals("zone")) {
						zones.add(currentZone);
					}
				}
				
				eventType = xpp.next();
			}
		}
		return zones;
	}
}