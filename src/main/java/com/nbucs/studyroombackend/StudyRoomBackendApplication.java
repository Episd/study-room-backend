package com.nbucs.studyroombackend;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.nbucs.studyroombackend.mapper")
public class StudyRoomBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(StudyRoomBackendApplication.class, args);
    }

}
