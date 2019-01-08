package pl.com.cierniak.android.exercise;

import org.json.JSONObject;

import java.util.Locale;

import androidx.annotation.NonNull;

/**
 * <br>This is a part of exercise project.<br>
 * <br>Copyright (c) CIERNIAK. All rights reserved.
 * <br>Created by Leszek J. Cierniak (leszek@cierniak.com.pl) on 17.11.2018.
 */
public class AlbumClass {
	private long userId;
	private long id;
	private String title;
	
	public AlbumClass() {
		setUserId(-1L);
		setId(-1L);
		setTitle("No title");
	}
	
	public AlbumClass(JSONObject json) {
		this();
		if (json != null) {
			setUserId(json.optLong("userId", -1L));
			setId(json.optLong("id", -1L));
			setTitle(json.optString("title", "No title"));
		}
	}
	
	public long getUserId() {
		return userId;
	}
	
	public void setUserId(long userId) {
		this.userId = userId;
	}
	
	public long getId() {
		return id;
	}
	
	public void setId(long id) {
		this.id = id;
	}
	
	public String getTitle() {
		return title;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	@NonNull
	@Override
	public String toString() {
		return String.format(Locale.getDefault(), "%3d", getId()) + ": " + getTitle();
	}
}
