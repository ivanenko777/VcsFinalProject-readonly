package lt.ivl.webExternalApp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
@EntityScan(basePackages = {"lt.ivl.components.domain"})
@EnableJpaRepositories(basePackages = {"lt.ivl.components.repository"})
@ComponentScan(basePackages = {"lt.ivl.components", "lt.ivl.webExternalApp"})
public class ExternalApplication {
    public static void main(String[] args) {
        SpringApplication.run(ExternalApplication.class);
    }
}
