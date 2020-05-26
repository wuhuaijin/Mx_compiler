
import AST.*;

import Backend.FinalProcess;
import Backend.RegAllocator;
import Backend.RiscvBuilder;
import Backend.RiscvPrinter;
import Frontend.ASTBuilder;
import Frontend.SemaChecker;
import Helper.SyntaxException;
import IR.*;
import Optim.FuncInline;
import Optim.GlobalVarResolve;
import Optim.PeepholeOptim;
import Parser.*;

import Parser.MxLexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import IR.Module;

public class Main {
    public static void main(String[] args) throws IOException {
//        System.out.println("hello world");

        try {
            InputStream inputStream = new FileInputStream("program.txt");
//            InputStream inputStream = new FileInputStream("codegen/sorting/bubble_sort.mx");
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

            FuncVisitor funcVisitor = new FuncVisitor(checker.getTypeTable());
            funcVisitor.visit(((ProgNode) astRoot));
            IRBuilder ir = new IRBuilder(checker.getGlobalScope());
            SystemFunc.addBuiltinFunction(ir.getModule(), checker.getGlobalScope());

            ir.visit(((ProgNode) astRoot));
            Module module = ir.getModule();
            FuncInline funcInline = new FuncInline(module);
            funcInline.run();
            GlobalVarResolve globalVarResolve = new GlobalVarResolve(module);
            globalVarResolve.run();

            IRPrinter irPrinter = new IRPrinter(new PrintStream("ir.txt"));
//            irPrinter.visit(ir.getModule());

//            IRInterpreter.main("ir.txt", System.out, new FileInputStream("test.in"), false);

            RiscvBuilder riscvBuilder = new RiscvBuilder();
            riscvBuilder.visit(ir.getModule());
            new RegAllocator(riscvBuilder.getModule()).run();
            new FinalProcess(riscvBuilder.getModule()).run();
            new PeepholeOptim(riscvBuilder.getModule()).run();
            new RiscvPrinter(riscvBuilder.getModule()).visit(new PrintStream("test.s"));

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