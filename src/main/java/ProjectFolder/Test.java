package ProjectFolder;

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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Test extends Application {
    private static final String FILENAME = "/Users/Ashley/IdeaProjects/FinalJavaFX/src/main/resources/files/recipes.txt";
    private static final String[] FILTER_OPTIONS = {"All", "Breakfast", "Lunch", "Dinner", "Dessert"};

    private ObservableList<Recipe> recipes = FXCollections.observableArrayList();
    private ObservableList<String> recipeNames = FXCollections.observableArrayList();
    private ListView<String> recipeListView;
    private TextArea ingredientsTextArea;
    private TextArea instructionsTextArea;
    private ComboBox<String> filterComboBox;
    private TextField searchTextField;

    public static void main(String[] args) { //main method
        launch(args);//runs the application
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Ashley's Nutritional Recipe Cookbook"); //name of cookbook
        readRecipesFromFile(); // Read recipes from file
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(10));

        // Create menu bar
        MenuBar menuBar = new MenuBar();
        Menu fileMenu = new Menu("File");
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
        GridPane searchPane = new GridPane();
        searchPane.setHgap(10);
        searchPane.setVgap(10);
        searchPane.setPadding(new Insets(10));

        Label filterLabel = new Label("Filter:");
        filterComboBox = new ComboBox<>(FXCollections.observableArrayList(FILTER_OPTIONS));
        filterComboBox.getSelectionModel().select(0);

        Label searchLabel = new Label("Search:");
        searchTextField = new TextField();

        searchPane.add(filterLabel, 0, 0);
        searchPane.add(filterComboBox, 1, 0);
        searchPane.add(searchLabel, 2, 0);
        searchPane.add(searchTextField, 3, 0);
        root.setBottom(searchPane);

        // Create recipe list view
        recipeListView = new ListView<>(recipeNames);
        recipeListView.setPrefWidth(200);
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
        ingredientsTextArea.setEditable(false);
        instructionsTextArea.setEditable(false);

        VBox textAreaBox = new VBox(10, new Label("Ingredients:"), ingredientsTextArea, new Label("Instructions:"), instructionsTextArea);
        root.setCenter(new SplitPane(recipeListView, textAreaBox));

        // Filter recipes based on filter and search text
        filterComboBox.setOnAction(e -> filterRecipes());
        searchTextField.textProperty().addListener((observable, oldValue, newValue) -> filterRecipes());

        // Show UI
        primaryStage.setScene(new Scene(root, 800, 600));
        primaryStage.show();
    }

    private void readRecipesFromFile() {
        try (BufferedReader br = new BufferedReader(new FileReader(FILENAME))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
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
            while ((line = br.readLine()) != null) {
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
                bw.write(recipe.getName() + "," + recipe.getIngredients() + "," + recipe.getInstructions());
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
        String selectedRecipeName = recipeListView.getSelectionModel().getSelectedItem();
        if (selectedRecipeName != null) {
            Recipe selectedRecipe = findRecipeByName(selectedRecipeName);
            if (selectedRecipe != null) {
                recipes.remove(selectedRecipe);
                recipeNames.remove(selectedRecipeName);
                recipeListView.getSelectionModel().clearSelection();
                deleteRecipeFromFile(selectedRecipe);
            }
        }
    }


    private void filterRecipes() {
        String filter = filterComboBox.getValue();
        String search = searchTextField.getText().toLowerCase();

        ObservableList<String> filteredRecipeNames = FXCollections.observableArrayList();

        for (Recipe recipe : recipes) {
            boolean filterMatch = filter.equals("All");
            boolean searchMatch = recipe.getName().toLowerCase().contains(search);

            if (filterMatch && searchMatch) {
                filteredRecipeNames.add(recipe.getName());
            }
        }

        recipeListView.setItems(filteredRecipeNames);
    }

    private Recipe findRecipeByName(String name) {
        for (Recipe recipe : recipes) {
            if (recipe.getName().equals(name)) {
                return recipe;
            }
        }
        return null;
    }

    private static class Recipe {
        private String name;
        private String ingredients;
        private String instructions;
        public Recipe(String name, String ingredients, String instructions) {
            this.name = name;
            this.ingredients = ingredients;
            this.instructions = instructions;
        }
        public String getName() {
            return name;
        }
        public void setName(String name) {
            this.name = name;
        }
        public String getIngredients() {
            return ingredients;
        }
        public void setIngredients(String ingredients) {
            this.ingredients = ingredients;
        }
        public String getInstructions() {
            return instructions;
        }
        public void setInstructions(String instructions) {
            this.instructions = instructions;
        }
    }
}