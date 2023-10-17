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
        File output = new File( input.getName().concat(".asm" ));

        if(output.exists())
        {
            System.out.println("Output file already exist. Replace it? n/y");
            Scanner in = new Scanner(System.in);

            char charin = in.nextLine().charAt(0);
            
            if(charin != 'y' || charin != 'Y' )
                System.exit(0);
            output.delete();
            in.close();
        }
        try { output.createNewFile(); } catch (IOException e) { e.printStackTrace(); }
        coderModule = new VMCoder( output.getPath() );

        if(input.isDirectory())
        {
            File[] files = input.listFiles();
            for(File f : files)
            {
                if(f.getName().contains(".vm"))
                    try { translate(f); } catch (IOException e) { e.printStackTrace(); }
            }
        }
        else
        {
            if(input.getName().contains(".vm"))
            {
                try { translate( input );} catch (IOException e) { e.printStackTrace(); }
            }
        }
    
    }

    private static void translate(File file) throws IOException
    {
        VMParser fileParser = new VMParser(file.getName());

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
                default:
                    break;
            }
        }

        fileParser.close();

    }

}