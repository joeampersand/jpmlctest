package pl.com.cierniak.android.exercise;

import org.junit.jupiter.api.Test;

import static junit.framework.TestCase.assertEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class AlbumsUnitTest {
	
	@Test
	public void albumClassCreationTest() {
		AlbumClass album = new AlbumClass(null);
		assertEquals(-1L, album.getId());
		assertEquals(-1L, album.getUserId());
		assertEquals("No title", album.getTitle());
	}
}
