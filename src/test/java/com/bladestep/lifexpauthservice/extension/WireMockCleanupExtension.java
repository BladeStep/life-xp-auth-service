package com.bladestep.lifexpauthservice.extension;

import com.github.tomakehurst.wiremock.client.WireMock;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
public class WireMockCleanupExtension implements AfterEachCallback {

    @Autowired
    private WireMock wireMockClient;

    @Override
    public void afterEach(ExtensionContext context) {
        log.info("Cleaning up WireMock...");

        try {
            if (wireMockClient != null) {
                wireMockClient.resetMappings();
                wireMockClient.resetRequests();
            }
            log.info("WireMock cleanup completed successfully");
        } catch (Exception e) {
            log.error("Error during WireMock cleanup", e);
            throw new RuntimeException("Failed to clean up WireMock", e);
        }
    }
}