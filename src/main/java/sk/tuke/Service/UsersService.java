package sk.tuke.Service;

import sk.tuke.Entity.Users;

public interface UsersService {
    void registerUser(Users users);

    boolean canLogIn(Users users);
}
