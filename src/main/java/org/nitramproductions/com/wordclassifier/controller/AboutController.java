package org.nitramproductions.com.wordclassifier.controller;

import javafx.animation.FadeTransition;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;
import org.nitramproductions.com.wordclassifier.controller.helper.AnimationHelper;
import org.nitramproductions.com.wordclassifier.controller.helper.NetworkHelper;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;
import java.util.Properties;

public class AboutController {

    @FXML
    private ImageView imageView;
    @FXML
    private Label updateInfo;
    @FXML
    private Hyperlink download;

    private final URI latestReleaseGithubAPI;
    private final URI latestReleaseGithub;
    private String latestVersion;
    private String currentVersion;
    private boolean updateAvailable;

    private final NetworkHelper networkHelper = new NetworkHelper();
    private final AnimationHelper animationHelper = new AnimationHelper();

    public AboutController() throws URISyntaxException, IOException {
        latestReleaseGithubAPI = new URI("https://api.github.com/repos/atom/atom/releases/latest");
        latestReleaseGithub = new URI("https://github.com/atom/atom/releases/latest");
        getProjectVersion();
    }

    @FXML
    private void initialize() {
        Image image = new Image(Objects.requireNonNull(getClass().getResourceAsStream("slow-zoom-nod.gif")));
        imageView.setImage(image);
        updateInfo.setVisible(false);
        download.setVisible(false);
    }

    @FXML
    private void onCheckForUpdatesClick() {
        handleCheckForUpdates();
    }

    @FXML
    private void onDownloadClick() throws IOException {
        networkHelper.openLinkInDefaultBrowser(latestReleaseGithub);
    }

    private void handleCheckForUpdates() {
        Task<String> task = new Task<>() {
            @Override
            protected String call() throws Exception {
                return networkHelper.getLatestGithubReleaseVersion(latestReleaseGithubAPI);
            }
        };
        task.setOnSucceeded(event -> {
            latestVersion = task.getValue();
            updateAvailable = !currentVersion.equals(latestVersion);
            showLabelDependingOnUpdateAvailable();
        });
        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    private void showLabelDependingOnUpdateAvailable() {
        FadeTransition fadeInTransitionLabel = animationHelper.setUpFadeInTransition(updateInfo, Duration.millis(2000));
        FadeTransition fadeInTransitionDownload = animationHelper.setUpFadeInTransition(download, Duration.millis(2000));
        if (updateAvailable) {
            updateInfo.setText("Eine neue Version steht zur Verfügung: " + latestVersion);
        } else {
            updateInfo.setText("Die Version ist auf dem aktuellsten Stand: " + currentVersion);
        }
        if (!updateInfo.isVisible()) {
            updateInfo.setVisible(true);
            fadeInTransitionLabel.playFromStart();
            if (updateAvailable) {
                download.setVisible(true);
                fadeInTransitionDownload.playFromStart();
            }
        }
        if (download.isVisible() && !updateAvailable) {
            FadeTransition fadeOutTransitionDownload = animationHelper.setUpFadeOutTransition(download, Duration.millis(2000));
            fadeOutTransitionDownload.setOnFinished(event -> download.setVisible(false));
            fadeOutTransitionDownload.playFromStart();
        }
    }

    private void getProjectVersion() throws IOException {
        Properties properties = new Properties();
        properties.load(getClass().getResourceAsStream("properties/version.properties"));
        currentVersion = properties.getProperty("version");
    }
}
