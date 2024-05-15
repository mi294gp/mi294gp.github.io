package sk.tuke.Server.Webservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import sk.tuke.Entity.History;
import sk.tuke.Service.HistoryService;

import java.util.List;

@RestController
@RequestMapping("/api/history")
public class HistoryServiceRest {
    @Autowired
    private HistoryService historyService;

    @PostMapping
    public void addHistory(History history){historyService.addToHistory(history);}

    @GetMapping("/{login}")
    public List<String> getHistory(@PathVariable String login){
        return historyService.getHistory(login);
    }
}
