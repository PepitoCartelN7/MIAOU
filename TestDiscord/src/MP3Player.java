import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;
 
public class MP3Player {

    private ArrayList<String> playlist = new ArrayList<>();

    public void play(String filename)  {
        try {
             Player player = new Player(new FileInputStream(filename)); // Creating a player
             player.play(); // Start playback
        } catch (FileNotFoundException | JavaLayerException e) {
            e.printStackTrace();
        }
    }


    public void play_list(){

        String current;
        playlist.add("assets/victory.mp3");
        playlist.add("assets/fly-me-to-the-moon-climax.mp3");

        
        while (!playlist.isEmpty()) {

            current = playlist.getFirst();
            System.out.println("PROUT1");
            play(current);
            playlist.remove(current);
            System.out.println("PROUT2");

        }
     };

    
}

    

