import java.io.FileInputStream;
import java.io.FileNotFoundException;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;
 
public class MP3Player {
    public void play(String filename)  {
        try {
             Player player = new Player(new FileInputStream(filename)); // Creating a player
             player.play(); // Start playback
        } catch (FileNotFoundException | JavaLayerException e) {
            e.printStackTrace();
        }
    }
}

    

