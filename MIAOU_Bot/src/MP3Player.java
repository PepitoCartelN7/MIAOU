import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.*;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;

import java.nio.file.*;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;

/**
 * Lecteur MP3 pour la lecture de fichiers audio dans le bot Discord MIAOU.
 * 
 * Cette classe gère la lecture de fichiers MP3 via le channel discord ou la commande a été faite
 * on peut jouer des musiques ainsi qu'une liste de lecture préenregistrée.
 */
public class MP3Player {

    private MessageChannel channel;
    private boolean playing = false;

    boolean playingPreset = false;
    boolean playingList = false;




    Player player;

    private ArrayList<String> playlist = new ArrayList<>();

    private ArrayList<String> presetlist = new ArrayList<>();

    /**
     * Construit une nouvelle instance de MP3Player.
     * 
     * Initialise le lecteur avec le canal cible et charge la liste de présélections
     * à partir du répertoire "assets/preset_playlist".
     *
     * @param channel le canal Discord où la commande du bot a été envoyée
     */
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

    /**
     * Joue un fichier MP3.
     * <p>
     * Arrête la lecture actuelle (si elle existe) et commence la lecture du nouveau fichier.
     *
     * @param filename le chemin d'accès au fichier MP3 à jouer
     */
    public synchronized void play(String filename) {
        try {
            if (player != null) {
                player.close();
            }
            player = new Player(new FileInputStream(filename));
            player.play();
        } catch (FileNotFoundException | JavaLayerException e) {
            e.printStackTrace();
        }
    }

    /**
     * Arrête la lecture audio en cours.
     * <p>
     * Ferme le lecteur et met à jour les états de lecture.
     * NE MARCHE PAS POUR L'INSTANT TODO
     */
    public synchronized void stop() {
        if (player != null) {
            player.close();
        }
        playing = false;
        playingPreset = false;
    }


    /**
     * Joue tous les fichiers de la liste de lecture participative dans un thread séparé.
     * <p>
     * Parcourt la liste de lecture participative et joue chaque fichier séquentiellement
     * Exécute un script de nettoyage après chaque chanson.
     */
    public void play_list() {
        playing = true;
        
        Thread playbackThread = new Thread(() -> {
            while ((!playlist.isEmpty()) && (playing == true)) {
                String current = playlist.get(0);  // ← Move this INSIDE the loop
                channel.sendMessage(current + " is now playing").queue();
                play(current);
                playlist.remove(0);  // ← Remove by index, more reliable
                
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
        playbackThread.setPriority(Thread.MAX_PRIORITY);
        playbackThread.start();
    }

    /**
     * Joue tous les fichiers de la liste préenregistrée dans un thread séparé.
     * <p>
     * Parcourt la liste préenregistrée et joue chaque fichier séquentiellement.
     */
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

    /**
     * Ajoute un fichier à la liste de lecture.
     *
     * @param filename le chemin d'accès du fichier MP3 à ajouter
     */
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

  