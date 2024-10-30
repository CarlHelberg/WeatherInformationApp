import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

public class WeatherData {
    private double temperature;
    private String humidity;
    private String windSpeed;
    private String conditions;
    private String weatherIcon;

    private String locale;
    private String unit;
    private String day;
    private String cloudCover;
    private String pressure;
    private String longInput;
    private String latInput;
    private String countryCode;
    private String zipCode;
    private String apiKey = "9165285debbbc2d2152db1a1e591d47e";

    public WeatherData(String locationInformation, String unit) {
        this.locale = locationInformation;
        this.unit = unit;
    }
    public WeatherData(String longInputOrZip,String latInputOrCountryCode, String unit) {
        try {
             Long.decode(longInputOrZip);
             Long.decode(latInputOrCountryCode);
            this.longInput = longInputOrZip;
            this.latInput = latInputOrCountryCode;
        }catch (Exception e){
            this.zipCode = longInputOrZip;
            this.countryCode = latInputOrCountryCode;
        }
        this.unit = unit;
    }

    public WeatherData(double temperature, String humidity, String windSpeed, String conditions, String icon, String latitude, String longitude, String countryCode, String zipCode) {
        this.temperature = temperature;
        this.humidity = humidity;
        this.windSpeed = windSpeed;
        this.conditions = conditions;
        this.weatherIcon = icon;
        this.latInput = latitude;
        this.longInput = longitude;
        this.countryCode = countryCode;
        this.zipCode = zipCode;
    }

    //Setters for all the private fields
    public String getLocale() {
        return locale;
    }
    public String getCountry() {return countryCode;}
    public String getDay() {
        return day;
    }
    public double getTemperature() { return temperature; }
    public String getHumidity() { return humidity; }
    public String getWindSpeed() { return windSpeed; }
    public String getConditions() { return conditions; }
    public String getWeatherIcon() { return weatherIcon; }
    public String getcloudCover() {return cloudCover;}

    public String getPressure() { return pressure; }
    public String getlongInput() { return longInput; }
    public String getlatInput() { return latInput; }

    //Build a String from the read Json file
    private String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }

    //Reads and returns the JsonObject
    public JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
        InputStream is = new URL(url).openStream();
        try {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            String jsonText = readAll(rd);
            JSONObject json = new JSONObject(jsonText);
            return json;
        } finally {
            is.close();
        }
    }

//    Make request to weather API
    public void fetchWeather(){
        if (!locale.equals("")){
            System.out.println("Inside the First IF, locale not nil");
                JSONObject json;

//        connects and asks the api to send the json file
                try {
                    System.out.println("Inside the weatherdata.fetchweather function");
                    String url = buildGeocodingUrl(locale,countryCode,zipCode,unit);
                    System.out.println("Building url, reading from url");
                    json = readJsonFromUrl(url);
                    System.out.println("end of fetchweather");
//                    json = readJsonFromUrl("http://api.openweathermap.org/data/2.5/weather?q="+locale+"&appid="+apiKey+"&lang=eng&units="+unit);
                } catch (IOException e) {
                    return;
                }

                applyWeatherData(json);

        }else if (!zipCode.equals("")&& !countryCode.equals("")) {
            System.out.println("Fetching Weather from Zip Code");
            fetchWeatherViaZip();
        } 
        else if (!latInput.equals("") && !longInput.equals("")) {
            System.out.println("Fetching Weather from Coordinates");
            fetchWeatherViaCoords();
        }
    }

    public void fetchWeatherViaZip() {

        JSONObject json;
        System.out.print("INSIDE fetchWeatherViaZip ");
        String zipUrl = ("https://api.openweathermap.org/data/2.5/weather?zip=" + zipCode + "," + countryCode + "&units="+unit +"&appid="+apiKey);

//        https://api.openweathermap.org/data/2.5/weather?zip={zip code},{country code}&appid={API key}
        try {
            json = readJsonFromUrl(zipUrl);
            applyWeatherData(json);
        } catch (Exception e) {
            System.out.println("Invalid Params to get Weather.");
        }
    }

    public void applyWeatherData(JSONObject json){
        JSONObject json_specific; //get specific data in jsonobject variable
        SimpleDateFormat df2 = new SimpleDateFormat("EEEE", Locale.ENGLISH); //Entire word/day as output
        Calendar c = Calendar.getInstance();
        int d = 0;

        try {
            this.locale = json.get("name").toString();
        }catch (Exception e){
            return;
        }


        json_specific = json.getJSONObject("main");
        this.temperature = json_specific.getInt("temp");

        this.pressure = json_specific.get("pressure").toString();

        this.humidity = json_specific.get("humidity").toString();

        json_specific = json.getJSONObject("wind");
        this.windSpeed = json_specific.get("speed").toString();

        json_specific = json.getJSONObject("clouds");
        this.cloudCover = json_specific.get("all").toString();

        c.add(Calendar.DATE, d);
        this.day = df2.format(c.getTime());

        json_specific = json.getJSONArray("weather").getJSONObject(0);
        this.conditions = json_specific.get("description").toString();

        this.weatherIcon = json_specific.get("icon").toString();

    }


//
    // Helper method to build the Geocoding API URL
    private String buildGeocodingUrl(String city, String country, String zip, String units) {
        System.out.println("Inside buildGEO");
        StringBuilder url = new StringBuilder("https://api.openweathermap.org/data/2.5/");
        if (!Objects.equals(null, zip)) {
            url.append("zip?zip=").append(zip);
        } else {
            url.append("weather?q=").append(city);
        }
        if (!Objects.equals(null, country)) url.append(",").append(country);

        url.append("&units=").append(units);
        url.append("&appid=").append(apiKey);
        System.out.println("End of build geo url");
        System.out.println(url);
        return url.toString();
    }
//
//    // Fetch weather data for given latitude and longitude coordinates
    public void fetchWeatherViaCoords() {

        JSONObject json;

        String coordUrl = ("https://api.openweathermap.org/data/2.5/weather?lat=" + latInput+ "&lon="+ longInput + "&units="+unit +"&appid="+apiKey);
        System.out.print("INSIDE fetchWeatherViaCoords: " + coordUrl);
        System.out.print(" ");

        try {
            json = readJsonFromUrl(coordUrl);
            applyWeatherData(json);
        } catch (IOException e) {
            return;
        }
    }
}
