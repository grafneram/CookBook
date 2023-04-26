package ProjectFolder;

//Ashley Grafner
//CSC 1061
//McDougle

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.io.*;
import java.util.*;

public class Test extends Application {
    private static final String FILENAME = "/Users/Ashley/IdeaProjects/FinalJavaFX/src/main/resources/files/recipes.txt";
    private final ObservableList<Recipe> recipes = FXCollections.observableArrayList();
    private final ObservableList<String> recipeNames = FXCollections.observableArrayList();
    private ListView<String> recipeListView;
    private TextArea ingredientsTextArea;
    private TextArea instructionsTextArea;
    private TextField searchTextField;
    private CheckBox dairyCheckBox;
    private ComboBox<String> fromUnitBox, toUnitBox;
    private TextField fromTextField, toTextField;
    private final String[] units = {"Teaspoons", "Tablespoons", "Cups"};
    private final double[] factors = {1.0, 3.0, 48.0};

    public static void main(String[] args) { //main method
        launch(args);//runs the application
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Ashley's Nutritional Recipe Cookbook"); //name of cookbook (title)
        readRecipesFromFile(); // Read recipes from file
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(10));

        //Conversion Calculator Labels:
        Label fromLabel = new Label("From:");
        Label toLabel = new Label("To:");
        fromUnitBox = new ComboBox<>();
        toUnitBox = new ComboBox<>();
        fromTextField = new TextField();
        toTextField = new TextField();
        Button convertButton = new Button("Convert");
        convertButton.setOnAction(e -> convert());


        // Create menu bar
        MenuBar menuBar = new MenuBar();
        Menu fileMenu = new Menu("EDIT COOKBOOK");
        fileMenu.setStyle("-fx-font-size: 20px;");
        MenuItem newMenuItem = new MenuItem("New");
        newMenuItem.setOnAction(e -> showNewRecipeDialog());
        MenuItem editMenuItem = new MenuItem("Edit");
        editMenuItem.setOnAction(e -> showEditRecipeDialog());
        MenuItem deleteMenuItem = new MenuItem("Delete");
        deleteMenuItem.setOnAction(e -> deleteSelectedRecipe());
        fileMenu.getItems().addAll(newMenuItem, editMenuItem, deleteMenuItem);
        menuBar.getMenus().add(fileMenu);
        root.setTop(menuBar);

        // Create search bar
        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(10));


        Label dairyFilterLabel = new Label("Filter by Dairy:");
        dairyCheckBox = new CheckBox("Contains Dairy");
        dairyCheckBox.setOnAction(e -> filterByDairy());
        gridPane.add(dairyFilterLabel, 0, 3);
        gridPane.add(dairyCheckBox, 1, 3);
        root.setBottom(new VBox(10, gridPane));

        Label searchLabel = new Label("Search:"); //Search Label
        searchTextField = new TextField();

//PLACEMENT FOR CONVERSION
        gridPane.add(new Label("Conversion Calculator: "), 5, 2);
        gridPane.add(fromLabel, 4, 4);
        gridPane.add(fromTextField, 5, 4);
        gridPane.add(fromUnitBox, 6, 4);
        gridPane.add(toLabel, 4, 5);
        gridPane.add(toTextField, 5, 5);
        gridPane.add(toUnitBox, 6, 5);
        gridPane.add(convertButton, 5, 6);
        fromUnitBox.getItems().addAll(units);
        toUnitBox.getItems().addAll(units);

//PLACEMENT FOR SEARCH;
        gridPane.add(searchLabel, 0, 0);
        gridPane.add(searchTextField, 1, 0);
        root.setBottom(gridPane);

        // Create recipe list view
        recipeListView = new ListView<>(recipeNames);
        recipeListView.setPrefWidth(200); //size of recipe View
        recipeListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                Recipe recipe = findRecipeByName(newValue);
                if (recipe != null) {
                    ingredientsTextArea.setText(recipe.getIngredients());
                    instructionsTextArea.setText(recipe.getInstructions());
                }
            }
        });
        // Create ingredients and instructions text areas
        ingredientsTextArea = new TextArea();
        instructionsTextArea = new TextArea();
        ingredientsTextArea.setEditable(false); //can't be edited from here
        instructionsTextArea.setEditable(false);//can't be edited from here

        VBox textAreaBox = new VBox(10, new Label(" Ingredients:"), ingredientsTextArea, new Label(" Instructions:"), instructionsTextArea);
        textAreaBox.getChildren().forEach(node -> {
            if (node instanceof Label) {
                node.setStyle("-fx-font-size: 14px;"); //text is size 20
            } else if (node instanceof TextArea) {
                node.setStyle("-fx-font-size: 14px;"); //text is size 20
            }
        });

        root.setCenter(new SplitPane(recipeListView, textAreaBox));

        searchTextField.textProperty().addListener((observable, oldValue, newValue) -> searchRecipes(recipes)); //listener for our search bar

        primaryStage.setScene(new Scene(root, 800, 600)); //size of scene
        primaryStage.show(); //show UI
    }

    private void convert() { //used to convert from cups to teaspoons to tablespoons
        try {
            int fromIndex = fromUnitBox.getSelectionModel().getSelectedIndex();
            int toIndex = toUnitBox.getSelectionModel().getSelectedIndex();
            String fromText = fromTextField.getText().trim(); //trims white space (spaces)

            if (fromIndex == -1 || toIndex == -1 || fromText.isEmpty()) { //if either number is -1 or empty
                return;
            }

            double fromValue = Double.parseDouble(fromText);
            double result = fromValue * factors[fromIndex] / factors[toIndex];
            toTextField.setText(String.format("%.2f", result)); //double value formatting
        } catch (NumberFormatException ex) {
            System.out.println("Cannot convert letters to numbers"); //If user enters 'C' calculations will not be done, Error message printed to console
        }
    }

    private void readRecipesFromFile() {
        try (BufferedReader br = new BufferedReader(new FileReader(FILENAME))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split("[,\\n]");
                if (values.length == 3) {
                    Recipe recipe = new Recipe(values[0], values[1], values[2]);
                    recipes.add(recipe);
                    recipeNames.add(recipe.getName());
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading recipes from file");
            e.printStackTrace();
        }
    }

    private void updateRecipeInFile(Recipe recipe) {
        try {
            List<String> lines = new ArrayList<>();
            BufferedReader br = new BufferedReader(new FileReader(FILENAME));
            String line;
            while ((line = br.readLine()) != null && !line.isEmpty()) {
                if (line.startsWith(recipe.getName() + ",")) {
                    line = recipe.getName() + "," + recipe.getIngredients() + "," + recipe.getInstructions();
                }
                lines.add(line);
            }
            br.close();
            BufferedWriter bw = new BufferedWriter(new FileWriter(FILENAME));
            for (String l : lines) {
                bw.write(l);
                bw.newLine();
            }
            bw.flush();
            bw.close();
        } catch (IOException e) {
            System.err.println("Error updating recipe in file");
            e.printStackTrace();
        }
    }

    private void deleteRecipeFromFile(Recipe recipe) {
        try {
            List<String> lines = new ArrayList<>();
            BufferedReader br = new BufferedReader(new FileReader(FILENAME));
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.startsWith(recipe.getName() + ",")) {
                    lines.add(line);
                }
            }
            br.close();
            BufferedWriter bw = new BufferedWriter(new FileWriter(FILENAME));
            for (String l : lines) {
                bw.write(l);
                bw.newLine();
            }
            bw.close();
        } catch (IOException e) {
            System.err.println("Error deleting recipe from file");
            e.printStackTrace();
        }
    }

    private void showNewRecipeDialog() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("New Recipe");
        dialog.setHeaderText("Enter the name of the new recipe:");
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(name -> {
            Recipe recipe = new Recipe(name, "", "");
            recipes.add(recipe);
            recipeNames.add(name);
            recipeListView.getSelectionModel().select(name);
            showEditRecipeDialog();

            // Add new recipe to file
            try {
                BufferedWriter bw = new BufferedWriter(new FileWriter(FILENAME, true));
                bw.write(recipe.getName() + "," + recipe.getIngredients().replaceAll("[\n,]", " ") + "," + recipe.getInstructions().replaceAll("[\n,]", " "));
                bw.newLine();
                bw.close();
            } catch (IOException e) {
                System.err.println("Error adding new recipe to file");
                e.printStackTrace();
            }
        });
    }


    private void showEditRecipeDialog() {
        String selectedRecipeName = recipeListView.getSelectionModel().getSelectedItem();
        if (selectedRecipeName != null) {
            Recipe selectedRecipe = findRecipeByName(selectedRecipeName);
            if (selectedRecipe != null) {
                Dialog<Recipe> dialog = new Dialog<>();
                dialog.setTitle("Edit Recipe ");
                dialog.setHeaderText("Edit recipe for " + selectedRecipe.getName());
                GridPane grid = new GridPane();
                grid.setHgap(10);
                grid.setVgap(10);
                grid.setPadding(new Insets(20, 150, 10, 10));
                TextField nameTextField = new TextField(selectedRecipe.getName());
                TextArea ingredientsTextArea = new TextArea(selectedRecipe.getIngredients());
                ingredientsTextArea.setPrefRowCount(10);
                TextArea instructionsTextArea = new TextArea(selectedRecipe.getInstructions());
                instructionsTextArea.setPrefRowCount(10);
                grid.add(new Label("Name:"), 0, 0);
                grid.add(nameTextField, 1, 0);
                grid.add(new Label("Ingredients:"), 0, 1);
                grid.add(ingredientsTextArea, 1, 1);
                grid.add(new Label("Instructions:"), 0, 2);
                grid.add(instructionsTextArea, 1, 2);
                dialog.getDialogPane().setContent(grid);

                // Add buttons to dialog
                ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
                dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

                // Set the result converter
                dialog.setResultConverter(buttonType -> {
                    if (buttonType == saveButtonType) {
                        return new Recipe(nameTextField.getText(), ingredientsTextArea.getText(), instructionsTextArea.getText());
                    }
                    return null;
                });

                // Show dialog and update recipe if user clicks save
                Optional<Recipe> result = dialog.showAndWait();
                result.ifPresent(recipe -> {
                    selectedRecipe.setName(recipe.getName());
                    selectedRecipe.setIngredients(recipe.getIngredients());
                    selectedRecipe.setInstructions(recipe.getInstructions());
                    recipeListView.refresh();
                    updateRecipeInFile(selectedRecipe);
                });
            }
        }
    }

    private void deleteSelectedRecipe() {
        String selectedRecipe = recipeListView.getSelectionModel().getSelectedItem();
        Recipe recipe = findRecipeByName(selectedRecipe);
        if (recipe != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation Dialog");
            alert.setHeaderText("Confirm Deletion");
            alert.setContentText("Are you sure you want to delete the recipe: " + recipe.getName() + "?");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.OK) {
                recipes.remove(recipe);
                recipeNames.remove(recipe.getName());
                deleteRecipeFromFile(recipe);
            }
        }
    }

    private void filterByDairy() {
        boolean dairyChecked = dairyCheckBox.isSelected();
        List<Recipe> filteredRecipes = new ArrayList<>();
        for (Recipe recipe : recipes) {
            boolean hasDairy = false;
            List<String> ingredients = Arrays.asList(recipe.getIngredients().split(","));
            for (String ingredient : ingredients) {
                if (ingredient.toLowerCase().contains("milk") ||
                        ingredient.toLowerCase().contains("cheese") ||
                        ingredient.toLowerCase().contains("yogurt") ||
                        ingredient.toLowerCase().contains("cream") ||
                        ingredient.toLowerCase().contains("dairy") ||
                        ingredient.toLowerCase().contains("butter")) {
                    hasDairy = true;
                    break;
                }
            }
            if (dairyChecked && hasDairy) {
                filteredRecipes.add(recipe);
            } else if (!dairyChecked) {
                filteredRecipes.add(recipe);
            }

        }
        searchRecipes(filteredRecipes);
    }

    private void searchRecipes(List<Recipe> recipes) {
        String searchText = searchTextField.getText().toLowerCase();
        List<Recipe> filteredRecipes = new ArrayList<>();
        for (Recipe recipe : recipes) {
            if (recipe.getName().toLowerCase().contains(searchText)) {
                filteredRecipes.add(recipe);
            }
        }
        ObservableList<String> recipeNames = FXCollections.observableArrayList();
        for (Recipe recipe : filteredRecipes) {
            recipeNames.add(recipe.getName());
        }
        recipeListView.setItems(recipeNames);
    }

    private Recipe findRecipeByName(String name) {
        for (Recipe recipe : recipes) {
            if (recipe.getName().equals(name)) {
                return recipe;
            }
        }
        return null;
    }
}