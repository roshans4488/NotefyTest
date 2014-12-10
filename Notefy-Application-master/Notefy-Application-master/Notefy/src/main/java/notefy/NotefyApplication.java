package notefy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan
@EnableAutoConfiguration
public class NotefyApplication {

    public static void main(String[] args) {
        SpringApplication.run(NotefyApplication.class, args);
    }
}
