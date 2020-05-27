package Backend;

import Backend.Inst.*;

public interface RiscvVisitor {
    void visit(Branch inst);
    void visit(Call inst);
    void visit(Jump inst);
    void visit(LA inst);
    void visit(LI inst);
    void visit(Load inst);
    void visit(Move inst);
    void visit(Return inst);
    void visit(Store inst);
    void visit(ImmAction inst);
    void visit(RegAction inst);
}
