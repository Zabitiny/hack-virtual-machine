import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Parser extends CodeWriter{

    private Scanner scan;
    private String currComm;

    public Parser(String inputFile, String outputFile) throws FileNotFoundException {
        super(inputFile, outputFile);
        scan = new Scanner(new File(inputFile));
    }

    public void setReadFile(String fileName) throws FileNotFoundException {
        scan = new Scanner(new File(fileName));
    }

    public boolean hasMoreCommands() {
        return scan.hasNextLine();
    }

    public void advance() {
        currComm = scan.nextLine();
        int n = currComm.length();
        while(n == 0){
            currComm = scan.nextLine();
            n = currComm.length();
        }
        
        StringBuilder sb = new StringBuilder();
        for(int i=0; i < n; i++) {
            char ch = currComm.charAt(i);
            
            if(ch == '/' && currComm.charAt(i+1) == '/') {
                if(sb.length() == 0){
                    currComm = scan.nextLine();
                    n = currComm.length();
                    i=-1;
                    continue;
                } else {
                    currComm = sb.toString();
                    break;
                }
            }
            sb.append(ch);
        }

        currComm = sb.toString().trim();
        if(currComm.length() == 0) advance();
    }

    public Command CommandType() {
        //char ch = currComm.charAt(0);
        //char next = currComm.charAt(1);
        if(currComm.contains("push")) return Command.PUSH;
        else if(currComm.contains("pop")) return Command.POP;
        else if(currComm.contains("label")) return Command.LABEL;
        else if(currComm.contains("if")) return Command.IF;
        else if(currComm.contains("goto")) return Command.GOTO;
        else if(currComm.contains("function")) return Command.FUNCTION;
        else if(currComm.contains("call")) return Command.CALL;
        else if(currComm.contains("return")) return Command.RETURN;
        return Command.ARITHMETIC;
    }

    public String arg1() {
        if(CommandType() == Command.ARITHMETIC) {
            return currComm;
        }
        
        StringBuilder rtn = new StringBuilder();
        int n = currComm.length();
        boolean arg = false;
        int i;
        for(i=0; i < n; i++) {
            char ch = currComm.charAt(i);
            
            while(ch == ' ') {
                arg = true;
                ch = currComm.charAt(++i);
            }
            if(arg) {
                break;
            }
        }

        while(i < n) {
            char ch = currComm.charAt(i++);
            if(ch == ' ') {
                break;
            }
            rtn.append(ch);
        }

        return rtn.toString().trim();
    }

    public int arg2() {
        int n = currComm.length();
        int i;
        for(i = n-1; i >= 0; i--) {
            if(currComm.charAt(i) == ' ') break;
        }
        i++;
        StringBuilder rtn = new StringBuilder();
        while(i < n) {
            rtn.append(currComm.charAt(i++));
        }

        return Integer.parseInt(rtn.toString());
    }
}