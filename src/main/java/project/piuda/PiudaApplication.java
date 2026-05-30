package project.piuda;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class PiudaApplication {

	public static void main(String[] args) {
		SpringApplication.run(PiudaApplication.class, args);
	}
}