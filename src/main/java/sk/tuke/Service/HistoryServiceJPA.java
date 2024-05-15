package sk.tuke.Service;

import sk.tuke.Entity.History;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.List;

@Transactional
public class HistoryServiceJPA implements HistoryService{

    @PersistenceContext
    private EntityManager entityManager;
    @Override
    public void addToHistory(History history) {
        entityManager.persist(history);
    }

    @Override
    public List<String> getHistory(String login) {
        return entityManager.createNativeQuery("SELECT input " +
                        "FROM history WHERE history.login=:login")
                .setParameter("login",login).setMaxResults(10).getResultList();
    }
}
