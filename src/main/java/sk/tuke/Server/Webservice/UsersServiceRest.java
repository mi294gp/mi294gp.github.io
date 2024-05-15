package sk.tuke.Server.Webservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import sk.tuke.Entity.Users;
import sk.tuke.Service.UsersService;

@RestController
@RequestMapping("/api/users")
public class UsersServiceRest {
    @Autowired
    private UsersService usersService;

    @GetMapping("/{users}")
    public boolean canLogIn(@PathVariable Users users){
        return usersService.canLogIn(users);
    }

    @PostMapping
    public void registerUser(Users users){
        usersService.registerUser(users);
    }
}
