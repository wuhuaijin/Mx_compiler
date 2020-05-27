package IR;

import Frontend.Scope;
import IR.Instruction.BinaryOp;
import IR.Instruction.Load;
import IR.Instruction.Return;
import IR.Operand.Const;
import IR.Operand.Int32;
import SymbolTable.Symbol.FuncSymbol;
import SymbolTable.Symbol.VarSymbol;
import SymbolTable.Type.ArrayType;
import SymbolTable.Type.StringType;

import java.util.ArrayList;
import java.util.Objects;

public class SystemFunc {

//    private Scope scope;
//
//    public SystemFunc(Scope scope) {
//        this.scope = scope;
//    }

    public static Function
            Print,
            Println,
            PrintInt,
            PrintlnInt,
            GetString,
            GetInt,
            ToString,

            StrLength,
            StrSubstring,
            StrParseInt,
            StrOrd,
            StrAdd,
            StrEq,
            StrNeq,
            StrLt,
            StrLe,
            StrGt,
            StrGe,

            ArraySize;


    public static Function newSystemFunction(Module module, String funcName, Scope scope) {
        Function function = new Function(funcName);
        function.setMethod(false);
        function.setSystem(true);
        ((FuncSymbol) (scope.getSymbol(funcName))).setFunction(function);
        module.addFunc(function);
        return function;
    }

    public static Function newStringMethod(Module module, String funcName) {
        Function function = new Function(funcName);
        function.setMethod(true);
        function.setSystem(true);
        if (StringType.getMethod(funcName) != null) {
            Objects.requireNonNull(StringType.getMethod(funcName)).setFunction(function);
            function.setFuncName("string_" + funcName);
        }
        module.addFunc(function);
        return function;
    }

    public static Function newArrayMethod(Module module, String funcName) {
        Function function = new Function("array_" + funcName);
        function.setMethod(true);
        function.setSystem(false);
        Objects.requireNonNull(ArrayType.getMethod(funcName)).setFunction(function);
        module.addFunc(function);
        return function;
    }

    public static void addBuiltinFunction(Module module, Scope scope){

        Print = newSystemFunction(module, "print", scope);
        Println = newSystemFunction(module, "println", scope);
        PrintInt = newSystemFunction(module, "printInt", scope);
        PrintlnInt = newSystemFunction(module, "printlnInt", scope);
        GetString = newSystemFunction(module, "getString", scope);
        GetInt = newSystemFunction(module, "getInt", scope);
        ToString = newSystemFunction(module, "toString", scope);

        // string
        StrLength = newStringMethod(module, "length");
        StrSubstring = newStringMethod(module, "substring");
        StrParseInt = newStringMethod(module, "parseInt");
        StrOrd = newStringMethod(module, "ord");
        StrAdd = newStringMethod(module, "string_add");
        StrEq = newStringMethod(module, "string_eq");
        StrNeq = newStringMethod(module, "string_neq");
        StrLt = newStringMethod(module, "string_lt");
        StrLe = newStringMethod(module, "string_le");
        StrGt = newStringMethod(module, "string_gt");
        StrGe = newStringMethod(module, "string_ge");

        // array
        ArraySize = newArrayMethod(module, "size");
        Int32 obj = new Int32("obj"), ret = new Int32("ret");
        BB bb = new BB(Lable.getLable());
        Int32 tmp = new Int32("tmp");
        bb.addInstruction(new BinaryOp(bb, false, BinaryOp.Op.sub, obj, new Const(4), tmp));
        bb.addInstruction(new Load(bb, false, ret, tmp));

        bb.addLastInstruction(new Return(bb, true, ret));
        ArraySize.setInBB(bb);
        ArraySize.setOutBB(bb);
        ArraySize.setObj(obj);


    }
}
