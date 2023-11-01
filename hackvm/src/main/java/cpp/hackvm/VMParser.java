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
    
    private Map<String, VMCommands> map; // KNOWN COMMANDS STORAGE
    private BufferedReader reader; // FILE ACCESS
    private String currentLine; // ACTIVE LINE

    private VMCommands command; // IF COMMAND IS KNOWN will be SET.
    private String ArgsOne; 

    private Integer ArgsTwo; 
    private int lineNum;

    public VMParser(String filepath)
    {
        lineNum = 0;
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

        map.put("return", VMCommands.C_RETURN);
        map.put("push", VMCommands.C_PUSH);
        map.put("pop", VMCommands.C_POP);
        map.put("function", VMCommands.C_FUNCTION);
        map.put("call", VMCommands.C_CALL);
        map.put("label", VMCommands.C_LABEL);
        map.put("goto", VMCommands.C_GOTO);
        map.put("if-goto", VMCommands.C_IF);
        try { reader = new BufferedReader( new FileReader(new File(filepath))); } catch(FileNotFoundException e) { e.printStackTrace(); }
        currentLine = "";
    }

    private String trimComments(String str)
    {
        int index = str.indexOf("//");
        if(index == -1)
            return str;

        return str.substring(0, index);
    }


    private void resetFields()
    {
        command = VMCommands.UNSET;
        ArgsOne = null;
        ArgsTwo = null;
    }

    private void parseLine()
    {
        resetFields();

        currentLine = trimComments( currentLine.trim() ).trim();

        String[] lineArray = currentLine.split("\\s+", 0); // returns one string at minimum
        
        String test = lineArray[0].trim();
        if(test.isEmpty())
        {
            command = VMCommands.EMPTY;
            return;
        }
        VMCommands cmd = map.get( test );
        if(cmd == null)
            return;
        command = cmd;

        if(command == VMCommands.C_ARITHMETIC || command == VMCommands.C_RETURN)
        {
            ArgsOne = lineArray[0].trim(); // args one hold ARITHMETIC Command
            return;
        }

        if(lineArray.length > 1)
            ArgsOne = lineArray[1].trim();
        else    
            System.err.println("Command Needs Arg");
        
        if(lineArray.length > 2)
        try {
            ArgsTwo = Integer.parseInt( lineArray[2].trim() );
        } catch (NumberFormatException e) {
            ArgsTwo = null;
            System.err.println("Invalid ARG TWO");
        }
        
    }

    public boolean isAtEndOfFile()
    {
        try 
        {
            currentLine = reader.readLine();
            if(currentLine == null)
                return true;
            parseLine();
            lineNum++;
        } catch (IOException e) { e.printStackTrace(); }
        return false;
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

    public int getLineNum()
    {
        return this.lineNum;
    }

    public String getCurrentLine() 
    {
        return currentLine;
    }

    public void close() throws IOException
    {
        reader.close();
    }

}
