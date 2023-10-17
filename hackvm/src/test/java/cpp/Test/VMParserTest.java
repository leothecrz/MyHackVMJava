package cpp.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

import cpp.hackvm.VMParser;

public class VMParserTest 
{
    
    @Test
    public void arrithmaticAddTest() throws IOException
    {
        File testFile = File.createTempFile("test", "vm");
        FileWriter writer = new FileWriter(testFile);

        writer.write("add\n");
        writer.close();

        VMParser p = new VMParser(testFile.getPath());
        p.isAtEndOfFile();

        Assertions.assertEquals("add", p.getArgs1());
    }


}
