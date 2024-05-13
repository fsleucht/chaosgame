package edu.ntnu.idatt2003.view.components;

import edu.ntnu.idatt2003.controller.ChaosGameController;
import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;

public class TopBar extends StackPane {
  private ChaosGameController controller;

  public TopBar(ChaosGameController controller) {
    super();
    this.controller = controller;

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
      System.out.println("Read File");
    });

    Button writeFileButton = new PrimaryButton("Write File");
    writeFileButton.setOnAction(e -> {
      System.out.println("Write File");
    });

    Button editButton = new PrimaryButton("Edit");
    editButton.setOnAction(e -> {
      System.out.println("Edit");
    });

    TextField iterationsField = new InputBar();
    iterationsField.setPromptText("Iterations");
    iterationsField.setText("100000");

    Button runButton = new PrimaryButton("Run");
    runButton.setOnAction(e -> {
      this.controller.resetViewCanvas();
      this.controller.resetChaosGame();
      int iterations = Integer.parseInt(iterationsField.getText());
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
    buttonBox.setAlignment(javafx.geometry.Pos.CENTER);

    buttonBox.getChildren().addAll(
        leftButtonBox,
        middleButtonBox,
        rightButtonBox
    );

    this.setMinHeight(60);
    this.setMinWidth(900);
    this.setAlignment(javafx.geometry.Pos.CENTER);
    this.getChildren().add(buttonBox);
  }
}
