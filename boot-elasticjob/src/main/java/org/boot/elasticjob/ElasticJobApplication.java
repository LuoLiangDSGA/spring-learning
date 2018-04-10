package org.boot.elasticjob;

import org.boot.elasticjob.job.ElasticJobHandler;
import org.boot.elasticjob.service.ElasticJobService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.Resource;

/**
 * @author luoliang
 */
@SpringBootApplication
public class ElasticJobApplication implements CommandLineRunner {
    @Resource
    private ElasticJobService elasticJobService;
    @Resource
    private ElasticJobHandler jobHandler;

    public static void main(String[] args) {
        SpringApplication.run(ElasticJobApplication.class, args);
    }

    @Override
    public void run(String... strings) throws Exception {
        elasticJobService.scanAddJob();
//        jobHandler.addJob("test-job", "0/3 * * * * ?", 1, "sss");
    }
}
