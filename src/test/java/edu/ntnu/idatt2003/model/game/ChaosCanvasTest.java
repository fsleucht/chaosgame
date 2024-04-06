package edu.ntnu.idatt2003.model.game;

import static org.junit.jupiter.api.Assertions.*;

import edu.ntnu.idatt2003.model.math.mathModel.Vector2D;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class ChaosCanvasTest {

  @Nested
  @DisplayName("Positive tests")
  class MethodsReturnsCorrectValues {

    ChaosCanvas canvas;
    @BeforeEach
    public void setUp() {
      canvas = new ChaosCanvas(100, 100, new Vector2D(0, 0), new Vector2D(200, 200));
    }

    @Nested
    @DisplayName("Positive tests for the getCanvasArray")
    public class PositiveTestsForGetCanvasArray {

      int[][] canvasArray;
      @BeforeEach
        public void setUp() {
        canvasArray = canvas.getCanvasArray();
      }

      @Test
      @DisplayName("getCanvasArray returns an array empty array")
      public void getCanvasArrayReturnsEmptyArray() {
        for (int i = 0; i < canvasArray.length; i++) {
          for (int j = 0; j < canvasArray[i].length; j++) {
            assertEquals(0, canvasArray[i][j]);
          }
        }
      }

      @Test
      @DisplayName("getCanvasArray returns an array with the correct dimensions")
      public void getCanvasArrayReturnsArrayWithCorrectDimensions() {
        assertEquals(100, canvasArray.length);
        assertEquals(100, canvasArray[0].length);
      }
    }

    @Test
    @DisplayName("Positive tests for the putPixel")
    public void setPixelSetsCorrectPixel() {
      canvas.putPixel(new Vector2D(150, 50));
      int[][] canvasArray = canvas.getCanvasArray();
      for (int i = 0; i < canvasArray.length; i++) {
        for (int j = 0; j < canvasArray[i].length; j++) {
          if (i == 74 && j == 74) {
            assertEquals(1, canvasArray[i][j]);
          } else {
            assertEquals(0, canvasArray[i][j]);
          }
        }
      }
    }

    @Nested
    @DisplayName("Sets up a canvas")
    class SetUpCanvas {
      @BeforeEach
      public void setUp() {
        canvas.putPixel(new Vector2D(150, 50));
      }

      @Test
      @DisplayName("Positive tests for the getPixel")
      public void getPixelReturnsCorrectValue() {
        canvas.putPixel(new Vector2D(150, 50));
        assertEquals(1, canvas.getPixel(new Vector2D(150, 50)));
        assertEquals(0, canvas.getPixel(new Vector2D(50, 150)));
        assertEquals(0, canvas.getPixel(new Vector2D(50, 50)));
      }

      @Test
      @DisplayName("Positive tests for the clear")
      public void clearSetsAllPixelsToZero() {
        canvas.clear();
        int[][] canvasArray = canvas.getCanvasArray();
        for (int i = 0; i < canvasArray.length; i++) {
          for (int j = 0; j < canvasArray[i].length; j++) {
            assertEquals(0, canvasArray[i][j]);
          }
        }
      }
    }
  }
}