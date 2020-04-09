import sa.*;
import sc.analysis.DepthFirstAdapter;
import sc.node.*;

public class Sc2sa extends DepthFirstAdapter {
    private SaNode returnValue;

    public void caseStart(Start node) {
        super.caseStart(node);
    }


    public void caseAProgramme(AProgramme node) {
        SaLDec var;
        SaLDec func;
        node.getOptdecvar().apply(this);
        var = (SaLDec) this.returnValue;
        node.getListfonct().apply(this);
        func = (SaLDec) this.returnValue;
        this.returnValue = new SaProg(var, func);
    }


    public void caseADeclaGlobOptdecvar(ADeclaGlobOptdecvar node) {
        SaLDec listeDecla;
        node.getDeclavarliste().apply(this);
        listeDecla = (SaLDec) this.returnValue;
        this.returnValue = listeDecla;
    }


    public void caseAVideOptdecvar(AVideOptdecvar node) {
        this.returnValue = null;
    }


    public void caseAListFonctListfonct(AListFonctListfonct node) {
        SaDec fonc;
        SaLDec list;
        node.getDeclafonct().apply(this);
        fonc = (SaDecFonc) this.returnValue;
        node.getListfonct().apply(this);
        list = (SaLDec) this.returnValue;
        this.returnValue = new SaLDec(fonc, list);
    }


    public void caseAMainListfonct(AMainListfonct node) {
        SaDec main;
        node.getMainFonct().apply(this);
        main = (SaDec) this.returnValue;
        this.returnValue = new SaLDec(main, null);
    }


    public void caseAMainFonctionMainFonct(AMainFonctionMainFonct node) {
        String nom = "main";
        SaLDec variables;
        SaInst corps;
        node.getOptdecvar().apply(this);
        variables = (SaLDec) this.returnValue;
        node.getBloc().apply(this);
        corps = (SaInst) this.returnValue;
        this.returnValue = new SaDecFonc(nom, null, variables, corps);
    }


    public void caseAListListExp(AListListExp node) {
        SaExp expression;
        SaLExp listeExp;
        node.getExp().apply(this);
        expression = (SaExp) this.returnValue;
        node.getListExpSuiv().apply(this);
        listeExp = (SaLExp) this.returnValue;
        this.returnValue = new SaLExp(expression, listeExp);
    }


    public void caseARienListExp(ARienListExp node) {
        this.returnValue = null;
    }


    public void caseAListExpListExpSuiv(AListExpListExpSuiv node) {
        SaExp expr;
        SaLExp list;
        node.getExp().apply(this);
        expr = (SaExp) this.returnValue;
        node.getListExpSuiv().apply(this);
        list = (SaLExp) this.returnValue;
        this.returnValue = new SaLExp(expr, list);
    }


    public void caseARienListExpSuiv(ARienListExpSuiv node) {
        this.returnValue = null;
    }


    public void caseAPvDeclavarliste(APvDeclavarliste node) {
        SaDec var;
        node.getVariabledeclar().apply(this);
        var = (SaDec) this.returnValue;
        this.returnValue = new SaLDec(var,null);
    }


    public void caseAListDeclavarliste(AListDeclavarliste node) {
        SaDec var;
        SaLDec liste;
        node.getVariabledeclar().apply(this);
        var = (SaDec) this.returnValue;
        node.getDeclavarliste().apply(this);
        liste = (SaLDec) this.returnValue;
        this.returnValue = new SaLDec(var, liste);
    }


    public void caseASeuleDeclavarliste(ASeuleDeclavarliste node) {
        SaDec nom;
        node.getDeclavarlistefin().apply(this);
        nom = (SaDec) this.returnValue;
        this.returnValue = new SaLDec(nom, null);
    }


    @Override
    public void caseADeclavarlistefin(ADeclavarlistefin node) {
        String var;
        var = String.valueOf(node.getVariabledeclar()).trim();
        this.returnValue = new SaDecVar(var);
    }


    @Override
    public void caseATableauVariabledeclar(ATableauVariabledeclar node) {
        String nom;
        int taille;
        node.getIdentif().apply(this);
        nom = String.valueOf(node.getIdentif());
        node.getNombre().apply(this);
        taille = Integer.parseInt(String.valueOf(node.getNombre()).trim());
        returnValue = new SaDecTab(nom, taille);
    }


    public void caseADeclaFnctDeclafonct(ADeclaFnctDeclafonct node) {
        String nom;
        SaLDec var;
        SaInst corps;
        node.getIdentif().apply(this);
        nom = String.valueOf(node.getIdentif()).trim();
        node.getLocale().apply(this);
        var = (SaLDec) this.returnValue;
        node.getBloc().apply(this);
        corps = (SaInst) this.returnValue;
        this.returnValue = new SaDecFonc(nom, null, var, corps);
    }


    public void caseAAffectationInstruction(AAffectationInstruction node) {
        super.caseAAffectationInstruction(node);
    }


    @Override

    public void caseAEntierVariabledeclar(AEntierVariabledeclar node) {
        String var;
        node.getIdentif().apply(this);
        var = String.valueOf(node.getIdentif()).trim();
        returnValue = new SaDecVar(var);
    }


    public void caseASiInstruction(ASiInstruction node) {
        SaExp si;
        SaInst alors;
        SaInst sinon;
        node.getExp().apply(this);
        si = (SaExp) this.returnValue;
        node.getBloc().apply(this);
        alors = (SaInst) this.returnValue;
        node.getSinonInstr().apply(this);
        sinon = (SaInst) this.returnValue;
        this.returnValue = new SaInstSi(si, alors, sinon);
    }


    public void caseAItantqueInstruction(AItantqueInstruction node) {
        super.caseAItantqueInstruction(node);
    }


    public void caseAAppelsimpleInstruction(AAppelsimpleInstruction node) {
        super.caseAAppelsimpleInstruction(node);
    }


    public void caseABlocInstruction(ABlocInstruction node) {
        super.caseABlocInstruction(node);
    }


    public void caseAAffectation(AAffectation node) {
        SaVar lhs;
        SaExp rhs;
        node.getVariable().apply(this);
        lhs = (SaVar) this.returnValue;
        node.getExp().apply(this);
        rhs = (SaExp) this.returnValue;
        this.returnValue = new SaInstAffect(lhs, rhs);
    }


    public void caseASinonSinonInstr(ASinonSinonInstr node) {
        super.caseASinonSinonInstr(node);
    }


    public void caseARienSinonInstr(ARienSinonInstr node) {
        this.returnValue = null;
    }


    public void caseAItantque(AItantque node) {
        SaExp test;
        SaInst todo;
        node.getTantque().apply(this);
        node.getExp().apply(this);
        test = (SaExp) this.returnValue;
        node.getFaire().apply(this);
        node.getBloc().apply(this);
        todo = (SaInst) this.returnValue;
        this.returnValue = new SaInstTantQue(test, todo);
    }


    @Override
    public void caseADeclaAvecParamDeclafonct(ADeclaAvecParamDeclafonct node) {
        String nom;
        SaLDec param;
        SaLDec var;
        SaInst corps;
        node.getIdentif().apply(this);
        nom = String.valueOf(node.getIdentif()).trim();
        node.getDeclavarliste().apply(this);
        param = (SaLDec) this.returnValue;
        node.getLocale().apply(this);
        var = (SaLDec) this.returnValue;
        node.getBloc().apply(this);
        corps = (SaInst) this.returnValue;
        this.returnValue = new SaDecFonc(nom, param, var, corps);
    }


    public void caseAEcrireAppelpredefini(AEcrireAppelpredefini node) {
        SaExp argument;
        argument = (SaExp) this.returnValue;
        node.getExp().apply(this);
        this.returnValue = new SaInstEcriture(argument);
    }


    public void caseALireAppelpredefini(ALireAppelpredefini node) {
        this.returnValue = new SaExpLire();
        node.getRpar().apply(this);
        node.getLpar().apply(this);
    }


    public void caseABlocBloc(ABlocBloc node) {
        SaLInst listeInstr;
        node.getListInstr().apply(this);
        listeInstr = (SaLInst) this.returnValue;
        this.returnValue = new SaInstBloc(listeInstr);
    }


    public void caseAOuExp(AOuExp node) {
        SaExp op1;
        SaExp op2;
        node.getExp().apply(this);
        op1 = (SaExp) this.returnValue;
        node.getOu().apply(this);
        node.getPrio1().apply(this);
        op2 = (SaExp) this.returnValue;
        this.returnValue = new SaExpOr(op1, op2);
    }


    public void caseAPrio1Exp(APrio1Exp node) {
        super.caseAPrio1Exp(node);
    }


    public void caseAEtPrio1(AEtPrio1 node) {
        SaExp op1;
        SaExp op2;
        node.getPrio1().apply(this);
        op1 = (SaExp) this.returnValue;
        node.getEt().apply(this);
        node.getPrio2().apply(this);
        op2 = (SaExp) this.returnValue;
        this.returnValue = new SaExpAnd(op1, op2);
    }


    public void caseAPrio2Prio1(APrio2Prio1 node) {
        super.caseAPrio2Prio1(node);
    }


    public void caseAEgalPrio2(AEgalPrio2 node) {
        SaExp op1;
        SaExp op2;
        node.getPrio2().apply(this);
        op1 = (SaExp) this.returnValue;
        node.getEgal().apply(this);
        node.getPrio3().apply(this);
        op2 = (SaExp) this.returnValue;
        this.returnValue = new SaExpEqual(op1, op2);
    }


    public void caseAInferPrio2(AInferPrio2 node) {
        SaExp op1;
        SaExp op2;
        node.getPrio2().apply(this);
        op1 = (SaExp) this.returnValue;
        node.getInfer().apply(this);
        node.getPrio3().apply(this);
        op2 = (SaExp) this.returnValue;
        this.returnValue = new SaExpInf(op1, op2);
    }


    public void caseAPrio3Prio2(APrio3Prio2 node) {
        super.caseAPrio3Prio2(node);
    }


    public void caseAPlusPrio3(APlusPrio3 node) {
        SaExp op1;
        SaExp op2;
        node.getPrio3().apply(this);
        op1 = (SaExp) this.returnValue;
        node.getPrio4().apply(this);
        op2 = (SaExp) this.returnValue;
        this.returnValue = new SaExpAdd(op1, op2);
    }


    public void caseAMoinsPrio3(AMoinsPrio3 node) {
        SaExp op1;
        SaExp op2;
        node.getPrio3().apply(this);
        op1 = (SaExp) this.returnValue;
        node.getPrio4().apply(this);
        node.getMinus().apply(this);
        op2 = (SaExp) this.returnValue;
        this.returnValue = new SaExpSub(op1, op2);
    }


    public void caseAPrio4Prio3(APrio4Prio3 node) {
        super.caseAPrio4Prio3(node);
    }


    public void caseAMultPrio4(AMultPrio4 node) {
        SaExp op1;
        SaExp op2;
        node.getPrio4().apply(this);
        op1 = (SaExp) this.returnValue;
        node.getMult().apply(this);
        node.getPrio5().apply(this);
        op2 = (SaExp) this.returnValue;
        this.returnValue = new SaExpMult(op1, op2);
    }


    public void caseADivPrio4(ADivPrio4 node) {
        SaExp op1;
        SaExp op2;
        node.getPrio4().apply(this);
        op1 = (SaExp) this.returnValue;
        node.getDiv().apply(this);
        node.getPrio5().apply(this);
        op2 = (SaExp) this.returnValue;
        this.returnValue = new SaExpDiv(op1, op2);
    }


    public void caseAPrio5Prio4(APrio5Prio4 node) {
        super.caseAPrio5Prio4(node);
    }


    public void caseANotPrio5(ANotPrio5 node) {
        SaExp op;
        node.getNot().apply(this);
        node.getLpar().apply(this);
        node.getNot().apply(this);
        node.getPrio6().apply(this);
        op = (SaExp) this.returnValue;
        node.getRpar().apply(this);
        this.returnValue = new SaExpNot(op);
    }


    public void caseAPrio6Prio5(APrio6Prio5 node) {
        super.caseAPrio6Prio5(node);
    }


    public void caseAParPrio6(AParPrio6 node) {
        super.caseAParPrio6(node);
    }


    public void caseANombrePrio6(ANombrePrio6 node) {
        super.caseANombrePrio6(node);
    }


    public void caseAVarPrio6(AVarPrio6 node) {
        SaVar var;
        node.getVariable().apply(this);
        var = (SaVar) this.returnValue;
        this.returnValue = new SaExpVar(var);
    }


    public void caseAAppelfctPrio6(AAppelfctPrio6 node) {
        SaAppel appel;
        node.getAppelfct().apply(this);
        appel = (SaAppel) this.returnValue;
        this.returnValue = new SaExpAppel(appel);
    }


    public void caseAChiffresNombre(AChiffresNombre node) {
        super.caseAChiffresNombre(node);
    }


    public void caseAChiffreNombre(AChiffreNombre node) {
        int chiffre;
        chiffre = Integer.parseInt(String.valueOf(node).trim());
        this.returnValue = new SaExpInt(chiffre);
    }


    public void caseAAplFctAppelfct(AAplFctAppelfct node) {
        String nom;
        SaLExp arg;
        node.getIdentif().apply(this);
        nom = String.valueOf(node.getIdentif()).trim();
        node.getListExp().apply(this);
        arg = (SaLExp) this.returnValue;
        this.returnValue = new SaAppel(nom, arg);
    }


    public void caseAEcrireAppelfct(AEcrireAppelfct node) {
        super.caseAEcrireAppelfct(node);
        SaExp argument;
        argument = (SaExp) this.returnValue;
        node.getExp().apply(this);
        this.returnValue = new SaInstEcriture(argument);
    }


    public void caseALireAppelfct(ALireAppelfct node) {
        this.returnValue = new SaExpLire();
    }


    public void caseARetourInstruction(ARetourInstruction node) {
        super.caseARetourInstruction(node);
    }


    public void caseARetour(ARetour node) {
        SaExp retour;
        node.getExp().apply(this);
        retour = (SaExp) this.returnValue;
        this.returnValue = new SaInstRetour(retour);
    }


    //TODO
    public void caseAEntierVariable(AEntierVariable node) {
        String var;
        node.getIdentif().apply(this);
        var = String.valueOf(node.getIdentif()).trim();
        returnValue = new SaVarSimple(var);
    }


    //TODO
    public void caseATableauVariable(ATableauVariable node) {
        String nom;
        SaExp taille;
        node.getIdentif().apply(this);
        nom = String.valueOf(node.getIdentif()).trim();
        node.getNombre().apply(this);
        taille = (SaExp) this.returnValue;
        returnValue = new SaVarIndicee(nom, taille);
    }


    public void caseAListInstrListInstr(AListInstrListInstr node) {
        SaInst instr;
        SaLInst listeInstr;
        node.getInstruction().apply(this);
        instr = (SaInst) this.returnValue;
        node.getSousListInst().apply(this);
        listeInstr = (SaLInst) this.returnValue;
        this.returnValue = new SaLInst(instr, listeInstr);
    }


    public void caseAVideListInstr(AVideListInstr node) {
        this.returnValue = null;
    }


    public void caseASousListSousListInst(ASousListSousListInst node) {
        SaInst instr;
        SaLInst listeInstr;
        node.getInstruction().apply(this);
        instr = (SaInst) this.returnValue;
        node.getSousListInst().apply(this);
        listeInstr = (SaLInst) this.returnValue;
        this.returnValue = new SaLInst(instr, listeInstr);
    }


    public void caseAVideSousListInst(AVideSousListInst node) {
        this.returnValue = null;
    }


    public SaNode getRoot() {
        return returnValue;
    }

}