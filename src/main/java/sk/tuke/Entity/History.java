package sk.tuke.Entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class History {

    @Id
    @GeneratedValue
    private int ident;
    private String login;
    public String input;
    private String state;

    public History(String login, String input, String state){
        this.login=login;
        this.input=input;
        this.state=state;
    }

    public History(String login, String input){
        this.login = login;
        this.input = input;
    }

    public History(){}

    public String getLogin(){return this.login;}
    public String getState(){return this.state;}
    public String getInput(){return this.input;}
    public int getIdent() { return ident; }
    public void setIdent(int ident) { this.ident = ident; }
}
