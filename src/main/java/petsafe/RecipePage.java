package petsafe;

import java.io.IOException;
import java.util.List;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Paint;
import javafx.util.Duration;

public class RecipePage {

  @FXML
  private Label description;

  @FXML
  private Label ingredients;

  @FXML
  private Label name;

  @FXML
  private Label procedure;

  @FXML
  private HBox ratingHolder;

  @FXML
  private ImageView imageContainer;

  @FXML
  private ScrollPane scrollPane;

  @FXML
  private Label isPetSafeLabel;

  @FXML
  void backToHome(ActionEvent event) throws IOException {
    App.setRoot("home");
  }

  public void printPassedParams(String name) {
    this.name.setText(name);
  }

  public void setRecipeData(int id, String name, int rating, Image recipeImg, String description, boolean ispetsafe,
      List<String> ingredients, List<String> procedure) {
    
    // Set all Data
    this.ingredients.setText(String.join("\n", ingredients)); // Ingredients
    this.procedure.setText(String.join("\n", procedure)); // Procedure

    this.name.setText(name); // Name
    this.description.setText(description); // Description
    
    // Set Rating
    isPetSafeLabel.setText(ispetsafe ? "Safe for pets" : "Not safe for pets");

    ratingToStars(rating);
    imageContainer.setImage(recipeImg);

    Timeline scrollTimeline = new Timeline();
    FadeTransition fadeOutTransition = new FadeTransition(Duration.seconds(1));

    Platform.runLater(() -> {
      scrollPane.lookup(".scroll-bar:vertical").setOpacity(0);

      scrollPane.vvalueProperty().addListener((observable, oldVal, newVal) -> {
        scrollPane.lookup(".scroll-bar:vertical").setOpacity(1);

        scrollTimeline.stop();
        scrollTimeline.getKeyFrames().clear();
        fadeOutTransition.stop();

        scrollTimeline.getKeyFrames().add(
            new KeyFrame(Duration.seconds(3), e -> {
              fadeOutTransition.setNode(scrollPane.lookup(".scroll-bar:vertical"));
              fadeOutTransition.setFromValue(1);
              fadeOutTransition.setToValue(0);
              fadeOutTransition.play();
            }));

        scrollTimeline.play();
      });
    });
  }

  private void ratingToStars(int rating) {
    for (int i = 0; i < rating; i++) { // Rating
      FontAwesomeIconView star_filled = new FontAwesomeIconView(FontAwesomeIcon.STAR, "16");
      star_filled.setFill(Paint.valueOf("#d6cc99"));
      ratingHolder.getChildren().add(star_filled);
    }

    for (int i = 0; i < (5 - rating); i++) {
      FontAwesomeIconView star_hollow = new FontAwesomeIconView(FontAwesomeIcon.STAR_ALT, "16");
      star_hollow.setFill(Paint.valueOf("#d6cc99"));
      ratingHolder.getChildren().add(star_hollow);
    }
  }

  @FXML
  private void addRecipe() throws IOException {
    App.setRoot("addRecipe");
  }

  @FXML
  private void about() throws IOException {
    App.setRoot("about");
  }
}
