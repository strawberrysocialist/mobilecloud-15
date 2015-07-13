package vandy.mooc.video.server;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collection;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import retrofit.RestAdapter;
import retrofit.RestAdapter.LogLevel;
import retrofit.client.Response;
import retrofit.mime.TypedFile;
import vandy.mooc.video.TestData;
import vandy.mooc.video.client.VideoSvcApi;
import vandy.mooc.video.server.repository.Video;
import vandy.mooc.video.server.repository.VideoStatus;
import vandy.mooc.video.server.repository.VideoStatus.VideoState;

public class VideoServiceDeleteTests {
	private static final String SERVER = TestData.SERVER;

	private File testVideoData = TestData.getTestVideoFile();
	
	private Video video = TestData.getTestVideo();

	private VideoSvcApi videoSvc = new RestAdapter.Builder()
			.setEndpoint(SERVER)
			.setLogLevel(LogLevel.HEADERS)
			.build()
			.create(VideoSvcApi.class);

	@Test
	public void testDeleteVideo() throws Exception {
		Collection<Video> videos = videoSvc.getVideoList();
		int initialVideoCount = videos.size();
		
		System.out.println("testDeleteVideo: Adding video metadata");
		// Add video metadata and confirm
		Video received = videoSvc.addVideo(video);
		assertTrue(received.getId() > 0);
		videos = videoSvc.getVideoList();
		//assertFalse(videos.isEmpty());
		assertTrue(videos.size() > initialVideoCount);
		assertTrue(videos.contains(video));
		
		System.out.println("testDeleteVideo: Adding video data");
		// Add video data and confirm
		VideoStatus status = videoSvc.uploadVideo(received.getId(),
				new TypedFile(received.getContentType(), testVideoData));
		assertEquals(VideoState.READY, status.getState());
		
		Response response = videoSvc.downloadVideo(received.getId());
		assertEquals(200, response.getStatus());
		
		InputStream videoData = response.getBody().in();
		byte[] originalFile = IOUtils.toByteArray(
				new FileInputStream(testVideoData));
		byte[] retrievedFile = IOUtils.toByteArray(videoData);
		assertTrue(Arrays.equals(originalFile, retrievedFile));
		
		System.out.println("testDeleteVideo: Attempting delete");
		// Delete all video data and confirm
		videoSvc.deleteVideo(received.getId());
		assertEquals(videos.size(), initialVideoCount);
		response = videoSvc.downloadVideo(received.getId());
		assertEquals(404, response.getStatus());
	}

	@Test
	public void testDeleteVideos() throws Exception {
		System.out.println("testDeleteVideos: Adding first video");
		Collection<Video> videos = videoSvc.getVideoList();
		// Add video metadata and confirm
		Video received = videoSvc.addVideo(video);
		assertTrue(received.getId() > 0);
		videos = videoSvc.getVideoList();
		//assertFalse(videos.isEmpty());
		assertTrue(videos.size() > 0);
		assertTrue(videos.contains(video));
		VideoStatus status = videoSvc.uploadVideo(received.getId(),
				new TypedFile(received.getContentType(), testVideoData));
		assertEquals(VideoState.READY, status.getState());
		
		System.out.println("testDeleteVideos: Adding second video");
		Video updated = videoSvc.addVideo(video);
		assertTrue(updated.getId() > 0);
		videos = videoSvc.getVideoList();
		assertTrue(videos.contains(updated));
		status = videoSvc.uploadVideo(updated.getId(),
				new TypedFile(updated.getContentType(), testVideoData));
		assertEquals(VideoState.READY, status.getState());
		
		System.out.println("testDeleteVideos: Checking video count >= 2");
		videos = videoSvc.getVideoList();
		assertTrue(videos.size() >= 2);

		System.out.println("testDeleteVideos: Attempting delete");
		videoSvc.deleteVideos();
		videos = videoSvc.getVideoList();
		assertEquals(videos.size(), 0);
	}
}
