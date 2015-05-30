package be.bruparking.maps;

import android.graphics.Color;

import java.util.ArrayList;

public class Zone {
	private int id;
	private String strokeColor;
	private String zoneColor;
	private String zoneName;
	private float zoneColorAlpha = 0.5f;
	ArrayList<Coordinate> coordinates = new ArrayList<Coordinate>();
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setCoordinates(Coordinate[] coordinates) {
		this.coordinates.clear();
		for (Coordinate c : coordinates) {
			this.coordinates.add(c);
		}
	}
	
	public ArrayList<Coordinate> getCoordinates() {
		return this.coordinates;
	}
	
	public void addCoordinate(Coordinate coordinate) {
		this.coordinates.add(coordinate);	
	}

	public String getStrokeColor() {
		return strokeColor;
	}
	
	public int getStrokeColorInt() {
		try {
			return Color.parseColor(this.strokeColor);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return 1;
	}

	public void setStrokeColor(String strokeColor) {
		this.strokeColor = strokeColor;
	}

	public String getZoneColor() {
		return zoneColor;
	}
	
	public int getZoneColorInt() {
		try {
			return this.makeTransparent(Color.parseColor(this.zoneColor));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 1;
	}

	public void setZoneColor(String zoneColor) {
		this.zoneColor = zoneColor;
	}
	
	private int makeTransparent(int color) {
		int alpha = Math.round(Color.alpha(color) * zoneColorAlpha);
	    int red = Color.red(color);
	    int green = Color.green(color);
	    int blue = Color.blue(color);
	    return Color.argb(alpha, red, green, blue);
	}

	public String getZoneName() {
		return zoneName;
	}

	public void setZoneName(String zoneName) {
		this.zoneName = zoneName;
	}
}
