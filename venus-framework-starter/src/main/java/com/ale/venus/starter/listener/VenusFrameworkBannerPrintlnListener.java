package com.ale.venus.starter.listener;

import org.springframework.boot.SpringBootVersion;
import org.springframework.boot.ansi.AnsiColor;
import org.springframework.boot.ansi.AnsiOutput;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.lang.NonNull;

/**
 * VenusFramework logo打印监听器
 *
 * @author Ale
 * @version 1.0.0
 **/
public class VenusFrameworkBannerPrintlnListener implements ApplicationListener<ApplicationStartedEvent> {

    @SuppressWarnings("checkstyle:Regexp")
    @Override
    public void onApplicationEvent(@NonNull ApplicationStartedEvent event) {
        ConfigurableEnvironment environment = event.getApplicationContext().getEnvironment();
        String applicationName = environment.getProperty("spring.application.name", String.class, "unknown");
        int port = environment.getProperty("server.port", Integer.class, 8080);
        AnsiOutput.setEnabled(AnsiOutput.Enabled.ALWAYS);
        System.out.println(AnsiOutput.toString(AnsiColor.BRIGHT_GREEN,
            """
             _    __                         ______                                             __ \s
            | |  / /__  ____  __  _______   / ____/________ _____ ___  ___ _      ______  _____/ /__
            | | / / _ \\/ __ \\/ / / / ___/  / /_  / ___/ __ `/ __ `__ \\/ _ \\ | /| / / __ \\/ ___/ //_/
            | |/ /  __/ / / / /_/ (__  )  / __/ / /  / /_/ / / / / / /  __/ |/ |/ / /_/ / /  / ,<  \s
            |___/\\___/_/ /_/\\__,_/____/  /_/   /_/   \\__,_/_/ /_/ /_/\\___/|__/|__/\\____/_/  /_/|_| \s
            """
        ));
        System.out.println("SpringBoot版本：" + SpringBootVersion.getVersion());
        System.out.println("应用名称：" + applicationName);
        System.out.println("应用端口：" + port);
    }
}
