package Parser;

import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import Helper.Location;
import Helper.SyntaxException;

public class MxErrorListener extends BaseErrorListener{

    public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e) {
        throw new SyntaxException(new Location(line, charPositionInLine), msg);
    }
}
