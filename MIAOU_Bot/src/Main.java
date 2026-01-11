import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;

public class Main {

    public static void main(String[] arguments) throws Exception
    {
        String token = new String(java.nio.file.Files.readAllBytes(java.nio.file.Paths.get("token.token")));
        JDA api = JDABuilder.createDefault(token)
            .enableIntents(GatewayIntent.MESSAGE_CONTENT)
            .addEventListeners(new Bot())
            .build();
    }
    
}