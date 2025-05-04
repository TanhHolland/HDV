package qbike.position;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class PositionApplication {
    public static void main(String[] args) {
        SpringApplication.run(PositionApplication.class,args);
    }


    //Rest Template
    @Bean
    public RestTemplate restTemplate(){
        return new RestTemplate();
    }
}
