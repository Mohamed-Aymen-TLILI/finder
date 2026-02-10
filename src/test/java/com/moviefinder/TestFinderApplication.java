package com.moviefinder;

import org.springframework.boot.SpringApplication;

public class TestFinderApplication {

    public static void main(String[] args) {
        SpringApplication.from(FinderApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}
