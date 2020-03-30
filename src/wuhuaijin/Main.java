
import AST.*;

import Frontend.ASTBuilder;
import Frontend.SemaChecker;
import Helper.SyntaxException;
import Parser.*;

import Parser.MxLexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class Main {
    public static void main(String[] args) throws IOException {
//        System.out.println("hello world");

        try {
            InputStream inputStream = new FileInputStream("program.txt");
//            InputStream inputStream = new FileInputStream("code.txt");
            CharStream charStream = CharStreams.fromStream(inputStream);

            MxLexer mxLexer = new MxLexer(charStream);
            mxLexer.removeErrorListeners();
            mxLexer.addErrorListener(new MxErrorListener());
            CommonTokenStream commonTokenStream = new CommonTokenStream(mxLexer);

            MxParser mxParser = new MxParser(commonTokenStream);
            mxParser.removeErrorListeners();
            mxParser.addErrorListener(new MxErrorListener());
            ASTBuilder ast = new ASTBuilder();
            Node astRoot = ast.visit(mxParser.mainProg());
            SemaChecker checker = new SemaChecker();
            checker.visit((ProgNode) astRoot);



        }
        catch (SyntaxException e) {
            System.err.println(e.getMessage());
            System.out.println("fuck!!!");
            System.exit(233);
        }

//        System.exit(0);
//        System.out.println("success!!!");

    }




}
