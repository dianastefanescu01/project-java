package org.example.rest;

import org.example.repository.rest.RaceRestRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

@SpringBootApplication
public class StartRestServer implements WebMvcConfigurer {
    public static void main(String[] args) {
        SpringApplication.run(StartRestServer.class, args);
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*");
    }

    @Bean(name = "appProperties")
    @Primary
    public Properties getDBProperties() {
        Properties props = new Properties();
        try {
            URL resource = StartRestServer.class.getClassLoader().getResource("swimmingserver.properties");
            System.out.println("Searching appserver.properties in directory " + resource);
            if (resource != null) {
                try {
                    Path path = Paths.get(resource.toURI());
                    props.load(new FileReader(path.toFile()));
                } catch (URISyntaxException e) {
                    System.err.println("Invalid URI syntax: " + e.getMessage());
                }
            } else {
                System.err.println("Configuration file swimmingserver.properties not found");
            }
        } catch (IOException e) {
            System.err.println("Error loading configuration file: " + e.getMessage());
        }
        return props;
    }

    @Bean
    public RaceRestRepository raceRepository(Properties props) {
        return new RaceRestRepository(props);
    }

    @Bean
    public RaceRestController raceRestController(RaceRestRepository raceRepository) {
        return new RaceRestController(raceRepository);
    }
}

