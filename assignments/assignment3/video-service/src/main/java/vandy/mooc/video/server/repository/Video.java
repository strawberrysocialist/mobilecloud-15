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
package vandy.mooc.video.server.repository;

import java.util.concurrent.atomic.AtomicLong;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Objects;

/**
 * A simple object to represent a video and its URL for viewing.
 * 
 * @author jules
 * 
 */
@Entity
public class Video {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	
	private String title;
	private long duration;
	private String location;
	private String subject;
	private String contentType;
	private AtomicLong totalSumOfRatings = new AtomicLong();
	private AtomicLong totalCountOfRatings = new AtomicLong();
	private float rating = 0; 

	// XXX How should the dataUrl be handled, JSON annotations needed?
	@JsonIgnore
	private String dataUrl;

	public Video() {
	}

	public Video(String title, long duration, String location, 
			String subject, String contentType, String dataUrl) {
		super();
		this.title = title;
		this.duration = duration;
		this.location = location;
		this.subject = subject;
		this.contentType = contentType;
		this.dataUrl = dataUrl;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public long getDuration() {
		return duration;
	}

	public void setDuration(long duration) {
		this.duration = duration;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	// XXX How should the dataUrl be handled, JSON annotations needed?
	@JsonProperty
	public String getDataUrl() {
		return dataUrl;
	}

	// XXX How should the dataUrl be handled, JSON annotations needed?
	@JsonIgnore
	public void setDataUrl(String dataUrl) {
		this.dataUrl = dataUrl;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public synchronized void addRating(int rating) {
		long sumOfStars = totalSumOfRatings.addAndGet(rating);
		long numberOfStars = totalCountOfRatings.incrementAndGet();
		this.rating = (float) sumOfStars / numberOfStars;
	}

	public float getRating() {
		return rating;
	}

	/**
	 * Two Videos will generate the same hashcode if they have exactly the same
	 * values for their title, duration, location, subject, and dataUrl.
	 * 
	 */
	@Override
	public int hashCode() {
		return Objects.hashCode(title, duration, 
				location, subject, dataUrl);
	}

	/**
	 * Two Videos are considered equal if they have exactly the same values for
	 * their title, duration, location, subject, and dataUrl.
	 * 
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Video) {
			Video other = (Video) obj;
			return Objects.equal(title, other.title)
					&& duration == other.duration
					&& Objects.equal(location, other.location)
					&& Objects.equal(subject, other.subject)
					&& Objects.equal(dataUrl, other.dataUrl);
		} else {
			return false;
		}
	}
}
