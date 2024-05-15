package sk.tuke.InputToAMInstructions;
import sk.tuke.Token;

import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.regex.Pattern;

public class LexerToAM {
    private String current;
    private int value;
    private String readHelper = "";
    private int readHelperIndex = 0;
    private String variable;
    private  final Scanner input;

    public LexerToAM(Scanner scanner){
        this.input = scanner;
        this.input.useDelimiter("[ \r\n]+");
        consume();
    }

    public Token nextToken() throws IOException{
        value = 0;
        if(current.charAt(0) > 47 && current.charAt(0) < 58){
            value = Integer.parseInt(current);
            consume();
            return Token.NUMBER;
        }

        switch (current){
            case "+":
                consume();
                return Token.PLUS;
            case "-":
                consume();
                return Token.MINUS;
            case "*":
                consume();
                return Token.TIMES;
            case "/":
                consume();
                return Token.DIVIDE;
            case "(":
                consume();
                return Token.LEFT_BRACKET;
            case ")":
                consume();
                return Token.RIGHT_BRACKET;
            case "if":
                consume();
                return Token.IF;
            case "then":
                consume();
                return Token.THEN;
            case "else":
                consume();
                return Token.ELSE;
            case "while":
                consume();
                return Token.WHILE;
            case "do":
                consume();
                return Token.DO;
            case "for":
                consume();
                return Token.FOR;
            case "to":
                consume();
                return Token.TO;
            case "repeat":
                consume();
                return Token.REPEAT;
            case "until":
                consume();
                return Token.UNTIL;
            case "true":
                consume();
                return Token.TRUE;
            case "false":
                consume();
                return Token.FALSE;
            case "!":
            case "¬":
            case "not":
                consume();
                return Token.NEG;
            case "&":
            case "∧":
            case "&&":
            case "and":
                consume();
                return Token.AND;
            case "||":
            case "|":
            case "or":
            case "∨":
                consume();
                return Token.OR;
            case "==":
                consume();
                return Token.EQ;
            case "!=":
                consume();
                return Token.NEQ;
            case "<=":
            case "≤":
                consume();
                return Token.LEQ;
            case "<":
                consume();
                return Token.LT;
            case ">":
                consume();
                return Token.GT;
            case ">=":
            case "≥":
                consume();
                return Token.GEQ;
            case ":=":
            case "=":
                consume();
                return Token.ASSIGN;
            case "skip":
                consume();
                return Token.SKIP;
            case ";":
                consume();
                return Token.LIM;
            case "exit":
                return Token.EOF;
            default:
                if(check_variable(current)){
                    variable = current;
                    consume();
                    return Token.VARIABLE;
                }
                throw new IOException();
        }
    }

    private boolean check_variable(String str){
        for (int i = 0 ; i < str.length() ; i++){
            if ((str.charAt(i) > 64 && str.charAt(i) < 91) || (str.charAt(i) > 96 && str.charAt(i) < 123)) continue;
            if(i!=0) if(str.charAt(i) > 47 && str.charAt(i) < 58) continue;
            return false;
        }
        return true;
    }

    private boolean check_number(String str){
        for (int i = 0; i < str.length(); i++){
            if(current.charAt(i) > 47 && current.charAt(i) < 58) continue;
            return false;
        }
        return true;
    }
    private void consume(){
        try{
            if(readHelper.equals("")) {
                current = input.next();
                if (current.length() > 1){
                    if(!check_all(current)){
                        readHelper = current;
                    }
                }
            }
            Pattern pattern = Pattern.compile("\\w+");
            if(!readHelper.equals("")){
                if (pattern.matcher(readHelper).matches()){
                    current = readHelper;
                    readHelper = "";
                    readHelperIndex = 0;
                    return;
                }
                if(readHelperIndex < readHelper.length()){
                    current = "";
                    while(!check_read(current)){
                        current = String.join("",current+readHelper.charAt(readHelperIndex++));
                    }
                    if(!(readHelperIndex == readHelper.length())) {
                        if (((current.equals("=") || current.equals("!") || current.equals("<") || current.equals(">")) && next_eq(readHelper, readHelperIndex)) || (current.equals("&") && next_and(readHelper, readHelperIndex)) || (current.equals("|") && next_or(readHelper, readHelperIndex))) {
                            current = String.join("", current + readHelper.charAt(readHelperIndex++));
                        }
                        if(Character.isLetter(current.charAt(0))){
                            while(Character.isLetter(readHelper.charAt(readHelperIndex))){
                                current = String.join("", current + readHelper.charAt(readHelperIndex++));
                            }
                        }
                        if(Character.isDigit(current.charAt(0))){
                            while(Character.isDigit(readHelper.charAt(readHelperIndex))){
                                current = String.join("", current + readHelper.charAt(readHelperIndex++));
                            }
                        }
                        if(!(readHelperIndex == readHelper.length())) {
                            readHelper = readHelper.substring(readHelperIndex);
                            readHelperIndex = 0;
                        }
                    }
                    if (readHelperIndex >= readHelper.length()) {
                        readHelper = "";
                        readHelperIndex = 0;
                    }
                }
            }
        }catch (IllegalStateException ignored){}
        catch (NoSuchElementException e){
            input.close();
            current = "exit";
        }
    }

    private boolean next_eq(String str,int idx){ return str.charAt(idx) == '=';}

    private boolean next_and(String str,int idx){ return str.charAt(idx) == '&';}

    private boolean next_or(String str,int idx){ return str.charAt(idx) == '|';}

    private boolean check_all(String str){
        return str.equals("if") || check_number(str) || str.equals("then") || str.equals("else") || str.equals("while") || str.equals("do") || str.equals("for") || str.equals("to") || str.equals("repeat") || str.equals("until") || str.equals("true") || str.equals("false") || str.equals("not") || str.equals("&&") || str.equals("and") || str.equals("||") || str.equals("or") || str.equals("==") || str.equals("!=") || str.equals("<=") || str.equals(">=") || str.equals(":=") || str.equals("skip") || str.equals("+") || str.equals("-") || str.equals("*") || str.equals("/") || str.equals("(") || str.equals(")") || str.equals("¬") || str.equals("!") || str.equals("&") || str.equals("|") || str.equals("∧") || str.equals("∨") || str.equals("≤") || str.equals("<") || str.equals(">") || str.equals("≥") || str.equals("=") || str.equals(";");
    }

    private boolean check_read(String str){
        if(str.equals(""))return false;
        return check_number(str) || check_variable(str) || str.equals(":=") || str.equals("+") || str.equals("-") || str.equals("*") || str.equals("/") || str.equals("(") || str.equals(")") || str.equals("¬") || str.equals("!") || str.equals("&") || str.equals("|") || str.equals("∧") || str.equals("∨") || str.equals("≤") || str.equals("<") || str.equals(">") || str.equals("≥") || str.equals("=") || str.equals(";");
    }
    public int getValue() {
        return value;
    }
    public String getVariable() {
        return variable;
    }
}
