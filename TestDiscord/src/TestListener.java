import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;


public class TestListener extends ListenerAdapter 
{

    private MP3Player player = new MP3Player();

    @Override
    public void onMessageReceived(MessageReceivedEvent event)
    {
        if (event.getAuthor().isBot()) return;
        // We don't want to respond to other bot accounts, including ourself
        Message message = event.getMessage();
        String content = message.getContentRaw(); 
        // getContentRaw() is an atomic getter
        // getContentDisplay() is a lazy getter which modifies the content for e.g. console view (strip discord formatting)
        if (content.startsWith("!miaou play"))
        {
            MessageChannel channel = event.getChannel();
            channel.sendMessage("PLAYING SONG").queue(); // Important to call .queue() on the RestAction returned by sendMessage(...)
            player.play("assets/victory.mp3");
        }
    }
}