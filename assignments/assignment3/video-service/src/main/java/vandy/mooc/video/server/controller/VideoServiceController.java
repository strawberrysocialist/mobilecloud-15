package vandy.mooc.video.server.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import vandy.mooc.video.server.VideoFileManager;
import vandy.mooc.video.client.VideoSvcApi;
import vandy.mooc.video.server.repository.Video;
import vandy.mooc.video.server.repository.VideoRepository;
import vandy.mooc.video.server.repository.VideoStatus;
import vandy.mooc.video.server.repository.VideoStatus.VideoState;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import retrofit.client.Response;
import retrofit.mime.TypedFile;

import com.google.common.collect.Lists;

@Controller
public class VideoServiceController implements VideoSvcApi {

	@Autowired
	private VideoRepository mVideoRepository;
	
	@Autowired
	private VideoFileManager mVideoDataRepository;


	/**
	 * This method returns a collection of all video 
	 * meta data stored by the service.
	 * @return
	 */
	@RequestMapping(value=VideoSvcApi.VIDEO_SVC_PATH, method=RequestMethod.GET)
 	@Override
	public Collection<Video> getVideoList() {
		// TODONE Implement the logic to return the list of all videos.
		return Lists.newArrayList(mVideoRepository.findAll());
	}
	
	/**
	 * Returns the video meta data specified by the @param id if found.
	 * @param id
	 * @return
	 */
	@RequestMapping(value=VideoSvcApi.VIDEO_INFO_PATH, method=RequestMethod.GET)
	@Override
	public Video getVideo(long id) {
		// TODONE Implement the logic to return the video meta data for the given ID.
		Video v = mVideoRepository.findOne(id);
		
		return v;
	}
	

	/**
	 * This method grabs the meta data for a new Video from the body, storing it in memory.
	 * It returns a unique ID to the client for use when uploading the actual video.
	 * @param v
	 * @return
	 */
	@RequestMapping(value=VideoSvcApi.VIDEO_SVC_PATH, method=RequestMethod.POST)
	@Override
	public Video addVideo(Video v) {
		// TODONE Implement the logic to store the meta data.
		assert(v != null);
		v = mVideoRepository.save(v);
		
		assert(v != null);
		// TODO Fix filename & path
		v.setDataUrl(getUrlBaseForLocalServer() 
				+ VideoSvcApi.VIDEO_DATA_PATH.replace(
						"{" + VideoSvcApi.ID_PARAMETER + "}",
						"" + v.getId()));
		
		return v;
	}
	

	/**
	 * This method grabs the meta data for the video with the id for
	 * the @param v, updating its representation in memory if found.
	 * It returns the updated video.
	 * @param id
	 * @param v
	 * @return
	 */
	@RequestMapping(value=VideoSvcApi.VIDEO_SVC_PATH, method=RequestMethod.PUT)
	@Override
	public Video updateVideo(long id, Video v) {
		// TODONE Implement the logic to update modified meta data.
		assert(v != null);
		// TODO Fix update instead of save
		v = mVideoRepository.save(v);
		
		assert(v != null);
		return v;
	}
	

	/**
	 * This method returns all videos whose title matches the @param title. 
	 * The Video objects should be returned as JSON.
	 * @param title
	 * @return
	 */
	@RequestMapping(value=VideoSvcApi.VIDEO_TITLE_SEARCH_PATH, method=RequestMethod.GET)
	@Override
	public Collection<Video> findByTitle(String title) {
		// TODONE Implement the logic to return the subset of all videos 
		// matching the @param title.
		return Lists.newArrayList(mVideoRepository.findByTitle(title));
	}
	

	/**
	 * This method returns all videos whose title matches the @param maxDuration. 
	 * The Video objects should be returned as JSON.
	 * @param maxDuration
	 * @return
	 */
	@RequestMapping(value=VideoSvcApi.VIDEO_DURATION_SEARCH_PATH, method=RequestMethod.GET)
	@Override
	public Collection<Video> findByDurationLessThan(long maxDuration) {
		// TODONE Implement the logic to return the subset of all videos 
		// matching the @param duration.
		return Lists.newArrayList(mVideoRepository.findByDurationLessThan(maxDuration));
	}
	

	/**
	 * This method returns all videos whose title matches the @param minRating. 
	 * The Video objects should be returned as JSON.
	 * @param minRating
	 * @return
	 */
	@RequestMapping(value=VideoSvcApi.VIDEO_RATING_SEARCH_PATH, method=RequestMethod.GET)
	@Override
	public Collection<Video> findByRatingGreaterThan(float minRating) {
		// TODONE Implement the logic to return the subset of all videos 
		// matching the @param rating.
		return Lists.newArrayList(mVideoRepository.findByRatingGreaterThan(minRating));
	}
	

	/**
	 * This method grabs the encoded video from the multi part body, writing it to disk.
	 * It returns the VideoStatus to indicate success or 400 for failure.
	 * @param id
	 * @param videoData
	 * @return
	 * @throws IOException 
	 */
	@RequestMapping(value=VideoSvcApi.VIDEO_DATA_PATH, method=RequestMethod.POST)
	@Override
	public VideoStatus uploadVideo(long id, TypedFile videoData) {
		// TODONE Implement the logic to store the video data.
		Video v = mVideoRepository.findOne(id);
		VideoStatus status = new VideoStatus(VideoState.PROCESSING);
		
		if (v != null) {
			try {
				mVideoDataRepository.saveVideoData(v, 
						videoData.in());
				status = new VideoStatus(VideoState.READY);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return status;
	}
	

	/**
	 * Returns the video specified by the @param id if found.
	 * @param id
	 * @return
	 * @throws IOException 
	 */
	@RequestMapping(value=VideoSvcApi.VIDEO_DATA_PATH, method=RequestMethod.GET)
	@Override
	public Response downloadVideo(long id) {
		// TODONE Implement the logic to return the video for the given ID.
		Video v = mVideoRepository.findOne(id);
		if (v != null) {
			OutputStream out = response.getOutputStream();
			try {
				if (mVideoDataRepository.hasVideoData(v)) {
					mVideoDataRepository.copyVideoData(v, out);
				}
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				out.close();
			}
		}
	}
	

	/**
	 * This method deletes the video data and the video meta data
	 * for the given @param id if found.
	 * @param id
	 * @return
	 */
	@RequestMapping(value=VideoSvcApi.VIDEO_INFO_PATH, method=RequestMethod.DELETE)
	@Override
	public Response deleteVideo(long id) {
		// TODONE Implement the logic to delete a specific video.
		try {
			mVideoDataRepository.deleteVideoData(
					mVideoRepository.findOne(id));
			mVideoRepository.delete(id);
		} catch (IOException e) {
			e.printStackTrace();
		}
		// TODO Check to find correct way to handle.
		return null;
	}
	

	/**
	 * This method deletes all the video data and the video meta data
	 * stored by the service.
	 * @return
	 */
	@RequestMapping(value=VideoSvcApi.VIDEO_SVC_PATH, method=RequestMethod.DELETE)
	@Override
	public Response deleteVideos() {
		// TODONE Implement the logic to delete all videos.
		for (Video v : mVideoRepository.findAll()) {
			try {
				mVideoDataRepository.deleteVideoData(v);
				mVideoRepository.delete(v.getId());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		// TODO Check to find correct way to handle.
		return null;
	}

	
	/* This method returns the url authority for the current request
	 * prepended by the http scheme.
	 */
 	private String getUrlBaseForLocalServer() {
	   HttpServletRequest request = ((ServletRequestAttributes) 
			   RequestContextHolder.getRequestAttributes()).getRequest();
	   String base = "http://" + request.getServerName() + ( 
	      (request.getServerPort() != 80) 
	    		  ? ":" + request.getServerPort() 
				  : "");
	   return base;
	}
}