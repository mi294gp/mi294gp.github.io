package sk.tuke.Service;

import org.springframework.web.client.RestTemplate;
import sk.tuke.Entity.History;

import java.util.Arrays;
import java.util.List;

public class HistoryServiceRestClient implements HistoryService{
    private final String url = "http://localhost:8080/api/history";
    private RestTemplate restTemplate = new RestTemplate();

    @Override
    public void addToHistory(History history) {
        restTemplate.postForEntity(url,history,History.class);
    }

    @Override
    public List<String> getHistory(String login) {
        return Arrays.asList(restTemplate.getForEntity(url + "/" + login, String[].class).getBody());
    }
}
