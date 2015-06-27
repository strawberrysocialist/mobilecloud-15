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
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

import org.magnum.dataup.VideoSvcApi;
import org.magnum.dataup.model.Video;
import org.magnum.dataup.model.VideoStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

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
	
	public static final String VIDEOS_PATH = VideoSvcApi.VIDEO_SVC_PATH + "s";
	
	private AtomicLong idGenerator = new AtomicLong(0L);
	
	private ConcurrentMap<Long,Video> videos = new ConcurrentHashMap<Long,Video>();
	
	/**
	 * This method grabs the meta data for a new Video from the body, storing it in memory.
	 * It returns a unique ID to the client for use when uploading the actual video.
	 * @param v
	 * @return
	 */
	@RequestMapping(value=VideoSvcApi.VIDEO_SVC_PATH, method=RequestMethod.POST)
	public @ResponseBody long addVideoMetadata(@RequestBody Video v) {
		// TODONE Implement the logic to store the meta data.
		assert(v != null);
		
		if (v.getId() == 0) {
			v.setId(new Long(idGenerator.incrementAndGet()));
			videos.put(v.getId(), v);
		} else {
			videos.replace(v.getId(), v);
		}
		
		return v.getId();
	}

	/**
	 * This method grabs the encoded video from the multi part body, writing it to disk.
	 * It returns the VideoStatus to indicate success or 400 for failure.
	 * @param id
	 * @return
	 */
	@RequestMapping(value=VideoSvcApi.VIDEO_DATA_PATH, method=RequestMethod.POST)
	public @ResponseStatus VideoStatus addVideo(@PathVariable long id) {
		// TODO Implement the logic to store the video data.
		assert(videos != null);
		
		Long longId = new Long(id);
		if (videos.containsKey(longId)) {
			Video v = videos.get(longId);
			assert(v != null);
			
			try {
				VideoFileManager.get().saveVideoData(v, videoData);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	/**
	 * Returns the video specified by the @param id if found.
	 * @param id
	 * @return
	 */
	@RequestMapping(value=VIDEO_ID_PATH, method=RequestMethod.GET)
	public @ResponseBody Video getVideo(@PathVariable long id) {
		// TODO Implement the logic to return the video for the given ID.
		assert(videos != null);
		
		Long longId = new Long(id);
		if (videos.containsKey(longId)) {
			Video v = videos.get(longId);
			
			try {
				if (VideoFileManager.get().hasVideoData(v)) {
					VideoFileManager.get().copyVideoData(v, out);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	/**
	 * This method returns a collection of all video 
	 * meta data stored by the service.
	 * @param v
	 * @return
	 */
	@RequestMapping(value=VIDEOS_PATH, method=RequestMethod.GET)
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
	@RequestMapping(value=VIDEOS_PATH, method=RequestMethod.DELETE)
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
}
