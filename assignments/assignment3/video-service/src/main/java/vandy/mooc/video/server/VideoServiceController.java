package vandy.mooc.video.server;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import vandy.mooc.video.server.VideoFileManager;
import vandy.mooc.video.server.VideoSvcApi;
import vandy.mooc.video.server.model.Video;
import vandy.mooc.video.server.model.VideoRepository;
import vandy.mooc.video.server.model.VideoStatus;
import vandy.mooc.video.server.model.VideoStatus.VideoState;

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

import com.google.common.collect.Lists;

@Controller
public class VideoServiceController {

	//public static final String VIDEO_ID_PATH = VideoSvcApi.VIDEO_SVC_PATH + "/{id}";
	
	@Autowired
	private VideoRepository mVideoRepository;
	
	@Autowired
	private VideoFileManager mVideoDataRepository;
	
	/**
	 * This method grabs the meta data for a new Video from the body, storing it in memory.
	 * It returns a unique ID to the client for use when uploading the actual video.
	 * @param v
	 * @return
	 */
	@RequestMapping(value=VideoSvcApi.VIDEO_SVC_PATH, method=RequestMethod.POST)
	public @ResponseBody Video addVideoMetadata(@RequestBody Video v, 
			HttpServletResponse response) {
		// TODONE Implement the logic to store the meta data.
		assert(v != null);
		
		v = mVideoRepository.save(v);
		
		if (v == null) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}
		
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
	 * @param v
	 * @return
	 */
	@RequestMapping(value=VideoSvcApi.VIDEO_SVC_PATH, method=RequestMethod.PUT)
	public @ResponseBody Video updateVideoMetadata(@RequestBody Video v, 
			HttpServletResponse response) {
		// TODONE Implement the logic to store the meta data.
		assert(v != null);
		
		v = mVideoRepository.save(v);
		
		if (v == null) {
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
		}
		
		return v;
	}
	
	/**
	 * Returns the video meta data specified by the @param id if found.
	 * @param id
	 * @return
	 */
	@RequestMapping(value=VideoSvcApi.VIDEO_INFO_PATH, method=RequestMethod.GET)
	public @ResponseBody Video getVideoMetadata(@PathVariable long id, 
			HttpServletResponse response) {
		// TODONE Implement the logic to return the video meta data for the given ID.
		Video v = mVideoRepository.findOne(id);
		
		if (v == null) {
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
		}
		
		return v;
	}

	/**
	 * This method grabs the encoded video from the multi part body, writing it to disk.
	 * It returns the VideoStatus to indicate success or 400 for failure.
	 * @param id
	 * @return
	 * @throws IOException 
	 */
	@RequestMapping(value=VideoSvcApi.VIDEO_DATA_PATH, method=RequestMethod.POST)
	public @ResponseBody VideoStatus addVideo(@PathVariable long id, 
			@RequestParam("data") MultipartFile videoFile, 
			HttpServletResponse response) throws IOException {
		// TODONE Implement the logic to store the video data.
		Video v = mVideoRepository.findOne(id);
		if (v != null) {
			try {
				mVideoDataRepository.saveVideoData(v, 
						videoFile.getInputStream());
			} catch (IOException e) {
				e.printStackTrace();
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
						"Unable to write file");
			}
		} else {
			response.sendError(HttpServletResponse.SC_NOT_FOUND,
					"Video not found");
		}
		return new VideoStatus(VideoState.READY);
	}

	/**
	 * Returns the video specified by the @param id if found.
	 * @param id
	 * @return
	 * @throws IOException 
	 */
	@RequestMapping(value=VideoSvcApi.VIDEO_DATA_PATH, method=RequestMethod.GET)
	public void getVideo(@PathVariable long id, 
			HttpServletResponse response) throws IOException {
		// TODONE Implement the logic to return the video for the given ID.
		Video v = mVideoRepository.findOne(id);
		if (v != null) {
			OutputStream out = response.getOutputStream();
			try {
				if (mVideoDataRepository.hasVideoData(v)) {
					mVideoDataRepository.copyVideoData(v, out);
				} else {
					response.sendError(HttpServletResponse.SC_NOT_FOUND,
							"Video not found");
				}
			} catch (IOException e) {
				e.printStackTrace();
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
						"Unable to read file");
			} finally {
				out.close();
			}
		} else {
			response.sendError(HttpServletResponse.SC_NOT_FOUND,
					"Video not found");
		}
	}

	/**
	 * This method returns a collection of all video 
	 * meta data stored by the service.
	 * @param v
	 * @return
	 */
	@RequestMapping(value=VideoSvcApi.VIDEO_SVC_PATH, method=RequestMethod.GET)
	public @ResponseBody Collection<Video> getVideos() {
		// TODONE Implement the logic to return the list of all videos.
		return Lists.newArrayList(mVideoRepository.findAll());
	}
	
	/**
	 * This method deletes the video data and the video meta data
	 * for the given @param id if found.
	 * @param id
	 */
	@RequestMapping(value=VideoSvcApi.VIDEO_DATA_PATH, method=RequestMethod.DELETE)
	//@RequestMapping(value=VideoSvcApi.VIDEO_INFO_PATH, method=RequestMethod.DELETE)
	public void deleteVideo(@PathVariable long id) {
		try {
			mVideoDataRepository.deleteVideoData(
					mVideoRepository.findOne(id));
			mVideoRepository.deleteOne(id);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * This method deletes all the video data and the video meta data
	 * stored by the service.
	 */
	@RequestMapping(value=VideoSvcApi.VIDEO_SVC_PATH, method=RequestMethod.DELETE)
	public void deleteVideos() {
		for (Video v : mVideoRepository.findAll()) {
			try {
				mVideoDataRepository.deleteVideoData(v);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		mVideoRepository.deleteAll();
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