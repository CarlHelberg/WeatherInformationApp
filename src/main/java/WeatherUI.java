//import javax.print.DocFlavor;
//import javax.swing.*;
//import java.awt.*;
//import java.awt.event.ActionEvent;
//import java.awt.event.ActionListener;
//import java.io.IOException;
//import org.json.JSONObject;
//
//public class WeatherUI extends JFrame {
//
//    private JTextField locationInput; // Input field for location
//    private JLabel weatherInfoLabel; // Label to display weather info
//    private JComboBox<String> unitSelector; // Dropdown for selecting temperature units
//    private JLabel weatherIconLabel; // Icon representing weather condition
//
//    private WeatherService weatherService;
//
//    public WeatherUI() {
//        weatherService = new WeatherService(); // Initialize the weather service
//
//        setTitle("Weather Information App");
//        setSize(400, 300);
//        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        setLocationRelativeTo(null);
//
//        initUI();
//    }
//
//    // Setup the user interface
//    private void initUI() {
//        JPanel mainPanel = new JPanel(new BorderLayout());
//
//        // Top panel with input field and button
//        JPanel topPanel = new JPanel();
//        locationInput = new JTextField(15);
//        JButton fetchWeatherButton = new JButton("Get Weather");
//        fetchWeatherButton.addActionListener(new FetchWeatherAction());
//
//        // Add location input and fetch button to the top panel
//        topPanel.add(locationInput);
//        topPanel.add(fetchWeatherButton);
//
//        // Weather data display area
//        weatherInfoLabel = new JLabel();
//        weatherIconLabel = new JLabel();
//        JPanel weatherPanel = new JPanel();
//        weatherPanel.add(weatherIconLabel);
//        weatherPanel.add(weatherInfoLabel);
//
//        // Dropdown for unit selection (Celsius/Fahrenheit)
//        unitSelector = new JComboBox<>(new String[]{"metric", "imperial"});
//        unitSelector.addActionListener(new FetchWeatherAction());
//
//        // Combine all panels
//        mainPanel.add(topPanel, BorderLayout.NORTH);
//        mainPanel.add(weatherPanel, BorderLayout.CENTER);
//        mainPanel.add(unitSelector, BorderLayout.SOUTH);
//
//        // Add main panel to the frame
//        add(mainPanel);
//    }
//
//    // Action for fetching and displaying weather data
//    private class FetchWeatherAction implements ActionListener {
//        @Override
//        public void actionPerformed(ActionEvent e) {
//            try {
//                // Fetch weather data based on user input and selected units
//                String location = locationInput.getText();
//                String units = (String) unitSelector.getSelectedItem();
//                WeatherData weatherData = weatherService.fetchWeatherData(location, units);
//
//                // Parse and display weather info
//                String temperature = weatherData.getJSONObject("main").get("temp").toString();
//                String humidity = weatherData.getJSONObject("main").get("humidity").toString();
//                String conditions = weatherData.getJSONArray("weather").getJSONObject(0).getString("description");
//                String iconCode = weatherData.getJSONArray("weather").getJSONObject(0).getString("icon");
//
//                // Display weather information and icon
//                weatherInfoLabel.setText(String.format("Temp: %s, Humidity: %s%%, Conditions: %s", temperature, humidity, conditions));
//                String iconUrl = "https://openweathermap.org/img/wn/" + iconCode + ".png";
//                weatherIconLabel.setIcon(new ImageIcon(String.valueOf(new DocFlavor.URL(iconUrl)))); // Load icon from OpenWeatherMap
//            } catch (Exception ex) {
//                showErrorDialog("Failed to fetch weather data. Please try again.");
//            }
//        }
//    }
//
//    // Error dialog for failed data fetch or invalid input
//    private void showErrorDialog(String message) {
//        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
//    }
//}
