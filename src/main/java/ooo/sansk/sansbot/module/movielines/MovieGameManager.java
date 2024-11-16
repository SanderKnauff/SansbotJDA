package ooo.sansk.sansbot.module.movielines;

import ooo.sansk.vaccine.annotation.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Queue;
import java.util.Random;

@Component
public class MovieGameManager {
    private static final Random random = new Random();
    private static final int LINE_COUNT = 5;

    private final MovieService movieService;
    private final List<MovieLinesGame> activeGames;

    public MovieGameManager(MovieService movieService) {
        this.movieService = movieService;
        this.activeGames = new ArrayList<>();
    }

    public Optional<MovieLinesGame> createGame() {
        if (!movieService.isReady()) {
            return Optional.empty();
        }

        LinkedList<Movie> movies = new LinkedList<>(movieService.getMovies());
        Collections.shuffle(movies);

        var winning = movies.pop();
        Queue<Line> lines = new LinkedList<>();
        var startingPoint = random.nextInt(winning.lines().size() - LINE_COUNT);
        for (var i = startingPoint; i < startingPoint + LINE_COUNT; i++) {
            lines.add(winning.lines().get(i));
        }
        final var newGame = new MovieLinesGame(null, winning, movies.pop(), movies.pop(), lines, false);
        activeGames.add(newGame);
        return Optional.of(newGame);
    }

    public Optional<MovieLinesGame> getGame(String messageId) {
        return activeGames.stream()
            .filter(game -> game.getMessageId().equals(messageId))
            .findAny();
    }

    public void removeGame(String messageId) {
        activeGames.removeIf(game -> game.getMessageId().equals(messageId));
    }
}
