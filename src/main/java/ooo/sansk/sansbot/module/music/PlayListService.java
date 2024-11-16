package ooo.sansk.sansbot.module.music;

import ooo.sansk.vaccine.annotation.Component;
import ooo.sansk.sansbot.module.music.playlist.PlayList;
import ooo.sansk.sansbot.repository.AbstractJsonRepository;

import java.nio.file.Paths;

@Component
public class PlayListService extends AbstractJsonRepository<String, PlayList> {
    public PlayListService() {
        super(Paths.get("music/playlist"));
    }
}
