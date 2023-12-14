package petsafe;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;

import com.google.gson.Gson;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Paint;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class AddRecipe {

  @FXML
  private TextArea description;

  @FXML
  private TextArea ingredients;

  @FXML
  private CheckBox ispetsafe;

  @FXML
  private TextField name;

  @FXML
  private TextArea procedure;

  @FXML
  private TextField imagePath;

  @FXML
  private Slider rating;

  @FXML
  private HBox ratingHolder;

  @FXML
  void addRecipe(ActionEvent event) {
    if (description.getText().trim().isEmpty()
        || ingredients.getText().trim().isEmpty()
        || name.getText().trim().isEmpty()
        || procedure.getText().trim().isEmpty()
        || imagePath.getText().trim().isEmpty()) {
      System.err.println("You need to fill in all inputs!");
      return;
    }

    Gson gson = new Gson();
    SQLite sql = new SQLite();

    List<String> ingredientList = Arrays.asList(ingredients.getText().trim().split("\n"));
    String jsonIngredients = gson.toJson(ingredientList);

    List<String> procedureList = Arrays.asList(procedure.getText().trim().split("\n"));
    String jsonProcedure = gson.toJson(procedureList);

    String[] sqlColumns = { "name", "description", "petsafe", "image", "ingredients", "procedure", "rating" };
    String[] sqlValues = { name.getText().trim(), description.getText().trim(), ispetsafe.isSelected() ? "1" : "0",
        imagePath.getText().trim(), jsonIngredients, jsonProcedure, rating.getValue() + "" };

    if (sql.addRow("recipes", sqlColumns, sqlValues)) {
      System.out.println("Successfully added new recipe!");

      name.clear();
      imagePath.clear();
      description.clear();
      ingredients.clear();
      procedure.clear();
      ispetsafe.setSelected(false);
    }
  }

  @FXML
  private void backToHome(ActionEvent event) throws IOException {
    App.setRoot("home");
  }

  @FXML
  private void about() throws IOException {
    App.setRoot("about");
  }

  @FXML
  private void importImage() {
    Stage stage = App.getStage();
    
    FileChooser fileChooser = new FileChooser();
    fileChooser.setTitle("Select Recipe Image");
    fileChooser.setInitialDirectory(new File(System.getProperty("user.home") + "/Documents"));
    fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif", "*.bmp"));

    File selectedFile = fileChooser.showOpenDialog(stage);

    if (selectedFile != null) {
      String destPath;
      if (App.runningFromJAR() || App.runningFromEXE()) {
        destPath = "classes/petsafe/assets/thumbnails/";
  
      } else {
        destPath = "src/main/resources/petsafe/assets/thumbnails/";
      }
      destPath = destPath + selectedFile.getName();

      try {
        Path srcPath = selectedFile.toPath();
        Path destPathObj = new File(destPath).toPath();

        Files.copy(srcPath, destPathObj, StandardCopyOption.REPLACE_EXISTING);

        imagePath.setText(selectedFile.getName());
        System.out.println("File copied successfully!");

      } catch (Exception e) {
        // TODO: handle exception
        e.printStackTrace();
        System.err.println("Error copying the file!");
      }
    }
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
  public void initialize() {
    rating.valueProperty().addListener((observable, oldVal, newVal) -> {
      ratingHolder.getChildren().clear();
      ratingToStars(newVal.intValue());
    });

    rating.setValue(4);
  }
}
