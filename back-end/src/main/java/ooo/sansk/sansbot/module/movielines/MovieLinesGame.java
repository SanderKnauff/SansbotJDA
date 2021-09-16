package ooo.sansk.sansbot.module.movielines;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

public class MovieLinesGame {

    private String messageId;
    private final Movie winningOption;
    private final Movie firstAlternativeOption;
    private final Movie secondAlternativeOption;
    private final Queue<Line> lines;
    private boolean done;
    private final List<String> reactedUsers;

    public MovieLinesGame(String messageId, Movie winningOption, Movie firstAlternativeOption, Movie secondAlternativeOption, Queue<Line> lines, boolean done) {
        this.messageId = messageId;
        this.winningOption = winningOption;
        this.firstAlternativeOption = firstAlternativeOption;
        this.secondAlternativeOption = secondAlternativeOption;
        this.lines = lines;
        this.reactedUsers = new ArrayList<>();
        this.done = done;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public Movie getWinningOption() {
        return winningOption;
    }

    public Movie getFirstAlternativeOption() {
        return firstAlternativeOption;
    }

    public Movie getSecondAlternativeOption() {
        return secondAlternativeOption;
    }

    public Queue<Line> getLines() {
        return lines;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    public List<String> getReactedUsers() {
        return reactedUsers;
    }
}
