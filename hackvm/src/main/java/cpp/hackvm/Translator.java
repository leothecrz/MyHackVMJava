package cpp.hackvm;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class Translator {
    
    private static VMCoder coderModule;

    public static void main(String[] args) 
    {
        
        if(args.length < 1)
            return;

        File input = new File(args[0]);
        //File output takes either Directory Name or Single File Name
        File output = new File( input.isDirectory() ?
            input.getPath().concat("/").concat( input.getName() ).concat(".asm") :
            input.getPath().substring(0, input.getPath().lastIndexOf(".")).concat(".asm" ) );
        if(output.exists())
        {
            System.out.println("Output file already exist. Replace it? n/y");
            Scanner in = new Scanner(System.in);

            char charin = in.nextLine().charAt(0);
            
            if( charin != 'y' && charin != 'Y' )
                System.exit(0);
            output.delete();
            in.close();
        }

        try 
        { 
            output.createNewFile();
            coderModule = new VMCoder( output.getPath() );
            coderModule.writeInit();
        } 
        catch (IOException e) { e.printStackTrace(); }
       
        if(input.isDirectory())
        {
            File[] files = input.listFiles();
            for(File f : files)
                if(f.getName().contains(".vm"))
                    try { translate(f); } catch (IOException e) { e.printStackTrace(); }
        }
        else
            if(input.getName().contains(".vm"))
                try { translate( input );} catch (IOException e) { e.printStackTrace(); }

        try { coderModule.closeFile(); } catch (IOException e) { e.printStackTrace(); }
    }

    private static void translate(File file) throws IOException
    {
        VMParser fileParser = new VMParser(file.getPath());

        while(!fileParser.isAtEndOfFile())
        {
            System.out.println( fileParser.getCommandType() + " "+ fileParser.getArgs1() + " " +  ( fileParser.getArgs2() == null ? "":fileParser.getArgs2())  );
            switch ( fileParser.getCommandType() ) 
            {
                case C_ARITHMETIC :
                    coderModule.writeArithmetic( fileParser.getArgs1() );
                    break;
                case C_PUSH:
                    coderModule.writePushPop( VMCommands.C_PUSH, fileParser.getArgs1(), fileParser.getArgs2());
                    break;
                case C_POP:
                    coderModule.writePushPop( VMCommands.C_POP, fileParser.getArgs1(), fileParser.getArgs2());
                    break;
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
                default:
                    break;
            }
        }
        fileParser.close();
    }

}