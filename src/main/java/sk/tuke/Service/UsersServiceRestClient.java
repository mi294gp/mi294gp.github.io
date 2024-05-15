package sk.tuke.Service;

import org.springframework.web.client.RestTemplate;
import sk.tuke.Entity.Users;

import java.util.Objects;

public class UsersServiceRestClient implements UsersService{
    private final String url = "http://localhost:8080/api/users";

    private RestTemplate restTemplate = new RestTemplate();
    @Override
    public void registerUser(Users users) {
        restTemplate.postForEntity(url,users,Users.class);
    }

    @Override
    public boolean canLogIn(Users users) {
        return Objects.equals(users.getPassword(), Objects.requireNonNull(restTemplate.getForEntity(url + "/" + users, Users.class).getBody()).getPassword());
    }
}
