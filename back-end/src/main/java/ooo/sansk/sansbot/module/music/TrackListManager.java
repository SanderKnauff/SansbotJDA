package ooo.sansk.sansbot.module.music;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEvent;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventListener;
import com.sedmelluq.discord.lavaplayer.player.event.TrackEndEvent;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import nl.imine.vaccine.annotation.AfterCreate;
import nl.imine.vaccine.annotation.Component;
import ooo.sansk.sansbot.module.music.playlist.PlayList;
import ooo.sansk.sansbot.module.music.playlist.Track;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Supplier;

@Component
public class TrackListManager implements AudioEventListener {

    private static final Logger logger = LoggerFactory.getLogger(TrackListManager.class);

    private final JDA jda;
    private final PlayListService playListService;
    private final AudioPlayerManager audioPlayerManager;
    private final Queue<AudioTrack> queue;
    private final List<Track> playlistQueue;
    private final PlayMode currentPlayMode;

    private AudioPlayer audioPlayer;
    private PlayList currentPlayList;

    public TrackListManager(JDA jda, PlayListService playListService) {
        this.jda = jda;
        this.playListService = playListService;
        this.audioPlayerManager = new DefaultAudioPlayerManager();
        this.queue = new LinkedBlockingQueue<>();
        this.playlistQueue = new ArrayList<>();
        this.currentPlayMode = PlayMode.SEQUENTIAL;
    }

    @AfterCreate
    public void afterCreation() {
        AudioSourceManagers.registerLocalSource(audioPlayerManager);
        AudioSourceManagers.registerRemoteSources(audioPlayerManager);
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

    public CompletableFuture<String> loadTrack(String track, String messageSender) {
        CompletableFuture<String> resultMessage = new CompletableFuture<>();
        audioPlayerManager.loadItem(track, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                queueSingleTrack(track);
                resultMessage.complete(String.format(":notes: Onze grote DJ %s heeft het volgende plaatje aangevraagd! :notes:%n%s", messageSender, track));
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                resultMessage.complete(String.format(":notes: Bereid je voor! %s heeft een hele stapel plaatjes aangevraagd! :notes:%n%s", messageSender, track));
                playlist.getTracks().forEach(TrackListManager.this::queueSingleTrack);
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

    private void queuePlaylist(PlayList playList) {
        playlistQueue.clear();
        playlistQueue.addAll(playList.getTrackList());
    }

    public boolean play(AudioTrack track) {
        logger.info("Playing audio track: {}", track.getInfo().title);
        return audioPlayer.startTrack(track, false);
    }

    public void skip() {
        audioPlayer.stopTrack();
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
                logger.warn("Could not load track \"{}\" in playlist \"{}\"", track.getSource(), currentPlayList.getId());
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
