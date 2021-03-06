package org.nuim.cyclone.compiler;

import java.io.PrintStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import org.antlr.runtime.ANTLRInputStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.nuim.cyclone.parser.*;
import org.nuim.cyclone.util.*;
import org.nuim.cyclone.parser.ast.ASTMachine;
import org.nuim.cyclone.parser.ast.*;
import org.nuim.cyclone.model.Machine;

public class MachineCompiler {

    public static final int COMPILE_ERROR=-1;
    public static final int UNEXPECTED_ERROR=-2;
    public static final int COMPILE_SUCCESS=0;

    public static int compileMachine(InputStream in,String inName, PrintWriter err){
        ParseErrorHandler errHandler = new ParseErrorHandler(inName, err);
        ANTLRInputStream aInput;
        ColorPrint out = new ColorPrint();
        out.println("Launching compiler...",Color.GREEN);
        int COMPILE_RESULT=COMPILE_SUCCESS;

        try {
			aInput = new ANTLRInputStream(in);
            aInput.name = inName;
		} catch (IOException e1) {
            err.println(e1.getMessage());
            return -1;
        }

        StateMachineLexer  lexer = new StateMachineLexer (aInput);
        CommonTokenStream tokenStream = new CommonTokenStream(lexer);
        StateMachineParser parser = new StateMachineParser(tokenStream);

        lexer.init(errHandler);
        parser.init(errHandler);

        try{
            ASTMachine node=parser.machine();
            Machine machine=node.gen(new ASTContext());
            out.println(machine.toString(),Color.BLUE);

            if (errHandler.errorCount() == 0 ){
                out.println("Syntax checking done.",Color.GREEN);
            }
            else{
                out.println(errHandler.errorCount()+" syntax error(s).",Color.RED);
                COMPILE_RESULT = COMPILE_ERROR;
            }

            if (node.context().errors()==0){
                out.println("Semantic checking done.",Color.GREEN);
            }
            else{
                out.println(node.context().errors()+" semantic error(s).",Color.RED);
                COMPILE_RESULT = COMPILE_ERROR;
            }

            if (machine.errors()==0){
                out.println("Type checking done.",Color.GREEN);
            }
            else{
                out.println(machine.errors()+" type error(s).",Color.RED);
                COMPILE_RESULT = COMPILE_ERROR;
            }

            if (COMPILE_RESULT == COMPILE_SUCCESS)
                out.println("Compile is successful.",Color.GREEN);
            else
                out.println("Compile is failed.",Color.RED);
            
            return COMPILE_RESULT;
        }
        catch(RecognitionException e){
            err.println(parser.getSourceName() +":" + 
            e.line + ":" +
            e.charPositionInLine + ": " + 
            e.getMessage());
        }

        out.println("compile is finished",Color.GREEN);
        return COMPILE_SUCCESS;
    }


}