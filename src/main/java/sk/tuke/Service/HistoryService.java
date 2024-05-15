package sk.tuke.Service;

import sk.tuke.Entity.History;

import java.util.List;

public interface HistoryService {
    void addToHistory(History history);

    List<String> getHistory(String login);
}
