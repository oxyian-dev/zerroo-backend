package com.hionstudios;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.hionstudios.db.DbUtil;
import com.hionstudios.zerroo.flow.cutoff.DistributorFinancials;

@SpringBootApplication
@EnableScheduling
public class HionApplication {

    @Value("${zerroo.reconcile-on-startup:true}")
    private boolean reconcileOnStartup;

    public static void main(String[] args) {
        SpringApplication.run(HionApplication.class);
    }

    @Bean
    public ApplicationRunner reconcileFinancialsOnStartup() {
        return new ApplicationRunner() {
            @Override
            public void run(ApplicationArguments args) {
                if (reconcileOnStartup) {
                    DbUtil.open();
                    try {
                        DistributorFinancials.reconcileAll();
                    } finally {
                        DbUtil.close();
                    }
                }
            }
        };
    }

}
