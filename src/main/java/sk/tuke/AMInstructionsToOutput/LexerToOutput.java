package sk.tuke.AMInstructionsToOutput;
import sk.tuke.Token;
import sk.tuke.MyException;

import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.regex.Pattern;

public class LexerToOutput {
    private String current;
    private Scanner input;
    public Scanner printing;
    public String variable;
    public boolean isBranch = false;
    public boolean isLoop = false;
    Pattern storePattern = Pattern.compile("STORE-[A-Za-z]+\\d*");
    Pattern putPattern = Pattern.compile("PUT-\\d+");
    Pattern fetchPattern = Pattern.compile("FETCH-[A-Za-z]+\\d*");
    Pattern getPattern = Pattern.compile("GET-\\d+");
    Pattern pushPattern = Pattern.compile("PUSH-\\d+");

    public LexerToOutput(Scanner scanner,Scanner printing) {
        this.input = scanner;
        this.printing = printing;
        input.useDelimiter("[ :()]");
        this.printing.useDelimiter("[ :()]");
        current = input.next();
        this.printing.next();
    }

    public Token nextToken(){
        if (storePattern.matcher(current).matches() || putPattern.matcher(current).matches()){
            int index = current.indexOf('-');
            variable = current.substring(index+1);
            if(!isBranch && !isLoop){
                consume();
            }
            return Token.STORE;
        }
        else if (fetchPattern.matcher(current).matches() || getPattern.matcher(current).matches()){
            int index = current.indexOf('-');
            variable = current.substring(index+1);
            if(!isBranch && !isLoop){
                consume();
            }
            return Token.FETCH;
        }
        else if (pushPattern.matcher(current).matches()){
            StringBuilder numberStr = new StringBuilder();
            for (char c : current.toCharArray()){
                if(Character.isDigit(c)) numberStr.append(c);
            }
            variable = String.valueOf(numberStr);
            if(!isBranch && !isLoop){
                consume();
            }
            return Token.PUSH;
        }
        switch (current){
            case "DUP":
            case "COPY":
                if(!isBranch && !isLoop){
                    consume();
                }
                return Token.COPY;
            case "ADD":
                if(!isBranch && !isLoop){
                    consume();
                }
                return Token.PLUS;
            case "SUB":
                if(!isBranch && !isLoop){
                    consume();
                }
                return Token.MINUS;
            case "MUL":
            case "MULT":
                if(!isBranch && !isLoop){
                    consume();
                }
                return Token.TIMES;
            case "DIV":
                if(!isBranch && !isLoop){
                    consume();
                }
                return Token.DIVIDE;
            case "TRUE":
                if(!isBranch && !isLoop){
                    consume();
                }
                return Token.TRUE;
            case "FALSE":
                if(!isBranch && !isLoop){
                    consume();
                }
                return Token.FALSE;
            case "EQ":
                if(!isBranch && !isLoop){
                    consume();
                }
                return Token.EQ;
            case "NEQ":
                if(!isBranch && !isLoop){
                    consume();
                }
                return Token.NEQ;
            case "LEQ":
                if(!isBranch && !isLoop){
                    consume();
                }
                return Token.LEQ;
            case "GEQ":
                if(!isBranch && !isLoop){
                    consume();
                }
                return Token.GEQ;
            case "LT":
                if(!isBranch && !isLoop){
                    consume();
                }
                return Token.LT;
            case "GT":
                if(!isBranch && !isLoop){
                    consume();
                }
                return Token.GT;
            case "NEG":
                if(!isBranch && !isLoop){
                    consume();
                }
                return Token.NEG;
            case "AND":
                if(!isBranch && !isLoop){
                    consume();
                }
                return Token.AND;
            case "OR":
                if(!isBranch && !isLoop){
                    consume();
                }
                return Token.OR;
            case "EMPTYOP":
            case "NOOP":
                if(!isBranch && !isLoop){
                    consume();
                }
                return Token.SKIP;
            case "BRANCH":
                isBranch = true;
                return Token.BRANCH;
            case "LOOP":
                isLoop = true;
                return Token.LOOP;
            case "":
            case "exit":
                return Token.EOF;
            default:
                throw new MyException("Error while reading AM instructions");
        }
    }

    public void consume(){
        try {
            current = "";
            while (current.equals("")) {
                current = input.next();
                if(printing.hasNext()) printing.next();
            }
        } catch (IllegalStateException ignored) {
        } catch (NoSuchElementException e){
            input.close();
            current = "exit";
        }

    }

    public void delim(String pattern){
        input.useDelimiter(pattern);
        printing.useDelimiter(pattern);
    }

    public void skip(String pattern){
        input.skip(pattern);
    }

    public String getVariable() {
        return variable;
    }

    public String getCurrent() {
        return current;
    }

    public void setBranch(boolean branch) {
        isBranch = branch;
    }

    public void setLoop(boolean loop) {
        isLoop = loop;
    }

    public void setInput(Scanner input) {
        this.input = input;
    }

    public boolean hasNext(){
        try{
            return input.hasNext();
        }catch (IllegalStateException ignored){
        }
        return false;
    }

    public Scanner getPrinting(){
        return printing;
    }

    public void setPrinting(String string){
        printing = new Scanner(string);
        printing.useDelimiter("[:()]");
    }
}
