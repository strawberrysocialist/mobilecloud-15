/*
 * 
 * Copyright 2014 Jules White
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
package org.magnum.dataup;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.magnum.dataup.VideoSvcApi;
import org.magnum.dataup.model.Video;
import org.magnum.dataup.model.VideoStatus;
import org.magnum.dataup.model.VideoStatus.VideoState;
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

@Controller
public class AnEmptyController {

	/**
	 * You will need to create one or more Spring controllers to fulfill the
	 * requirements of the assignment. If you use this file, please rename it
	 * to something other than "AnEmptyController"
	 * 
	 * 
		 ________  ________  ________  ________          ___       ___  ___  ________  ___  __       
		|\   ____\|\   __  \|\   __  \|\   ___ \        |\  \     |\  \|\  \|\   ____\|\  \|\  \     
		\ \  \___|\ \  \|\  \ \  \|\  \ \  \_|\ \       \ \  \    \ \  \\\  \ \  \___|\ \  \/  /|_   
		 \ \  \  __\ \  \\\  \ \  \\\  \ \  \ \\ \       \ \  \    \ \  \\\  \ \  \    \ \   ___  \  
		  \ \  \|\  \ \  \\\  \ \  \\\  \ \  \_\\ \       \ \  \____\ \  \\\  \ \  \____\ \  \\ \  \ 
		   \ \_______\ \_______\ \_______\ \_______\       \ \_______\ \_______\ \_______\ \__\\ \__\
		    \|_______|\|_______|\|_______|\|_______|        \|_______|\|_______|\|_______|\|__| \|__|
                                                                                                                                                                                                                                                                        
	 * 
	 */

	public static final String VIDEO_ID_PATH = VideoSvcApi.VIDEO_SVC_PATH + "/{id}";
	
	private AtomicLong idGenerator = new AtomicLong(0L);
	
	private ConcurrentMap<Long,Video> videos = new ConcurrentHashMap<Long,Video>();
	
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
		
		Long id = initializeVideo(v);
		if (id == null) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}
		
		return videos.get(id);
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
		
		Long id = new Long(v.getId());
		if (id != 0 && videos.containsKey(id)) {
			videos.replace(id, v);
		} else{
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
		}
		return videos.get(id);
	}
	
	/**
	 * Returns the video meta data specified by the @param id if found.
	 * @param id
	 * @return
	 */
	@RequestMapping(value=VIDEO_ID_PATH, method=RequestMethod.GET)
	public @ResponseBody Video getVideoMetadata(@PathVariable long id) {
		// TODONE Implement the logic to return the video meta data for the given ID.
		assert(videos != null);
		
		Long longId = new Long(id);
		if (videos.containsKey(longId)) {
			return videos.get(longId);
		}
		return null;
	}

	/**
	 * This method grabs the encoded video from the multi part body, writing it to disk.
	 * It returns the VideoStatus to indicate success or 400 for failure.
	 * @param id
	 * @return
	 */
	@RequestMapping(value=VideoSvcApi.VIDEO_DATA_PATH, method=RequestMethod.POST)
	public @ResponseBody VideoStatus addVideo(@PathVariable long id, 
			@RequestParam("data") MultipartFile videoFile, 
			HttpServletResponse response) {
		// TODONE Implement the logic to store the video data.
		assert(videos != null);
		
		Long longId = new Long(id);
		if (videos.containsKey(longId)) {
			Video v = videos.get(longId);
			assert(v != null);
			
			try {
				VideoFileManager.get().saveVideoData(v, 
						videoFile.getInputStream());
			} catch (IOException e) {
				e.printStackTrace();
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			}
			//return new VideoStatus(VideoState.READY);
		} else {
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
		}
		//return null;
		return new VideoStatus(VideoState.READY);
	}

	/**
	 * Returns the video specified by the @param id if found.
	 * @param id
	 * @return
	 */
	@RequestMapping(value=VideoSvcApi.VIDEO_DATA_PATH, method=RequestMethod.GET)
	public void getVideo(@PathVariable long id, 
			HttpServletResponse response) {
		//public @ResponseBody OutputStream getVideo(@PathVariable long id, 
		//		HttpServletResponse response) {
		// TODONE Implement the logic to return the video for the given ID.
		assert(videos != null);
		
		Long longId = new Long(id);
		if (videos.containsKey(longId)) {
			Video v = videos.get(longId);
			
			try {
				OutputStream out = response.getOutputStream();
				if (VideoFileManager.get().hasVideoData(v)) {
					VideoFileManager.get().copyVideoData(v, out);
				} else {
					response.setStatus(HttpServletResponse.SC_NOT_FOUND);
				}
				out.close();
				//return out;
			} catch (IOException e) {
				e.printStackTrace();
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			}
		} else {
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
		}
		//return null;
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
		assert(videos != null);
		
		return videos.values();
	}
	
	/**
	 * This method deletes the video data and the video meta data
	 * for the given @param id if found.
	 * @param id
	 */
	@RequestMapping(value=VIDEO_ID_PATH, method=RequestMethod.DELETE)
	public void deleteVideo(@PathVariable long id) {
		assert(videos != null);
		
		Long longId = new Long(id);
		if (videos.containsKey(longId)) {
			Video v = videos.get(longId);
			
			try {
				VideoFileManager.get().deleteVideoData(v);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			videos.remove(longId);
		}
	}
	
	/**
	 * This method deletes all the video data and the video meta data
	 * stored by the service.
	 */
	@RequestMapping(value=VideoSvcApi.VIDEO_SVC_PATH, method=RequestMethod.DELETE)
	public void deleteVideos() {
		assert(videos != null);
		
		for (Video v : videos.values()) {
			try {
				VideoFileManager.get().deleteVideoData(v);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		videos.clear();
	}
	
	/* This method assigns a new ID and a url to a new video.
	 * It returns the ID of the new video or Null if the
	 * video was already in the collection. 
	 */
	private Long initializeVideo(Video v) {
		Long id = new Long(v.getId());
		if (id == 0 && !videos.containsKey(id)) {
			id = new Long(idGenerator.incrementAndGet());
			v.setId(id.longValue());
			v.setDataUrl(getUrlBaseForLocalServer() 
					+ VideoSvcApi.VIDEO_DATA_PATH.replaceFirst(
							"\\{id\\}", id.toString()));
			videos.put(id, v);
		} else {
			return null;
		}
		return id;
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
