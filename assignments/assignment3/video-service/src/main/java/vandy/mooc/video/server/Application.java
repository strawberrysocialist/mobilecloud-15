package vandy.mooc.video.server;

import java.io.IOException;

import javax.servlet.MultipartConfigElement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.embedded.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
//import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import vandy.mooc.video.server.repository.VideoRepository;

// SpringBootApplication is shorthand for the following:
//@EnableAutoConfiguration
//@Configuration
//@ComponentScan
@SpringBootApplication
//Tell Spring to automatically create a JPA implementation of our
//VideoRepository
@EnableJpaRepositories(basePackageClasses = VideoRepository.class)
//Tell Spring to turn on WebMVC (e.g., it should enable the DispatcherServlet
//so that requests can be routed to our Controllers)
//@EnableWebMvc
public class Application {

	private static final String MAX_REQUEST_SIZE = "50MB";

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
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
}
