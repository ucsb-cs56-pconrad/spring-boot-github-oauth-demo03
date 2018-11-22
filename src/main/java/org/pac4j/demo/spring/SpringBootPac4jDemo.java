package org.pac4j.demo.spring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.autoconfigure.data.mongo.MongoRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;

@SpringBootApplication
@EnableAutoConfiguration(exclude={
		MongoAutoConfiguration.class,
		MongoRepositoriesAutoConfiguration.class,
		MongoDataAutoConfiguration.class})
public class SpringBootPac4jDemo {

    public static void main(final String[] args) {
        SpringApplication.run(SpringBootPac4jDemo.class, args);
    }
}
