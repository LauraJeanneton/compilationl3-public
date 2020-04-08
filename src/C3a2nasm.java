import c3a.*;
import nasm.*;
import ts.Ts;
import ts.TsItemFct;
import ts.TsItemVar;

public class C3a2nasm implements C3aVisitor <NasmOperand> {
    private Nasm nasm;
    private Ts tableGlobale;
    private TsItemFct currentFct;


    public C3a2nasm(C3a c3a, Ts ts) {
        this.nasm = new Nasm(ts);
        this.tableGlobale = ts;
        this.init();
        for (C3aInst c3aInst : c3a.listeInst){
            c3aInst.accept(this);
        }
    }


    public Nasm getNasm() {
        return nasm;
    }

    public void init() {
        NasmLabel nasmLabel = new NasmLabel("main");
        this.currentFct = tableGlobale.getFct("main");
        nasm.ajouteInst(new NasmCall(null, nasmLabel, ""));

        NasmRegister nasmRegister = nasm.newRegister();
        nasmRegister.colorRegister(Nasm.REG_EBX);
        nasm.ajouteInst(new NasmMov(null, nasmRegister, new NasmConstant(0), " valeur de retour du programme"));

        NasmRegister nasmRegister2 = nasm.newRegister();
        nasmRegister2.colorRegister(Nasm.REG_EAX);

        nasm.ajouteInst(new NasmMov(null, nasmRegister2, new NasmConstant(1), ""));
        nasm.ajouteInst(new NasmInt(null, ""));
    }


    @Override
    public NasmOperand visit(C3aInstAdd inst) {
        NasmOperand label = (inst.label != null) ?
                inst.label.accept(this) :
                null;
        NasmOperand oper1 = inst.op1.accept(this);
        NasmOperand oper2 = inst.op2.accept(this);
        NasmOperand dest = inst.result.accept(this);
        nasm.ajouteInst(new NasmMov(label, dest, oper1, ""));
        nasm.ajouteInst(new NasmAdd(null, dest, oper2, ""));
        return null;
    }


    @Override
    public NasmOperand visit(C3aInstCall inst) {
        NasmRegister nasmRegister = new NasmRegister(Nasm.REG_ESP);
        nasmRegister.colorRegister(Nasm.REG_ESP);
        nasm.ajouteInst(new NasmSub(null, nasmRegister, new NasmConstant(4), "allocation mémoire pour la valeur de retour"));
        NasmLabel nasmLabel = (NasmLabel) inst.op1.accept(this);
        nasm.ajouteInst(new NasmCall(null, nasmLabel, ""));

        if (inst.result != null) {
            NasmOperand nasmOperand = inst.result.accept(this);
            nasm.ajouteInst(new NasmPop(null,nasmOperand , "récupération de la valeur de retour"));
        }
        if (tableGlobale.getFct(nasmLabel.val).nbArgs > 0) {
            nasm.ajouteInst(new NasmAdd(null, nasmRegister, new NasmConstant(inst.op1.val.nbArgs * 4), "désallocation des arguments"));
        }
        return null;
    }


    @Override
    public NasmOperand visit(C3aInstFBegin inst) {
        NasmOperand nasmOperand = new NasmLabel(inst.val.identif);
        NasmRegister EBPRegister = new NasmRegister(Nasm.REG_EBP);
        EBPRegister.colorRegister(Nasm.REG_EBP);
        NasmRegister ESPRegister = new NasmRegister(Nasm.REG_ESP);
        ESPRegister.colorRegister(Nasm.REG_ESP);

        nasm.ajouteInst(new NasmPush(nasmOperand, EBPRegister,"sauvegarde la valeur de ebp"));
        nasm.ajouteInst(new NasmMov(null, EBPRegister,ESPRegister, "nouvelle valeur de ebp"));
        this.currentFct = inst.val;
        int num = 0;
        for (TsItemVar tsItemVar : inst.val.getTable().variables.values()) {
            if(tsItemVar!=null)num =num+ 4;
        }

        nasm.ajouteInst(new NasmSub(null, ESPRegister, new NasmConstant(num), "allocation des variables locales"));
        return null;
    }


    @Override
    public NasmOperand visit(C3aInst inst) {
        return null;
    }


    @Override
    public NasmOperand visit(C3aInstJumpIfLess inst) {
        NasmOperand nasmOperand = (inst.label != null) ? inst.label.accept(this) : null;
        NasmOperand nasmOperand1 = inst.op1.accept(this);
        NasmOperand nasmOperand2 = inst.op2.accept(this);
        NasmOperand nasmOperand3 = inst.result.accept(this);

        if (nasmOperand1 instanceof NasmConstant) {
            NasmRegister nasmRegister = nasm.newRegister();
            nasm.ajouteInst(new NasmMov(null, nasmRegister, nasmOperand1, "JumpIfLess 1"));
            nasm.ajouteInst(new NasmCmp(nasmOperand, nasmRegister, nasmOperand2, "on passe par un registre temporaire"));
        } else {
            nasm.ajouteInst(new NasmCmp(nasmOperand, nasmOperand1, nasmOperand2, "JumpIfLess 1"));
        }
        nasm.ajouteInst(new NasmJl(null, nasmOperand3, "JumpIfLess 2"));
        return null;
    }


    @Override
    public NasmOperand visit(C3aInstMult inst) {
        NasmOperand label = (inst.label != null) ? inst.label.accept(this) : null;
        NasmOperand oper1 = inst.op1.accept(this);
        NasmOperand oper2 = inst.op2.accept(this);
        NasmOperand dest = inst.result.accept(this);
        nasm.ajouteInst(new NasmMov(label, dest, oper1, ""));
        nasm.ajouteInst(new NasmMul(null, dest, oper2, ""));
        return null;

    }

    @Override
    public NasmOperand visit(C3aInstRead inst) {
        return null;
    }


    @Override
    public NasmOperand visit(C3aInstSub inst) {
        NasmOperand label = (inst.label != null) ? inst.label.accept(this) : null;
        NasmOperand oper1 = inst.op1.accept(this);
        NasmOperand oper2 = inst.op2.accept(this);
        NasmOperand dest = inst.result.accept(this);
        nasm.ajouteInst(new NasmMov(label, dest, oper1, ""));
        nasm.ajouteInst(new NasmSub(null, dest, oper2, ""));
        return null;
    }


    @Override
    public NasmOperand visit(C3aInstAffect inst) {
        NasmOperand label = (inst.label != null) ? inst.label.accept(this) : null;
        NasmOperand nasmOperand = inst.op1.accept(this);
        NasmOperand nasmOperand1 = inst.result.accept(this);
        nasm.ajouteInst(new NasmMov(label, nasmOperand1, nasmOperand, "Affect"));
        return null;
    }


    @Override
    public NasmOperand visit(C3aInstDiv inst) {
        NasmOperand label = (inst.label != null) ? inst.label.accept(this) : null;
        NasmOperand nasmOperand = inst.op1.accept(this);
        NasmOperand nasmOperand1 = inst.op2.accept(this);

        NasmOperand nasmOperand2 = inst.result.accept(this);
        NasmRegister nasmRegister = nasm.newRegister();
        nasmRegister.colorRegister(Nasm.REG_EAX);
        NasmRegister nasmRegister1= nasm.newRegister();

        nasm.ajouteInst(new NasmMov(label, nasmRegister, nasmOperand, ""));
        nasm.ajouteInst(new NasmMov(null, nasmRegister1, nasmOperand1, ""));
        nasm.ajouteInst(new NasmDiv(null, nasmRegister1, ""));
        nasm.ajouteInst(new NasmMov(null, nasmOperand2, nasmRegister, ""));
        return null;
    }


    @Override
    public NasmOperand visit(C3aInstFEnd inst) {
        NasmOperand label = (inst.label != null) ? inst.label.accept(this) : null;
        NasmRegister nasmRegister = new NasmRegister(Nasm.REG_ESP);
        nasmRegister.colorRegister(Nasm.REG_ESP);

        int space = tableGlobale.getFct(currentFct.identif).getTable().variables.size()*4;
        nasm.ajouteInst(new NasmAdd(label, nasmRegister, new NasmConstant(space), "désallocation des variables locales"));
        NasmRegister nasmRegisterEBP = new NasmRegister(Nasm.REG_EBP);
        nasmRegisterEBP.colorRegister(Nasm.REG_EBP);
        nasm.ajouteInst(new NasmPop(null, nasmRegisterEBP, "restaure la valeur de ebp"));
        nasm.ajouteInst(new NasmRet(null, ""));
        return null;
    }



    @Override
    public NasmOperand visit(C3aInstJumpIfEqual inst) {
        NasmOperand label = (inst.label != null) ? inst.label.accept(this) : null;
        NasmOperand nasmOperand = inst.op1.accept(this);
        NasmOperand nasmOperand1 = inst.op2.accept(this);
        NasmOperand nasmOperand2 = inst.result.accept(this);
        if (nasmOperand instanceof NasmConstant) {
            NasmRegister nasmRegister = nasm.newRegister();
            nasm.ajouteInst(new NasmMov(label, nasmRegister, nasmOperand, "JumpIfEqual 1"));
            nasm.ajouteInst(new NasmCmp(null, nasmRegister, nasmOperand1, "on passe par un registre temporaire"));
        } else {
            nasm.ajouteInst(new NasmCmp(label, nasmOperand, nasmOperand1, "JumpIfEqual 1"));
        }
        nasm.ajouteInst(new NasmJe(null, nasmOperand2, "JumpIfEqual 2"));
        return null;
    }



    @Override
    public NasmOperand visit(C3aInstJumpIfNotEqual inst) {
        NasmOperand label = (inst.label != null) ? inst.label.accept(this) : null;
        NasmOperand nasmOperand = inst.op1.accept(this);
        NasmOperand nasmOperand1 = inst.op2.accept(this);
        NasmOperand nasmOperand2 = inst.result.accept(this);
        if (nasmOperand instanceof NasmConstant) {
            NasmRegister nasmRegister = nasm.newRegister();
            nasm.ajouteInst(new NasmMov(null, nasmRegister, nasmOperand, "jumpIfNotEqual 1"));
            nasm.ajouteInst(new NasmCmp(label, nasmRegister, nasmOperand1, "on passe par un registre temporaire"));
        } else {
            nasm.ajouteInst(new NasmCmp(label, nasmOperand, nasmOperand1, "jumpIfNotEqual 1"));
        }
        nasm.ajouteInst(new NasmJne(null, nasmOperand2, "jumpIfNotEqual 2"));
        return null;
    }



    @Override
    public NasmOperand visit(C3aInstJump inst) {
        NasmOperand nasmOperand = (inst.label != null) ? inst.label.accept(this) : null;
        NasmOperand nasmOperand1 = inst.result.accept(this);
        nasm.ajouteInst(new NasmJmp(nasmOperand, nasmOperand1, "Jump"));
        return null;
    }


    @Override
    public NasmOperand visit(C3aInstParam inst) {
        NasmOperand nasmOperand = inst.op1.accept(this);
        nasm.ajouteInst(new NasmPush(null, nasmOperand,"Param"));
        return null;
    }


    @Override
    public NasmOperand visit(C3aInstReturn inst) {
        NasmOperand nasmOperand = inst.op1.accept(this);
        NasmRegister nasmRegister = new NasmRegister(Nasm.REG_EBP);
        nasmRegister.colorRegister(Nasm.REG_EBP);
        NasmAddress nasmAddress = new NasmAddress(nasmRegister, '+',new NasmConstant(2));
        nasm.ajouteInst(new NasmMov(null,nasmAddress , nasmOperand, "ecriture de la valeur de retour"));
        return null;
    }


    @Override
    public NasmOperand visit(C3aInstWrite inst) {
        NasmRegister nasmRegister = nasm.newRegister();
        nasmRegister.colorRegister(Nasm.REG_EAX);
        NasmOperand nasmOperand = inst.op1.accept(this);
        NasmOperand nasmOperand1 = (inst.label != null) ? inst.label.accept(this) : null;
        nasm.ajouteInst(new NasmMov(nasmOperand1, nasmRegister, nasmOperand, "Write 1"));
        NasmLabel nasmLabel = new NasmLabel("iprintLF");
        nasm.ajouteInst(new NasmCall(null, nasmLabel, "Write 2"));
        return null;
    }


    @Override
    public NasmOperand visit(C3aConstant oper) {
        return new NasmConstant(oper.val);
    }


    @Override
    public NasmOperand visit(C3aLabel oper) {
        return new NasmLabel(oper.toString());
    }


    @Override
    public NasmOperand visit(C3aTemp oper) {
        NasmRegister nasmRegister = nasm.newRegister();
        nasmRegister.val = oper.num;
        return nasmRegister;
    }



    @Override
    public NasmOperand visit(C3aVar oper) {
        if (tableGlobale.getVar(oper.item.identif) != null) {
            if (oper.index != null) {
                NasmLabel nasmLabel = new NasmLabel(oper.item.identif);
                NasmOperand nasmOperand = oper.index.accept(this);
                return new NasmAddress(nasmLabel, '+', nasmOperand);
            }
            return new NasmAddress(new NasmLabel(oper.item.identif));
        }
        if (tableGlobale.getFct(this.currentFct.identif).getTable().getVar(oper.item.identif).isParam) {
            NasmRegister nasmRegister = new NasmRegister(Nasm.REG_EBP);
            nasmRegister.colorRegister(Nasm.REG_EBP);
            int num = 2 + tableGlobale.getFct(this.currentFct.identif).getNbArgs() - oper.item.adresse;
            return new NasmAddress(nasmRegister, '+', new NasmConstant(num));
        }
        if (tableGlobale.getFct(this.currentFct.identif).getTable().getVar(oper.item.identif) != null) {
            NasmRegister nasmRegister = new NasmRegister(Nasm.REG_EBP);
            nasmRegister.colorRegister(Nasm.REG_EBP);
            int num = oper.item.adresse + oper.item.taille;
            return new NasmAddress(nasmRegister, '-', new NasmConstant(num));
        }
        return null;
    }



    @Override
    public NasmOperand visit(C3aFunction oper) {
        return new NasmLabel(oper.val.identif);
    }
}