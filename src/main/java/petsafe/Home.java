package petsafe;

import java.io.IOException;
import java.lang.reflect.Type;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import petsafe.components.Recipe;

public class Home {

  @FXML
  private FlowPane content;

  @FXML
  private ScrollPane scrollPane;

  @FXML
  private TextField searchBar;

  @FXML
  private VBox loading;

  private List<Recipe> recipes;

  @FXML
  public void initialize() {
    Gson gson = new Gson();

    recipes = new ArrayList<>();
    SQLite sql = new SQLite();
    ResultSet results = sql.selectAll("SELECT * FROM recipes ORDER BY name");

    try {
      while (results.next()) {
        // int id = results.getInt("id");
        String name = results.getString("name");
        String description = results.getString("description");
        String image = results.getString("image");
        boolean petsafe = results.getInt("petsafe") == 1;
        int rating = results.getInt("rating");
        int id = results.getInt("id");

        List<String> ingredients, procedure;

        Type listOfMyClassObject = new TypeToken<ArrayList<String>>() {
        }.getType();
        ingredients = gson.fromJson(results.getString("ingredients"), listOfMyClassObject);
        procedure = gson.fromJson(results.getString("procedure"), listOfMyClassObject);

        Recipe recipe = new Recipe(id, name, description, image, rating, petsafe, ingredients, procedure);
        recipes.add(recipe);
      }

      results.close();

    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }

    Timeline scrollTimeline = new Timeline();
    FadeTransition fadeOutTransition = new FadeTransition(Duration.seconds(1));

    // Scrollbar customization

    Platform.runLater(() -> {
      loading.setVisible(false);
      
      content.getChildren().addAll(recipes);
      adjustWidth();

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

  @FXML
  private void listenTextChange() {
    String searchString = searchBar.getText().trim().toLowerCase();

    List<Recipe> tmp = new ArrayList<>();

    for (Recipe recipe : recipes) {
      if (recipe.getRecipeName().toLowerCase().contains(searchString)) {
        tmp.add(recipe);
      }
    }

    content.getChildren().clear();
    content.getChildren().addAll(tmp);

  }

  private void adjustWidth() {
    if (recipes.size() == 0)
      return;

    double availableWidth = content.getWidth();

    int columns = (int) (availableWidth / (recipes.get(0).getPrefWidth()));
    double gap = Math.max((availableWidth % recipes.get(0).getPrefWidth()) / columns, 0.0);

    content.setHgap(gap);
    content.setVgap(gap);
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