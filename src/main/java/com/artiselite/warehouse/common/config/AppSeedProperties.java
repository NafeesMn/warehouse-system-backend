package com.artiselite.warehouse.common.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "app.seed")
public class AppSeedProperties {

    private String managerEmail;
    private String managerPassword;
    private String managerFullName;
    private String operatorEmail;
    private String operatorPassword;
    private String operatorFullName;
}
