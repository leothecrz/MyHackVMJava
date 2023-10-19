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
                writeline( "@R5".concat(String.valueOf(index + 5)) );
                break;

            case "static":
                writeline( "@".concat( file.getName() ).concat( String.valueOf(index) ) );
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
            if(!seg.equals( "constant" ))
                writeline("D=M");
            else
                writeline("D=A");

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

    /**
     * Register A 
     * @param seg
     * @param index
     * @throws IOException
     */
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

        writeline( "(LABEL".concat( String.valueOf(labelCount) ).concat(")")  );
        loadSPInA();
        writeline("M=-1 // False\n"); // False

        writeline( "(LABELEND".concat( String.valueOf(labelCount) ).concat(")") );
        labelCount++;

    }

    private void writeline(String str) throws IOException
    {
        writer.write(str.concat("\n"));
    }

    public void writeGoto(String label) throws IOException
    {
        writeline("// Goto ".concat(label) );
        writeline( "@".concat(label) );
        writeline("0;JMP");
        //Jump To Label Unconditionally
        writeline("");
    }

    public void writeLabel(String label) throws IOException
    {
        writeline("// Label ".concat(label) );
        writeline( "(".concat(label).concat(")") );
        writeline("");
    }

    public void writeFunction(String name, int counts) throws IOException
    {
        writeline("// Function ".concat(name).concat( " Args#:".concat( String.valueOf(counts) ) ) );
        writeLabel(name);
        for (int i = 0; i < counts; i++) 
            writePushPop(VMCommands.C_PUSH, "constant", 0);    
    }

    public void writeIf(String label) throws IOException
    {
        writeline("// If-Goto: ".concat(label) );
        popToD();
        //Pop Stack To D
        writeline( "@".concat(label) );
        writeline( "D;JNE" );
        //If Stack top NOT == 0 jump to label
        writeline("");
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
        String lbl = "RETURN".concat( String.valueOf(labelCount++) );
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
        //GOTO Fucntion
        writeLabel(lbl);
        //RETURN POINT
        writeline("");

    }

    private void storeFrame(String seg) throws IOException
    {
        writeline("@R11");
        writeline("D=M-1");
        //Decrement R11 by 1 and store in D
        writeline("M=D");
        writeline("A=D");
        //Save and goto D
        writeline("D=M");
        // Store in D
        writeline("@".concat(seg));
        writeline("M=D");
        //Store D in seg
    }

    public void writeReturn() throws IOException
    {
        writeline("// Return");
        writeline("@LCL");
        writeline("D=M");
        //Store Original SP in D

        writeline("@R11");
        writeline("M=D");
        //Store D's content in R11

        writeline("@5");
        writeline("A=D-A");
        writeline("D=M");
        // M[LCL] - 5 -> D

        writeline("@R12");
        writeline("M=D");
        // Store RETURN ADRS in R12
    
        writePushPop(VMCommands.C_POP, "ARG", 0);
        writeline("@ARG");
        writeline("D=M");
        //Store Stack POP in D

        writeline("@SP");
        writeline("M=D+1");
        //Move Stack Pointer to D+1

        storeFrame("THAT");
        storeFrame("THIS");
        storeFrame("ARG");
        storeFrame("LCL");

        writeline("@R12");
        writeline("A=M");
        writeline("0;JMP");
        writeline("");
        //GOTO ADRS SAVED IN R12
    }

    public void writeInit() throws IOException
    {
        writeline("// System Init");
        writeline("@256");
        writeline("D=A");
        //Store 256 in D
        writeline("@SP");
        writeline("M=D");
        //SP = 256
        writeCall("Sys.init", 0);
    }

}
