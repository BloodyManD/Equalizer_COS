import Audio_Handler.*;
import java.io.File;
import javafx.fxml.FXMLLoader;

public class Main {
    public static void main(String[] args) {
        try {
            AudioPlay audioPlayer = new AudioPlay(new File("C:/Users/ngavr/Downloads/deep-purple-burn.wav"));

            Thread playThread = new Thread(audioPlayer::play);

            playThread.start();
            System.out.println("PLAY");

        } catch (Exception exception) {
            System.out.println(exception.getMessage());
        }
    }
}