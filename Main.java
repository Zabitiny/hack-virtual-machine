import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.List;

public class Main extends Parser {

    public Main(String inputFile, String outputFile) throws FileNotFoundException {
        super(inputFile, outputFile);
    }
    public static void main(String[] args) throws FileNotFoundException {
        String dir = "../../08/FunctionCalls/FibonacciElement/";
        //get all files with the .vm extension
        final File folder = new File(dir);
        final List<File> fileList = Arrays.asList(folder.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                int n = pathname.getName().length();
                if(pathname.getName().charAt(n-2) == 'v' && pathname.getName().charAt(n-1) == 'm') {
                    return pathname.isFile();
                }
                return false;
            }
        }));

        Main main=null;
        boolean hasSys = false;
        for(File f : fileList) {
            String inputFile = dir + f.getName();
            String outputFile = dir + "FibonacciElement.asm";
            if(f.getName().equals("Sys.vm")) {
                System.out.println(f.getName());
                main = new Main(inputFile, outputFile);
                main.writeInit();
                while(main.hasMoreCommands()) {
                    main.advance();
                    Command c = main.CommandType();
                    if(c == Command.ARITHMETIC){
                        main.writeArithmetic(main.arg1());
                    }
                    else if(c == Command.PUSH || c == Command.POP) {
                        main.writePushPop(main.CommandType(), main.arg1(), main.arg2());
                    }
                    else if(c == Command.LABEL) {
                        main.writeLabel(main.arg1());
                    }
                    else if(c == Command.GOTO) {
                        main.writeGoto(main.arg1());
                    }
                    else if(c == Command.IF) {
                        main.writeIf(main.arg1());
                    }
                    else if(c == Command.FUNCTION) {
                        main.writeFunction(main.arg1(), main.arg2());
                    }
                    else if(c == Command.CALL) {
                        main.writeCall(main.arg1(), main.arg2());
                    }
                    else if(c == Command.RETURN) {
                        main.writeReturn();
                    }
                }
                hasSys = true;
                break;
            }
        }

        for(File f : fileList) {
            String inputFile = dir + f.getName();
            String outputFile = dir + "FibonacciElement.asm";
            if(f.getName().equals("Sys.vm")) continue;
            else if(!hasSys) main = new Main(inputFile, outputFile);
            main.setFileName(inputFile);
            main.setReadFile(inputFile);
            System.out.println(f.getName());

            while(main.hasMoreCommands()) {
                main.advance();
                Command c = main.CommandType();
                if(c == Command.ARITHMETIC){
                    main.writeArithmetic(main.arg1());
                }
                else if(c == Command.PUSH || c == Command.POP) {
                    main.writePushPop(main.CommandType(), main.arg1(), main.arg2());
                }
                else if(c == Command.LABEL) {
                    main.writeLabel(main.arg1());
                }
                else if(c == Command.GOTO) {
                    main.writeGoto(main.arg1());
                }
                else if(c == Command.IF) {
                    main.writeIf(main.arg1());
                }
                else if(c == Command.FUNCTION) {
                    main.writeFunction(main.arg1(), main.arg2());
                }
                else if(c == Command.CALL) {
                    main.writeCall(main.arg1(), main.arg2());
                }
                else if(c == Command.RETURN) {
                    main.writeReturn();
                }
            }
        }
        main.close();
    }    
}