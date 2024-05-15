package sk.tuke.Entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQuery;

@Entity
@NamedQuery(name = "Users.canLogIn",
            query = "SELECT u.password FROM Users u WHERE u.login=:login")

public class Users {

    @Id
    @GeneratedValue
    private int ident;
    private String login;
    private String email;
    private String password;

    private boolean isLogged = false;

    public Users(String login, String email, String password){
        this.login = login;
        this.email = email;
        this.password = password;
    }

    public Users(String login, String password){
        this.login = login;
        this.password = password;
    }

    public Users(){}

    public String getLogin() {
        return login;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public int getIdent() {
        return ident;
    }

    public void setIdent(int ident) {
        this.ident = ident;
    }

    public void toggle(){
        this.isLogged = !this.isLogged;
    }

    public boolean IsLogged(){
        return this.isLogged;
    }


}
