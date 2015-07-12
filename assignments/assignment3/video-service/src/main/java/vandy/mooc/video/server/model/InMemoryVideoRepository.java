package vandy.mooc.video.server.model;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

public class InMemoryVideoRepository implements VideoRepository {

	private AtomicLong idGenerator = new AtomicLong(0L);
	
	private ConcurrentMap<Long,Video> videos = new ConcurrentHashMap<Long,Video>();
	
	@Override
	public Iterable<Video> findAll() {
		return videos.values();
	}

	@Override
	public Video findOne(long id) {
		if (videos.containsKey(id)) {
			return videos.get(id);
		} else {
			return null;
		}
	}

	@Override
	public Video save(Video entity) {
		checkAndSetId(entity);
		
		if (entity.getId() == 0) {
			return null;
		} else  if (!videos.containsKey(entity.getId())) {
			videos.put(entity.getId(), entity);
		} else {
			videos.replace(entity.getId(), entity);;
		}
		
		if (videos.containsKey(entity.getId())) {
			return videos.get(entity.getId());
		} else {
			return null;
		}
	}

	@Override
	public void deleteOne(long id) {
		if (videos.containsKey(id)) {
			videos.remove(id);
		}
	}
	
	@Override
	public void deleteAll() {
		videos.clear();
	}
	
	private void checkAndSetId(Video entity) {
		Long id = entity.getId();
		if (id == 0 && !videos.containsKey(id)) {
			entity.setId(idGenerator.incrementAndGet());
		}
	}
}
