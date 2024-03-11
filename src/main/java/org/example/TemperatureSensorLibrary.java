package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;


public class TemperatureSensorLibrary {

    private static final Logger log = LogManager.getLogger(TemperatureSensorLibrary.class);

    private static final int TIMEOUT_SECONDS = 30;
    private static final ExecutorService executorService = Executors.newFixedThreadPool(5);

    private final ObjectMapper objectMapper;
    private final RequestConfig requestConfig;

    public TemperatureSensorLibrary() {
        this.objectMapper = new ObjectMapper();
        this.requestConfig = RequestConfig.custom()
                .setSocketTimeout(TIMEOUT_SECONDS * 1_000)
                .setConnectTimeout(TIMEOUT_SECONDS * 1_000)
                .build();
    }

    public CompletableFuture<List<TemperatureData>> queryTemperature(List<String> sensorUrls) {
        CompletableFuture<Void> allOf = CompletableFuture.allOf(
                sensorUrls.stream()
                        .map(url -> CompletableFuture.supplyAsync(() -> getTemperature(url), executorService))
                        .toArray(CompletableFuture[]::new)
        );

        return allOf.thenApply(v ->
                        sensorUrls.stream()
                                .map(this::getTemperature)
                                .collect(Collectors.toList())
                )
                .exceptionally(throwable -> {
                    log.error("Error querying temperature sensors: {}", throwable.getMessage());
                    return null;
                })
                .whenComplete((result, throwable) -> executorService.shutdown());
    }

    private TemperatureData getTemperature(String sensorUrl) {
        try (CloseableHttpClient httpClient = HttpClients.custom().setDefaultRequestConfig(this.requestConfig).build()) {
            HttpGet request = new HttpGet(sensorUrl);
            String response = httpClient.execute(request,
                    httpResponse -> ResponseReader.getBody(httpResponse.getEntity().getContent()));

            return parseTemperature(sensorUrl, response);
        } catch (IOException e) {
            throw new RuntimeException("Error querying temperature from sensor " + sensorUrl, e);
        }
    }

    private TemperatureData parseTemperature(String sensorUrl, String response) {
        try {
            return objectMapper.readValue(response, TemperatureData.class);
        } catch (IOException e) {
            log.error("Error parsing temperature data from sensor {}: {}", sensorUrl, response);
            return new TemperatureData(sensorUrl, null);
        }
    }
}
