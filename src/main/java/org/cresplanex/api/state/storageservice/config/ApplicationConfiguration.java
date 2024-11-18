package org.cresplanex.api.state.storageservice.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter @Setter
@RequiredArgsConstructor
@ConfigurationProperties("app")
public class ApplicationConfiguration {

    private final String name;
    private final String version;
}
