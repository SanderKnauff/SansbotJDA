package ooo.sansk.sansbot.options;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import java.io.IOException;

public class AudioTrackSerializer extends JsonSerializer<AudioTrack> {
    @Override
    public void serialize(AudioTrack value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeObject(value.getInfo());
    }
}
