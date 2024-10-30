import javafx.scene.image.Image;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;

public class IconUtils {
    private static final String ICON_FOLDER = "icons/"; // set path to icons folder

    public static Image getWeatherIcon(String iconCode) { // icon and icon code comes from API
        String iconPath = ICON_FOLDER + iconCode + ".png"; // build complete icon path
        File iconFile = new File(iconPath); // define file

        if (!iconFile.exists()) { // Download ico if it doesn't exist
            try (InputStream in = new URL("https://openweathermap.org/img/wn/" + iconCode + ".png").openStream();
                 FileOutputStream out = new FileOutputStream(iconFile)) {

                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = in.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesRead);
                }
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        return new Image(iconFile.toURI().toString()); // whether downloaded or exists, returns the icon as image
    }
}
