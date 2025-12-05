import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;


public class TestListener extends ListenerAdapter 
{

    private MP3Player player;

    @Override
    public void onMessageReceived(MessageReceivedEvent event)
    {
        if (event.getAuthor().isBot()) return;
        Message message = event.getMessage();
        String content = message.getContentRaw(); 
        if (content.startsWith("!miaou play"))
        {
            MessageChannel channel = event.getChannel();
            String restOfMessage = content.substring("!miaou play".length()).trim();
            channel.sendMessage(restOfMessage + " added to the playlist").queue();
            if (player == null) {
                player = new MP3Player(channel);
            }
            player.addToList("assets/" + restOfMessage + ".mp3");


            if (!player.isPlaying()) {
                player.play_list();
            }

        }
    }
}