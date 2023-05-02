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

public class TestCookBook extends Application {
    static final String FILENAME = "src/main/resources/files/recipes.txt"; //path for recipes.txt
    static final ObservableList<Recipe> recipes = FXCollections.observableArrayList(); //Observable list for our recipes
    static final ObservableList<String> recipeNames = FXCollections.observableArrayList(); //Observable list for our recipe names
    static ListView<String> recipeListView;
    static TextArea ingredientsTextArea, instructionsTextArea;
    static TextField searchTextField;
    static CheckBox dairyCheckBox; //Check box for if we are filtering for dairy
    static ComboBox<String> fromUnitBox, toUnitBox;
    static TextField fromTextField, toTextField;
    static final String[] units = {"Teaspoons", "Tablespoons", "Cups"}; //units of measurement for our conversion
    static final double[] factors = {1.0, 3.0, 48.0}; //factors needed for conversion.

    public static void main(String[] args) { //main method
        launch(args);//runs the application
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Ashley's Recipe Cookbook"); //name of cookbook (title)
        RecipeMethods.readRecipesFromFile(); // Read recipes from the file recipes.txt
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
        convertButton.setOnAction(e -> ConvertMethod.convert()); //when convert is pressed convert() calculations will be made

        // Create menu bar
        MenuBar menuBar = new MenuBar();
        Menu fileMenu = new Menu("EDIT COOKBOOK");
        fileMenu.setStyle("-fx-font-size: 20px;"); //font size
        MenuItem newMenuItem = new MenuItem("New");
        newMenuItem.setOnAction(e -> RecipeMethods.showNewRecipeDialog()); //when new is pressed showNewRecipeDialog()
        MenuItem editMenuItem = new MenuItem("Edit");
        editMenuItem.setOnAction(e -> RecipeMethods.showEditRecipeDialog()); //when edit is pressed showEditRecipeDialog()
        MenuItem deleteMenuItem = new MenuItem("Delete");
        deleteMenuItem.setOnAction(e -> RecipeMethods.deleteSelectedRecipe()); //when delete is pressed deleteSelectedRecipe()
        fileMenu.getItems().addAll(newMenuItem, editMenuItem, deleteMenuItem);
        menuBar.getMenus().add(fileMenu); //adds (new, edit and delete)
        borderPane.setTop(menuBar); //menuBar is at the top

        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(10));

        // Create dairy display info
        Label dairyFilterLabel = new Label("Filter by Dairy:");
        dairyCheckBox = new CheckBox("Contains Dairy"); //CheckBox
        dairyCheckBox.setOnAction(e -> RecipeMethods.filterByDairy()); //when checked, it will filterByDairy()
        gridPane.add(dairyFilterLabel, 0, 3); //position
        gridPane.add(dairyCheckBox, 1, 3); //position
        borderPane.setBottom(new VBox(10, gridPane));

        // Create search bar
        Label searchLabel = new Label("Search:"); //Search Label
        searchTextField = new TextField();
        //Listener for searchTextField. calls the searchRecipes method with the updated list of recipes if changes:
        searchTextField.textProperty().addListener((observable, oldValue, newValue) -> RecipeMethods.searchRecipes());
        gridPane.add(searchLabel, 0, 0);
        gridPane.add(searchTextField, 1, 0);
        borderPane.setBottom(gridPane);

//PLACEMENT FOR CONVERSION:
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
            Recipe recipe = RecipeMethods.findRecipeByName(newValue); // Find the selected recipe by its name
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
}