package cpp.hackvm;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
public class VMParser 
{
    
    private Map<String, VMCommands> map;
    private BufferedReader reader;
    private String currentLine;

    private VMCommands command;
    private String ArgsOne;
    private Integer ArgsTwo; 

    public VMParser(String filepath)
    {
        map = new HashMap<>();
        resetFields();
        map.put("add", VMCommands.C_ARITHMETIC);
        map.put("sub", VMCommands.C_ARITHMETIC);
        map.put("neg", VMCommands.C_ARITHMETIC);
        map.put("eq", VMCommands.C_ARITHMETIC);
        map.put("gt", VMCommands.C_ARITHMETIC);
        map.put("lt", VMCommands.C_ARITHMETIC);
        map.put("and", VMCommands.C_ARITHMETIC);
        map.put("or", VMCommands.C_ARITHMETIC);
        map.put("not", VMCommands.C_ARITHMETIC);
        map.put("push", VMCommands.C_PUSH);
        map.put("pop", VMCommands.C_POP);
        map.put("label", VMCommands.C_LABEL);
        map.put("goto", VMCommands.C_GOTO);
        map.put("if-goto", VMCommands.C_IF);
        map.put("function", VMCommands.C_FUNCTION);
        map.put("return", VMCommands.C_RETURN);
        map.put("call", VMCommands.C_CALL);
        try { reader = new BufferedReader( new FileReader(new File(filepath))); } catch(FileNotFoundException e) { e.printStackTrace(); }
        currentLine = "";
    }

    public boolean isAtEndOfFile()
    {
        try 
        {
            currentLine = reader.readLine();
            if(currentLine == null)
                return true;
            parseLine();
        } catch (IOException e) { e.printStackTrace(); }
        return false;
    }

    private String trimComments(String str)
    {
        int index = str.indexOf("//");
        if(index == -1)
            return str;

        return str.substring(0, index);
    }

    private void parseLine()
    {
        resetFields();
        currentLine = trimComments( currentLine.trim() ).trim();
        String[] lineArray = currentLine.split(" +"); // returns one string at minimum

        VMCommands cmd = map.get( lineArray[0].trim() );
        if(cmd == null)
            return;
        command = cmd;

        if(command == VMCommands.C_ARITHMETIC)
            ArgsOne = lineArray[0].trim();

        if(lineArray.length > 1 && command != VMCommands.C_ARITHMETIC)
            ArgsOne = lineArray[1].trim();
        
        if(lineArray.length > 2)
            ArgsTwo = Integer.parseInt( lineArray[2].trim() );
        
    }

    private void resetFields()
    {
        command = VMCommands.UNSET;
        ArgsOne = "";
        ArgsTwo = null;
    }

    public VMCommands getCommandType()
    {
        return command;
    }

    public String getArgs1()
    {
        return ArgsOne;
    }

    public Integer getArgs2()
    {
        return ArgsTwo;
    }

    public void close()
    {
        try 
        {
            reader.close();
        } 
        catch (IOException e) 
        {
            e.printStackTrace();
        }
    }

}
