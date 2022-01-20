package org.nitramproductions.com.wordclassifier.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;

public class AboutController {

    @FXML
    private ImageView imageView;

    public AboutController() {

    }

    @FXML
    private void initialize() {
        Image image = new Image(Objects.requireNonNull(getClass().getResourceAsStream("slow-zoom-nod.gif")));
        imageView.setImage(image);
    }

    private boolean updateAvailable() throws IOException, URISyntaxException, InterruptedException {
        return !getProjectVersion().equals(getLatestRelease());
    }

    private String getProjectVersion() throws IOException {
        Properties properties = new Properties();
        properties.load(getClass().getResourceAsStream("properties/version.properties"));
        return properties.getProperty("version");
    }

    private String getLatestRelease() throws IOException, URISyntaxException, InterruptedException {
        // test-repo for testing because actual repo is still private
        URI uri = new URI("https://api.github.com/repos/atom/atom/releases/latest");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .header("Accept", "application/vnd.github.v3+json")
                .build();

        HttpResponse<String> response = HttpClient
                .newBuilder()
                .followRedirects(HttpClient.Redirect.ALWAYS)
                .build()
                .send(request, HttpResponse.BodyHandlers.ofString());

        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> map = objectMapper.readValue(response.body(), new TypeReference<>() {});

        return map.get("name").toString();
    }
}
