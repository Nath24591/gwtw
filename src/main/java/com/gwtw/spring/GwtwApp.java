package com.gwtw.spring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gcp.data.datastore.core.DatastoreTemplate;
import org.springframework.cloud.gcp.data.datastore.repository.config.EnableDatastoreRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableDatastoreRepositories
@EnableScheduling
public class GwtwApp {

    @Autowired
    DatastoreTemplate datastoreTemplate;

    public static void main(String[] args) {
        SpringApplication.run(GwtwApp.class, args);
    }

}
