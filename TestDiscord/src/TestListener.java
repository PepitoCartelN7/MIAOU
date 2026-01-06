import java.util.ArrayList;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;


public class TestListener extends ListenerAdapter 
{

    private MP3Player player;
    private boolean prePlayList;

    @Override
    public void onMessageReceived(MessageReceivedEvent event)
    {
        if (event.getAuthor().isBot()) return;
        if (prePlayList) return;

        Message message = event.getMessage();
        String content = message.getContentRaw(); 
        if (content.startsWith("!miaou playsong"))
        {   
            MessageChannel channel = event.getChannel();

            if (player == null) {
                player = new MP3Player(channel);
            }

            if (player.isPlayingPreset()) {
                channel.sendMessage("preset list already running, run \"!miaou stop\" before running this command").queue();
                return;
            }
            
            
            String restOfMessage = content.substring("!miaou play".length()).trim();
            channel.sendMessage(restOfMessage + " added to the playlist").queue();


            player.addToList("assets/" + restOfMessage + ".mp3");


            System.out.println(player.isPlaying());
            
            if (!player.isPlaying()) {
                player.play_list();
            }

        }
        if (content.startsWith("!miaou list")) {

            MessageChannel channel = event.getChannel();

            if (player == null || player.getPlayList().isEmpty()) {
                channel.sendMessage("The playlist is empty.").queue();
                return;
            }
        
            ArrayList<String> playlist = player.getPlayList();
        
            StringBuilder sb = new StringBuilder("**Playlist:**\n\n");
            for (int i = 0; i < playlist.size(); i++) {
                String song = playlist.get(i);
            
                
                song = song.replace("assets/", "").replace(".mp3","");
            
                if (i == 0) {
                    sb.append("▶ **Currently playing:** ").append(song).append("\n");
                } else {
                    sb.append(i).append(". ").append(song).append("\n");
                }
            }
        
            channel.sendMessage(sb.toString()).queue();
        
        }
        if (content.startsWith("!miaou stop")) {
            MessageChannel channel = event.getChannel();

            if (player == null) {
                channel.sendMessage("miaou not running").queue();
                return;
            }

            player.stop();

        }
        if (content.startsWith("!miaou play_preset")) {

            MessageChannel channel = event.getChannel();
            
            if (player == null) {
                player = new MP3Player(channel);
            }

            if (player.isPlayingPreset()) {
                channel.sendMessage("participative list already running, run \"!miaou stop\" before running this command").queue();
                return;
            }
            
            player.play_preset();

        }


        
    }
}       