package hk.fish.fishpicturebackend;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("hk.fish.fishpicturebackend.mapper")
public class FishPictureBackendApplication {
    public static void main(String[] args) {
        SpringApplication.run(FishPictureBackendApplication.class, args);
    }
}
