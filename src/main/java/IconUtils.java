import javafx.scene.image.Image;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;

public class IconUtils {
    private static final String ICON_FOLDER = "icons/";

    public static Image getWeatherIcon(String iconCode) {
        String iconPath = ICON_FOLDER + iconCode + ".png";
        File iconFile = new File(iconPath);

        if (!iconFile.exists()) {
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

        return new Image(iconFile.toURI().toString());
    }
}
