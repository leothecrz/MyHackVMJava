package cpp.hackvm;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class VMCoder 
{
    private Map<String, String> addresses;

    private File file;
    private FileWriter writer;

    private int labelCount;

    public VMCoder(String fileName)
    {
        labelCount = 0;
        addresses = new HashMap<>();
        addresses.put("local", "LCL");
        addresses.put("argument", "ARG");
        addresses.put("this", "THIS");
        addresses.put("that", "THAT");
        addresses.put("pointer", "3");
        addresses.put("temp", "5");
        addresses.put("static", "16");

        file = new File(fileName);
        try { writer = new FileWriter(file); } catch (IOException e) { e.printStackTrace(); }
    }

    public void closeFile() throws IOException
    {
        writer.close();
    }

    public void writeArithmetic(String cmd) throws IOException
    {
        writeline( "// ".concat(cmd) );
        switch (cmd) 
        {
            case "eq":
                comparisonLogic("JEQ");
            case "gt":
                comparisonLogic("JGT");
            case "lt":
                comparisonLogic("JLT");
                break;
            case "not":
                decrementSP();
                loadSPInA();
                writeline("M=!M");
                incrementSP();
            case "neg":
                decrementSP();
                loadSPInA();
                writeline("M=-M");
                incrementSP();
            default:
                writeArithmeticOperation(cmd);
                break;
        }
        writeline("");
    }

    private void writeArithmeticOperation(String cmd) throws IOException
    {

        popToD();
        decrementSP();
        loadSPInA();
        switch (cmd) 
        {
            case "add":
                writer.write("M=D+M\n");
                break;
            case "sub":
                writer.write("M=M-D\n");
                break;
            case "and":
                writer.write("M=D&M\n");
                break;
            case "or":
                writer.write("M=D|M\n");
                break;
            default:
                throw new RuntimeException(cmd);
        }
        incrementSP();
    }

    public void writePushPop(VMCommands cmd, String seg, int index ) throws IOException
    {

        if(cmd == VMCommands.C_PUSH)
            writeline( "// PUSH ".concat(seg).concat(" ").concat( String.valueOf(index) ) );
        else if( cmd == VMCommands.C_POP )
            writeline( "// POP ".concat(seg).concat(" ").concat( String.valueOf(index) ) );

        switch (seg)
        {
            case "pointer":
                writeline( "@R".concat(String.valueOf(index + 3)) );
                break;
            case "temp":
                writeline( "@R".concat(String.valueOf(index + 5)) );
                break;
            case "static":
                writeline( "@".concat(file.getName()).concat( String.valueOf(index)) );
                break;
            case "constant":
                writeline( "@".concat(  String.valueOf(index) ) );
                break;
            case "local":
                loadSeg("LCL", index);
                break;
            case "argument":
                loadSeg("ARG", index);
                break;
            case "this":
                loadSeg("THIS", index);
                break;
            case "that":
                loadSeg("THAT", index);
                break;
        }

        if(cmd == VMCommands.C_PUSH)
        {
            writeline("D=M");
            loadSPInA();
            writeline("M=D");
            incrementSP();
            return;
        }
        else if( cmd == VMCommands.C_POP )
        {
            writeline("D=A");
            writeline("@R13");
            writeline("M=D");
            popToD();
            writeline("@R3");
            writeline("A=M");
            writeline("M=D");
            return;
        }
        
        throw new RuntimeException( cmd.toString() );
                
    }

    private void loadSPInA() throws IOException
    {
        writeline("@SP");
        writeline("A=M");
    }

    private void incrementSP() throws IOException
    {
        writeline("@SP");
        writeline("M=M+1");
    }

    private void decrementSP() throws IOException
    {
        writeline("@SP");
        writeline("M=M-1");  
    }

    private void popToD() throws IOException
    {
        decrementSP();
        writeline("A=M");
        writeline("D=M");
    }

    private void loadSeg(String seg, int index) throws IOException
    {
        writeline( "@".concat(seg) );
        writeline( "D=M");
        writeline( "@".concat( String.valueOf(index) ) );
        writeline( "A=D+A");
    }

    private void comparisonLogic(String eq) throws IOException
    {
        popToD();
        decrementSP();
        loadSPInA();
        writeline( "D=M-D\n" );

        writeline( "@LABEL".concat( String.valueOf(labelCount) ) );
        writeline( "D;".concat(eq) );
        
        loadSPInA();
        writeline("M=0 // True\n"); // True
        writeline( "@LABELEND".concat( String.valueOf(labelCount) ) );
        writeline("0;JMP\n");

        writeline( "(LABEL".concat( String.valueOf(labelCount) ));
        loadSPInA();
        writeline("M=-1 // False\n"); // False

        writeline( "(LABELEND".concat( String.valueOf(labelCount) ));
        labelCount++;

    }

    private void writeline(String str) throws IOException
    {
        writer.write(str.concat("\n"));
    }

}
