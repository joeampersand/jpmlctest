package pl.com.cierniak.android.exercise;

import android.widget.ListView;

import org.junit.Rule;
import org.junit.Test;

import androidx.test.rule.ActivityTestRule;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class AlbumsInstrumentedTestSuit {
	@Rule
	public ActivityTestRule<MainActivity> main = new ActivityTestRule<>(MainActivity.class);
	
	@Test
	public void activityIsCreated() {
		assertNotNull(main.getActivity().getAlbumsListView());
		assertNotNull(main.getActivity().getAlbumsList());
		assertNotNull(main.getActivity().getBroadcastManager());
	}
	
	@Test
	public void albumsListIsFetched() {
		ListView listView = main.getActivity().getAlbumsListView();
		assertTrue(listView.getCount() > 0);
	}
	
	@Test
	public void albumsListIsFullyFetched() {
		assertEquals(100, ((ListView) main.getActivity().findViewById(android.R.id.list)).getCount());
	}
	
	/**
	 * Application should have screen which displays albums list sorted by title
	 */
	@Test
	public void albumsListIsSorted() {
		ListView listView = main.getActivity().getAlbumsListView();
		
		AlbumClass a1 = (AlbumClass) listView.getItemAtPosition(0);
		AlbumClass a2 = (AlbumClass) listView.getItemAtPosition(1);
		AlbumClass a3 = (AlbumClass) listView.getItemAtPosition(2);
		assertTrue(a1.getTitle().compareToIgnoreCase(a2.getTitle()) < 0);
		assertTrue(a2.getTitle().compareToIgnoreCase(a3.getTitle()) < 0);
	}
}
