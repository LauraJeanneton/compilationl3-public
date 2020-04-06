import c3a.*;
import sa.*;
import ts.Ts;
import ts.TsItemFct;

public class Sa2c3a extends SaDepthFirstVisitor<C3aOperand> {
    private C3a c3a;
    private Ts ts;
    private TsItemFct tsItemFct;


    public Sa2c3a(SaNode root, Ts ts){
        c3a = new C3a();
        this.ts = ts;
        root.accept(this);
    }


    public C3a getC3a() {
        return c3a;
    }


    @Override
    public C3aOperand visit(SaProg node) {
        node.getFonctions().accept(this);
        return null;
    }


    @Override
    public C3aOperand visit(SaExpInt node) {
        return new C3aConstant(node.getVal());
    }



    @Override
    public C3aOperand visit(SaExpVar node) {
        return  node.getVar().accept(this);
    }



    @Override
    public C3aOperand visit(SaInstEcriture node) {
        c3a.ajouteInst(new C3aInstWrite(node.getArg().accept(this), ""));
        return null;
    }



    @Override
    public C3aOperand visit(SaInstTantQue node) {
        C3aLabel c3aLabel = c3a.newAutoLabel();
        C3aLabel c3aLabel1 = c3a.newAutoLabel();

        c3a.addLabelToNextInst(c3aLabel);
        C3aOperand codeTest = node.getTest().accept(this);
        c3a.ajouteInst(new C3aInstJumpIfEqual(codeTest,new C3aConstant(0),c3aLabel1,""));
        node.getFaire().accept(this);
        c3a.ajouteInst(new C3aInstJump(c3aLabel,""));
        c3a.addLabelToNextInst(c3aLabel1);
        return null;
    }



    @Override
    public C3aOperand visit(SaLInst node) {
        node.getTete().accept(this);
        if (node.getQueue() != null) node.getQueue().accept(this);
        return null;
    }



    @Override
    public C3aOperand visit(SaDecFonc node) {
        this.tsItemFct = ts.getFct(node.getNom());
        c3a.ajouteInst(new C3aInstFBegin(ts.getFct(node.getNom()),"entree fonction"));
        node.getCorps().accept(this);
        c3a.ajouteInst(new C3aInstFEnd(""));
        return null;
    }


    @Override
    public C3aOperand visit(SaInstAffect node) {
        C3aOperand op = node.getRhs().accept(this);
        C3aOperand result =  node.getLhs().accept(this);
        c3a.ajouteInst(new C3aInstAffect(op, result,""));
        return result;
    }


    @Override
    public C3aOperand visit(SaLDec node) {
        node.getTete().accept(this);
        if (node.getQueue() != null) node.getQueue().accept(this);
        return null;
    }



    @Override
    public C3aOperand visit(SaVarSimple node) {
        C3aVar c3aVar;
        if (ts.getVar(node.getNom()) == null) {
            c3aVar = new C3aVar(ts.getTableLocale(tsItemFct.getIdentif()).getVar(node.getNom()), null);
        } else {
            c3aVar = new C3aVar(ts.getVar(node.getNom()), null);
        }
        return c3aVar;
    }


    @Override
    public C3aOperand visit(SaAppel node) {
        if (node.getArguments() != null) node.getArguments().accept(this);
        return new C3aFunction(ts.getFct(node.getNom()));
    }


    @Override
    public C3aOperand visit(SaExpAppel node) {
        C3aFunction c3aFunction = (C3aFunction) node.getVal().accept(this);
        C3aTemp c3aTemp = c3a.newTemp();
        c3a.ajouteInst(new C3aInstCall(c3aFunction, c3aTemp,""));
        return c3aTemp;
    }


    @Override
    public C3aOperand visit(SaExpAdd node) {
        C3aOperand c3aOperand = node.getOp1().accept(this);
        C3aOperand c3aOperand1 = node.getOp2().accept(this);
        C3aTemp c3aTemp = c3a.newTemp();
        c3a.ajouteInst(new C3aInstAdd(c3aOperand, c3aOperand1, c3aTemp, ""));
        return c3aTemp;
    }


    @Override
    public C3aOperand visit(SaExpSub node) {
        C3aOperand c3aOperand = node.getOp1().accept(this);
        C3aOperand c3aOperand1 = node.getOp2().accept(this);
        C3aTemp c3aTemp = c3a.newTemp();
        c3a.ajouteInst(new C3aInstSub(c3aOperand, c3aOperand1, c3aTemp, ""));
        return c3aTemp;
    }


    @Override
    public C3aOperand visit(SaExpMult node) {
        C3aOperand c3aOperand = node.getOp1().accept(this);
        C3aOperand c3aOperand1 = node.getOp2().accept(this);
        C3aTemp c3aTemp = c3a.newTemp();
        c3a.ajouteInst(new C3aInstMult(c3aOperand, c3aOperand1, c3aTemp, ""));
        return c3aTemp;
    }


    @Override
    public C3aOperand visit(SaExpDiv node) {
        C3aOperand c3aOperand = node.getOp1().accept(this);
        C3aOperand c3aOperand1 = node.getOp2().accept(this);

        C3aTemp c3aTemp = c3a.newTemp();
        c3a.ajouteInst(new C3aInstDiv(c3aOperand ,c3aOperand1, c3aTemp, ""));
        return c3aTemp;
    }


    @Override
    public C3aOperand visit(SaExpInf node) {
        C3aTemp c3aTemp = c3a.newTemp();
        C3aOperand c3aOperand = node.getOp1().accept(this);
        C3aOperand c3aOperand1 = node.getOp2().accept(this);
        c3a.ajouteInst(new C3aInstAffect(new C3aConstant(1), c3aTemp, ""));
        C3aLabel c3aLabel = c3a.newAutoLabel();
        c3a.ajouteInst(new C3aInstJumpIfLess(c3aOperand ,c3aOperand1 , c3aLabel, ""));
        c3a.ajouteInst(new C3aInstAffect(new C3aConstant(0), c3aTemp, ""));
        c3a.addLabelToNextInst(c3aLabel);
        return c3aTemp;
    }


    @Override
    public C3aOperand visit(SaExpAnd node) {
        C3aTemp c3aTemp = c3a.newTemp();
        C3aLabel c3aLabel = c3a.newAutoLabel();
        C3aLabel c3aLabel1 = c3a.newAutoLabel();

        C3aOperand c3aOperand = node.getOp1().accept(this);
        C3aOperand c3aOperand1 = node.getOp2().accept(this);
        c3a.ajouteInst(new C3aInstJumpIfEqual(c3aOperand,new C3aConstant(0),c3aLabel1,""));
        c3a.ajouteInst(new C3aInstJumpIfEqual(c3aOperand1,new C3aConstant(0),c3aLabel1,""));
        c3a.ajouteInst(new C3aInstAffect(new C3aConstant(1),c3aTemp,""));
        c3a.ajouteInst(new C3aInstJump(c3aLabel,""));
        c3a.addLabelToNextInst(c3aLabel1);
        c3a.ajouteInst(new C3aInstAffect(new C3aConstant(0),c3aTemp,""));
        c3a.addLabelToNextInst(c3aLabel);
        return c3aTemp;
    }


    @Override
    public C3aOperand visit(SaExpOr node) {
        C3aTemp c3aTemp = c3a.newTemp();
        C3aLabel c3aLabel = c3a.newAutoLabel();
        C3aLabel c3aLabel1 = c3a.newAutoLabel();
        C3aOperand c3aOperand = node.getOp1().accept(this);
        C3aOperand c3aOperand1 = node.getOp2().accept(this);

        c3a.ajouteInst(new C3aInstJumpIfNotEqual(c3aOperand,new C3aConstant(0),c3aLabel1,""));
        c3a.ajouteInst(new C3aInstJumpIfNotEqual(c3aOperand1,new C3aConstant(0),c3aLabel1,""));
        c3a.ajouteInst(new C3aInstAffect(new C3aConstant(0),c3aTemp,""));
        c3a.ajouteInst(new C3aInstJump(c3aLabel,""));
        c3a.addLabelToNextInst(c3aLabel1);
        c3a.ajouteInst(new C3aInstAffect(new C3aConstant(1),c3aTemp,""));
        c3a.addLabelToNextInst(c3aLabel);
        return c3aTemp;
    }


    @Override
    public C3aOperand visit(SaExpLire node) {
        c3a.ajouteInst(new C3aInstRead( node.accept(this), ""));
        return null;
    }


    @Override
    public C3aOperand visit(SaInstBloc node) {
        node.getVal().accept(this);
        return null;
    }


    @Override
    public C3aOperand visit(SaInstSi node) {
        C3aLabel c3aLabel = c3a.newAutoLabel();
        C3aLabel c3aLabel1 = c3a.newAutoLabel();

        if (node.getSinon() == null) {
            c3a.ajouteInst(new C3aInstJumpIfEqual(node.getTest().accept(this), new C3aConstant(0), c3aLabel1, ""));
            node.getAlors().accept(this);
        } else {
            c3a.ajouteInst(new C3aInstJumpIfEqual(node.getTest().accept(this), new C3aConstant(0), c3aLabel, ""));
            node.getAlors().accept(this);
            c3a.ajouteInst(new C3aInstJump(c3aLabel1, ""));
            c3a.addLabelToNextInst(c3aLabel);
            node.getSinon().accept(this);
        }
        c3a.addLabelToNextInst(c3aLabel1);
        return null;
    }


    @Override
    public C3aOperand visit(SaInstRetour node) {
        c3a.ajouteInst(new C3aInstReturn(node.getVal().accept(this), ""));
        return null;
    }


    @Override
    public C3aOperand visit(SaLExp node) {
        C3aOperand c3aOperand = node.getTete().accept(this);
        c3a.ajouteInst(new C3aInstParam(c3aOperand, ""));
        if (node.getQueue() != null) {
            node.getQueue().accept(this);
        }
        return null;
    }


    @Override
    public C3aOperand visit(SaVarIndicee node) {
        return new C3aVar(ts.getVar(node.getNom()), node.getIndice().accept(this));
    }

}