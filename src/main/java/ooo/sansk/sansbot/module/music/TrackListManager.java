package ooo.sansk.sansbot.module.music;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEvent;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventListener;
import com.sedmelluq.discord.lavaplayer.player.event.TrackEndEvent;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import dev.lavalink.youtube.YoutubeAudioSourceManager;
import dev.lavalink.youtube.clients.WebEmbedded;
import jakarta.annotation.Nullable;
import net.dv8tion.jda.api.JDA;
import ooo.sansk.vaccine.annotation.AfterCreate;
import ooo.sansk.vaccine.annotation.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;

@Component
public class TrackListManager implements AudioEventListener {
    private static final Logger logger = LoggerFactory.getLogger(TrackListManager.class);

    private final JDA jda;
    private final AudioPlayerManager audioPlayerManager;
    private final LinkedList<AudioTrack> queue;

    @Nullable
    private AudioPlayer audioPlayer;

    public TrackListManager(JDA jda) {
        this.jda = jda;
        this.audioPlayerManager = new DefaultAudioPlayerManager();
        this.queue = new LinkedList<>();
    }

    @AfterCreate
    public void afterCreation() {
        final var guild = jda.getGuilds().getFirst();
        final var sourceManager = new YoutubeAudioSourceManager(false, new WebEmbedded());
        sourceManager.setPlaylistPageCount(20);
        audioPlayerManager.registerSourceManager(sourceManager);
        audioPlayer = audioPlayerManager.createPlayer();
        audioPlayer.addListener(this);
        guild.getAudioManager().setSendingHandler(new AudioPlayerSendHandler(audioPlayer));
    }

    public CompletableFuture<String> loadTrack(String trackUrl, String messageSender) {
        CompletableFuture<String> resultMessage = new CompletableFuture<>();

        audioPlayerManager.loadItem(trackUrl, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                queueSingleTrack(track);
                resultMessage.complete(String.format(":notes: Onze grote DJ %s heeft het volgende plaatje aangevraagd! :notes:%n%s", messageSender, trackUrl));
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                playlist.getTracks().forEach(TrackListManager.this::queueSingleTrack);
                resultMessage.complete(String.format(":notes: Bereid je voor! %s heeft een hele stapel plaatjes aangevraagd! :notes:%n%s", messageSender, trackUrl));
            }

            @Override
            public void noMatches() {
                resultMessage.complete(String.format(":angry: Ik snap geen hol van die muziek van je, %s! Ik kan niks vinden wat ook maar een *beetje* lijkt op wat je me net vroeg...", messageSender));
            }

            @Override
            public void loadFailed(FriendlyException e) {
                resultMessage.complete(String.format(":fire: *Tijdens het aanvragen van %s's nummertje is de jukebox in de brand gevlogen. Wij zijn druk bezig met het vuur te blussen* :fire:", messageSender));
                logger.error("An exception occurred while loading AudioTrack", e);
            }
        });
        return resultMessage;
    }

    private void queueSingleTrack(AudioTrack audioTrack) {
        if (audioPlayer == null) {
            return;
        }
        if (queue.isEmpty() && audioPlayer.getPlayingTrack() == null) {
            play(audioTrack);
        } else {
            queue.add(audioTrack);
        }
    }

    public boolean play(AudioTrack track) {
        if (audioPlayer == null) {
            return false;
        }
        logger.info("Playing audio track: {}", track.getInfo().title);
        return audioPlayer.startTrack(track, false);
    }

    public void skip(long amountToSkip) {
        if (audioPlayer == null) {
            return;
        }
        for (long i = 0; i < amountToSkip - 1; i++) {
            queue.poll();
        }
        audioPlayer.stopTrack();
    }

    public void shuffle() {
        Collections.shuffle(queue);
    }

    public boolean pause() {
        if (audioPlayer == null) {
            return false;
        }
        if (audioPlayer.getPlayingTrack() != null) {
            audioPlayer.setPaused(true);
            return true;
        } else {
            return false;
        }
    }

    public boolean resume() {
        if (audioPlayer == null) {
            return false;
        }
        audioPlayer.setPaused(false);
        if (audioPlayer.getPlayingTrack() == null) {
            AudioTrack track = queue.poll();
            if (track != null) {
                play(track);
                return true;
            }
            return false;
        } else {
            return true;
        }
    }

    @Nullable
    public AudioTrack getCurrentTrack() {
        if (audioPlayer == null) {
            return null;
        }
        return audioPlayer.getPlayingTrack();
    }

    public Queue<AudioTrack> getQueue() {
        return queue;
    }

    @Override
    public void onEvent(AudioEvent event) {
        if (event instanceof TrackEndEvent) {
            if (queue.isEmpty()) {
                jda.getGuilds().getFirst().getAudioManager().closeAudioConnection();
                return;
            }
            playNextQueuedTrack();
        }
    }

    private void playNextQueuedTrack() {
        AudioTrack track = queue.poll();
        if (track != null) {
            play(track);
        }
    }
}
