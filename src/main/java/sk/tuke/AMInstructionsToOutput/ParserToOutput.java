package sk.tuke.AMInstructionsToOutput;
import sk.tuke.Token;
import sk.tuke.MyException;

import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class ParserToOutput {
    private final LexerToOutput lexer;
    private Token symbol;
    private String var;
    private Stack<String> stack = new Stack<>();
    public String[] stackToPrint = new String[1000];
    private Map<String,Integer> variables = new HashMap<>();
    private Integer[] values = new Integer[100];
    public Integer[][] valsToPrint = new Integer[100][];
    private int valsIndex = 0;
    private int value;
    private int branchesCount;
    private int loopsCount;
    public String[] printing = new String[1000];
    public String[] memPrinting = new String[1000];
    public int printingNum = 0;
    public int stateCount = 0;
    public String[] states = new String[1000];

    public ParserToOutput(LexerToOutput lexer){
        this.lexer = lexer;
        valsIndex++;
        consume();
    }

    public ParserToOutput(LexerToOutput lexer, Map<String,Integer> storage){
        this.lexer = lexer;
        this.variables = storage;
        states[stateCount] = String.join("", "s" + stateCount + " = " + getVariables()+ "\n");
        consume();
    }

    public ParserToOutput(LexerToOutput lexer, Integer[] memory){
        this.lexer = lexer;
        this.values = memory;
        valsToPrint[valsIndex++] = memory.clone();
        consume();
    }

    private void printInstruction(String toPrint){
        if(!toPrint.equals("") && !toPrint.equals("exit")){
            String[] morePrinting = new String[2];
            if(toPrint.contains("BRANCH") || toPrint.contains("LOOP")) {
                if (toPrint.contains("BRANCH")) {
                    morePrinting = printBranch(lexer.getPrinting());
                } else if (toPrint.contains("LOOP")) {
                    morePrinting = printLoop(lexer.getPrinting());
                }
                stackToPrint[printingNum] = Arrays.toString(stack.toArray());
                memPrinting[printingNum] = String.join("","< " + morePrinting[0] + " : CODE, " + Arrays.toString(stack.toArray()) + " , m >\n");
                printing[printingNum++] = String.join("","< " + morePrinting[0] + " : CODE , " + Arrays.toString(stack.toArray()) + " , s" + stateCount + " >\n");
                lexer.setPrinting(morePrinting[1]);
            }
            else{
                stackToPrint[printingNum] = Arrays.toString(stack.toArray());
                memPrinting[printingNum] = String.join("","< " + toPrint + " : CODE , " + Arrays.toString(stack.toArray()) + " , m >\n");
                printing[printingNum++] = String.join("","< " + toPrint + " : CODE , " + Arrays.toString(stack.toArray()) + " , s" + stateCount + " >\n");
            }
        }
    }

    public int count(String pattern, String input) {
        int count = 0;
        Pattern patternObject = Pattern.compile(pattern);
        Matcher matcher = patternObject.matcher(input);
        while(matcher.find()){
            count++;
        }
        return count;
    }

    private String[] printBranch(Scanner printing){
        printing.useDelimiter(",");
        if (printing.hasNext("[(]")) printing.skip("[(]");
        String truePrint;
        String falsePrint;
        String toDo = "";
        truePrint = printing.next();
        branchesCount = count("BRANCH",truePrint) + count("LOOP",truePrint);
        if(branchesCount > 0){
            int counter = 0;
            while(counter < branchesCount) {
                truePrint = String.join(",", truePrint, printing.next());
                branchesCount = count("BRANCH",truePrint) + count("LOOP",truePrint);
                counter++;
            }
        }
        printing.useDelimiter("[)]");
        printing.skip(",");
        falsePrint = printing.next();
        branchesCount = count("BRANCH",falsePrint) + count("LOOP",falsePrint);
        if(branchesCount > 0) {
            int counter = 0;
            while (counter < branchesCount) {
                falsePrint = String.join("", falsePrint + printing.next());
                branchesCount = count("BRANCH",falsePrint) + count("LOOP",falsePrint);
                counter++;
            }
        }
        if(stack.peek().equals("true")) toDo = truePrint;
        else if(stack.peek().equals("false")) toDo = falsePrint;
        printing.useDelimiter("[:()]");
        String[] inst = new String[100];
        inst[0] = String.join("","BRANCH" + truePrint + "," + falsePrint + ")");
        inst[1] = toDo;
        return inst;
    }

    private String[] printLoop(Scanner printing){
        printing.useDelimiter(",");
        if(printing.hasNext("[(]")) printing.skip("[(]");
        String conditionPrint = printing.next();
        printing.skip(",");
        printing.useDelimiter("[)]");
        String loopPrint = printing.next();
        int bracketCounter = count("BRANCH",loopPrint) + count("LOOP",loopPrint);
        if(bracketCounter > 0){
            int counter = 0;
            while(counter < bracketCounter){
                loopPrint = String.join(")",loopPrint,printing.next());
                bracketCounter = count("BRANCH",loopPrint) + count("LOOP",loopPrint);
                counter++;
            }
        }
        String toDo = String.join(":",conditionPrint,"BRANCH(" + loopPrint, "LOOP" + conditionPrint + "," + loopPrint + "),EMPTYOP)");
        String[] inst = new String[100];
        inst[0] = String.join(",","LOOP" + conditionPrint,loopPrint + ")");
        inst[1] = toDo;
        return inst;
    }

    public void parse() throws IOException {
        while (symbol != Token.EOF){
            switch (symbol){
                case PUSH:
                    var = lexer.getVariable();
                    stack.push(var);
                    if(!lexer.isBranch && !lexer.isLoop) {
                        consume();
                        continue;
                    } else return;
                case FETCH:
                    var = lexer.getVariable();
                    if(Character.isDigit(var.charAt(0))){
                        if(values[Integer.parseInt(var)-1] != null) {
                            value = values[Integer.parseInt(var)-1];
                        }
                        else throw new MyException("No such variable");
                    }
                    else {
                        if (variables.containsKey(var)) {
                            value = variables.get(var);
                        } else throw new MyException("No such variable");
                    }
                    stack.push(String.valueOf(value));
                    if (!lexer.isBranch && !lexer.isLoop) {
                        consume();
                        continue;
                    } else return;
                case STORE:
                    var = lexer.getVariable();
                    if(Character.isDigit(var.charAt(0))) {
                        values[Integer.parseInt(var)-1] = Integer.parseInt(stack.pop());
                    }
                    else {
                        value = Integer.parseInt(stack.pop());
                        if (variables.containsKey(var)) variables.replace(var, value);
                        else variables.put(var, value);
                    }
                    valsToPrint[valsIndex++] = values.clone();
                    stateCount++;
                    states[stateCount] = String.join("","s" + stateCount + " = " + getVariables()+"\n");
                    if(!lexer.isBranch && !lexer.isLoop) {
                        consume();
                        continue;
                    } else return;
                case COPY:
                    stack.push(stack.peek());
                    if(!lexer.isBranch && !lexer.isLoop) {
                        consume();
                        continue;
                    } else return;
                case PLUS:
                    int plus = Integer.parseInt(stack.pop()) + Integer.parseInt(stack.pop());
                    stack.push(String.valueOf(plus));
                    if(!lexer.isBranch && !lexer.isLoop) {
                        consume();
                        continue;
                    } else return;
                case MINUS:
                    int minus = Integer.parseInt(stack.pop()) - Integer.parseInt(stack.pop());
                    stack.push(String.valueOf(minus));
                    if(!lexer.isBranch && !lexer.isLoop) {
                        consume();
                        continue;
                    } else return;
                case TIMES:
                    int times = Integer.parseInt(stack.pop()) * Integer.parseInt(stack.pop());
                    stack.push(String.valueOf(times));
                    if(!lexer.isBranch && !lexer.isLoop) {
                        consume();
                        continue;
                    } else return;
                case DIVIDE:
                    int divide = Integer.parseInt(stack.pop()) / Integer.parseInt(stack.pop());
                    stack.push(String.valueOf(divide));
                    if(!lexer.isBranch && !lexer.isLoop) {
                        consume();
                        continue;
                    } else return;
                case TRUE:
                    stack.push("true");
                    if(!lexer.isBranch && !lexer.isLoop) {
                        consume();
                        continue;
                    } else return;
                case FALSE:
                    stack.push("false");
                    if(!lexer.isBranch && !lexer.isLoop) {
                        consume();
                        continue;
                    } else return;
                case EQ:
                    stack.push(String.valueOf((Integer.parseInt(stack.pop()) == Integer.parseInt(stack.pop()))));
                    if(!lexer.isBranch && !lexer.isLoop) {
                        consume();
                        continue;
                    } else return;
                case NEQ:
                    stack.push(String.valueOf((Integer.parseInt(stack.pop()) != Integer.parseInt(stack.pop()))));
                    if(!lexer.isBranch && !lexer.isLoop) {
                        consume();
                        continue;
                    } else return;
                case LEQ:
                    stack.push(String.valueOf((Integer.parseInt(stack.pop()) <= Integer.parseInt(stack.pop()))));
                    if(!lexer.isBranch && !lexer.isLoop) {
                        consume();
                        continue;
                    } else return;
                case GEQ:
                    stack.push(String.valueOf((Integer.parseInt(stack.pop()) >= Integer.parseInt(stack.pop()))));
                    if(!lexer.isBranch && !lexer.isLoop) {
                        consume();
                        continue;
                    } else return;
                case LT:
                    stack.push(String.valueOf((Integer.parseInt(stack.pop()) < Integer.parseInt(stack.pop()))));
                    if(!lexer.isBranch && !lexer.isLoop) {
                        consume();
                        continue;
                    } else return;
                case GT:
                    stack.push(String.valueOf((Integer.parseInt(stack.pop()) > Integer.parseInt(stack.pop()))));
                    if(!lexer.isBranch && !lexer.isLoop) {
                        consume();
                        continue;
                    } else return;
                case NEG:
                    boolean bExp = Boolean.parseBoolean(stack.pop());
                    stack.push(String.valueOf(!bExp));
                    if(!lexer.isBranch && !lexer.isLoop) {
                        consume();
                        continue;
                    } else return;
                case AND:
                    stack.push(String.valueOf(Boolean.parseBoolean(stack.pop())
                                            && Boolean.parseBoolean(stack.pop())));
                    if(!lexer.isBranch && !lexer.isLoop) {
                        consume();
                        continue;
                    } else return;
                case OR:
                    stack.push(String.valueOf(Boolean.parseBoolean(stack.pop())
                                            || Boolean.parseBoolean(stack.pop())));
                    if(!lexer.isBranch && !lexer.isLoop) {
                        consume();
                        continue;
                    } else return;
                case SKIP:
                    if(!lexer.isBranch && !lexer.isLoop) {
                        consume();
                        continue;
                    } else{
                        return;
                    }
                case BRANCH:
                    executeBranch();
                    if(!lexer.isBranch && !lexer.isLoop) {
                        consume();
                        continue;
                    } else return;
                case LOOP:
                    executeLoop();
                    if(!lexer.isBranch && !lexer.isLoop) {
                        consume();
                        continue;
                    } else return;
                default:
                    throw new MyException("Error parsing AM instructions");
            }
        }
    }

    private void consume() throws MyException {
        printInstruction(lexer.getCurrent());
        symbol = lexer.nextToken();
    }

    private void executeBranch() throws IOException {
        String c1,c2;
        lexer.setBranch(true);
        lexer.skip("[(]");
        lexer.delim("[ ,]");
        lexer.consume();
        c1 = lexer.getCurrent();
        lexer.delim("[ )]");
        lexer.skip(",");
        lexer.consume();
        c2 = lexer.getCurrent();
        if (c1.contains("BRANCH")){
            branchesCount = count("BRANCH",c1);
            if(lexer.isLoop) branchesCount++;
            while(branchesCount > 0) {
                c1 = String.join(",", c1, c2 + ")");
                lexer.delim("[ ),]");
                lexer.consume();
                c2 = lexer.getCurrent();
                branchesCount--;
            }
        }
        else if (c1.contains("LOOP")){
            lexer.delim("[ ,]");
            c1 = String.join(",",c1,c2);
            do {
                lexer.consume();
                c2 = lexer.getCurrent();
                if (c2.charAt(0) == ')') c1 = String.join("",c1,c2);
                else c1 = String.join(",", c1, c2);
            }
            while(c2.contains("LOOP"));
            lexer.delim("[ ,)]");
            lexer.consume();
            c2 = lexer.getCurrent();
        }
        if (c2.contains("BRANCH")){
            c2 = String.join("",c2 + ")");
        }
        lexer.delim("[ :]");
        String whole = "";
        lexer.consume();
        while (!lexer.getCurrent().equals("exit") && !lexer.getCurrent().equals("")){
            whole = String.join(":",whole,lexer.getCurrent());
            lexer.consume();
        }
        String condition = stack.pop();
        if (condition.equals("true")) {
            Scanner scanner = new Scanner(c1);
            lexer.setInput(scanner);
            lexer.setPrinting(c1);
        }
        else if (condition.equals("false")) {
            Scanner scanner = new Scanner(c2);
            lexer.setInput(scanner);
            lexer.setPrinting(c2);
        }
        else throw new MyException("No boolean in stack!");
        lexer.delim("[ :()]");
        while(lexer.hasNext()){
            lexer.consume();
            consume();
            parse();
        }
        lexer.setInput(new Scanner(whole));
        lexer.setPrinting(whole);
        lexer.delim("[ :,()]");
        lexer.setBranch(false);
        lexer.consume();
    }

    private void executeLoop() throws IOException {
        String c1,c2;
        lexer.setLoop(true);
        lexer.delim("[ (),]");
        lexer.consume();
        c1 = lexer.getCurrent();
        lexer.consume();
        c2 = lexer.getCurrent();
        if (c2.contains("BRANCH")){
            lexer.delim("[ )]");
            branchesCount = count("BRANCH",c2)+1;
            while(branchesCount > 0) {
                lexer.consume();
                if(lexer.isLoop && branchesCount == 1) c2 = String.join("",c2, lexer.getCurrent());
                else c2 = String.join("", c2, lexer.getCurrent() + ")");
                branchesCount--;
            }
        }
        if(c2.contains("LOOP")){
            lexer.delim("[ )]");
            loopsCount = count("LOOP",c2) + 1;
            while(loopsCount > 0) {
                lexer.consume();
                if (loopsCount == 1)  c2 = String.join("",c2,lexer.getCurrent());
                else c2 = String.join("", c2, lexer.getCurrent() + ")");
                loopsCount--;
            }
        }
        String nextCode = String.join(":",c1, "BRANCH(" + c2, "LOOP(" + c1 + "," + c2 + "),EMPTYOP)");
        lexer.delim("[ :]");
        String whole = "";
        lexer.consume();
        while (!lexer.getCurrent().equals("exit")){
            whole = String.join(":",whole,lexer.getCurrent());
            lexer.consume();
        }
        Scanner scanner = new Scanner(nextCode);
        lexer.setInput(scanner);
        lexer.setPrinting(nextCode);
        lexer.delim("[ :()]");
        while(lexer.hasNext()){
            lexer.consume();
            consume();
            parse();
        }
        lexer.setInput(new Scanner(whole));
        lexer.setPrinting(whole);
        lexer.delim("[ :()]");
        lexer.setLoop(false);
        lexer.consume();
    }

    public Map<String, Integer> getVariables() {
        return variables;
    }

    public String getPrinting(int idx){
        return printing[idx];
    }

    public String getMemPrinting(int idx){return memPrinting[idx];}

    public int getMax(){
        return printingNum-1;
    }

    public void lastOne(){
        printing[printingNum] = String.join("","< ε , " + Arrays.toString(stack.toArray()) + " , s" + stateCount + " >\n");
        memPrinting[printingNum] = String.join("","< ε , " + Arrays.toString(stack.toArray()) + " , m >\n");
    }

    public String getStack(int idx){
        if(idx == printingNum) return Arrays.toString(stack.toArray());
        return stackToPrint[idx];
    }

    public String getAllStates(int idx){
        return states[idx];
    }

    public Integer[] getMemory(int idx){
        return valsToPrint[idx];
    }

}
