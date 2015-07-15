package vandy.mooc.video.server.controller.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import retrofit.RestAdapter;
import retrofit.RestAdapter.LogLevel;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedFile;
import vandy.mooc.video.TestData;
import vandy.mooc.video.client.VideoSvcApi;
import vandy.mooc.video.server.repository.Video;
import vandy.mooc.video.server.repository.VideoStatus;
import vandy.mooc.video.server.repository.VideoStatus.VideoState;

public class VideoServiceControllerTest {

	private static final String SERVER = TestData.SERVER;

	private File testVideoData = TestData.getTestVideoFile();
	
	private Video video = TestData.getTestVideo();

	private VideoSvcApi videoSvc = new RestAdapter.Builder()
			.setEndpoint(SERVER)
			.setLogLevel(LogLevel.FULL)
			.build()
			.create(VideoSvcApi.class);

	@Test
	public void testAddVideoMetadata() throws Exception {
		Video received = videoSvc.addVideo(video);
		assertEquals(video.getTitle(), received.getTitle());
		assertEquals(video.getDuration(), received.getDuration());
		assertEquals(video.getContentType(), received.getContentType());
		assertEquals(video.getLocation(), received.getLocation());
		assertEquals(video.getSubject(), received.getSubject());
		assertTrue(received.getId() > 0);
		assertTrue(received.getDataUrl() != null);
	}
	/**
	@Test
	public void testUpdateVideoMetadata() throws Exception {
		Video received = videoSvc.addVideo(video);
		assertEquals(video.getTitle(), received.getTitle());
		assertEquals(video.getDuration(), received.getDuration());
		assertEquals(video.getContentType(), received.getContentType());
		assertEquals(video.getLocation(), received.getLocation());
		assertEquals(video.getSubject(), received.getSubject());
		assertTrue(received.getId() > 0);
		assertTrue(received.getDataUrl() != null);
	}
	
	@Test
	public void testGetVideoMetadata() throws Exception {
		Video received = videoSvc.addVideo(video);
		assertEquals(video.getTitle(), received.getTitle());
		assertEquals(video.getDuration(), received.getDuration());
		assertEquals(video.getContentType(), received.getContentType());
		assertEquals(video.getLocation(), received.getLocation());
		assertEquals(video.getSubject(), received.getSubject());
		assertTrue(received.getId() > 0);
		assertTrue(received.getDataUrl() != null);
		long id = received.getId();
		Video updated = videoSvc.addVideo(video);
		assertTrue(updated.getId() > 0);
		assertNotEquals(updated.getId(), id);
	}
	
	@Test
	public void testAddGetVideo() throws Exception {
		videoSvc.addVideo(video);
		Collection<Video> stored = videoSvc.getVideoList();
		assertTrue(stored.contains(video));
	}
	
	@Test
	public void testAddVideoData() throws Exception {
		Video received = videoSvc.addVideo(video);
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
	}

	@Test
	public void testGetNonExistantVideosData() throws Exception {
		
		long nonExistantId = getInvalidVideoId();
		
		try{
			Response r = videoSvc.downloadVideo(nonExistantId);
			assertEquals(404, r.getStatus());
		}catch(RetrofitError e){
			assertEquals(404, e.getResponse().getStatus());
		}
	}
	
	@Test
	public void testAddNonExistantVideosData() throws Exception {
		long nonExistantId = getInvalidVideoId();
		try {
			videoSvc.uploadVideo(nonExistantId, 
					new TypedFile(video.getContentType(), 
							testVideoData));
			fail("The client should receive a 404 error code "
					+ "and throw an exception if an invalid"
					+ " video ID is provided in setVideoData()");
		} catch (RetrofitError e) {
			assertEquals(404, e.getResponse().getStatus());
		}
	}
	*/
	private long getInvalidVideoId() {
		Set<Long> ids = new HashSet<Long>();
		Collection<Video> stored = videoSvc.getVideoList();
		for(Video v : stored){
			ids.add(v.getId());
		}
		
		long nonExistantId = Long.MIN_VALUE;
		while(ids.contains(nonExistantId)){
			nonExistantId++;
		}
		return nonExistantId;
	}
}
