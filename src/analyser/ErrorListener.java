package analyser;

import com.sun.org.apache.xpath.internal.operations.Bool;
import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class ErrorListener extends BaseErrorListener {

    private static String fileName = null;
    private static FileWriter outputFile = null;
    private boolean error;

    public ErrorListener(String fileName)
    {
        this.error = false;
        try {
            this.fileName = fileName;
            outputFile = new FileWriter(fileName, false);
        }catch (IOException e){
            System.out.println(e.getMessage());
        }

    }

    public boolean error(){ return error; }

    public static void appendStringToFile(String str)
    {
        try {

            // Open given file in append mode.
            outputFile = new FileWriter(fileName, true);
            BufferedWriter out = new BufferedWriter(outputFile);
            out.write(str);
            out.close();
        }
        catch (IOException e) {
            System.out.println("exception occoured" + e);
        }
    }

    @Override
    public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e)
    {
        error = true;
        String errorMessage = "syntax-error - line: " + line + ", position: " + charPositionInLine + ", message: " + msg + "\n";
        System.out.println(errorMessage);
        appendStringToFile(errorMessage);

    }
}
