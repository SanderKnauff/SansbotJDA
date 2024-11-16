package ooo.sansk.sansbot.module.music;

import ooo.sansk.sansbot.module.music.playlist.Track;

import java.util.List;
import java.util.Random;

public enum PlayMode {
    SEQUENTIAL {
        @Override
        public Track getNextTrack(List<Track> tracks) {
            return tracks.removeFirst();
        }
    }, SHUFFLE {
        @Override
        public Track getNextTrack(List<Track> tracks) {
            return tracks.remove(RANDOM.nextInt(tracks.size()));
        }
    };

    private static final Random RANDOM = new Random();

    public abstract Track getNextTrack(List<Track> tracks);
}
