import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.net.URL;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

public class WeatherUIController implements Initializable {
    // UI components for user interaction and display
    @FXML private JFXTextField countryCode, locationInput, longInput, latInput, invis, zipCode;
    @FXML private Label locale, temperature, day, windSpeed, cloudCover, pressure, humidity, conditions ,errors;
    @FXML private ImageView weatherIcon;
    @FXML private VBox mainLayout;
    @FXML private JFXComboBox<String> unitSelector;
    @FXML private JFXTextArea history;
    @FXML private JFXButton fetchWeather, reset;



    private List<String> searchHistory;

    public WeatherData weatherData;
    public String localeInformation;

    // Initialize the controller, weather service, and search history list
    public WeatherUIController() {
        searchHistory = new ArrayList<>();
        //Constructor to set the initial city to Pune
        this.localeInformation = "Pretoria";
    }

    public void initialize(URL location, ResourceBundle resources) {
        // Set up the unit selection options
        unitSelector.getItems().addAll("Metric", "Imperial");
        unitSelector.setValue("Metric");
        countryCode.setText("ZA");
        locationInput.setText("Pretoria");
        adjustBackgroundColor(); // Adjust background color based on time of day

        errors.setText("");
        weatherData = new WeatherData(localeInformation, unitSelector.getValue());
//        invis.requestFocus();

        //try catch block to see if there is internet and disabling ecery field
        try{
            System.out.println("In try for init. " );
            System.out.println(localeInformation );
            weatherData.fetchWeather();
            showWeatherData();
        } catch (Exception e){
            locale.setText("Data not available");
            locale.setTextFill(Color.TOMATO);
            showErrorModal("Could be a connection problem, please retry.");
//            clearFields();
        }
        //Set the city entered into textField on pressing enter on Keyboard
        locationInput.setOnKeyPressed(e -> {
            if(e.getCode() == KeyCode.ENTER){
                updateUI();
            }
        });
    }

//    @FXML
//    public void initialize() {
//        // Set up the unit selection options
//        unitSelector.getItems().addAll("Metric", "Imperial");
//        unitSelector.setValue("Metric");
//        locationInput.setText("Pretoria");
//        adjustBackgroundColor(); // Adjust background color based on time of day
//    }

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

    // Update UI with fetched weather data, displaying temperature, humidity, wind speed, and conditions
    private void updateUI() {
        //if user enters nothing into cityName field
        if(locationInput.getText().equals("") || (longInput.getText().equals("") && latInput.getText().equals(""))){
            if (zipCode.getText().equals("")) {
                showErrorModal("A City name or Coordinates are required");
                return;
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
//                bottomSet(false);
                invis.requestFocus();
            }catch(Exception e){
                System.out.println("failed to Update UI");
                locale.setText("Error!!");
                locale.setTextFill(Color.TOMATO);
                showErrorModal("City with the given name was not found.");
                clearFields();
                invis.requestFocus();
            }
        }


//        String weatherInfo = String.format("Temperature: %.1f°\nHumidity: %d%%\nWind Speed: %.1f m/s\nConditions: %s",
//                data.getTemperature(), data.getHumidity(), data.getWindSpeed(), data.getConditions());
//        weatherInfoLabel.setText(weatherInfo);

        // Load and display the appropriate weather icon
//        Image icon = IconUtils.getWeatherIcon(data.getWeatherIcon());
//        weatherIcon.setImage(icon);
    }

    private String[] displayUnit(){
        switch (unitSelector.getValue().toLowerCase()){
            case "imperial":
                return new String[] {"°F", "mp/h"};
            default:
                return new String[] {"°C", "m/s"};
        }
    }

    public void showWeatherData(){
        System.out.println("INSIDE SHOWW EATHER DATA! setting text in UI");
//        System.out.println("1" + weatherData.getLocale() + " +-+ " +weatherData.getTemperature() + " +-+ " + weatherData.getDay() + " +-+ " + weatherData.getConditions()+ " +-+ " );
        if (!weatherData.getLocale().equals("")) {
            String location = weatherData.getLocale();
            locale.setText(location.toUpperCase());
            addToSearchHistory(location);
        }

        temperature.setText(weatherData.getTemperature()+displayUnit()[0]);

        day.setText(weatherData.getDay().toUpperCase());
        conditions.setText(weatherData.getConditions().toUpperCase());
        addToSearchHistory(weatherData.getLocale());
        weatherIcon.setImage(IconUtils.getWeatherIcon(weatherData.getWeatherIcon()));
        windSpeed.setText(weatherData.getWindSpeed()+" "+displayUnit()[1]);
        cloudCover.setText(weatherData.getcloudCover()+"%");
        pressure.setText(weatherData.getPressure()+" hpa");
        humidity.setText(weatherData.getHumidity()+"%");
        System.out.println("End of SHOWW EATHER DATA!");
        adjustBackgroundColor();
    }

    private void showErrorModal(String message) {
        errors.setText(message);
        errors.setTextFill(Color.TOMATO);
        errors.setStyle("-fx-background-color: #fff; -fx-background-radius: 50px;");

        FadeTransition fadeIn = new FadeTransition(Duration.seconds(1), errors);
        fadeIn.setToValue(1);
        fadeIn.setFromValue(0);
        fadeIn.play();

        fadeIn.setOnFinished(event -> {
            PauseTransition pause = new PauseTransition(Duration.seconds(2));
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
        if (currentTime.isBefore(LocalTime.of(6, 0))) {
            mainLayout.setStyle("-fx-background-color: #0b26f4;"); // Morning: light blue
        } else if (currentTime.isBefore(LocalTime.of(8, 0))) {
            mainLayout.setStyle("-fx-background-color: #549eab;"); // Afternoon: orange
        } else if (currentTime.isBefore(LocalTime.of(12, 0))) {
            mainLayout.setStyle("-fx-background-color: #3c81c3;"); // Afternoon: orange
        } else if (currentTime.isBefore(LocalTime.of(17, 0))) {
            mainLayout.setStyle("-fx-background-color: #e6df57;"); // Afternoon: orange
        } else if (currentTime.isBefore(LocalTime.of(19, 0))) {
            mainLayout.setStyle("-fx-background-color: #d19910;"); // Afternoon: orange
        }

        else {
            mainLayout.setStyle("-fx-background-color: #0b26f4;"); // Evening: dark blue
        }
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
}
