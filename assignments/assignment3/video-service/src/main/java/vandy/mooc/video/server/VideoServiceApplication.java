package vandy.mooc.video.server;

import javax.servlet.MultipartConfigElement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.embedded.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
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
}
