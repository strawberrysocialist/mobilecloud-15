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
package vandy.mooc.video.server.model;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fluentinterface.ReflectionBuilder;
import com.fluentinterface.builder.Builder;

public class Video {

	public static VideoBuilder create() {
		return ReflectionBuilder.implementationFor(VideoBuilder.class).create();
	}

	public interface VideoBuilder extends Builder<Video> {
		public VideoBuilder withTitle(String title);
		public VideoBuilder withDuration(long duration);
		public VideoBuilder withSubject(String subject);
		public VideoBuilder withContentType(String contentType);
		public VideoBuilder withTotalNumberOfStars(double totalSumOfStars);
		public VideoBuilder withTotalSumOfStars(double totalNumberOfStars);
	}

	private long id;
	private String title;
	private long duration;
	private String location;
	private String subject;
	private String contentType;
	private double totalSumOfStars = 0;
	private double totalNumberOfStars = 0;

	@JsonIgnore
	private String dataUrl;

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

	@JsonProperty
	public String getDataUrl() {
		return dataUrl;
	}

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

	public double getTotalSumOfStars() {
		return totalSumOfStars;
	}

	public void setTotalSumOfStars(double totalSumOfStars) {
		this.totalSumOfStars = totalSumOfStars;
	}

	public double getTotalNumberOfStars() {
		return totalNumberOfStars;
	}

	public void setTotalNumberOfStars(double totalNumberOfStars) {
		this.totalNumberOfStars = totalNumberOfStars;
	}

	public void addRating(int numberOfStars) {
		setTotalSumOfStars(getTotalSumOfStars() + numberOfStars);
		setTotalNumberOfStars(getTotalNumberOfStars() + 1);
	}

	public float getAverageRating() {
		if (getTotalNumberOfStars() > 0) {
			return (float) (getTotalSumOfStars() / getTotalNumberOfStars());
		} else {
			return 0;
		}
	}

	@Override
	public int hashCode() {
		return Objects.hash(getTitle(), getDuration());
	}

	@Override
	public boolean equals(Object obj) {
		return (obj instanceof Video)
				&& Objects.equals(getTitle(), ((Video) obj).getTitle())
				&& getDuration() == ((Video) obj).getDuration();
	}
}