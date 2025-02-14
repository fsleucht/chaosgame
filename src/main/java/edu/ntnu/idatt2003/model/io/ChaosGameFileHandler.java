package edu.ntnu.idatt2003.model.io;

import edu.ntnu.idatt2003.exceptions.CouldNotWriteException;
import edu.ntnu.idatt2003.exceptions.EmptyListException;
import edu.ntnu.idatt2003.exceptions.InvalidSignException;
import edu.ntnu.idatt2003.exceptions.InvalidVectorRangeException;
import edu.ntnu.idatt2003.exceptions.IsNullException;
import edu.ntnu.idatt2003.exceptions.WrongFileFormatException;
import edu.ntnu.idatt2003.exceptions.WrongLengthException;
import edu.ntnu.idatt2003.model.game.ChaosGameDescription;
import edu.ntnu.idatt2003.model.math.mathModel.Complex;
import edu.ntnu.idatt2003.model.math.mathModel.Matrix2x2;
import edu.ntnu.idatt2003.model.math.mathModel.Vector2D;
import edu.ntnu.idatt2003.model.math.transformation.AffineTransform2D;
import edu.ntnu.idatt2003.model.math.transformation.JuliaTransform;
import edu.ntnu.idatt2003.model.math.transformation.Transform2D;
import edu.ntnu.idatt2003.util.InputValidation;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Stream;

/**
 * This class handles reading and writing of files for the ChaosGameDescription class.
 */
public class ChaosGameFileHandler {

  /**
   * Reads a ChaosGameDescription from a file.
   *
   * @param file the file to be read.
   * @return the ChaosGameDescription read from the file.
   * @throws FileNotFoundException if the file is not found.
   * @throws WrongFileFormatException if the ChaosGameDescription could not be created.
   * @throws IsNullException if an object is null.
   * @throws EmptyListException if a list is empty.
   * @throws InvalidVectorRangeException if the vector range is invalid.
   * @throws InvalidSignException if the sign is invalid.
   */
  public ChaosGameDescription readFromFile(File file)
      throws WrongFileFormatException, IsNullException, EmptyListException,
      InvalidVectorRangeException, InvalidSignException, FileNotFoundException {
    List<List<String>> lines = divideFileToLines(file);

    try {
      InputValidation.validateListLessOrEqualThan(lines, 3, "lines");
    } catch (WrongLengthException e) {
      throw new WrongFileFormatException("The file should have at least three lines", e);
    }

    List<String> type = lines.getFirst();
    try {
      InputValidation.validateNotNull(type, "type");
      InputValidation.validateListLength(type, 1, "type");
    } catch (IsNullException | WrongLengthException e) {
      throw new WrongFileFormatException("The file should have one transformation type", e);
    }
    if (!type.getFirst().equals("Affine2D") && !type.getFirst().equals("Julia")) {
      throw new WrongFileFormatException("The file has an unsupported transformation type");
    }

    List<String> minCoords = lines.get(1);
    List<String> maxCoords = lines.get(2);
    try {
      InputValidation.validateNotNull(minCoords, "minCoords");
      InputValidation.validateNotNull(maxCoords, "maxCoords");
      InputValidation.validateListLength(minCoords, 2, "minCoords");
      InputValidation.validateListLength(maxCoords, 2, "maxCoords");
    } catch (IllegalArgumentException | WrongLengthException e) {
      throw new WrongFileFormatException("The file should have two min/max coordinates each", e);
    }

    double minX;
    double minY;
    double maxX;
    double maxY;
    try {
      minX = Double.parseDouble(minCoords.get(0));
      minY = Double.parseDouble(minCoords.get(1));
      maxX = Double.parseDouble(maxCoords.get(0));
      maxY = Double.parseDouble(maxCoords.get(1));
    } catch (NumberFormatException e) {
      throw new WrongFileFormatException("The min/max coordinates in the file are not numbers", e);
    }
    if (minX >= maxX || minY >= maxY) {
      throw new WrongFileFormatException(
          "The min coordinates should be less than the max coordinates");
    }

    List<Transform2D> transforms =
        (type.getFirst().equals("Affine2D"))
            ? packageToAffineList(lines) : packageToJuliaList(lines);

    return new ChaosGameDescription(transforms, new Vector2D(minX, minY),
        new Vector2D(maxX, maxY));
  }

  /**
   * Divides a file into lines.
   *
   * @param file the file to divide.
   * @return a list of lines in the file.
   * @throws FileNotFoundException if the file is not found.
   */
  private List<List<String>> divideFileToLines(File file)
      throws FileNotFoundException {
    List<List<String>> lines = new ArrayList<>();
    try (Scanner scanner = new Scanner(file)) {
      scanner.useDelimiter("#.*|\n");
      int i = 0;
      while (scanner.hasNext()) {
        String line = scanner.next().trim();
        if (line.isEmpty()) {
          continue;
        }
        lines.add(new ArrayList<>());
        try (Scanner lineScanner = new Scanner(line)) {
          while (lineScanner.hasNext()) {
            lineScanner.useDelimiter(", ");
            lines.get(i).add(lineScanner.next());
          }
          i++;
        }
      }
    } catch (FileNotFoundException e) {
      throw new FileNotFoundException("File not found");
    }
    return lines;
  }

  /**
   * Packages the lines of a file into a list of AffineTransform2D.
   *
   * @param lines the lines of the file.
   * @return a list of AffineTransform2D.
   * @throws WrongFileFormatException if the file is not formatted correctly.
   */
  private List<Transform2D> packageToAffineList(List<List<String>> lines)
      throws WrongFileFormatException {
    for (int i = 3; i < lines.size(); i++) {
      try {
        InputValidation.validateListLength(lines.get(i), 6, "transformation");
      } catch (WrongLengthException e) {
        throw new WrongFileFormatException(
            "The file should have six values for each transformation", e);
      }
      for (String value : lines.get(i)) {
        try {
          Double.parseDouble(value);
        } catch (NumberFormatException e) {
          throw new WrongFileFormatException(
              "The transformations in the file should only contain numbers", e);
        }
      }
    }

    List<Transform2D> transforms = new ArrayList<>();
    for (int i = 3; i < lines.size(); i++) {
      List<String> line = lines.get(i);
      double a00 = Double.parseDouble(line.get(0));
      double a01 = Double.parseDouble(line.get(1));
      double a10 = Double.parseDouble(line.get(2));
      double a11 = Double.parseDouble(line.get(3));

      double x0 = Double.parseDouble(line.get(4));
      double x1 = Double.parseDouble(line.get(5));

      transforms.add(new AffineTransform2D(
          new Matrix2x2(a00, a01, a10, a11), new Vector2D(x0, x1)));
    }
    return transforms;
  }

  /**
   * Packages the lines of a file into a list of JuliaTransform.
   *
   * @param lines the lines of the file.
   * @return a list of JuliaTransform.
   * @throws WrongFileFormatException if the file is not formatted correctly.
   * @throws InvalidSignException if the sign is invalid.
   */
  private List<Transform2D> packageToJuliaList(List<List<String>> lines)
      throws WrongFileFormatException, InvalidSignException {

    try {
      InputValidation.validateListLength(lines, 4, "Julia transformation");
    } catch (WrongLengthException e) {
      throw new WrongFileFormatException("The file should have one Julia transformation", e);
    }

    List<String> julia = lines.get(3);
    try {
      InputValidation.validateNotNull(julia, "Julia transformation");
    } catch (IsNullException e) {
      throw new WrongFileFormatException("The file should have one Julia transformation", e);
    }

    try {
      InputValidation.validateListLength(julia, 2, "Julia transformation");
    } catch (WrongLengthException e) {
      throw new WrongFileFormatException("The Julia transformation should have two values", e);
    }

    double real;
    double imaginary;
    try {
      real = Double.parseDouble(julia.get(0));
      imaginary = Double.parseDouble(julia.get(1));
    } catch (NumberFormatException e) {
      throw new WrongFileFormatException("The Julia transformation should only contain numbers", e);
    }

    JuliaTransform juliaTransformPositive = new JuliaTransform(new Complex(real, imaginary), 1);
    JuliaTransform juliaTransformNegative = new JuliaTransform(new Complex(real, imaginary), -1);

    List<Transform2D> transforms = new ArrayList<>();
    transforms.add(juliaTransformNegative);
    transforms.add(juliaTransformPositive);
    return transforms;
  }

  /**
   * Writes a ChaosGameDescription to a file.
   *
   * @param chaosGameDescription the ChaosGameDescription to write.
   * @param path the path to the file.
   * @throws CouldNotWriteException if the file could not be written to.
   */
  public void writeToFile(ChaosGameDescription chaosGameDescription, File path)
      throws CouldNotWriteException {
    try (BufferedWriter writer = new BufferedWriter(new FileWriter(path))) {
      String type = chaosGameDescription.getTransforms().getFirst()
          instanceof AffineTransform2D ? "Affine2D" : "Julia";
      String minCoords = chaosGameDescription.getMinCoords().getX0() + ", "
          + chaosGameDescription.getMinCoords().getX1();
      String maxCoords = chaosGameDescription.getMaxCoords().getX0() + ", "
          + chaosGameDescription.getMaxCoords().getX1();

      writer.write(type + "\n");
      writer.write(minCoords + "\n");
      writer.write(maxCoords + "\n");

      if (type.equals("Affine2D")) {
        for (Transform2D transform : chaosGameDescription.getTransforms()) {
          AffineTransform2D affineTransform = (AffineTransform2D) transform;
          writeAffineToFile(writer, affineTransform);
        }
      } else {
        JuliaTransform juliaTransform =
            (JuliaTransform) chaosGameDescription.getTransforms().getFirst();
        writeJuliaToFile(writer, juliaTransform);
      }
    } catch (IOException e) {
      throw new CouldNotWriteException("Could not write to file", e);
    }
  }

  /**
   * Writes an AffineTransform2D description to a file.
   *
   * @param writer the writer to write to.
   * @param affineTransform the AffineTransform2D to write.
   * @throws CouldNotWriteException if the AffineTransform2D could not be written to the file.
   */
  private void writeAffineToFile(BufferedWriter writer, AffineTransform2D affineTransform)
      throws CouldNotWriteException {
    Matrix2x2 matrix = affineTransform.getMatrix();
    double a00 = matrix.getA00();
    double a01 = matrix.getA01();
    double a10 = matrix.getA10();
    double a11 = matrix.getA11();

    Vector2D vector = affineTransform.getVector();
    double x0 = vector.getX0();
    double x1 = vector.getX1();

    try {
      writer.write(a00 + ", " + a01 + ", " + a10 + ", " + a11 + ", " + x0 + ", " + x1 + "\n");
    } catch (IOException e) {
      throw new CouldNotWriteException("Could not write to file", e);
    }
  }

  /**
   * Writes a JuliaTransform description to a file.
   *
   * @param writer the writer to write to.
   * @param juliaTransform the JuliaTransform to write.
   * @throws CouldNotWriteException if the JuliaTransform could not be written to the file.
   */
  private void writeJuliaToFile(BufferedWriter writer, JuliaTransform juliaTransform)
      throws CouldNotWriteException {
    Complex complex = juliaTransform.getPoint();
    double real = complex.getX0();
    double imaginary = complex.getX1();

    try {
      writer.write(real + ", " + imaginary + "\n");
    } catch (IOException e) {
      throw new CouldNotWriteException("Could not write to file", e);
    }
  }

  /**
   * Lists all files in the resources directory.
   *
   * @return a list of all files in the resources directory.
   * @throws IOException if the files could not be listed.
   */
  public List<String> listFiles() throws IOException {
    List<String> fileList = new ArrayList<>();
    try {
      Path resourceDirectory = Paths.get("src/main/resources");
      try (Stream<Path> paths = Files.walk(resourceDirectory)) {
        paths
            .filter(Files::isRegularFile)
            .forEach(path -> fileList.add(path.toString()));
      }
    } catch (IOException e) {
      throw new IOException("Could not list files");
    }
    return fileList;
  }
}
