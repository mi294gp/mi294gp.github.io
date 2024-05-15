package sk.tuke.Service;

import sk.tuke.Entity.Users;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

@Transactional
public class UsersServiceJPA implements UsersService {
    @PersistenceContext
    private EntityManager entityManager;
    @Override
    public void registerUser(Users users) {
        entityManager.persist(users);
    }

    @Override
    public boolean canLogIn(Users users) {
        String result = "";
        try{
            result = entityManager.createNativeQuery
                    ("SELECT password FROM users WHERE users.login=:login")
                    .setParameter("login",users.getLogin()).setMaxResults(1)
                    .getSingleResult().toString();
        }catch (NoResultException e){
                return false;
        }
        return result.equals(users.getPassword());
    }
}
