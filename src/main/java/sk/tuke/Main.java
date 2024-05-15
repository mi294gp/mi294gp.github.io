/*package sk.tuke;
import sk.tuke.AMInstructionsToOutput.LexerToOutput;
import sk.tuke.AMInstructionsToOutput.ParserToOutput;
import sk.tuke.InputToAMInstructions.LexerToAM;
import sk.tuke.InputToAMInstructions.ParserToAM;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String input = "";
        try{
            while(!input.contains("exit")){
                input = String.join("",input+reader.readLine());
            }
        }catch (IOException e){
            e.printStackTrace();
        }
        Scanner scanner = new Scanner(input);
        LexerToAM lexerToAM = new LexerToAM(scanner);
        ParserToAM parserToAM = new ParserToAM(lexerToAM);
        String code = parserToAM.parse();
        System.out.println(code);
        Scanner toPrint;
        if(!code.equals("")) {
            scanner = new Scanner(code);
            scanner.useDelimiter("[ :,()]");
            toPrint = new Scanner(code);
        }
        else{
            scanner = new Scanner(input);
            scanner.useDelimiter("[ :,()]");
            toPrint = new Scanner(input);
        }
        LexerToOutput lexerToOutput = new LexerToOutput(scanner,toPrint);
        ParserToOutput parserToOutput = new ParserToOutput(lexerToOutput);
        parserToOutput.parse();
        System.out.println(parserToOutput.getVariables());
    }

}*/