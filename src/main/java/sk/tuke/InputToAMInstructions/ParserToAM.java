package sk.tuke.InputToAMInstructions;
import sk.tuke.Token;
import sk.tuke.MyException;

import java.io.IOException;

public class ParserToAM {
    private final LexerToAM lexer;
    private Token symbol;
    private String variable;

    public ParserToAM(LexerToAM lexer) throws MyException, IOException{
        this.lexer = lexer;
        consume();
    }

    public String parse() throws MyException, IOException {
        return S();
    }

    public String S() throws IOException {
        switch (symbol){
            case VARIABLE:
                variable = lexer.getVariable();
                consume();
                return S();
            case ASSIGN:
                consume();
                String assVar = variable;
                String expr = E();
                if (symbol == Token.LIM || symbol == Token.SKIP){
                    return String.join(":",expr,"STORE-" + assVar + S());
                }else{
                    return String.join(":", expr, "STORE-" + assVar);
                }
            case SKIP:
                consume();
                if(symbol == Token.EOF || symbol == Token.LIM) return ":EMPTYOP" + S();
                return ":EMPTYOP:" + S();
            case LIM:
                consume();
                if(symbol == Token.EOF || symbol == Token.SKIP) return S();
                else if (symbol == Token.ELSE) return "";
                return ":" + S();
            case IF:
                consume();
                String ifCondition = B();
                consume();
                String s1 = S();
                consume();
                String s2 = S();
                return ifCondition + ":" + "BRANCH(" + s1 + "," + s2 + ")";
            case WHILE:
                consume();
                String whileCondition = B();
                consume();
                String whileLoop = S();
                return "LOOP(" + whileCondition + "," + whileLoop + ")";
            case FOR:
                consume();
                String forVar = lexer.getVariable();
                consume();
                consume();
                String exp1 = E();
                consume();
                String exp2 = E();
                consume();
                String forDo = S();
                return String.join(":",exp1,"LOOP(STORE-" + forVar, "FETCH-" + forVar, "COPY", exp2,"GE," + forDo,"PUSH-1","ADD)");
            case REPEAT:
                consume();
                String repeat = S();
                consume();
                String repeatCondition = B();
                return String.join(":",repeat, "LOOP(" + repeatCondition, "NEG," + repeat + ")");
            case LEFT_BRACKET:
                consume();
                String bracket = S();
                consume();
                return bracket;
            case EOF:
                return "";
            default:
                throw new MyException("Invalid STATEMENT" + symbol);
        }

    }

    public String B() throws IOException {
        switch (symbol){
            case TRUE:
                consume();
                if(symbol == Token.AND) {
                    String and = B();
                    return String.join(":",and,"TRUE","AND");
                } else if (symbol == Token.OR) {
                    String or = B();
                    return String.join(":",or,"TRUE","OR");
                }
                return "TRUE";
            case FALSE:
                consume();
                if(symbol == Token.AND) {
                    String and = B();
                    return String.join(":",and,"FALSE","AND");
                } else if (symbol == Token.OR) {
                    String or = B();
                    return String.join(":",or,"FALSE","OR");
                }
                return "FALSE";
            case NEG:
                consume();
                return String.join(":",B(),"NEG");
            case AND:
            case OR:
                consume();
                return B();
            case LEFT_BRACKET:
                consume();
                String bracket = B();
                consume();
                return bracket;
            default:
                String be1 = BE();
                if(symbol == Token.AND) {
                    String and = B();
                    return String.join(":",and,be1,"AND");
                } else if (symbol == Token.OR) {
                    String or = B();
                    return String.join(":",or,be1,"OR");
                }
                return be1;
        }

    }

    public String BE() throws IOException{
        String exp1 = E();
        switch (symbol){
            case EQ:
                consume();
                String eqexp2 = E();
                return String.join(":",eqexp2,exp1,"EQ");
            case NEQ:
                consume();
                String neqexp2 = E();
                return String.join(":",neqexp2,exp1,"NEQ");
            case LEQ:
                consume();
                String leqexp2 = E();
                return String.join(":",leqexp2,exp1,"LEQ");
            case LT:
                consume();
                String ltexp2 = E();
                return String.join(":",ltexp2,exp1,"LT");
            case GT:
                consume();
                String gtexp2 = E();
                return String.join(":",gtexp2,exp1,"GT");
            case GEQ:
                consume();
                String geqexp2 = E();
                return String.join(":",geqexp2,exp1,"GEQ");
            default:
                throw new MyException("Invalid BOOL EXPRESSION WITH NUMBERS" + symbol);
        }
    }

    public String E() throws IOException {
        String toReturn = "";
        String f1 = T();
        int times = 0;
        char znak;
        while(symbol == Token.PLUS || symbol == Token.MINUS){
            if (symbol == Token.PLUS) znak = '+';
            else znak = '-';
            consume();
            String f2 = T();
            if (znak == '+'){
                if(times == 0) toReturn = String.join(":",f2,f1,"ADD");
                else toReturn = String.join(":",toReturn,f2,"ADD");
            }
            else{
                if(times == 0) toReturn = String.join(":",f2,f1,"SUB");
                else toReturn = String.join(":",toReturn,"STORE-help",f2,"FETCH-help","SUB");
            }
            times++;
        }
        if(toReturn.isEmpty()) toReturn = f1;
        return toReturn;
    }

    public String T() throws IOException {
        String toReturn = "";
        String f1 = F();
        int times = 0;
        char znak;
        while(symbol == Token.TIMES || symbol == Token.DIVIDE){
            if (symbol == Token.TIMES) znak = '*';
            else znak = '/';
            consume();
            String f2 = F();
            if (znak == '*'){
                if(times == 0) toReturn = String.join(":",f2,f1,"MUL");
                else toReturn = String.join(":",toReturn,f2,"MUL");
            }
            else{
                if(times == 0) toReturn = String.join(":",f2,f1,"DIV");
                else toReturn = String.join(":",toReturn,"STORE-help",f2,"FETCH-help","DIV");
            }
            times++;
        }
        if(toReturn.isEmpty()) toReturn = f1;
        return toReturn;
    }

    public String F() throws IOException {
        switch (symbol){
            case NUMBER:
                String num = String.valueOf(lexer.getValue());
                consume();
                return "PUSH-"+num;
            case VARIABLE:
                variable = lexer.getVariable();
                consume();
                return "FETCH-"+variable;
            case LEFT_BRACKET:
                consume();
                String bracket = E();
                consume();
                return bracket;
            default:
                throw new MyException("Invalid symbol" + symbol);
        }
    }

    private void consume() throws MyException, IOException {
        symbol = lexer.nextToken();
    }
}
