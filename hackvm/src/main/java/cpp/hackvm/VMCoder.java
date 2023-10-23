package cpp.hackvm;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
public class VMCoder 
{
    private File file;
    private FileWriter writer;
    private int labelCount;
    private String functionName;

    public VMCoder(String fileName)
    {
        labelCount = 0;
        functionName = "OS";
        file = new File(fileName);
        try { writer = new FileWriter(file); } catch (IOException e) { e.printStackTrace(); }
    }

    public void closeFile() throws IOException
    {
        System.out.println("File Closed");
        writer.close();
    }

    private void writeline(String str) throws IOException
    {
        writer.write(str.concat("\n"));
    }

    public void writeArithmetic(String cmd) throws IOException
    {
        writeline( "//".concat(cmd) );
        switch (cmd) 
        {
            case "eq":
                comparisonLogic("JEQ");
                break;
            case "gt":
                comparisonLogic("JGT");
                break;
            case "lt":
                comparisonLogic("JLT");
                break;
            case "not":
                decrementSP();
                loadSPInA();
                writeline("M=!M");
                incrementSP();
                break;
            case "neg":
                decrementSP();
                loadSPInA();
                writeline("M=-M");
                incrementSP();
                break;
            default:
                writeArithmeticOperation(cmd);
                break;
        }
        writeline("");
    }

    private void writeArithmeticOperation(String cmd) throws IOException
    {
        popToD(); // TopStack To D
        decrementSP();
        loadSPInA(); // Second TopStack To M
        switch (cmd) 
        {
            case "add":
                writeline("M=D+M");
                break;
            case "sub":
                writeline("M=M-D");
                break;
            case "and":
                writeline("M=D&M");
                break;
            case "or":
                writeline("M=D|M");
                break;
            default:
                System.err.println("UKNOWN COMMAND");
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
        else
            throw new RuntimeException("PUSH POP called with invalid op");

        switch (seg)
        {
            case "pointer":
                writeline( "@R".concat(String.valueOf(index + 3)) );
                break;

            case "temp":
                writeline( "@R".concat(String.valueOf(index + 5)) );
                break;

            case "static":
                writeline( "@".concat( file.getName() ).concat(".").concat( String.valueOf(index) ) );
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
            default:
                throw new RuntimeException("Unknown Segment");
        }
        //Asume A REG has ADRS for PUSH or POP
        if(cmd == VMCommands.C_PUSH)
        {
            if(seg.equals( "constant" )) 
                writeline("D=A");
            else
                writeline("D=M");

            loadSPInA();
            writeline("M=D");
            incrementSP();
            writeline("");
            return;
        }
        else if( cmd == VMCommands.C_POP )
        {

            writeline("D=A");
            writeline("@R13");
            writeline("M=D");
            //ADRS -> M[R13]

            popToD();
            writeline("@R13");
            writeline("A=M");
            writeline("M=D");
            //Store D -> ADRS M[R13]
            writeline("");
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
        writeline( "@TrueLABEL".concat( String.valueOf(labelCount) ) );
        writeline( "D;".concat(eq) );
        
        loadSPInA();
        writeline("M=0 // False\n");
        writeline( "@FalseLABEL".concat( String.valueOf(labelCount) ) );
        writeline("0;JMP\n");

        writeline( "(TrueLABEL".concat( String.valueOf(labelCount) ).concat(")")  );
        loadSPInA();
        writeline("M=-1 // True\n");
        writeline( "(FalseLABEL".concat( String.valueOf(labelCount) ).concat(")") );
        incrementSP();
        labelCount++;
    }

    public void writeGoto(String label) throws IOException
    {
        writeline("// Goto ".concat(label) );
        writeline( "@".concat(functionName).concat("$").concat(label) );
        writeline("0;JMP");
        writeline("");
        //Jump To Label Unconditionally
    }

    public void writeLabel(String label) throws IOException
    {
        writeline( "(".concat(functionName).concat("$").concat(label).concat(")") );
    }

    public void writeFunction(String name, int counts) throws IOException
    {
        writeline("// Function ".concat(name).concat( " Args#: ").concat( String.valueOf(counts) ) );
        functionName = name;
        writeline("(".concat(functionName).concat(")"));
        for (int i = 0; i < counts; i++) 
            writePushPop(VMCommands.C_PUSH, "constant", 0);    
    }

    public void writeIf(String label) throws IOException
    {
        writeline("// If-Goto: ".concat(label) );
        popToD();
        writeline( "@".concat(functionName).concat("$").concat(label) );
        writeline( "D;JNE" );
        writeline("");
        //If Stack top NOT == 0 jump to label
    }

    private void writeDirectPush(String seg) throws IOException
    {
        writeline( "@".concat(seg) );
        writeline( "D=M" );

        writeline( "@SP");
        writeline( "A=M");
        writeline( "M=D");
        incrementSP();
        //Push Segment ADRS to stack
    }

    public void writeCall(String name, int argsCount) throws IOException
    {
        writeline("// Call function:".concat(name).concat(" Args#:").concat( String.valueOf(argsCount) ) );
        functionName = name;
        String lbl = functionName.concat("$RETURN_").concat( String.valueOf(labelCount) );
        labelCount++;
        writeline( "@".concat(lbl) );
        writeline( "D=A");
        writeline( "@SP");
        writeline( "A=M");
        writeline( "M=D");
        incrementSP();
        writeDirectPush( "LCL");
        writeDirectPush( "ARG");
        writeDirectPush( "THIS");
        writeDirectPush( "THAT");
        //Store ReturnADRS, LCL,ARG,THIS,and THAT ADRS On STACK

        writeline("@SP");
        writeline("D=M");
        writeline("@5");
        writeline("D=D-A");
        writeline("@".concat( String.valueOf(argsCount) ));
        writeline("D=D-A");
        writeline("@ARG");
        writeline("M=D");
        //Store argument Stack pos in ARG

        writeline("@SP");
        writeline("D=M");
        writeline("@LCL");
        writeline("M=D");
        //Store SP pos in LCL

        writeline("@".concat(name));
        writeline("0;JMP");
        //GOTO Function
        writeline("(".concat(lbl).concat(")"));
        //RETURN POINT
        writeline("");

    }

    private void restoreFrame(String seg) throws IOException
    {
        writeline("@R15");
        writeline("AM=M-1");
        writeline("D=M");
        writeline("@".concat(seg));
        writeline("M=D");
    }
    
    public void writeReturn() throws IOException
    {
        writeline("// Return");
        writeline("@LCL");
        writeline("D=M");
        writeline("@R15");
        writeline("M=D");
        //Store SP in R15

        writeline("@5");
        writeline("A=D-A");
        writeline("D=M");
        writeline("@R14");
        writeline("M=D");
        //Store RETURN ADRS in R14
    
        writePushPop(VMCommands.C_POP, "argument", 0); // USES r13
        writeline("@ARG");
        writeline("D=M");
        writeline("@SP");
        writeline("M=D+1");
        //Restore Stack Pointer

        restoreFrame("THAT");
        restoreFrame("THIS");
        restoreFrame("ARG");
        restoreFrame("LCL");

        writeline("@R14");
        writeline("A=M");
        writeline("0;JMP");
        writeline("");
        //GOTO ADRS SAVED IN R14
    }

    public void writeInit() throws IOException
    {
        writeline("// System Init");
        writeline("@256");
        writeline("D=A");
        writeline("@SP");
        writeline("M=D");
        //SP = 256
        writeCall("Sys.init", 0);
    }

}
