import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;

/**
 * Cette classe initialise l'instance JDA (Java Discord API) et configure le bot
 * avec les listener d'évenements.
 */
public class Main {

    /**
     * Lit le jeton du bot à partir d'un fichier "token.token", crée une instance JDA
     * Qui enregistre le listener qui est décrit dans la classe Bot
     *
     * @param arguments On en a pas
     * @throws Exception si une erreur se produit lors de la lecture du fichier
     *                   de jeton ou si une erreur se produit lors de l'initialisation de JDA
     */
    public static void main(String[] arguments) throws Exception
    {
        String token = new String(java.nio.file.Files.readAllBytes(java.nio.file.Paths.get("token.token"))).trim();
        JDA api = JDABuilder.createDefault(token)
            .enableIntents(GatewayIntent.MESSAGE_CONTENT)
            .addEventListeners(new Bot())
            .build();
    }
    
}