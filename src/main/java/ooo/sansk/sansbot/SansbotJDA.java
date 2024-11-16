package ooo.sansk.sansbot;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import ooo.sansk.vaccine.Vaccine;
import ooo.sansk.vaccine.annotation.AfterCreate;
import ooo.sansk.vaccine.annotation.Component;
import ooo.sansk.vaccine.annotation.Property;
import ooo.sansk.vaccine.annotation.Provided;
import ooo.sansk.sansbot.options.AudioTrackSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.util.Properties;

@Component
public class SansbotJDA {
    private static final Logger logger = LoggerFactory.getLogger(SansbotJDA.class);

    private final String botToken;

    public static void main(String[] args) {
        final var mergedProperties = new Properties();
        try (
                final var externalPropertiesStream = Files.newInputStream(Paths.get("application.properties"));
                final var defaultPropertiesStream = ClassLoader.getSystemResource("default.properties").openStream()
        ) {
            final var internalProperties = new Properties();
            internalProperties.load(defaultPropertiesStream);
            mergedProperties.putAll(internalProperties);
            final var externalProperties = new Properties();
            externalProperties.load(externalPropertiesStream);
            mergedProperties.putAll(externalProperties);
        } catch (NoSuchFileException e) {
            createPropertiesFile();
            logger.error("No application.properties file found. Creating new file. Please provide your Bot token in the applications.properties");
            System.exit(0);
        } catch (IOException e) {
            logger.error("Unable to load properties. Exiting. ({}: {})", e.getClass().getSimpleName(), e.getMessage());
            System.exit(1);
        }

        new Vaccine().inject(mergedProperties, "ooo.sansk.sansbot");
    }

    private static void createPropertiesFile() {
        try {
            final var path = Paths.get("application.properties");
            Files.createFile(path);
            Files.writeString(path, "sansbot.token=");
        } catch (IOException e) {
            logger.error("Could not create properties file. Exiting. e");
            System.exit(1);
        }
    }

    public SansbotJDA(@Property("sansbot.token") String botToken) {
        this.botToken = botToken;
    }

    @AfterCreate
    public void onPostConstruct() {
        logger.info("Loading Sansbot");
    }

    @Provided
    public JDA jda() throws InterruptedException {
        logger.info("Creating JDA...");
        final JDA jda = JDABuilder.create(botToken, GatewayIntent.getIntents(GatewayIntent.ALL_INTENTS))
                .enableCache(CacheFlag.VOICE_STATE)
                .build().awaitReady();
        logger.info("JDA created");

        Runtime.getRuntime().addShutdownHook(new Thread(jda::shutdown));

        return jda;
    }

    @Provided
    public ObjectMapper objectMapper() {
        return new ObjectMapper()
                .disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .enable(JsonParser.Feature.ALLOW_SINGLE_QUOTES)
                .registerModule(new SimpleModule().addSerializer(AudioTrack.class, new AudioTrackSerializer()));
    }
}
