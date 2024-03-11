package org.example;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;


@WebServlet("/temperatures")
public class TemperatureServlet extends HttpServlet {

    private static final Logger log = LogManager.getLogger(TemperatureServlet.class);

    private final TemperatureSensorLibrary temperatureSensorLibrary;

    public TemperatureServlet(TemperatureSensorLibrary temperatureSensorLibrary) {
        this.temperatureSensorLibrary = temperatureSensorLibrary;
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String requestBody = ResponseReader.getBody(request.getInputStream());

        if (requestBody.isEmpty()) {
            sendResponse(response, "Empty body");
            return;
        }

        List<String> sensorUrls = Arrays.asList(requestBody.split(","));

        CompletableFuture<List<TemperatureData>> temperatureData = temperatureSensorLibrary.queryTemperature(sensorUrls);

        try {
            List<TemperatureData> result = temperatureData.get();
            sendResponse(response, result.toString());
        } catch (InterruptedException | ExecutionException e) {
            String errorMessage = "Error getting temperature data: " + e.getMessage();
            log.error(errorMessage);
            sendResponse(response, errorMessage);
        }
    }

    private void sendResponse(HttpServletResponse response, String message) throws IOException {
        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");

        PrintWriter out = response.getWriter();
        out.println(message);
    }
}
