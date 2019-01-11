package com.accelerator.signbar;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.util.Formatter;
import java.util.Properties;

public enum Environment {

    RELEASE("release"),

    DEVELOP("develop");

    private static Environment CURRENT_ENVIRONMENT;

    private static String CONFIG_DIR_PATH;

    private static File CONFIG_DIR_FILE;

    private String profile;

    Environment(String profile) {
        this.profile = profile;
    }

    public String profile() {
        return profile;
    }

    public static void setCurrentEnvironment(Environment environment) {
        if (CURRENT_ENVIRONMENT != null) {
            throw new RuntimeException("当前Environment已经设置！不可重复操作！");
        }
        CURRENT_ENVIRONMENT = environment;

        Class<Environment> environmentClass = Environment.class;
        ProtectionDomain protectionDomain = environmentClass.getProtectionDomain();
        CodeSource codeSource = protectionDomain.getCodeSource();
        URL thisLocation = codeSource.getLocation();
        File thisFile = FileUtils.toFile(thisLocation);

        switch (Environment.getCurrentEnvironment()) {
            case DEVELOP:
                CONFIG_DIR_PATH = "classpath:";
                break;
            case RELEASE:
                try {
                    File signBarRootDirFile = thisFile.getParentFile().getParentFile();
                    String signBarRootDirPath = signBarRootDirFile.getCanonicalPath();
                    CONFIG_DIR_PATH = signBarRootDirPath + File.separator + "config";
                } catch (IOException e) {
                    throw new RuntimeException("获取配置文件路径发生异常！");
                }
                break;
        }

        try {
            CONFIG_DIR_FILE = ResourceUtils.getFile(CONFIG_DIR_PATH);
            CONFIG_DIR_PATH = CONFIG_DIR_FILE.getCanonicalPath();
        } catch (IOException e) {
            throw new RuntimeException("获取配置文件信息发生异常！");
        }

    }

    public static Environment getCurrentEnvironment() {
        if (CURRENT_ENVIRONMENT == null) {
            throw new RuntimeException("当前Environment未设置！不可获取Environment！");
        }
        return CURRENT_ENVIRONMENT;
    }

    public static String getConfigDirPath() {
        if (CURRENT_ENVIRONMENT == null) {
            throw new RuntimeException("当前Environment未设置！不可获取配置文件路径！");
        }
        if (StringUtils.isBlank(CONFIG_DIR_PATH)) {
            throw new RuntimeException("当前配置文件路径未设置！不可获取配置文件路径！");
        }
        return CONFIG_DIR_PATH;
    }

    public static File getConfigDirFile() {
        if (CURRENT_ENVIRONMENT == null) {
            throw new RuntimeException("当前Environment未设置！不可获取配置文件！");
        }
        if (StringUtils.isBlank(CONFIG_DIR_PATH)) {
            throw new RuntimeException("当前配置文件路径未设置！不可获取配置文件！");
        }
        if (CONFIG_DIR_FILE == null) {
            throw new RuntimeException("当前配置文件未设置！不可获取配置文件！");
        }
        return CONFIG_DIR_FILE;
    }

    public static String getConfigFilePath(String fileName) {
        return getConfigDirPath() + File.separator + fileName;
    }

    public static File getConfigFileFile(String fileName) {
        try {
            return ResourceUtils.getFile(getConfigFilePath(fileName));
        } catch (FileNotFoundException e) {
            String errorMsg = String.format("文件名:%s不存在!", fileName);
            throw new IllegalArgumentException(errorMsg);
        }
    }

    public static Resource getConfigFileSpringResource(String fileName) {
        return new FileSystemResource(getConfigFileFile(fileName));
    }


    public static String collectEnvironmentInfo() {
        String signBarVersion;
        if (CURRENT_ENVIRONMENT == Environment.RELEASE) {
            Class<Environment> environmentClass = Environment.class;
            Package environmentPackage = environmentClass.getPackage();
            signBarVersion = environmentPackage.getImplementationVersion();
        } else {
            signBarVersion = CURRENT_ENVIRONMENT.name();
        }
        Properties properties = System.getProperties();
        Formatter formatter = new Formatter();
        formatter.format("\n******************** Welcome to Signbar ********************\n");
        formatter.format("Signbar Version: %s\n", signBarVersion);
        formatter.format("Current Profile: %s\n", CURRENT_ENVIRONMENT.name());
        formatter.format("Java Home: %s\n", properties.get("java.home"));
        formatter.format("Java Vendor: %s\n", properties.get("java.vendor"));
        formatter.format("Java Version: %s\n", properties.get("java.version"));
        formatter.format("OS Architecture: %s\n", properties.get("os.arch"));
        formatter.format("OS Name: %s\n", properties.get("os.name"));
        formatter.format("OS Version: %s\n", properties.get("os.version"));
        formatter.format("*******************************************************\n");

        return formatter.toString();
    }


}