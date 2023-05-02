package ProjectFolder;

import static ProjectFolder.TestCookBook.*;

public class ConvertMethod {
    /**
     Method to for converting cups to teaspoons to tablespoons
     The user can enter a value and convert using the result calculation
     toTextField sets the text to a decimal number with 2 places and converts to string value.
     If the calculation can not be made, then error message is printed to console.
     */
    static void convert() { //used to convert from cups to teaspoons to tablespoons
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
