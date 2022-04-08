package ooo.sansk.sansbot.module.music;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEvent;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventListener;
import com.sedmelluq.discord.lavaplayer.player.event.TrackEndEvent;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.VoiceChannel;
import nl.imine.vaccine.annotation.AfterCreate;
import nl.imine.vaccine.annotation.Component;
import ooo.sansk.sansbot.module.music.playlist.Track;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;

@Component
public class TrackListManager implements AudioEventListener {

    private static final Logger logger = LoggerFactory.getLogger(TrackListManager.class);

    private final JDA jda;
    private final AudioPlayerManager audioPlayerManager;
    private final LinkedList<AudioTrack> queue;
    private final List<Track> playlistQueue;
    private final PlayMode currentPlayMode;

    private AudioPlayer audioPlayer;

    public TrackListManager(JDA jda) {
        this.jda = jda;
        this.audioPlayerManager = new DefaultAudioPlayerManager();
        this.queue = new LinkedList<>();
        this.playlistQueue = new ArrayList<>();
        this.currentPlayMode = PlayMode.SEQUENTIAL;
    }

    @AfterCreate
    public void afterCreation() {
        AudioSourceManagers.registerLocalSource(audioPlayerManager);
        AudioSourceManagers.registerRemoteSources(audioPlayerManager);
        audioPlayerManager.source(YoutubeAudioSourceManager.class).setPlaylistPageCount(20);
        Guild guild = jda.getGuilds().get(0);
        for (VoiceChannel voiceChannel : guild.getVoiceChannels()) {
            guild.getAudioManager().openAudioConnection(voiceChannel);
        }
        audioPlayer = audioPlayerManager.createPlayer();
        audioPlayer.addListener(this);
        setSendingHandler(guild);
    }

    public void setSendingHandler(Guild guild) {
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
        if (queue.isEmpty() && audioPlayer.getPlayingTrack() == null) {
            play(audioTrack);
        } else {
            queue.add(audioTrack);
        }
    }

    public boolean play(AudioTrack track) {
        logger.info("Playing audio track: {}", track.getInfo().title);
        return audioPlayer.startTrack(track, false);
    }

    public void skip(long amountToSkip) {
        for (long i = 0; i < amountToSkip - 1; i++) {
            queue.poll();
        }
        audioPlayer.stopTrack();
    }

    public void shuffle() {
        Collections.shuffle(queue);
    }

    public boolean pause() {
        if (audioPlayer.getPlayingTrack() != null) {
            audioPlayer.setPaused(true);
            return true;
        } else {
            return false;
        }
    }

    public boolean resume() {
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

    public AudioTrack getCurrentTrack() {
        return audioPlayer.getPlayingTrack();
    }

    public Queue<AudioTrack> getQueue() {
        return queue;
    }

    @Override
    public void onEvent(AudioEvent event) {
        if (event instanceof TrackEndEvent) {
            handleTrackEndEvent();
        }
    }

    private void handleTrackEndEvent() {
        if (playlistQueue.isEmpty())
            playNextQueuedTrack();
        else
            playNextPlayListTrack();
    }

    private void playNextQueuedTrack() {
        AudioTrack track = queue.poll();
        if (track != null)
            play(track);
    }

    private void playNextPlayListTrack() {
        loadAndPlayTrack(currentPlayMode.getNextTrack(playlistQueue));
    }

    private void loadAndPlayTrack(Track track) {
        audioPlayerManager.loadItem(track.getSource(), new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                play(track);
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
            }

            @Override
            public void noMatches() {
                logger.warn("Could not load track \"{}\"", track.getSource());
                playNextPlayListTrack();
            }

            @Override
            public void loadFailed(FriendlyException e) {
                logger.error("An exception occurred while loading AudioTrack ({}: {})", e.getCause().getClass().getSimpleName(), e.getMessage());
                playNextPlayListTrack();
            }
        });
    }
}
