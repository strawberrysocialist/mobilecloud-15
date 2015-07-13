package vandy.mooc.video;

import java.io.File;
import java.util.UUID;

import vandy.mooc.video.server.repository.Video;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * This is a utility class to aid in the construction of
 * Video objects with random names, urls, and durations.
 * The class also provides a facility to convert objects
 * into JSON using Jackson, which is the format that the
 * VideoSvc controller is going to expect data in for
 * integration testing.
 * 
 * @author jules
 *
 */
public class TestData {

	public static final String SERVER = "http://localhost:8080";

	private static final ObjectMapper objectMapper = new ObjectMapper();
	
	private static File testVideoData = new File(
			"src/test/resources/test.mp4");
	
	private static Video video;
	
	/**
	 * Construct and return a Video object
	 * with random values.
	 * 
	 * @return
	 */
	public static Video getTestVideo() {
		// Information about the video
		if (video == null) {
			// Construct a random identifier using Java's UUID class
			String id = UUID.randomUUID().toString();
			String title = "Video-" + id;
			String location = "Taken at " + id;
			String subject = id + " appears in picture";
			String contentType = "video/mpeg";
			String url = "http://coursera.org/some/video-"+id;
			long duration = 60 * (int)Math.rint(Math.random() * 60) * 1000; // random time up to 1hr
			video = new Video(title, duration, location, 
					subject, contentType, url);
		}
		return video;
	}
	
	/**
	 *  Convert an object to JSON using Jackson's ObjectMapper
	 *  
	 * @param o
	 * @return
	 * @throws Exception
	 */
	public static String toJson(Object o) throws Exception{
		return objectMapper.writeValueAsString(o);
	}

	/**
	 * Return the Test Video data file.
	 * 
	 * @return
	 */
	public static File getTestVideoFile() {
		return testVideoData;
	}
}
