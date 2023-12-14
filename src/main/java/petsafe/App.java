package petsafe;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

/**
 * JavaFX App
 */
public class App extends Application {

  private static Scene scene;
  private static Stage stage;

  @Override
  public void start(Stage stage) throws IOException {
    App.stage = stage;

    Image splashImage = new Image(App.class.getResourceAsStream("assets/splashscreen.png"));
    ImageView splashImageView = new ImageView(splashImage);

    Stage splashStage = new Stage();
    StackPane splashPane = new StackPane(splashImageView);

    Scene splashScene = new Scene(splashPane);
    splashStage.setScene(splashScene);

    splashStage.getIcons().add(new Image(App.class.getResourceAsStream("assets/logo-3.png")));
    splashStage.initStyle(StageStyle.UNDECORATED);
    splashStage.show();

    Platform.runLater(() -> {
      try {
        RunMainStage();
      } catch (IOException e) {
        e.printStackTrace();
      }

      splashStage.close();
      stage.show();
    });

  }

  private void RunMainStage() throws IOException {
    scene = new Scene(loadFXML("home"));
    stage.setScene(scene);

    stage.setHeight(720);
    stage.setWidth(1280);

    stage.setTitle("The Pet-safe Recipe Book");
    stage.setMaximized(true);

    stage.getIcons().add(new Image(App.class.getResourceAsStream("assets/logo-3.png")));
  }

  public static <T> T setRootGetController(String fxml) throws IOException {
    FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
    scene.setRoot(fxmlLoader.load());

    T controller = fxmlLoader.getController();
    return controller;
  }

  public static boolean runningFromJAR() {
    return App.class.getProtectionDomain().getCodeSource().getLocation().getPath().endsWith(".jar");
  }

  public static boolean runningFromEXE() {
    return App.class.getProtectionDomain().getCodeSource().getLocation().getPath().endsWith(".exe");
  }

  static void setRoot(String fxml) throws IOException {
    scene.setRoot(loadFXML(fxml));
  }

  private static Parent loadFXML(String fxml) throws IOException {
    FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
    return fxmlLoader.load();
  }

  public static Stage getStage() {
    return stage;
  }

  public static void main(String[] args) {
    launch();
  }
}