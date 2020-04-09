import sa.*;
import ts.*;

public class Sa2ts extends SaDepthFirstVisitor {


    private Ts tableGlobale;
    private Ts tableLocale;

    private char param;

    public Sa2ts(SaNode root) {
        tableGlobale = new Ts();
        param = 'g';
        root.accept(this);
    }

    public Ts getTableGlobale() {
        return tableGlobale;
    }

    public Object visit(SaDecVar node) {
        TsItemVar tsItemVar;
            if (param == 'g') tsItemVar = tableGlobale.addVar(node.getNom(), 1);
            else if (param == 'l') tsItemVar = tableLocale.addVar(node.getNom(), 1);
            else tsItemVar = tableLocale.addParam(node.getNom());
            node.tsItem = tsItemVar;
        return null;
    }

    public Object visit(SaDecTab node){
        TsItemVar item = null;
        if(param == 'g') item = tableGlobale.addVar(node.getNom(), node.getTaille());
        node.tsItem = item;
        return null;
    }

    public Object visit(SaDecFonc node) {
        Ts table = new Ts();
        TsItemFct item = tableGlobale.addFct(node.getNom(), node.getParametres() != null ? node.getParametres().length() : 0, table, node);
        node.tsItem = item;
        tableLocale = table;
        param = 'a';
        if(node.getParametres() != null) node.getParametres().accept(this);
        param = 'l';
        if(node.getVariable() != null) node.getVariable().accept(this);
        node.getCorps().accept(this);
        param = 'g';
        return null;
    }


    public Object visit(SaVarSimple node) {
        TsItemVar itemLoc = tableLocale.getVar(node.getNom());
        TsItemVar itemGlob = tableGlobale.getVar(node.getNom());
        if (itemLoc != null) node.tsItem = itemLoc;
        if (itemGlob != null) node.tsItem = itemGlob;
        return null;
    }

    public Object visit(SaVarIndicee node) {
        node.getIndice().accept(this);
        TsItemVar itemLoc = tableLocale.getVar(node.getNom());
        TsItemVar itemGlob = tableGlobale.getVar(node.getNom());
        if (itemLoc != null) node.tsItem = itemLoc;
        if (itemGlob != null) node.tsItem = itemGlob;
        return null;
    }

    public Object visit(SaAppel node) {
        if(node.getArguments() != null) node.getArguments().accept(this);
        TsItemFct itemLoc = tableLocale.getFct(node.getNom());
        TsItemFct itemGlob = tableGlobale.getFct(node.getNom());
        if (itemLoc != null) node.tsItem = itemLoc;
        if (itemGlob != null) node.tsItem = itemGlob;
        return null;
    }
}
