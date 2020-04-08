import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.EmptyStackException;
import java.util.Stack;

public class CodeWriter {

    private PrintWriter pw;
    private int eqLbl=0, ltLbl=0, gtLbl=0, contLbl=0, rtnAdd=0;
    private String fileName, currFun;
    private Stack<String> callStack = new Stack<>();

    public enum Command {
        ARITHMETIC,
        PUSH,
        POP,
        LABEL,
        GOTO,
        IF,
        FUNCTION,
        RETURN,
        CALL;
    }

    public CodeWriter(String inFileName, String outFileName) throws FileNotFoundException {
        pw = new PrintWriter(outFileName);
        fileName = cleanFileName(inFileName);
    }

    private String cleanFileName(String inFileName) {
        StringBuilder rtn = new StringBuilder();
        int n = inFileName.length();
        int i;
        for(i=n-1; i >= 0; i--) {
            char ch = inFileName.charAt(i);
            if(ch == '/' || ch == '\\') {
                i++;
                break;
            }
            else if(i == 0) {
                rtn.append(i++);
                break;
            }
        }
        while(i < n) {
            char ch = inFileName.charAt(i++);
            if(ch == '.') break;
            rtn.append(ch);
        }
        return rtn.toString();
    }

    /**used for translating new file */
    public void setFileName(String fn) throws FileNotFoundException {
        fileName = cleanFileName(fn);
    }

    public void writeArithmetic(String str) {
        switch (str) {
            case("add"):
                pw.println("@SP");
                pw.println("AM=M-1");
                pw.println("D=M");
                pw.println("A=A-1");
                pw.println("M=M+D");
                pw.println("//END ADD");
                break;
            case("sub"):
                pw.println("@SP");
                pw.println("AM=M-1");
                pw.println("D=M");
                pw.println("A=A-1");
                pw.println("M=M-D");
                pw.println("//END SUB");
                break;
            case("neg"):
                pw.println("@SP");
                pw.println("A=M-1");
                pw.println("M=-M");
                pw.println("//END NEG");
                break;
            case("eq"):
                pw.println("@SP");
                pw.println("AM=M-1");
                pw.println("D=M");
                pw.println("@SP");
                pw.println("AM=M-1");
                pw.println("D=D-M");
                pw.println("@EQUAL"+eqLbl);
                pw.println("D;JEQ");
                pw.println("@SP");
                pw.println("A=M");
                pw.println("M=0");
                pw.println("@CONTINUE"+contLbl);
                pw.println("0;JMP");
                pw.println("(EQUAL"+(eqLbl++) +")");
                pw.println("@SP");
                pw.println("A=M");
                pw.println("M=-1");
                pw.println("(CONTINUE"+(contLbl++) +")");
                pw.println("@SP");
                pw.println("M=M+1");
                pw.println("//END EQ");
                break;
            case("gt"):
                pw.println("@SP");
                pw.println("AM=M-1");
                pw.println("D=M");
                pw.println("@SP");
                pw.println("AM=M-1");
                pw.println("D=M-D");
                pw.println("@GT"+gtLbl);
                pw.println("D;JGT");
                pw.println("@SP");
                pw.println("A=M");
                pw.println("M=0");
                pw.println("@CONTINUE"+contLbl);
                pw.println("0;JMP");
                pw.println("(GT"+(gtLbl++) +")");
                pw.println("@SP");
                pw.println("A=M");
                pw.println("M=-1");
                pw.println("(CONTINUE"+(contLbl++) +")");
                pw.println("@SP");
                pw.println("M=M+1");
                pw.println("//END GT");
                break;
            case("lt"):
                pw.println("@SP");
                pw.println("AM=M-1");
                pw.println("D=M");
                pw.println("@SP");
                pw.println("AM=M-1");
                pw.println("D=M-D");
                pw.println("@LT"+ltLbl);
                pw.println("D;JLT");
                pw.println("@SP");
                pw.println("A=M");
                pw.println("M=0");
                pw.println("@CONTINUE"+contLbl);
                pw.println("0;JMP");
                pw.println("(LT"+(ltLbl++) +")");
                pw.println("@SP");
                pw.println("A=M");
                pw.println("M=-1");
                pw.println("(CONTINUE"+(contLbl++) +")");
                pw.println("@SP");
                pw.println("M=M+1");
                pw.println("//END LT");
                break;
            case("and"):
                pw.println("@SP");
                pw.println("AM=M-1");
                pw.println("D=M");
                pw.println("A=A-1");
                pw.println("M=D&M");
                pw.println("//END AND");
                break;
            case("or"):
                pw.println("@SP");
                pw.println("AM=M-1");
                pw.println("D=M");
                pw.println("A=A-1");
                pw.println("M=D|M");
                pw.println("//END OR");
                break;
            case("not"):
                pw.println("@SP");
                pw.println("A=M-1");
                pw.println("M=!M");
                pw.println("//END NOT");
                break;
        }
    }

    public void writePushPop(Command comm, String seg, int index) {
        if(comm == Command.PUSH) {
            if(seg.equals("constant")) {
                pw.println("@"+index);
                pw.println("D=A");
                pw.println("@SP");
                pw.println("A=M");
                pw.println("M=D");
                pw.println("@SP");
                pw.println("M=M+1");
                pw.println("//END PUSH CONST");
            }
            else if(seg.equals("local")) {
                pw.println("@"+index);
                pw.println("D=A");
                pw.println("@LCL");
                pw.println("A=M+D");
                pw.println("D=M");
                pw.println("@SP");
                pw.println("A=M");
                pw.println("M=D");
                pw.println("@SP");
                pw.println("M=M+1");
                pw.println("//END PUSH LCL");
            }
            else if(seg.equals("argument")) {
                pw.println("@"+index);
                pw.println("D=A");
                pw.println("@ARG");
                pw.println("A=M+D");
                pw.println("D=M");
                pw.println("@SP");
                pw.println("A=M");
                pw.println("M=D");
                pw.println("@SP");
                pw.println("M=M+1");
                pw.println("//END PUSH ARG");
            }
            else if(seg.equals("this")) {
                pw.println("@"+index);
                pw.println("D=A");
                pw.println("@THIS");
                pw.println("A=M+D");
                pw.println("D=M");
                pw.println("@SP");
                pw.println("A=M");
                pw.println("M=D");
                pw.println("@SP");
                pw.println("M=M+1");
                pw.println("//END PUSH THIS");
            }
            else if(seg.equals("that")) {
                pw.println("@"+index);
                pw.println("D=A");
                pw.println("@THAT");
                pw.println("A=M+D");
                pw.println("D=M");
                pw.println("@SP");
                pw.println("A=M");
                pw.println("M=D");
                pw.println("@SP");
                pw.println("M=M+1");
                pw.println("//END PUSH THAT");
            }
            else if(seg.equals("temp")) {
                pw.println("@"+index);
                pw.println("D=A");
                pw.println("@5");
                pw.println("A=A+D");
                pw.println("D=M");
                pw.println("@SP");
                pw.println("A=M");
                pw.println("M=D");
                pw.println("@SP");
                pw.println("M=M+1");
                pw.println("//END PUSH TEMP");
            }
            else if(seg.equals("pointer")) {
                pw.println("@"+index);
                pw.println("D=A");
                pw.println("@3");
                pw.println("A=A+D");
                pw.println("D=M");
                pw.println("@SP");
                pw.println("A=M");
                pw.println("M=D");
                pw.println("@SP");
                pw.println("M=M+1");
                pw.println("//END PUSH POINT");
            }
            else if(seg.equals("static")) {
                pw.println("@"+fileName+"."+index);
                pw.println("D=M");
                pw.println("@SP");
                pw.println("A=M");
                pw.println("M=D");
                pw.println("@SP");
                pw.println("M=M+1");
                pw.println("//END PUSH STATIC");
            }
        }
        else if(comm == Command.POP) {
            if(seg.equals("local")) {
                pw.println("@"+index);
                pw.println("D=A");
                pw.println("@LCL");
                pw.println("A=M");
                pw.println("D=D+A");
                pw.println("@R13");
                pw.println("M=D");
                pw.println("@SP");
                pw.println("AM=M-1");
                pw.println("D=M");
                pw.println("@R13");
                pw.println("A=M");
                pw.println("M=D");
                pw.println("//END POP LCL");
            }
            else if(seg.equals("argument")) {
                pw.println("@"+index);
                pw.println("D=A");
                pw.println("@ARG");
                pw.println("A=M");
                pw.println("D=D+A");
                pw.println("@R13");
                pw.println("M=D");
                pw.println("@SP");
                pw.println("AM=M-1");
                pw.println("D=M");
                pw.println("@R13");
                pw.println("A=M");
                pw.println("M=D");
                pw.println("//END POP ARG");
            }
            else if(seg.equals("this")) {
                pw.println("@"+index);
                pw.println("D=A");
                pw.println("@THIS");
                pw.println("A=M");
                pw.println("D=D+A");
                pw.println("@R13");
                pw.println("M=D");
                pw.println("@SP");
                pw.println("AM=M-1");
                pw.println("D=M");
                pw.println("@R13");
                pw.println("A=M");
                pw.println("M=D");
                pw.println("//END POP THIS");
            }
            else if(seg.equals("that")) {
                pw.println("@"+index);
                pw.println("D=A");
                pw.println("@THAT");
                pw.println("A=M");
                pw.println("D=D+A");
                pw.println("@R13");
                pw.println("M=D");
                pw.println("@SP");
                pw.println("AM=M-1");
                pw.println("D=M");
                pw.println("@R13");
                pw.println("A=M");
                pw.println("M=D");
                pw.println("//END POP THAT");
            }
            else if(seg.equals("temp")) {
                pw.println("@"+index);
                pw.println("D=A");
                pw.println("@5");
                pw.println("D=D+A");
                pw.println("@R13");
                pw.println("M=D");
                pw.println("@SP");
                pw.println("AM=M-1");
                pw.println("D=M");
                pw.println("@R13");
                pw.println("A=M");
                pw.println("M=D");
                pw.println("//END POP TEMP");
            }
            else if(seg.equals("pointer")) {
                pw.println("@"+index);
                pw.println("D=A");
                pw.println("@3");
                pw.println("D=D+A");
                pw.println("@R13");
                pw.println("M=D");
                pw.println("@SP");
                pw.println("AM=M-1");
                pw.println("D=M");
                pw.println("@R13");
                pw.println("A=M");
                pw.println("M=D");
                pw.println("//END POP POINT");
            }
            else if(seg.equals("static")) {
                pw.println("@SP");
                pw.println("AM=M-1");
                pw.println("D=M");
                pw.println("@"+fileName+"."+index);
                pw.println("M=D");
                pw.println("//END POP STATIC");
            }
        }
    }

    public void writeInit() {
        pw.println("@256");
        pw.println("D=A");
        pw.println("@SP");
        pw.println("M=D");            //SP = 256
        writeCall("Sys.init", 0);     //call Sys.init
    }

    public void writeLabel(String label) {
        pw.println("("+currFun+"$"+label+")");
    }

    public void writeGoto(String label) {
        pw.println("@"+currFun+"$"+label);
        pw.println("0; JMP");
    }

    public void writeIf(String label) {
        pw.println("@SP");
        pw.println("AM=M-1");
        pw.println("D=M");
        pw.println("@"+currFun+"$"+label);
        pw.println("D; JNE");
        pw.println("//END IF");
    }

    public void writeCall(String funName, int numArgs) {
        callStack.push(currFun = funName);
        //push return-address
        pw.println("@RETURN"+rtnAdd);
        pw.println("D=A");
        pw.println("@SP");
        pw.println("A=M");
        pw.println("M=D");
        pw.println("@SP");
        pw.println("M=M+1");
        //push LCL
        pw.println("@LCL");
        pw.println("D=M");
        pw.println("@SP");
        pw.println("A=M");
        pw.println("M=D");
        pw.println("@SP");
        pw.println("M=M+1");
        //push ARG
        pw.println("@ARG");
        pw.println("D=M");
        pw.println("@SP");
        pw.println("A=M");
        pw.println("M=D");
        pw.println("@SP");
        pw.println("M=M+1");
        //push THIS
        pw.println("@THIS");
        pw.println("D=M");
        pw.println("@SP");
        pw.println("A=M");
        pw.println("M=D");
        pw.println("@SP");
        pw.println("M=M+1");
        //push THAT
        pw.println("@THAT");
        pw.println("D=M");
        pw.println("@SP");
        pw.println("A=M");
        pw.println("M=D");
        pw.println("@SP");
        pw.println("M=M+1");
        //ARG = SP-n-5
        pw.println("@5");
        pw.println("D=A");
        pw.println("@"+numArgs);
        pw.println("D=D+A");
        pw.println("@SP");
        pw.println("D=M-D");
        pw.println("@ARG");
        pw.println("M=D");
        //LCL = SP
        pw.println("@SP");
        pw.println("D=M");
        pw.println("@LCL");
        pw.println("M=D");
        //goto f
        pw.println("@"+funName);
        pw.println("0; JMP");
        pw.println("(RETURN"+(rtnAdd++) +")");
        pw.println("//END CALL");
    }

    public void writeReturn() {
        try{
            currFun = callStack.pop();
        } catch(EmptyStackException e) {
            callStack.push(currFun = "main");
        }
        //FRAME = LCL
        pw.println("@LCL");
        pw.println("D=M");
        pw.println("@FRAME");
        pw.println("M=D");
        //RET = *(FRAME-5)
        pw.println("@5");
        pw.println("A=D-A");
        pw.println("D=M");
        pw.println("@RETADD");
        pw.println("M=D");
        //*ARG=pop()
        pw.println("@SP");
        pw.println("AM=M-1");
        pw.println("D=M");
        pw.println("@ARG");
        pw.println("A=M");
        pw.println("M=D");
        //SP=ARG+1
        pw.println("@ARG");
        pw.println("D=M");
        pw.println("@SP");
        pw.println("M=D+1");
        //THAT = *(FRAME-1)
        pw.println("@FRAME");
        pw.println("A=M-1");
        pw.println("D=M");
        pw.println("@THAT");
        pw.println("M=D");
        //THIS = *(FRAME-2)
        pw.println("@FRAME");
        pw.println("D=M");
        pw.println("@2");
        pw.println("A=D-A");
        pw.println("D=M");
        pw.println("@THIS");
        pw.println("M=D");
        //ARG = *(FRAME-3)
        pw.println("@FRAME");
        pw.println("D=M");
        pw.println("@3");
        pw.println("A=D-A");
        pw.println("D=M");
        pw.println("@ARG");
        pw.println("M=D");
        //LCL = *(FRAME-4)
        pw.println("@FRAME");
        pw.println("D=M");
        pw.println("@4");
        pw.println("A=D-A");
        pw.println("D=M");
        pw.println("@LCL");
        pw.println("M=D");
        //goto RET
        pw.println("@RETADD");
        pw.println("A=M");
        pw.println("0; JMP");
        pw.println("//END RETURN");
    }

    public void writeFunction(String funName, int numLocals) {
            pw.println("("+funName+")");
            pw.println("@0");
            pw.println("D=A");
            for(int i=0; i < numLocals; i++) {
                pw.println("@SP");
                pw.println("A=M");
                pw.println("M=D");
                pw.println("@SP");
                pw.println("M=M+1");
            }
            pw.println("//END FUN");
    }

    public void close() {
        pw.println("(END-VM-PROGRAM)");
        pw.println("@END-VM-PROGRAM");
        pw.println("0; JMP");
        pw.close();
    }
}