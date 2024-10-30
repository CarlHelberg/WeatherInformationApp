//import org.json.JSONObject;
//import java.io.BufferedReader;
//import java.io.InputStreamReader;
//import java.net.HttpURLConnection;
//import java.net.URL;
//
//public class WeatherService {
//    private static final String API_KEY = "6028d105c73c0bf362b52b363373776c"; // Replace with your OpenWeatherMap API key
//
//    // Fetches weather data for the given location
//    public WeatherData fetchWeatherData(String city,String country, String units) throws Exception {
//        String apiUrl = String.format(
//                "http://api.openweathermap.org/data/2.5/weather?q=%s&units=%s&appid=%s",
//                city,country, units, API_KEY);
//
//        URL url = new URL(apiUrl);
//        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//        connection.setRequestMethod("GET");
//
//        try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
//            StringBuilder response = new StringBuilder();
//            String line;
//            while ((line = reader.readLine()) != null) {
//                response.append(line);
//            }
//            JSONObject json = new JSONObject(response.toString());
//            return parseWeatherData(json);
//        }
//    }
//
//
//    // Parses JSON data and constructs a WeatherData object
//    private WeatherData parseWeatherData(JSONObject json) {
//        double temperature = json.getJSONObject("main").getDouble("temp");
//        String humidity = json.getJSONObject("main").getString("humidity");
//        String windSpeed = json.getJSONObject("wind").getString("speed");
//        String conditions = json.getJSONArray("weather").getJSONObject(0).getString("description");
//        String icon = json.getJSONArray("weather").getJSONObject(0).getString("icon");
//
//        return new WeatherData(temperature, humidity, windSpeed, conditions, icon);
//    }
////}
