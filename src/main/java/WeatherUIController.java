import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.net.URL;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class WeatherUIController implements Initializable {
    // UI components for user interaction and display
    @FXML private JFXTextField countryCode, locationInput, longInput, latInput, zipCode; // These are editable fields that we use to get input.
    @FXML private Label additionalInfoLabel,weatherInfoLabel,windSpeedLabel, cloudCoverLabel, pressureLabel, humidityLabel; // These are actual labels, headers of a sort.
    @FXML private Label locale, temperature, day, windSpeed, cloudCover, pressure, humidity, conditions ,errors; // These are used to display data from the weather API, not editable
    @FXML private ImageView weatherIcon;
    @FXML private VBox mainLayout;
    @FXML private JFXComboBox<String> unitSelector; // Another input source
    @FXML private JFXTextArea history; // non-editable history window
    @FXML private JFXButton fetchWeather, reset; // Button input sources



    private List<String> searchHistory; // Init the history

    public WeatherData weatherData; // Declare our main data holding object
    public String localeInformation; // Declare the anticipated most used input source

    // Initialize the controller, weather service, and search history list
    public WeatherUIController() {
        searchHistory = new ArrayList<>();
        //Constructor to set the initial city to pretoria (south africa) where I'm from
        this.localeInformation = "Pretoria";
    }

    // On launch this initializing function will set default values and use *MY* default location, set on line 44
    public void initialize(URL location, ResourceBundle resources) {
        // Set up the unit selection options
        unitSelector.getItems().addAll("Metric", "Imperial");
        unitSelector.setValue("Metric"); // the best unit to use, so use it
        countryCode.setText("ZA"); // South Africa Country Code
        locationInput.setText("Pretoria"); //Default City entered in text area
        adjustBackgroundColor(); // Adjust background color based on time of day

        errors.setText(""); // blank string to start with, acts as placeholder
        weatherData = new WeatherData(localeInformation, unitSelector.getValue()); //start populating our weatherData with default values

        //try catch block to see if there is internet or compile issues
        try{
            weatherData.fetchWeather(); // get weather based on collected input (default info in this case)
            showWeatherData(); // This updates the UI with the values returned from the API
        } catch (Exception e){ // error handling for initial launch problems
            locale.setText("Data not available");
            locale.setTextFill(Color.RED);
            showErrorModal("Failed to Initialize."); // show a modal that displays more info, could return e or error code
            clearFields(); // Set input fields to empty strings
        }
        //Add key press listener to location input text field
        locationInput.setOnKeyPressed(e -> {
            if(e.getCode() == KeyCode.ENTER){ // listens for enter key
                updateUI();
            }
        });
    }

    @FXML
    private void handleButtonClicks(javafx.event.ActionEvent e) {
        if(e.getSource() == reset){
            clearFields();
        }else if (e.getSource() == fetchWeather) {
            onFetchWeather(e);
        }
    }

    @FXML
    private void clearFields() {
        System.out.println("Reset was clicked");
        locationInput.setText("");
        countryCode.setText("");
        unitSelector.setValue("Metric");
        longInput.setText("");
        latInput.setText("");
        locationInput.requestFocus();
    }

    // Action handler for fetching weather data based on location and units
    @FXML
    public void onFetchWeather(javafx.event.ActionEvent e) {
        System.out.println("Inside onFetchWeather, action registered");
        String newlocale = locationInput.getText();
        try {
            // Fetch weather data and update UI with the response
            if (!newlocale.equals("")){
                System.out.println("Trying to Fetching weather, locationInfo text not null: ");
                System.out.println("currently set location: " + weatherData.getLocale() + "New location: "+ newlocale);
                weatherData = new WeatherData(newlocale , unitSelector.getValue());
                weatherData.fetchWeather();
                showWeatherData();
            }else if (!zipCode.getText().equals("") && !countryCode.getText().equals("")) {
                System.out.println("Trying to Fetching weather from zip,info provided: zip:"+zipCode+" & countryCode: "+countryCode);
                weatherData = new WeatherData(zipCode.getText(), countryCode.getText() , unitSelector.getValue());
                weatherData.fetchWeatherViaZip();
                showWeatherData();
            }
            else {
                System.out.println("Inside catch onFetchWeather, trying  coordinates");
                try {
                    weatherData = new WeatherData(longInput.getText(), latInput.getText() , unitSelector.getValue());
                    weatherData.fetchWeatherViaCoords();
                    showWeatherData();
                }catch (Exception exception){
                    System.out.println(exception);
                }
            }

            System.out.println("After Fetching weather...");
        } catch (Exception ex) {
            showErrorDialog("Failed to fetch weather data. Please try again." + ex); // Error handling
        }
    }

    // Almost the same as showWeatherData(), this function gets input values from
    private void updateUI() {
        //if user enters nothing into cityName field
        if(locationInput.getText().equals("") || (longInput.getText().equals("") && latInput.getText().equals(""))){
            if (zipCode.getText().equals("")) {
                showErrorModal("A City name or Coordinates are required");
            }
        } else {
            try {
                System.out.println("Trying to Update UI");
                errors.setText("");
                this.localeInformation = locale.getText().trim();
                locale.setText((locationInput.getText().trim()).toUpperCase());
                weatherData = new WeatherData(localeInformation , unitSelector.getValue());
                System.out.println("Trying to show weather data");
                weatherData.fetchWeather();
                showWeatherData();
            }catch(Exception e){
                System.out.println("failed to Update UI");
                locale.setText("Error!!");
                locale.setTextFill(Color.RED);
                showErrorModal("City with the given name was not found.");
                clearFields();
            }
        }
    }

    private String[] displayUnit(){
        switch (unitSelector.getValue().toLowerCase()){
            case "imperial":
                return new String[] {"°F", "mp/h"};
            default:
                return new String[] {"°C", "m/s"};
        }
    }

//Updates all the fields on the UI with the data returned for the weather
    public void showWeatherData(){
        if (!weatherData.getLocale().equals("")) { // Checks if we already set the location from input
            locale.setText(weatherData.getLocale().toUpperCase());
        }
        // We need either the city name, or BOTH parts of the coordinates, at least
        if(locationInput.getText().equals("") || (longInput.getText().equals("") && latInput.getText().equals(""))){
            // If either of the below are empty after above fail, we can't get data
            if (zipCode.getText().equals("") || countryCode.getText().equals("")) {
                showErrorModal("A City name or Coordinates are required"); // We have no data to get weather with
            }
        }else { // This Else is the positive case where we have all the info we need
            try { // build weatherData object
                errors.setText("");
                this.localeInformation = locale.getText().trim();
                locale.setText((locationInput.getText().trim()).toUpperCase()); // checked above that this is not nil
                weatherData = new WeatherData(localeInformation , unitSelector.getValue()); // construct object
                // Call to get weather from API and further build data object
                weatherData.fetchWeather();
            }catch(Exception e){
                locale.setText("Error!!");  // General Alert
                locale.setTextFill(Color.RED);
                showErrorModal("City with the given name was not found."); // popup with more info for the user
                clearFields(); // reset fields to blank strings
            }
        }
        // If the try-catch above succeeds we have the data we need and can display it
        // Set the non-editable labels to display data
        temperature.setText(weatherData.getTemperature()+displayUnit()[0]); // index 0 is Celsius/Fahrenheit

        day.setText(weatherData.getDay().toUpperCase());
        conditions.setText(weatherData.getConditions().toUpperCase());
        weatherIcon.setImage(IconUtils.getWeatherIcon(weatherData.getWeatherIcon()));
        windSpeed.setText(weatherData.getWindSpeed()+" "+displayUnit()[1]); //index 1 is  m/s for metric and mp/h for imperial
        cloudCover.setText(weatherData.getcloudCover()+"%");
        pressure.setText(weatherData.getPressure()+" hpa");
        humidity.setText(weatherData.getHumidity()+"%");

        addToSearchHistory(weatherData.getLocale()); // add text to search history with time stamp
        adjustBackgroundColor(); // checks if background color needs to update
    }

    // Popup window that gives more info on the failure.
    private void showErrorModal(String message) {
        errors.setText(message);
        errors.setTextFill(Color.RED);
        errors.setStyle("-fx-background-color: #fff; -fx-background-radius: 50px;");

        FadeTransition fadeIn = new FadeTransition(Duration.seconds(1), errors);
        fadeIn.setToValue(1);
        fadeIn.setFromValue(0);
        fadeIn.play();

        fadeIn.setOnFinished(event -> {
            PauseTransition pause = new PauseTransition(Duration.seconds(4));
            pause.play();
            pause.setOnFinished(event2 -> {
                FadeTransition fadeOut = new FadeTransition(Duration.seconds(2), errors);
                fadeOut.setToValue(0);
                fadeOut.setFromValue(1);
                fadeOut.play();
            });
        });
    }

    // Adjusts the background color based on the current time (morning, afternoon, evening)
    private void adjustBackgroundColor() {
        LocalTime currentTime = LocalTime.now();
        if (currentTime.isBefore(LocalTime.of(6, 0))) { // white-washed blue
            mainLayout.setStyle("-fx-background-color: #0b26f4;");
        } else if (currentTime.isBefore(LocalTime.of(8, 0))) { // light blue
            mainLayout.setStyle("-fx-background-color: #549eab;");
        } else if (currentTime.isBefore(LocalTime.of(12, 0))) { // sky blue
            mainLayout.setStyle("-fx-background-color: #3c81c3;");
        } else if (currentTime.isBefore(LocalTime.of(17, 0))) { // yellow or light orange
            mainLayout.setStyle("-fx-background-color: #e6df57;");
        } else if (currentTime.isBefore(LocalTime.of(19, 0))) {
            mainLayout.setStyle("-fx-background-color: #d19910;"); // orange
        }

        else {
            mainLayout.setStyle("-fx-background-color: #3441a3;"); // Evening: dark blue
        }
        updateTextColors(); // Edit text to be visible on different backgrounds
    }

    // Adds location and timestamp to the search history and updates the history label
    private void addToSearchHistory(String location) {
        String entry = location + " at " + LocalTime.now().toString();
        searchHistory.add(entry);
        history.setText("Recent Searches:\n" + String.join("\n", searchHistory));
    }

    // Displays an error dialog box with a specified message
    private void showErrorDialog(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setContentText(message);
        alert.showAndWait();
    }



    // This method will be called from the update background function, mainly header labels
    public void updateTextColors() {
        // Get the current local time
        LocalTime now = LocalTime.now();
        boolean isNightTime = now.isAfter(LocalTime.of(19, 0)) || now.isBefore(LocalTime.of(6, 0));

        // Set the text colors based on the time of day
        String nightColor = "#b7b7b7"; // lighter grey on dark background
        String defaultColor = "#22448a"; // darker during day

        // Update label colors
        if (isNightTime) {
            setLabelColor(locale, nightColor);
            setLabelColor(temperature, nightColor);
            setLabelColor(weatherInfoLabel, nightColor);
            setLabelColor(day, nightColor);
            setLabelColor(windSpeedLabel, nightColor);
            setLabelColor(cloudCoverLabel, nightColor);
            setLabelColor(pressureLabel, nightColor);
            setLabelColor(humidityLabel, nightColor);
            setLabelColor(conditions, nightColor);
            setLabelColor(additionalInfoLabel, nightColor);
        } else {
            setLabelColor(locale, defaultColor);
            setLabelColor(temperature, defaultColor);
            setLabelColor(weatherInfoLabel, defaultColor);
            setLabelColor(day, defaultColor);
            setLabelColor(windSpeedLabel, defaultColor);
            setLabelColor(cloudCoverLabel, defaultColor);
            setLabelColor(pressureLabel, defaultColor);
            setLabelColor(humidityLabel, defaultColor);
            setLabelColor(conditions, defaultColor);
            setLabelColor(additionalInfoLabel, defaultColor);
        }

        // Update text field colors (assuming they should stay white)
        updateTextFieldColors(isNightTime);
    }

    // Helper function that changes the values on the labels
    private void setLabelColor(Label label, String color) {
        if (label != null && label.getTextFill() != null) {
            label.setTextFill(Color.web(color));
        }
    }

    // Change text colours to a darker white colour for eye strain at night
    private void updateTextFieldColors(boolean isNightTime) {
        String textFieldColor = isNightTime ? "#b7b7b7" : "white";
        countryCode.setStyle("-fx-text-fill: " + textFieldColor + "; -fx-prompt-text-fill: "+ textFieldColor + ";");
        locationInput.setStyle("-fx-text-fill: " + textFieldColor + "; -fx-prompt-text-fill: "+ textFieldColor + ";");
        longInput.setStyle("-fx-text-fill: " + textFieldColor + "; -fx-prompt-text-fill: "+ textFieldColor + ";");
        latInput.setStyle("-fx-text-fill: " + textFieldColor + "; -fx-prompt-text-fill: "+ textFieldColor + ";");
        zipCode.setStyle("-fx-text-fill: " + textFieldColor + "; -fx-prompt-text-fill: "+ textFieldColor + ";");
    }
}
