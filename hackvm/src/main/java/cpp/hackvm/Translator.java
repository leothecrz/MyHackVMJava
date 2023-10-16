package cpp.hackvm;

import java.io.File;

public class Translator {
    
    private static VMCoder coderModule;

    public static void main(String[] args) 
    {
        
        if(args.length < 1)
            return;

        File input = new File(args[0]);
        coderModule = new VMCoder( input.getName().concat(".asm") );

        if(input.isDirectory())
        {
            input.listFiles();
        }
        






    }
}