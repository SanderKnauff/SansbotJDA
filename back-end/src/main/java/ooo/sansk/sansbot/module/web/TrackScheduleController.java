package ooo.sansk.sansbot.module.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import nl.imine.vaccine.annotation.Component;
import ooo.sansk.sansbot.module.music.TrackListManager;
import ooo.sansk.sansbot.module.web.util.Controller;
import ooo.sansk.sansbot.module.web.util.Mapping;
import ooo.sansk.sansbot.module.web.util.MappingType;
import spark.Request;
import spark.Response;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

@Component
@Mapping(location = TrackScheduleController.BASE_URL)
public class TrackScheduleController implements Controller {

    public static final String BASE_URL = "/api/music";

    private final ObjectMapper objectMapper;
    private final TrackListManager trackListManager;

    public TrackScheduleController(ObjectMapper objectMapper, TrackListManager trackListManager) {
        this.objectMapper = objectMapper;
        this.trackListManager = trackListManager;
    }

    @Mapping
    public String getCurrentQueue(Request request, Response response) throws JsonProcessingException {
        response.body(objectMapper.writeValueAsString(trackListManager.getQueue()));
        return response.body();
    }

    @Mapping(location = "/current")
    public String getCurrentPlaying(Request request, Response response) throws JsonProcessingException {
        response.body(objectMapper.writeValueAsString(trackListManager.getCurrentTrack()));
        return response.body();
    }

    @Mapping(type = MappingType.POST)
    public String postTrack(Request request, Response response) throws IOException {
        String url = new ObjectMapper().readValue(request.body(), ObjectNode.class).get("url").asText();
        try {
            trackListManager.loadTrack(url, "een of andere sukkel over de API");
            response.body("Added track " + url);
        } catch (ExecutionException | InterruptedException | TimeoutException e) {
            response.body("Failed adding track due to timeout (" + url + ")");
        }
        return response.body();
    }
}
