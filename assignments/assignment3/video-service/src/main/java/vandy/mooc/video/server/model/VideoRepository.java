package vandy.mooc.video.server.model;

import vandy.mooc.video.server.model.Video;

public interface VideoRepository {
	public Iterable<Video> findAll();
	
	public Video findOne(long id);
	
	public Video save(Video entity);
	
	public void deleteOne(long id);
	
	public void deleteAll();
}
