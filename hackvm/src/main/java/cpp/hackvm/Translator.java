package cpp.hackvm;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class Translator {
    
    private static VMCoder coderModule;
    private static Scanner in;

    public static void main(String[] args) 
    {
        // End if no args given
        if(args.length < 1)
            return;

        boolean verbose = false;
        
        // First arg is file path
        File input = new File(args[0]);

        if( args.length > 1 && args[1].contains("-v"))
            verbose = true;

        // File output takes either Directory Name or Single File Name
        File output = new File( input.isDirectory() ?
            input.getPath().concat("/").concat( input.getName() ).concat(".asm") 
            :
            input.getPath().substring(0, input.getPath().lastIndexOf(".")).concat(".asm" ) );
        
        // If output exist ask user if they want to replace it else END. 
        if(output.exists())
        {
            in = new Scanner(System.in);
            System.out.println("Output file already exist. Replace it? (n/y)");
            char charin = in.nextLine().trim().charAt(0);
            if( charin != 'y' && charin != 'Y' )
                System.exit(0);
            output.delete();
        }

        //Create files and coder module. Write INIT.
        try 
        { 
            output.createNewFile();
            coderModule = new VMCoder( output.getPath() );
            coderModule.writeInit();
        } 
        catch (IOException e) { e.printStackTrace(); }
       
        if(input.isDirectory())
        { // If input file path is directory iterated on every vm file.
            File[] files = input.listFiles();
            for(File f : files)
                if(f.getName().contains(".vm"))
                    translate( f, verbose );
        }
        else // On single file confirm file ends with vm. 
            if(input.getName().contains(".vm"))
                translate( input, verbose );
        
        // Close Coder Module and Scanner
        try { coderModule.closeFile(); } catch (IOException e) { e.printStackTrace(); }
        if(in != null)  
            in.close();
    }

    private static void translate(File file, boolean printLine)
    {
        VMParser fileParser = new VMParser( file.getPath() );
        System.out.println("Opened File: " + file.getName() );
        coderModule.setFileName( file.getName() );

        while(!fileParser.isAtEndOfFile())
        {
            
            if(printLine)
                System.out.println( 
                    "LN: " + 
                    fileParser.getLineNum() + " CMD:" + 
                    fileParser.getCommandType() + " ARG1:" +
                    fileParser.getArgs1() + " ARG2:" +
                    fileParser.getArgs2() == null ? "" : fileParser.getArgs2() 
                );
            try
            {
                switch ( fileParser.getCommandType() ) 
                {
                    //Part 7 (Stack Arithmetic and Memory Access)
                    case C_ARITHMETIC :
                        coderModule.writeArithmetic( fileParser.getArgs1() );
                        break;
                    case C_PUSH:
                        coderModule.writePushPop( VMCommands.C_PUSH, fileParser.getArgs1(), fileParser.getArgs2());
                        break;
                    case C_POP:
                        coderModule.writePushPop(  VMCommands.C_POP, fileParser.getArgs1(), fileParser.getArgs2());
                        break;

                    //Part 8 (Program Flow and Function-functions )
                    case C_GOTO:
                        coderModule.writeGoto( fileParser.getArgs1() );
                        break;
                    case C_LABEL:
                        coderModule.writeLabel( fileParser.getArgs1() );
                        break;
                    case C_IF:
                        coderModule.writeIf( fileParser.getArgs1() );
                        break;
                    case C_FUNCTION:
                        coderModule.writeFunction( fileParser.getArgs1(), fileParser.getArgs2() );
                        break;
                    case C_CALL:
                        coderModule.writeCall( fileParser.getArgs1(), fileParser.getArgs2() );
                        break;
                    case C_RETURN:
                        coderModule.writeReturn();
                        break;
                    case EMPTY:
                        break;
                    case UNSET:
                        System.err.println("Error in: ");
                        System.err.println(fileParser.getCurrentLine());
                        throw new IOException();

                    //Error STATE
                    default:
                        System.err.println("UNKNOW COMMAND WARNING");
                        break;
                }
            }
            catch(IOException e)
            {
                System.err.println("Error on line number: " + fileParser.getLineNum() + "." );
                break;
            }
        }
        try {fileParser.close();} catch (IOException e) { 
            System.err.println( "Failed to close file. File: " + file.getName() );
            e.printStackTrace();
        }
    }
}