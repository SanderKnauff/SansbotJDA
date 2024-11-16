package ooo.sansk.sansbot.module.music.playlist;

import ooo.sansk.sansbot.repository.Identifiable;

import java.util.List;

public record PlayList(String id, List<Track> trackList) implements Identifiable<String> {
}
