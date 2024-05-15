package edu.ntnu.idatt2003.view.components;

import edu.ntnu.idatt2003.controller.ChaosGameController;
import java.io.File;

import edu.ntnu.idatt2003.view.components.buttons.PrimaryButton;
import edu.ntnu.idatt2003.view.components.buttons.SecondaryButton;
import edu.ntnu.idatt2003.view.components.popups.EditPopup;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.stage.Window;

/**
 * The top bar that contains buttons for choosing chaos game types,
 * editing and running the chaos game.
 */
public class TopBar extends StackPane {
  private final ChaosGameController controller;
  private int iterations;
  private EditPopup editPopup;
  private Window ownerWindow;

  /**
   * Constructor for the TopBar class.
   *
   * @param controller the controller for the chaos game.
   */
  public TopBar(ChaosGameController controller) {
    super();
    this.controller = controller;

    sceneProperty().addListener((observable, oldScene, newScene) -> {
      if (newScene != null) {
        newScene.windowProperty().addListener((observable1, oldWindow, newWindow) -> {
          if (newWindow != null) {
            ownerWindow = newWindow;
          }
        });
      }
    });


    Button juliaButton = new SecondaryButton("Julia Set");
    juliaButton.setOnAction(e -> {
      this.controller.resetViewCanvas();
      this.controller.resetChaosGameWithDescription("Julia Set");
    });

    Button sierpinskiButton = new SecondaryButton("Sierpinski");
    sierpinskiButton.setOnAction(e -> {
      this.controller.resetViewCanvas();
      this.controller.resetChaosGameWithDescription("Sierpinski");
    });

    Button barnsleyButton = new SecondaryButton("Barnsley");
    barnsleyButton.setOnAction(e -> {
      this.controller.resetViewCanvas();
      this.controller.resetChaosGameWithDescription("Barnsley");
    });

    Button readFileButton = new SecondaryButton("Read File");
    readFileButton.setOnAction(e -> {
      this.controller.resetViewCanvas();
      FileChooser fileChooser = new FileChooser();
      File file = fileChooser.showOpenDialog(ownerWindow);
      if (file != null) {
        this.controller.resetChaosGameWithFile(file);
      }
    });

    Button writeFileButton = new PrimaryButton("Write File");
    writeFileButton.setOnAction(e -> {
      FileChooser fileChooser = new FileChooser();
      FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt");
      fileChooser.getExtensionFilters().add(extFilter);
      File file = fileChooser.showSaveDialog(ownerWindow);
      if (file != null) {
        this.controller.writeChaosGameToFile(file);
      }
    });

    Button editButton = new PrimaryButton("Edit");
    editButton.setOnAction(e -> {
      this.controller.getPopupHandler().showEditPopup();
    });

    TextField iterationsField = new InputBar();
    iterationsField.setPromptText("Iterations");
    iterationsField.setText("100000");

    Button runButton = new PrimaryButton("Run");
    runButton.setOnAction(e -> {
      this.controller.resetViewCanvas();
      this.controller.resetChaosGame();
      try {
        iterations = Integer.parseInt(iterationsField.getText());
      } catch (NumberFormatException ex) {
        this.controller.getPopupHandler().showErrorPopup("Iterations must be a number");
        return;
      }
      this.controller.animateIterations(iterations);
    });

    HBox leftButtonBox = new HBox();
    HBox middleButtonBox = new HBox();
    HBox rightButtonBox = new HBox();

    leftButtonBox.getChildren().addAll(writeFileButton, editButton);
    middleButtonBox.getChildren().addAll(juliaButton, sierpinskiButton, barnsleyButton, readFileButton);
    rightButtonBox.getChildren().addAll(iterationsField, runButton);

    leftButtonBox.setSpacing(8);
    middleButtonBox.setSpacing(8);
    rightButtonBox.setSpacing(8);

    HBox buttonBox = new HBox();
    buttonBox.setSpacing(50);
    buttonBox.setMaxHeight(25);
    buttonBox.setMaxWidth(860);
    buttonBox.setAlignment(Pos.CENTER);

    buttonBox.getChildren().addAll(
        leftButtonBox,
        middleButtonBox,
        rightButtonBox
    );

    this.setMinHeight(60);
    this.setMinWidth(900);
    this.setAlignment(Pos.CENTER);
    this.getChildren().add(buttonBox);
  }
}
