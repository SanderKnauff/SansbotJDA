package ooo.sansk.sansbot.module.movielines;

import com.fasterxml.jackson.databind.ObjectMapper;
import ooo.sansk.vaccine.annotation.AfterCreate;
import ooo.sansk.vaccine.annotation.Component;
import ooo.sansk.vaccine.annotation.Property;
import ooo.sansk.sansbot.SansbotJDA;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class MovieService {
    private static final Logger logger = LoggerFactory.getLogger(MovieService.class);

    private final String basePath;

    private List<Movie> movies;
    private boolean ready = false;

    public MovieService(@Property("sansbot.module.moviegame.path") String moviesPath) {
        this.basePath = moviesPath;
    }

    @AfterCreate
    public void postConstruct() {
        new Thread(this::loadMovies).start();
    }

    private void loadMovies() {
        SansbotJDA sansbotJDA = new SansbotJDA("");
        ObjectMapper objectMapper = sansbotJDA.objectMapper();

        try (Stream<Path> list = Files.list(Paths.get(basePath, "/marvel"))) {
            movies = list.filter(file -> file.toString().endsWith("json"))
                    .map(file -> readMovie(objectMapper, file))
                    .filter(this::verifyMovieHasEnoughLines)
                    .toList();
        } catch (IOException e) {
            logger.warn("Could not load movies from disk.", e);
        }
        if (movies.size() < 3) {
            logger.warn("Just {} movies have been loaded from disk. This game requires at least 3 to work.", movies.size());
            return;
        }
        logger.info("Loaded {} movies from disk", movies.size());
        ready = true;
    }

    private Movie readMovie(ObjectMapper objectMapper, Path file) {
        try {
            return objectMapper.readValue(Files.newInputStream(file), Movie.class);
        } catch (IOException e) {
            logger.warn("{} failed to load.", file.toAbsolutePath(), e);
        }
        return new Movie("Failed to load", Collections.emptyList());
    }

    private boolean verifyMovieHasEnoughLines(Movie movie) {
        if(movie.lines().size() < 5) {
            logger.error("Movie {} did not have 5 or more lines", movie.title());
            return false;
        }
        return true;
    }

    public List<Movie> getMovies() {
        if(!ready) {
            throw new IllegalStateException("MovieService is not ready yet.");
        }
        return movies;
    }

    public boolean isReady() {
        return ready;
    }
}
