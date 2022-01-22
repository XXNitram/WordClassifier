package org.nitramproductions.com.wordclassifier.controller.helper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

public class NetworkHelper {

    public NetworkHelper() {

    }

    public void openLinkInDefaultBrowser(URI uri) throws IOException {
        if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
            Desktop.getDesktop().browse(uri);
        }
    }

    public String getLatestGithubReleaseVersion(URI latestGithubReleaseAPI) throws IOException, InterruptedException {
        HttpResponse<String> response = getLatestGithubReleaseResponse(latestGithubReleaseAPI);
        Map<String, Object> map = parseJSONResponseToMap(response);
        return map.get("name").toString();
    }

    private HttpResponse<String> getLatestGithubReleaseResponse(URI latestGithubReleaseAPI) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(latestGithubReleaseAPI)
                .header("Accept", "application/vnd.github.v3+json")
                .build();

        return HttpClient
                .newBuilder()
                .followRedirects(HttpClient.Redirect.ALWAYS)
                .build()
                .send(request, HttpResponse.BodyHandlers.ofString());
    }

    private Map<String, Object> parseJSONResponseToMap(HttpResponse<String> response) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(response.body(), new TypeReference<>() {});
    }
}
