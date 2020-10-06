package lt.ivl.webInternalApp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan(basePackages = {"lt.ivl.components.domain"})
@EnableJpaRepositories(basePackages = {"lt.ivl.components.repository"})
@ComponentScan(basePackages = {"lt.ivl.components", "lt.ivl.webInternalApp"})
public class InternalApplication {
    public static void main(String[] args) {
        SpringApplication.run(InternalApplication.class);
    }
}
