package pl.com.cierniak.android.exercise;

import android.app.ActionBar;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import androidx.fragment.app.FragmentActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class MainActivity extends FragmentActivity {
	private static final String TAG = MainActivity.class.getSimpleName();
	private static final String UPDATE = "update";
	private static final String FAILURE = "failure";
	private static final String OFFLINE = "offline";
	private static final String ALBUMS_URL = "https://jsonplaceholder.typicode.com/albums";
	
	private ListView albumsListView;
	private List<AlbumClass> albumsList = null;
	private LocalBroadcastManager broadcastManager;
	
	public ListView getAlbumsListView() {
		return albumsListView;
	}
	
	public List<AlbumClass> getAlbumsList() {
		return albumsList;
	}
	
	public LocalBroadcastManager getBroadcastManager() {
		return broadcastManager;
	}
	
	private final BroadcastReceiver listener = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent != null && intent.getAction() != null) {
				String action = intent.getAction();
				String message = intent.getStringExtra(getString(R.string.message));
				if (action.equals(UPDATE)) {
					// todo: in the future try to save to a local database, but for now use the internal storage
					PreferenceManager.getDefaultSharedPreferences(context).edit().putString(OFFLINE, message).apply();
					buildAndSortAlbumList(message);
				} else
					if (action.equals(FAILURE))
						Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
			}
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getActionBar() != null)
			getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_TITLE | ActionBar.DISPLAY_USE_LOGO);
		
		setContentView(R.layout.album_list);
		albumsListView = findViewById(android.R.id.list);
		broadcastManager = LocalBroadcastManager.getInstance(getApplicationContext());
		if (savedInstanceState == null)
			fetchAlbums();
		buildAndSortAlbumList(PreferenceManager.getDefaultSharedPreferences(this).getString(OFFLINE, null));
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		LocalBroadcastManager.getInstance(this).registerReceiver(listener, new IntentFilter(UPDATE));
		LocalBroadcastManager.getInstance(this).registerReceiver(listener, new IntentFilter(FAILURE));
	}
	
	@Override
	public void onStop() {
		super.onStop();
		LocalBroadcastManager.getInstance(this).unregisterReceiver(listener);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main_menu, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.action_refresh) {
			fetchAlbums();
			return true;
		} else
			return super.onOptionsItemSelected(item);
	}
	
	private void fetchAlbums() {
		Toast.makeText(this, R.string.refreshing, Toast.LENGTH_SHORT).show();
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					HttpURLConnection connection = (HttpURLConnection) (new URL(ALBUMS_URL)).openConnection();
					connection.setDoInput(true);
					InputStream inputStream = connection.getErrorStream();
					if (inputStream == null) inputStream = connection.getInputStream();
					
					BufferedReader stream = new BufferedReader(new InputStreamReader(inputStream));
					StringBuilder body = new StringBuilder();
					String line;
					while ((line = stream.readLine()) != null)
						body.append(line);
					broadcastManager.sendBroadcast(new Intent(UPDATE).putExtra(getString(R.string.message), body.toString()));
					connection.disconnect();
				} catch (Exception e) {
					Log.e(TAG, e.getLocalizedMessage());
					broadcastManager.sendBroadcast(new Intent(FAILURE).putExtra(getString(R.string.message), e.getLocalizedMessage()));
				}
			}
		}).start();
	}
	
	private void buildAndSortAlbumList(String jsonAlbums) {
		if (albumsList == null)
			albumsList = new ArrayList<>();
		albumsList.clear();
		
		if (jsonAlbums != null && !jsonAlbums.isEmpty() && !jsonAlbums.equals("{}"))
			try {
				JSONArray jsonArray = new JSONArray(jsonAlbums);
				for (int i = 0, size = jsonArray.length(); i < size; i++)
					albumsList.add(new AlbumClass(jsonArray.getJSONObject(i)));
				Collections.sort(albumsList, new Comparator<AlbumClass>() {
					@Override
					public int compare(AlbumClass a1, AlbumClass a2) {
						return a1.getTitle().compareToIgnoreCase(a2.getTitle());
					}
				});
			} catch (JSONException e) {
				Log.e(TAG, e.getLocalizedMessage());
				Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
			}
		
		albumsListView.setAdapter(new BaseAdapter() {
			@Override
			public int getCount() {
				return albumsList.size();
			}
			
			@Override
			public Object getItem(int position) {
				return albumsList.get(position);
			}
			
			@Override
			public long getItemId(int position) {
				return albumsList.get(position).getId();
			}
			
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				if (convertView == null)
					convertView = getLayoutInflater().inflate(R.layout.album_item, null);
				((TextView) convertView.findViewById(R.id.item)).setText(albumsList.get(position).toString());
				return convertView;
			}
		});
	}
}
