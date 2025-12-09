import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

public class Commands extends ListenerAdapter {

	@Override
	public void onMessageReceived(MessageReceivedEvent event) {
		if (event.getAuthor().isBot()) return;
		Message message = event.getMessage();
		String content = message.getContentRaw(); 
		/* We formalise for any method in the private methods of this class */
		Arrays.stream(this.getClass().getDeclaredMethods())
			.filter(method -> method.isAnnotationPresent(Command.class))
			.filter(method -> content.startsWith("!miaou " + method.getName().toLowerCase()))
			.findFirst()
			.ifPresent(method -> {
				try {
					method.setAccessible(true);
					method.invoke(this, event);
				} catch (Exception e) {
					e.printStackTrace();
				}
			});
	}

	private @interface Command {}

	@Command
	private void Ping(MessageReceivedEvent event) {
		System.out.println("Pong!");
	}

	private MP3Player player;
	private void Play(MessageReceivedEvent event) {
		// Implementation for play command
		Message message = event.getMessage();
        String content = message.getContentRaw();
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
