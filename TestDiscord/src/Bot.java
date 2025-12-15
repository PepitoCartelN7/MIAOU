import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

public class Bot extends ListenerAdapter {

	@Override
	public void onMessageReceived(MessageReceivedEvent event) {
		if (event.getAuthor().isBot())
			return;
		Message message = event.getMessage();
		String content = message.getContentRaw();

		// Only process messages that start with !miaou
		if (!content.startsWith("!miaou ")) {
			return;
		}

		// Extract the command name from the message
		String commandName = "";
		String[] parts = content.substring("!miaou ".length()).split("\\s+", 2);
		if (parts.length > 0) {
			commandName = parts[0];
		}

		String finalCommandName = commandName;

		// On prend toutes les méthodes de la classe
		Method[] allMethods = this.getClass().getDeclaredMethods();
		
		Arrays.stream(allMethods)
				// On garde celles qui ont l'annotation Command
				.filter(method -> method.isAnnotationPresent(Command.class))
				// On check que la méthode a bien le bon nombre d'arguments
				.filter(method -> {
					Class<?>[] paramTypes = method.getParameterTypes();
					// Soit 1 param (MessageReceivedEvent) soit 2 params (MessageReceivedEvent,
					// String[])
					if (paramTypes.length == 1) {
						if (!paramTypes[0].equals(MessageReceivedEvent.class)) {
							throw new IllegalStateException(
									"@Command method " + method.getName() +
											" must have MessageReceivedEvent as first parameter");
						}
					} else if (paramTypes.length == 2) {
						if (!paramTypes[0].equals(MessageReceivedEvent.class)
								|| !paramTypes[1].equals(String[].class)) {
							throw new IllegalStateException(
									"@Command method " + method.getName() +
											" must have (MessageReceivedEvent, String[]) as parameters");
						}
					} else {
						throw new IllegalStateException(
								"@Command method " + method.getName() +
										" must have either (MessageReceivedEvent) or (MessageReceivedEvent, String[]) as parameters");
					}
					return true;
				})
				// On vérifie que le nom de la méthode correspond (case-insensitive)
				.filter(method -> method.getName().equalsIgnoreCase(finalCommandName))
				// On prend la première, si y'en a deux y'a un problème de toute façon
				.findFirst()
				.ifPresentOrElse(
						// Si on trouve une commande
						method -> {
							try {
								method.setAccessible(true);

								// Si on attends des arguments
								if (method.getParameterCount() == 2) {
									// On choppe le reste du message
									String prefix = "!miaou " + method.getName().toLowerCase();
									String argsString = content.length() > prefix.length()
											? content.substring(prefix.length()).trim()
											: "";
									String[] args = argsString.isEmpty() ? new String[0] : argsString.split("\\s+");
									method.invoke(this, event, args);
								} else {
									method.invoke(this, event);
								}
							} catch (Exception e) {
								e.printStackTrace();
							}
						},
						// Sinon, on indique qu'on peut run "help"
						() -> event.getChannel()
								.sendMessage("Miaou ? (la commande \"" + finalCommandName
										+ "\" n'existe pas, utilisez \"!miaou help\" pour une liste des commandes)")
								.queue());
	}

	@Retention(RetentionPolicy.RUNTIME)
	private @interface Command {
		String description() default "No description provided";

		String args() default "";
	}

	@Command(description = "Répond avec Pong!")
	private void Ping(MessageReceivedEvent event) {
		System.out.println("Pong!");
		event.getChannel().sendMessage("Miaou ! (Pong!)").queue();
	}

	@Command(description = "Affiche la liste de toutes les commandes disponibles")
	private void Help(MessageReceivedEvent event) {
		StringBuilder helpMessage = new StringBuilder("### __Miaou : (Les commandes disponibles sont:)__\n\n");

		// On choppe toutes les commandes
		List<Method> commandMethods = Arrays.stream(this.getClass().getDeclaredMethods())
				.filter(method -> method.isAnnotationPresent(Command.class))
				// J'ai rarement écrit un filtre aussi bullshit mais putain c'est beau
				.sorted((m1, m2) -> {
					// Put Help first, then sort alphabetically
					if (m1.getName().equalsIgnoreCase("help"))
						return -1;
					if (m2.getName().equalsIgnoreCase("help"))
						return 1;
					return m1.getName().compareToIgnoreCase(m2.getName());
				})
				.toList();

		// Construction du message
		for (Method method : commandMethods) {
			Command cmd = method.getAnnotation(Command.class);
			String cmdName = method.getName().toLowerCase();
			String args = cmd.args().isEmpty() ? "" : " " + cmd.args();

			helpMessage.append("• `!miaou ").append(cmdName).append(args).append("`\n");
			helpMessage.append("  ").append(cmd.description()).append("\n\n");
		}

		event.getChannel().sendMessage(helpMessage.toString()).queue();
	}

	private MP3Player player;

	@Command(description = "Ajoute un fichier MP3 à la playlist et le joue", args = "<filename>")
	private void Play(MessageReceivedEvent event, String[] args) {
		MessageChannel channel = event.getChannel();

		if (args.length == 0) {
			channel.sendMessage("Miaou ? (il faut spécifier un fichier à jouer)").queue();
			return;
		}

		String filename = String.join(" ", args);
		channel.sendMessage(filename + " added to the playlist").queue();

		if (player == null) {
			player = new MP3Player(channel);
		}
		player.addToList("assets/" + filename + ".mp3");

		if (!player.isPlaying()) {
			player.play_list();
		}
	}
}
