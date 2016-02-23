package com.accelerator.signbar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.File;
import java.io.IOException;

public abstract class AppInitialize {

    private static final String LOG_PATH_KEY = "logPath";

    private static final String LOG4J2_CONFIG_KEY = "log4j.configurationFile";

    private static Logger LOGGER;

    public static void initialize(Environment environment) {
        Environment.setCurrentEnvironment(environment);
        initLog4j2();
        initSpring();
        LOGGER.info(Environment.collectEnvironmentInfo());
    }

    private static void initLog4j2() {
        String log4jConfigXmlPath = Environment.getConfigFilePath("log4j2.xml");
        System.setProperty(LOG4J2_CONFIG_KEY, log4jConfigXmlPath);
        try {
            File configDirFile = Environment.getConfigDirFile();
            String logPathPrefix = configDirFile.getParentFile().getCanonicalPath();
            System.setProperty(LOG_PATH_KEY, logPathPrefix + File.separator + "logs");
        } catch (IOException e) {
            throw new RuntimeException("获取日志目录发生异常！");
        }
        LOGGER = LoggerFactory.getLogger(AppInitialize.class);
    }

    private static void initSpring() {
        String currentProfile = Environment.getCurrentEnvironment().profile();
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext();
        context.getEnvironment().setActiveProfiles(currentProfile);
        context.setConfigLocation("classpath:applicationContext.xml");
        context.refresh();
        context.start();
    }

}
