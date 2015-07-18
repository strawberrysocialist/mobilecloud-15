package vandy.mooc.video.server.repository;

import java.util.Collection;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import vandy.mooc.video.client.VideoSvcApi;
import vandy.mooc.video.server.repository.Video;

/**
 * An interface for a repository that can store Video
 * objects and allow them to be searched by title.
 * 
 * @author jules
 *
 */
@Repository
public interface VideoRepository extends CrudRepository<Video, Long>{
	/**
	// Find all videos with a matching title (e.g., Video.name)
	public Collection<Video> findByTitle(
			// The @Param annotation tells Spring Data Rest which HTTP request
			// parameter it should use to fill in the "title" variable used to
			// search for Videos
			@Param(VideoSvcApi.TITLE_PARAMETER) String title);
	
	// Find all videos that are shorter than a specified duration
	public Collection<Video> findByDurationLessThan(
			// The @Param annotation tells tells Spring Data Rest which HTTP request
			// parameter it should use to fill in the "duration" variable used to
			// search for Videos
			@Param(VideoSvcApi.DURATION_PARAMETER) long maxDuration);
	
	// Find all videos that are shorter than a specified duration
	public Collection<Video> findByRatingGreaterThan(
			// The @Param annotation tells tells Spring Data Rest which HTTP request
			// parameter it should use to fill in the "rating" variable used to
			// search for Videos
			@Param(VideoSvcApi.RATING_PARAMETER) float minRating);
	*/
}
