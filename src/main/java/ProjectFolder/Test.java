package ProjectFolder;

//Ashley Grafner
//CSC 1061
//McDougle

import javafx.application.Application;
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
    private static final String FILENAME = "/Users/Ashley/IdeaProjects/FinalJavaFX/src/main/resources/files/recipes.txt"; //path for recipes.txt
    private final ObservableList<Recipe> recipes = FXCollections.observableArrayList(); //Observable list for our recipes
    private final ObservableList<String> recipeNames = FXCollections.observableArrayList(); //Observable list for our recipe names
    private ListView<String> recipeListView;
    private TextArea ingredientsTextArea, instructionsTextArea; //Text Area for ingredients, Text Area for instructions
    private TextField searchTextField; //Text Field for our search bar
    private CheckBox dairyCheckBox; //Check box for if we are filtering for dairy
    private ComboBox<String> fromUnitBox, toUnitBox;
    private TextField fromTextField, toTextField;
    private final String[] units = {"Teaspoons", "Tablespoons", "Cups"}; //units of measurement for conversion
    private final double[] factors = {1.0, 3.0, 48.0}; //factors needed for conversion.

    public static void main(String[] args) { //main method
        launch(args);//runs the application
    }

    @Override
    public void start(Stage primaryStage) { //UI aspects:
        primaryStage.setTitle("Ashley's Recipe Cookbook"); //name of cookbook (title)
        readRecipesFromFile(); // Read recipes from the file recipes.txt
        BorderPane borderPane = new BorderPane();
        borderPane.setPadding(new Insets(10));

        //Conversion Calculator Labels:
        Label fromLabel = new Label("From:");
        Label toLabel = new Label("To:");
        fromUnitBox = new ComboBox<>();
        toUnitBox = new ComboBox<>();
        fromTextField = new TextField();
        toTextField = new TextField();
        Button convertButton = new Button("Convert");
        convertButton.setOnAction(e -> convert()); //when convert is pressed convert() calculations will be made


        // Create menu bar
        MenuBar menuBar = new MenuBar();
        Menu fileMenu = new Menu("EDIT COOKBOOK");
        fileMenu.setStyle("-fx-font-size: 20px;"); //font size
        MenuItem newMenuItem = new MenuItem("New");
        newMenuItem.setOnAction(e -> showNewRecipeDialog()); //when new is pressed showNewRecipeDialog()
        MenuItem editMenuItem = new MenuItem("Edit");
        editMenuItem.setOnAction(e -> showEditRecipeDialog()); //when edit is pressed showEditRecipeDialog()
        MenuItem deleteMenuItem = new MenuItem("Delete");
        deleteMenuItem.setOnAction(e -> deleteSelectedRecipe()); //when delete is pressed deleteSelectedRecipe()
        fileMenu.getItems().addAll(newMenuItem, editMenuItem, deleteMenuItem);
        menuBar.getMenus().add(fileMenu); //adds (new, edit and delete)
        borderPane.setTop(menuBar); //menuBar is at the top


        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(10));

        // Create search bar
        Label dairyFilterLabel = new Label("Filter by Dairy:");
        dairyCheckBox = new CheckBox("Contains Dairy"); //CheckBox
        dairyCheckBox.setOnAction(e -> filterByDairy()); //when checked, it will filterByDairy()
        gridPane.add(dairyFilterLabel, 0, 3); //position
        gridPane.add(dairyCheckBox, 1, 3); //position
        borderPane.setBottom(new VBox(10, gridPane));

        Label searchLabel = new Label("Search:"); //Search Label
        searchTextField = new TextField();
        //Listener for searchTextField. calls the searchRecipes method with the updated list of recipes if changes:
        searchTextField.textProperty().addListener((observable, oldValue, newValue) -> searchRecipes());
        gridPane.add(searchLabel, 0, 0);
        gridPane.add(searchTextField, 1, 0);
        borderPane.setBottom(gridPane);

//PLACEMENT FOR CONVERSION
        Label conversion = new Label ("Conversion Calculator: "); //text label
        conversion.setStyle("-fx-font-size: 20px;"); //style
        gridPane.add(conversion, 5, 3);
        gridPane.add(fromLabel, 4, 4);
        gridPane.add(fromTextField, 5, 4);
        gridPane.add(fromUnitBox, 6, 4);
        gridPane.add(toLabel, 4, 5);
        gridPane.add(toTextField, 5, 5);
        gridPane.add(toUnitBox, 6, 5);
        gridPane.add(convertButton, 5, 6);
        fromUnitBox.getItems().addAll(units);
        toUnitBox.getItems().addAll(units);

        recipeListView = new ListView<>(recipeNames); // Creates a ListView to display the recipe names with the given array of strings
        recipeListView.setPrefWidth(200); //size of recipe View


        // Adds a listener to the selection of the recipeListView, which updates the displayed recipe's ingredients and instructions when a new recipe is selected:
        recipeListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            Recipe recipe = findRecipeByName(newValue); // Find the selected recipe by its name
            if (recipe != null) { // If the recipe exists, update the displayed ingredients and instructions
                ingredientsTextArea.setText(recipe.getIngredients());
                instructionsTextArea.setText(recipe.getInstructions());
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

        borderPane.setCenter(new SplitPane(recipeListView, textAreaBox));
        primaryStage.setScene(new Scene(borderPane, 800, 600)); //size of scene
        primaryStage.show(); //show UI
    }




    /**
     Method to display and read recipes to the recipes list and recipeNames list.
     Will read each line of the file and split array until 3 values for name, ingredients and instructions.
     Adds recipe and gets the names for recipe
     */
    private void readRecipesFromFile() {
        try (BufferedReader br = new BufferedReader(new FileReader(FILENAME))) { // Open the file
            String line;
            while ((line = br.readLine()) != null) { // Read each line of the file
                String[] values = line.split("[,\\n]"); // Split line into array of values using a regex to match commas and newline characters
                if (values.length == 3) { //3 values which are the name, the ingredients and the instructions:
                    Recipe recipe = new Recipe(values[0], values[1], values[2]);
                    recipes.add(recipe); //adds recipe
                    recipeNames.add(recipe.getName()); //get the names
                }
            }
        } catch (IOException e) { //error message
            System.out.println("Error reading recipes from file");
        }
    }
    /**
     Method to update a pre-existing recipe.
     Read each line and get the recipe name, ingredients and instructions.
     Adds each line to an array
     Then will open a file for writing the updated recipe
     */
    private void updateRecipeInFile(Recipe recipe) { //updating a recipe
        try {
            List<String> lines = new ArrayList<>();
            BufferedReader br = new BufferedReader(new FileReader(FILENAME)); //Open the file for reading
            String line;
            // Read each line of the file and add it to a list of lines, updating the line for the specified recipe if it is found:
            while ((line = br.readLine()) != null && !line.isEmpty()) { //while not empty or null:
                if (line.startsWith(recipe.getName() + ",")) {
                    //If ingredients/instructions have commas/newlines then replace with space
                    String ingredients = recipe.getIngredients().replaceAll("[,\n]", " ");
                    String instructions = recipe.getInstructions().replaceAll("[,\n]", " ");
                    line = recipe.getName() + "," + ingredients + "," + instructions;
                }
                lines.add(line); //adds line to array
            }
            br.close(); //close
            BufferedWriter bw = new BufferedWriter(new FileWriter(FILENAME)); //Open the file for writing
            for (String l : lines) { //Write each line in the list to the file
                bw.write(l);
                bw.newLine();
            }
            bw.flush(); // Flush and close the BufferedWriter
            bw.close();
    } catch (IOException e) { //error message
            System.out.println("Error updating recipe in file");
        }
    }
    /**
     Method to delete a recipe
     Will open a file for reading and add it to our arraylist and skipping the line of the specified recipe is found
     Open the file for writing and write each line to the file

     Will be used in the method deleteSelectedRecipe() later to delete a selected recipe.
     */
    private void deleteRecipeFromFile(Recipe recipe) {
        try {
            List<String> lines = new ArrayList<>();
            BufferedReader br = new BufferedReader(new FileReader(FILENAME)); //open file for reading
            String line;
            while ((line = br.readLine()) != null) { //Read each line of the file and add it to a list of lines, skipping the line for the specified recipe if it is found
                if (!line.startsWith(recipe.getName() + ",")) {
                    lines.add(line);
                }
            }
            br.close(); //close reader
            BufferedWriter bw = new BufferedWriter(new FileWriter(FILENAME)); /// Open the file for writing
            for (String l : lines) { //Write each line in the list to the file using the BufferedWriter
                bw.write(l);
                bw.newLine();
            }
            bw.close(); //close writer
        } catch (IOException e) { //error message
            System.out.println("Error deleting recipe from file");
        }
    }
    /**
     Method to display a dialog for a new recipe.
     The user can enter the name, ingredients, and instructions of the new recipe.
     Adds that recipe to the file.
     Writing the recipe name, ingredients and instructions to the file with a comma separated format.
     Any new lines or commas in the ingredients and instructions are replaced with a space.
     */
    private void showNewRecipeDialog() { //creating a new recipe
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("New Recipe");
        dialog.setHeaderText("Enter the name of the new recipe:");
        Optional<String> result = dialog.showAndWait(); //Prompts the user to enter the name of the new recipe.
        result.ifPresent(name -> { // If the user enters a name, creates a new Recipe object with an empty ingredients and instructions fields.
            Recipe recipe = new Recipe(name, "", ""); //starts blank for ingredients and instructions field
            // Adds the new recipe to the list of recipes and to the recipe list view.
            recipes.add(recipe);
            recipeNames.add(name);
            recipeListView.getSelectionModel().select(name);
            showEditRecipeDialog();


            try {  // Add new recipe to file
                BufferedWriter bw = new BufferedWriter(new FileWriter(FILENAME, true)); //open file for writing
                // Writing the recipe name, ingredients and instructions to the file with a comma separated format.
                // Any new lines or commas in the ingredients and instructions are replaced with a space.
                bw.write(recipe.getName() + "," + recipe.getIngredients().replaceAll("[\n,]", " ") + "," + recipe.getInstructions().replaceAll("[\n,]", " "));
                bw.newLine();
                bw.close();
            } catch (IOException e) { //error message
                System.out.println("Error adding new recipe to file");
            }
        });
    }
/**
 Method to display a dialog for editing a recipe.
 The user can edit the name, ingredients, and instructions of the selected recipe.
 If the user clicks save, the recipe is updated in the list view and saved to a file.
*/
 private void showEditRecipeDialog() { //editing a recipe
        String selectedRecipeName = recipeListView.getSelectionModel().getSelectedItem();
        if (selectedRecipeName != null) { //if the selected name isn't null:
            Recipe selectedRecipe = findRecipeByName(selectedRecipeName);// Get the selected recipe from the list view
            if (selectedRecipe != null) { //if the selected recipe isn't null:
                Dialog<Recipe> dialog = new Dialog<>(); // Create a new dialog for editing the recipe
                dialog.setTitle("Edit Recipe ");
                dialog.setHeaderText("Edit recipe for " + selectedRecipe.getName());

                // Create a grid pane to hold the text fields and labels
                GridPane grid = new GridPane();
                grid.setHgap(10);
                grid.setVgap(10);
                grid.setPadding(new Insets(20, 150, 10, 10));

                TextField nameTextField = new TextField(selectedRecipe.getName());
                TextArea ingredientsTextArea = new TextArea(selectedRecipe.getIngredients());
                ingredientsTextArea.setPrefRowCount(10);
                TextArea instructionsTextArea = new TextArea(selectedRecipe.getInstructions());
                instructionsTextArea.setPrefRowCount(10);

                //Positioning for our Labels and Text fields in the grid:
                grid.add(new Label("Name:"), 0, 0);
                grid.add(nameTextField, 1, 0);
                grid.add(new Label("Ingredients:"), 0, 1);
                grid.add(ingredientsTextArea, 1, 1);
                grid.add(new Label("Instructions:"), 0, 2);
                grid.add(instructionsTextArea, 1, 2);
                dialog.getDialogPane().setContent(grid);

                // Add buttons to dialog (Save and cancel)
                ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
                dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

              // Set the result converter to return a new recipe with the updated values
                dialog.setResultConverter(buttonType -> {
                    if (buttonType == saveButtonType) { //if saved return the new recipe we created!
                        return new Recipe(nameTextField.getText(), ingredientsTextArea.getText(), instructionsTextArea.getText());
                    }
                    return null; //otherwise we canceled
                });

                // Show the dialog and update the selected recipe if the user clicks save
                Optional<Recipe> result = dialog.showAndWait();
                result.ifPresent(recipe -> {
                    selectedRecipe.setName(recipe.getName());
                    selectedRecipe.setIngredients(recipe.getIngredients());
                    selectedRecipe.setInstructions(recipe.getInstructions());
                    recipeListView.refresh(); //refreshes
                    updateRecipeInFile(selectedRecipe); //updates
                });
            }
        }
    }
    /**
     Method to delete the selected recipe from the list view and from the file.
     If the user confirms the deletion, the recipe is removed from the list view and from the file.
     If the user cancels the deletion, no changes are made.
     */
    private void deleteSelectedRecipe() { //deleting a recipe
        String selectedRecipe = recipeListView.getSelectionModel().getSelectedItem();//Get the name of the selected recipe from the list view
        Recipe recipe = findRecipeByName(selectedRecipe); ///Find the recipe object from the name
        if (recipe != null) { //if the recipe isn't null
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION); //confirmation alert
            alert.setTitle("Confirmation Dialog");
            alert.setHeaderText("Confirm Deletion");
            alert.setContentText("Are you sure you want to delete the recipe: " + recipe.getName() + "?"); //confirmation

            Optional<ButtonType> result = alert.showAndWait(); /// Get the user's response and delete the recipe if the user confirms
            if (result.get() == ButtonType.OK) { // Remove the recipe from the list view and from the file
                recipes.remove(recipe);
                recipeNames.remove(recipe.getName());
                deleteRecipeFromFile(recipe);
                recipeListView.refresh(); //refreshes
            }
        }
    }
    /**
     Method to filter the list of recipes by dairy products.
     If the "dairyCheckBox" is selected, the method filters the recipes that contain dairy products.
     If the "dairyCheckBox" is not selected, the method shows all the recipes.
     The filtered list of recipes is then passed to the "searchRecipes" method to update the list view.
     */
    private void filterByDairy() { //filtering by dairy products
        boolean dairyChecked = dairyCheckBox.isSelected(); // Check if the "dairyCheckBox" is selected
        List<Recipe> filteredRecipes = new ArrayList<>(); // Create a list to store the filtered recipes
        for (Recipe recipe : recipes) { // Iterate over the list of recipes and check if they contain dairy products
            boolean hasDairy = false; //set the false until true

            // Split the ingredients string by comma delimiter into a list of individual ingredients:
            List<String> ingredients = List.of((recipe.getIngredients().split(",")));
            // Check each ingredient with our dairy-related keywords and set hasDairy flag to true if it does:
            for (String ingredient : ingredients) {
                if (ingredient.toLowerCase().contains("milk") ||
                        ingredient.toLowerCase().contains("cheese") ||
                        ingredient.toLowerCase().contains("yogurt") ||
                        ingredient.toLowerCase().contains("cream") ||
                        ingredient.toLowerCase().contains("butter")) {
                    hasDairy = true;
                    break;
                }
            }
            //Add the recipe to the filtered list if it contains dairy products and the "dairyCheckBox" is selected
            // or if it does not contain dairy products and the "dairyCheckBox" is not selected
            if (dairyChecked && hasDairy) {
                filteredRecipes.add(recipe);
            } else if (!dairyChecked) {
                filteredRecipes.add(recipe);
            }
        }
        searchRecipes();
    }
    /**
     * Search recipes based on the name of the recipe.
     */
    private void searchRecipes() {
        String searchText = searchTextField.getText().toLowerCase(); // from searchTextField and converted to lowercase.
        boolean dairyChecked = dairyCheckBox.isSelected(); // Check if the "dairyCheckBox" is selected
        List<Recipe> filteredRecipes = new ArrayList<>(); //List of Recipe objects called filteredRecipes.
        for (Recipe recipe : recipes) {
            boolean hasDairy = false; //set the false until true

            // Split the ingredients string by comma delimiter into a list of individual ingredients:
            List<String> ingredients = List.of((recipe.getIngredients().split(",")));
            // Check each ingredient with our dairy-related keywords and set hasDairy flag to true if it does:
            for (String ingredient : ingredients) {
                if (ingredient.toLowerCase().contains("milk") ||
                        ingredient.toLowerCase().contains("cheese") ||
                        ingredient.toLowerCase().contains("yogurt") ||
                        ingredient.toLowerCase().contains("cream") ||
                        ingredient.toLowerCase().contains("butter")) {
                    hasDairy = true;
                    break;
                }
            }

            //Add the recipe to the filtered list if it contains dairy products and the "dairyCheckBox" is selected
            // or if it does not contain dairy products and the "dairyCheckBox" is not selected
            if (!dairyChecked || hasDairy) {
                if (recipe.getName().toLowerCase().contains(searchText)) {
                    filteredRecipes.add(recipe);
                }
            }
        }
        //String objects called recipeNames is created to store the names of the filtered recipes:
        ObservableList<String> recipeNames = FXCollections.observableArrayList();
        for (Recipe recipe : filteredRecipes) { //The recipe names are added to the recipeNames list by iterating over the filtered recipes.
            recipeNames.add(recipe.getName());
        }
        recipeListView.setItems(recipeNames); //  recipeListView is then updated with the filtered recipe names using the setItems() method.
    }

    /**
     * Find a recipe by its name.
     * * return the recipe object if found, null otherwise.
     */
    private Recipe findRecipeByName(String name) {
        for (Recipe recipe : recipes) { //for every recipe
            if (recipe.getName().equals(name)) { //if getName() = name
                return recipe; //return
            }
        }
        return null; //otherwise null
    }
    /**
     Method to for converting cups to teaspoons to tablespoons
     The user can enter a value and convert using the result calculation
     toTextField sets the text to a decimal number with 2 places and converts to string value.
     If the calculation can not be made, then error message is printed to console.
     */
    private void convert() { //used to convert from cups to teaspoons to tablespoons
        try {
            // Get the indices of the selected units of measurement from the two ComboBoxes bellow:
            int fromIndex = fromUnitBox.getSelectionModel().getSelectedIndex();
            int toIndex = toUnitBox.getSelectionModel().getSelectedIndex();
            String fromText = fromTextField.getText().trim(); //text fromTextField and remove any whitespace

            if (fromIndex == -1 || toIndex == -1 ||fromText.isEmpty()) {// If either ComboBox index is -1 or empty
                return; //exit
            }

            double fromValue = Double.parseDouble(fromText); //parse double value from (fromText)
            double result = fromValue * factors[fromIndex] / factors[toIndex]; //fromValue * appropriate conversion factor then divide by the conversion factor.
            toTextField.setText(Double.toString(Math.round(result * 100.0) / 100.0));//rounds result, two decimal places and convert to string

        } catch (NumberFormatException ex) { //If user enters 'C' calculations will not be done, Error message printed to console
            System.out.println("Cannot convert your input. Please enter a number.");
        }
    }
}