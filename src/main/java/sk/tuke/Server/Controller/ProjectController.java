package sk.tuke.Server.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.WebApplicationContext;
import sk.tuke.AMInstructionsToOutput.LexerToOutput;
import sk.tuke.AMInstructionsToOutput.ParserToOutput;
import sk.tuke.InputToAMInstructions.LexerToAM;
import sk.tuke.InputToAMInstructions.ParserToAM;
import sk.tuke.Entity.History;
import sk.tuke.Entity.Users;
import sk.tuke.Service.HistoryService;
import sk.tuke.Service.UsersService;

import java.io.IOException;
import java.util.*;

@Controller
@Scope(WebApplicationContext.SCOPE_SESSION)
public class ProjectController {

    String inst;
    private Users users;
    private List<String> historyList;
    boolean needMemory = false;
    int instIndex = 0;
    int actualState;
    Map<String,Integer> storage = new HashMap<>();
    Integer[] memory = new Integer[100];

    ParserToOutput parserToOutput;

    @Autowired
    private UsersService usersService;

    @Autowired
    private HistoryService historyService;

    @RequestMapping("/")
    public String index(){
        return "index";
    }

    @RequestMapping("/instructions")
    public String instructions(){
        return "instructions";
    }

    @RequestMapping("/input")
    public String input(){
        storage = new HashMap<>();
        memory = new Integer[100];
        return "input";
    }

    @RequestMapping("/change")
    public String change(){
        needMemory = !needMemory;
        return "redirect:/input";
    }

    @RequestMapping("/getInput")
    public String getInput(String input, String storage) throws IOException {
        boolean start = input.contains(":") && !input.contains(":=");
        if(start){
            if(needMemory){
                if(input.contains("STORE-") || input.contains("FETCH-")){
                    return "redirect:/input";
                }
            }
            else{
                if(input.contains("PUT-") || input.contains("GET-")){
                    return "redirect:/input";
                }
            }
        }
        String code = "";
        if(!start) {
            Scanner scanner = new Scanner(input);
            LexerToAM lexerToAM = new LexerToAM(scanner);
            ParserToAM parserToAM = new ParserToAM(lexerToAM);
            code = parserToAM.parse();
        }
        if(code.equals("")) inst = input;
        else inst = code;
        Scanner readingStorage = new Scanner(storage);
        readingStorage.useDelimiter("[ ;=\r\n]+");
        if(!needMemory) {
            while (readingStorage.hasNext()) {
                String var = readingStorage.next();
                int val = Integer.parseInt(readingStorage.next());
                this.storage.put(var, val);
            }
        }
        else{
            int memoryIdx = 0;
            while (readingStorage.hasNext()){
                memory[memoryIdx++] = Integer.parseInt(readingStorage.next());
            }
        }
        Scanner scanner2 = new Scanner(inst);
        Scanner toPrint = new Scanner(inst);
        LexerToOutput lexerToOutput = new LexerToOutput(scanner2,toPrint);
        if(users != null){
            History history = new History(users.getLogin(), input, storage);
            historyService.addToHistory(history);
        }
        if (this.storage.size() != 0) parserToOutput = new ParserToOutput(lexerToOutput,this.storage);
        else if (this.memory[0] != null) parserToOutput = new ParserToOutput(lexerToOutput,this.memory);
        else parserToOutput = new ParserToOutput(lexerToOutput);
        return "redirect:/loading";
    }

    @RequestMapping("/loading")
    public String loading(){
        return "loading";
    }

    @RequestMapping("/login")
    public String login(){
        return "login";
    }

    @RequestMapping("/toLogin")
    public String toLogin(String login, String password){
        users = new Users(login,password);
        if(usersService.canLogIn(users)){
            users.toggle();
            return "index";
        }
        else return "login";
    }

    @RequestMapping("/register")
    public String register(){
        return "register";
    }

    @RequestMapping("/toRegister")
    public String toRegister(String login, String email, String password){
        users = new Users(login,email,password);
        usersService.registerUser(users);
        return "login";
    }

    @RequestMapping("/visualisation")
    public String visualisation(){
        return "visualisation";
    }

    @RequestMapping("/storage")
    public String storage(){
        return "storage";
    }

    @RequestMapping("/account")
    public String account(){return "account";}

    @RequestMapping("/profile")
    public String profile(){return "profile";}

    @RequestMapping("/history")
    public String history(){
        historyList = historyService.getHistory(users.getLogin());
        return "history";
    }

    @RequestMapping("/logout")
    public String logout(){
        users.toggle();
        return "index";
    }

    public void reset(){
        instIndex = 0;
    }

    @RequestMapping("/visualisation/add")
    public String addInst(){
        instIndex++;
        return "visualisation";
    }

    @RequestMapping("/visualisation/sub")
    public String subInst(){
        instIndex--;
        return "visualisation";
    }

    public boolean isZero(){
        return (instIndex < 1);
    }

    public boolean isMax(){
        return (instIndex >= parserToOutput.getMax());
    }

    @RequestMapping("/visualisation/fin")
    public String goFinish(){
        parserToOutput.lastOne();
        instIndex = parserToOutput.getMax()+1;
        return "visualisation";
    }

    public String getPrint(){
        String toPrint ="";
        if(!needMemory){
            for (int i = 0 ; i <= instIndex; i++){
                toPrint = String.join("",toPrint + parserToOutput.getPrinting(i));
                actualState = Integer.parseInt(String.valueOf(parserToOutput.getPrinting(i).charAt(parserToOutput.getPrinting(i).length()-4)));
                if (Character.isDigit(parserToOutput.getPrinting(i).charAt(parserToOutput.getPrinting(i).length()-5))){
                    actualState = actualState + (Integer.parseInt(String.valueOf(parserToOutput.getPrinting(i).charAt(parserToOutput.getPrinting(i).length()-5)))*10);
                }
                if (Character.isDigit(parserToOutput.getPrinting(i).charAt(parserToOutput.getPrinting(i).length()-6))){
                    actualState = actualState + (Integer.parseInt(String.valueOf(parserToOutput.getPrinting(i).charAt(parserToOutput.getPrinting(i).length()-6)))*100);
                }
            }
        }
        else{
            for (int i = 0 ; i <= instIndex; i++){
                toPrint = String.join("",toPrint + parserToOutput.getMemPrinting(i));
                actualState = Integer.parseInt(String.valueOf(parserToOutput.getPrinting(i).charAt(parserToOutput.getPrinting(i).length()-4)));
                if (Character.isDigit(parserToOutput.getPrinting(i).charAt(parserToOutput.getPrinting(i).length()-5))){
                    actualState = actualState + (Integer.parseInt(String.valueOf(parserToOutput.getPrinting(i).charAt(parserToOutput.getPrinting(i).length()-5)))*10);
                }
                if (Character.isDigit(parserToOutput.getPrinting(i).charAt(parserToOutput.getPrinting(i).length()-6))){
                    actualState = actualState + (Integer.parseInt(String.valueOf(parserToOutput.getPrinting(i).charAt(parserToOutput.getPrinting(i).length()-6)))*100);
                }
            }
        }
        return toPrint;
    }

    public String getState(){
        return parserToOutput.getAllStates(actualState);
    }

    public String getStack(){
        return parserToOutput.getStack(instIndex);
    }

    public String getAllStates(){
        String allStates="";
        for (int i = 0; i < actualState;i++){
            allStates = String.join("",allStates + parserToOutput.getAllStates(i));
        }
        return allStates;
    }

    public void start() throws IOException {
        parserToOutput.parse();
    }

    public String getCode(){
        return inst;
    }

    public boolean isLogged(){
        if(users==null) return false;
        return users.IsLogged();
    }

    public String getPassword(){
        return users.getPassword();
    }

    public String getLogin(){
        return users.getLogin();
    }

    public boolean isThere(int index){
        return index < historyList.size();
    }

    public String getFromHistory(int index){
        String help = historyList.get(index);
        return help;
    }

    public boolean isMemory(){
        return needMemory;
    }

    public String getMemory(){
        Integer[] mem = parserToOutput.getMemory(actualState);
        String result = "m[";
        if(mem!=null){
            for (int i = 0; i < 100; i++){
                if(mem[i] != null) result = String.join("",result + (i+1) + ":" + mem[i].toString() + ", ");
            }
        }
        if (result.equals("m[")) return result + "]";
        result = result.substring(0,result.length()-2);
        return result + "]";
    }

}
