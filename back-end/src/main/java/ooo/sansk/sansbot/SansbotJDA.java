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
import nl.imine.vaccine.Vaccine;
import nl.imine.vaccine.annotation.AfterCreate;
import nl.imine.vaccine.annotation.Component;
import nl.imine.vaccine.annotation.Property;
import nl.imine.vaccine.annotation.Provided;
import ooo.sansk.sansbot.options.AudioTrackSerializer;
import ooo.sansk.sansbot.options.PersistentProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.LoginException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.Scanner;

@Component
public class SansbotJDA {

    private static final Logger logger = LoggerFactory.getLogger(SansbotJDA.class);

    private final String botToken;

    public static void main(String[] args) {
        var mergedProperties = new Properties();
        try (var inputStream = Files.newInputStream(Paths.get("application.properties"))) {
            var internalProperties = new Properties();
            internalProperties.load(ClassLoader.getSystemResource("default.properties").openStream());
            mergedProperties.putAll(internalProperties);
            var externalProperties = new Properties();
            externalProperties.load(inputStream);
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

        listenForStopCommand();
    }

    private static void createPropertiesFile() {
        try {
            var path = Paths.get("application.properties");
            Files.createFile(path);
            Files.write(path, "sansbot.token=".getBytes());
        } catch (IOException e) {
            logger.error("Could not create properties file. Exiting. ({}: {})", e.getClass().getSimpleName(), e.getMessage());
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
    public JDA jda() {
        try {
            return JDABuilder.create(botToken, GatewayIntent.getIntents(GatewayIntent.ALL_INTENTS)).build().awaitReady();
        } catch (InterruptedException | LoginException e) {
            logger.error("Failed to create JDA", e);
            System.exit(1);
            return null;
        }
    }

    @Provided
    public PersistentProperties applicationOptions() {
        var persistentPropertiesPath = Paths.get("persistent.properties");
        var persistentProperties = new PersistentProperties(persistentPropertiesPath);
        if(!persistentPropertiesPath.toFile().exists()) {
            try {
                Files.createFile(persistentPropertiesPath);
            } catch (IOException e) {
                logger.error("Could not create persistent property storage at {}, continue loading without options ({}: {})",
                        persistentPropertiesPath,
                        e.getClass().getSimpleName(),
                        e.getMessage());
            }
        } else {
            try (var inputStream = Files.newInputStream(persistentPropertiesPath)){
                persistentProperties.load(inputStream);
            } catch (IOException e) {
                logger.error("Could not read persistent property storage at {}, continue loading without options ({}: {})",
                        persistentPropertiesPath,
                        e.getClass().getSimpleName(),
                        e.getMessage());
            }
        }
        return persistentProperties;
    }

    @Provided
    public ObjectMapper objectMapper() {
        var objectMapper = new ObjectMapper();
        objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        objectMapper.enable(JsonParser.Feature.ALLOW_SINGLE_QUOTES);
        var serializers = new SimpleModule();
        serializers.addSerializer(AudioTrack.class, new AudioTrackSerializer());
        objectMapper.registerModule(serializers);
        return objectMapper;
    }

    private static void listenForStopCommand() {
        var scanner = new Scanner(System.in);
        while(scanner.hasNext()) {
            if(scanner.next().equals("stop")) {
                System.exit(0);
            }
        }
    }
}
