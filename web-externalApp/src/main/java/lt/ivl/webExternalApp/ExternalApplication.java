package lt.ivl.webExternalApp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
@EntityScan(basePackages = {"lt.ivl.components.domain"})
public class ExternalApplication {
    public static void main(String[] args) {
        SpringApplication.run(ExternalApplication.class);
    }
}
