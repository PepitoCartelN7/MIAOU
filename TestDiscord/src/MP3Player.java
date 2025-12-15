import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
 
public class MP3Player {

    private MessageChannel channel;
    private boolean playing = false;

    public MP3Player(MessageChannel channel) {
        this.channel = channel;
    }

    private ArrayList<String> playlist = new ArrayList<>();

    public void play(String filename)  {
        try {
             Player player = new Player(new FileInputStream(filename)); // Creating a player
             player.play(); // Start playback
        } catch (FileNotFoundException | JavaLayerException e) {
            e.printStackTrace();
        }
    }


    public void play_list() {
        String current;
        playing = true;
        current = playlist.getFirst();
        // Send message asynchronously, not blocking playback
        channel.sendMessage(current + " is now playing").queue();
        Thread playbackThread = new Thread(() -> {
            while (!playlist.isEmpty()) {
                play(current);
                playlist.remove(current);
            }
            playing = false;
        });
        playbackThread.setPriority(Thread.MAX_PRIORITY); // Give audio thread highest priority
        playbackThread.start();
    }


    public void addToList(String filename) {
        playlist.add(filename);
    }

    public boolean playlistEmpty() {
        return playlist.isEmpty();
    }

    public boolean isPlaying() {
        return playing;
    }

    public ArrayList<String> getPlayList() {
        return playlist;
    }

    public void setPlaying(boolean playing) {
        this.playing = playing;
    }




    
}

    

