package lt.ivl.webExternalApp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class ExternalApplication {
    public static void main(String[] args) {
        SpringApplication.run(ExternalApplication.class);
    }
}
