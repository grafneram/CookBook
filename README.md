# **Welcome to Ashley's Recipe Cookbook:**

```diff
Display Recipe: Name, ingredients, and instructions (read from file)
COOKBOOK: edit, new, delete each recipe. (write/update file)
Conversion Calculator: convert from cups, tablespoons, and teaspoons (research area)
```

Link to GitHub for this project: https://github.com/grafneram/CookBook

![alt-text](https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTfpcrYTcwV0f8xAuTSth4lMcEFaSVXMFJURxMBbq6I5E6IF22173zp1zQ2Tvv47dG8I74&usqp=CAU)

## Project Base Requirements:
1. [x] The project shall be written in Java.
2. [x] The project shall have a GUI interface.
3. [x] The project shall use JavaFX for the GUI.
4. [x] The project shall read from a file.
5. [x] The project shall write to a file.
6. [x] The project shall update a file.

## Project Specific Requirements:
1. [x] The project shall store and display information about cooking.
2. [x] The project shall allow the user to search for a recipe.
3. [x] The project shall allow the user to filter for a recipe.
4. [x] The project shall give the user a list of ingredients for a recipe.
5. [x] The project shall give the user instructions on how to cook the recipe.

## Research Area:
1. [x] Conversion to different measurements.
2. [x] The user will enter a number. Then they will select measurement type (Cups, tablespoons, or teaspoons)
3. [x] Then the user will select the measurement type they will convert to.
4. [x] From there, the user will click the 'Convert' button, and it will calculate the conversion.

## User Instructions:

#### Layout:
- When the user first opens the application they can view any recipes that are entered into the book
- The name of the recipe will be displayed in the top left. These will be in a list.
- In the top right, the user can see the ingredients for each recipe.
- In the bottom right, the user can see the instructions for each recipe.
- Each recipe is read from the Recipe.txt file.
- To select a different recipe, the user can left-click on a different recipe name to view details for a different recipe.

#### "COOKBOOK":
- If the user wants to make a new recipe, edit a recipe, or delete a recipe, the user will select the "EDIT COOKBOOK" button located in the very top left of the UI.
- From there, the user can select new, edit, or delete

#### New button in "COOKBOOK":
- If the User wants to create a new recipe. They will select the new button.
- A new panel will be opened and the user can enter details of the recipe including the name, the ingredients and the instructions for each recipe.
- If the user clicks "Save", the information will be written into Recipe.java.
- If the user clicks "Cancel", the information will not be written into Recipe.java and this panel will be closed.

#### Edit button in "COOKBOOK":

- If the user wants to edit an already existing recipe. They will select the edit button.
- The selected recipe will be opened in a new panel and the user can edit the name, the instructions, and the ingredients for that recipe.
- Once the user clicks "Save", the information will be updated in Recipe.java.
- IF the user clicks "Cancel", the information will not be updated and the existing information will stay the same.

#### Delete button in "COOKBOOK":

- If the user wants to delete an already existing recipe in the cook book they will select the delete button.
- If the user wants to delete a recipe they will first select the recipe they want to delete. Then, click the delete button.
- A new panel will be opened where it asks the user if they want to delete the recipe.
- The panel asks; "Are you sure you want to delete the recipe: (Recipe name)?"
- If the user selects "Ok", the recipe will be deleted from Recipe.java.
- If the user selects "Cancel", the recipe will not be deleted, and it will still be in the cookbook.

#### Search bar:

- The user can search by name if they wish.
- By left-clicking the search bar they can type into the bar. The cookbook will return any recipes that match by name.

#### Filter by dairy:
- The user can click the "Filter by Dairy:" check box to filter only by recipes that contain dairy.
- When clicking the box, the cookbook will filter by recipes that include dairy products.
- This includes the terms; milk, cheese, yogurt, cream, and butter within the ingredients.
- The user can also still search from ONLY dairy products as well.

#### Conversion Calculator: 
- If the user wants to convert the measurement types of (cups, tablespoons, or teaspoons) they will use the conversion calculator.
- In the "From:" section, the user will input the amount (ex: 2, 2.5, 9).
- In the dropdown next to this, the user will select if they are starting with cups, teaspoons, or tablespoons.
- In the "To:" selection, the user will select the measurement type they want to convert to.
- Then, the user will select the "Convert" button and the calculations will be made.
- **WARNING:** if the user enters a non-number (ex: "Test", "c"), the calculations will NOT be made. A custom error message will be entered to the console.