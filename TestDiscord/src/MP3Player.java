import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.*;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;

import java.nio.file.*;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
 
public class MP3Player {

    private MessageChannel channel;
    private boolean playing = false;

    boolean playingPreset = false;
    boolean playingList = false;




    Player player;

    private ArrayList<String> playlist = new ArrayList<>();

    private ArrayList<String> presetlist = new ArrayList<>();

    public MP3Player(MessageChannel channel) {
        this.channel = channel;

        try {

            Path presetDir = Paths.get("assets/preset_playlist");
            Files.list(presetDir)
                .filter(Files::isRegularFile)
                .map(p -> "assets/preset_playlist/" + p.getFileName().toString())
                .forEach(presetlist::add);
                Collections.reverse(presetlist);
        } catch (java.io.IOException e) {
            System.out.println("Erreur quand on collecte les fichier de presetlist");
        }


    }

    

    public void play(String filename)  {
        try {
             player = new Player(new FileInputStream(filename)); // Creating a player
             player.play(); // Start playback
        } catch (FileNotFoundException | JavaLayerException e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        player.close();
        playing = false;
        playingPreset = false;
    }


    public void play_list() {
        String current;
        playing = true;
        current = playlist.getFirst();
        // Send message asynchronously, not blocking playback
        Thread playbackThread = new Thread(() -> {
            while ((!playlist.isEmpty()) & (playing == true)) {
                channel.sendMessage(current + " is now playing").queue();
                play(current);
                playlist.remove(current);
                
                // Run cleanup after song finishes
                try {
                    ProcessBuilder pb = new ProcessBuilder("bash", "./miaoudeur.sh", "-ro");
                    pb.directory(new java.io.File(System.getProperty("user.dir"), "src"));
                    pb.redirectErrorStream(true);
                    Process process = pb.start();
                    process.waitFor();
                } catch (Exception e) {
                    System.err.println("Error running cleanup command: " + e.getMessage());
                }
            }
            playing = false;
        });
        playbackThread.setPriority(Thread.MAX_PRIORITY); // Give audio thread highest priority
        playbackThread.start();
    }

    public void play_preset() {
                playingPreset = true;
                Thread playbackThread = new Thread(() -> {
                    int index = 0;
                    while (index < presetlist.size() & playingPreset == true) {
                        String track = presetlist.get(index);
                        channel.sendMessage(track + " is now playing").queue();
                        play(track);
                        index++;
                    }
                    playingPreset = false;
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

    public boolean isPlayingPreset() {
        return playingPreset;
    }

    public void setPlayingPreset(boolean playingPreset) {
        this.playingPreset = playingPreset;
    }

    public boolean isPlayingList() {
        return playingList;
    }

    public void setPlayingList(boolean playingList) {
        this.playingList = playingList;
    }

    
    



    

    







    
}

  