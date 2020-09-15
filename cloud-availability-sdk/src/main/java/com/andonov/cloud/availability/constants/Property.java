package com.andonov.cloud.availability.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Property {
    public static final String PROJECT_DIR = System.getenv("PROJECT_DIR");
    public static final String USER_DIR = System.getProperty("user.dir");
    public static final String PROJECT_BASE_DIR = System.getenv().getOrDefault("MAVEN_PROJECTBASEDIR", PROJECT_DIR);
    public static final String BASE_DIR = System.getProperty("basedir", USER_DIR);
    public static final String CF_PUBLIC_DOMAIN = "cf_public_domain";
    public static final String CF_APP_NAME = "app_name";
    public static final String CF_USER = "cf_user";
    public static final String CF_PASS = "cf_pass";
    public static final String CF_SPACE = "cf_space";
    public static final String CF_ORG = "cf_org";
    public static final String CF_INSTANCES = "cf_instances";
    public static final String CF_MEMORY = "cf_memory";
    public static final String CF_DISK_QUOTA = "cf_disk_quota";
    public static final String CF_HEALTHCHECK_TYPE = "cf_healthcheck_type";
    public static final String NETWORK_API = "network";
    public static final String CF_EU1_URL = "https://api.cf.eu10.hana.ondemand.com";
    public static final String CF_US1_URL = "https://api.cf.us10.hana.ondemand.com";

}
