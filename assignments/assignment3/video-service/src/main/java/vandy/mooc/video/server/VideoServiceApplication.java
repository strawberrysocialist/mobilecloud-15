package vandy.mooc.video.server;

import java.io.IOException;

import javax.servlet.MultipartConfigElement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.embedded.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import vandy.mooc.video.server.model.InMemoryVideoRepository;
import vandy.mooc.video.server.model.VideoRepository;

@SpringBootApplication
//@EnableAutoConfiguration
//@ComponentScan
//@Configuration
public class VideoServiceApplication {

	private static final String MAX_REQUEST_SIZE = "50MB";

    public static void main(String[] args) {
        SpringApplication.run(VideoServiceApplication.class, args);
    }

	@Bean
    public MultipartConfigElement multipartConfigElement() {
		final MultipartConfigFactory factory = new MultipartConfigFactory();
		
		factory.setMaxFileSize(MAX_REQUEST_SIZE);
		factory.setMaxRequestSize(MAX_REQUEST_SIZE);

		return factory.createMultipartConfig();
	}
	
	@Bean
	public VideoFileManager videoFileManager() throws IOException {
		return VideoFileManager.get();
	}

	@Bean
	public VideoRepository videoRepository() {
		return new InMemoryVideoRepository();
	}
}
