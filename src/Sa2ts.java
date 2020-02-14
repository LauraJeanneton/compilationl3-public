import sa.*;
import ts.Ts;
import ts.TsItemVar;

public class Sa2ts extends SaDepthFirstVisitor {
    Ts tableGlobale = new Ts();
    Ts tableLocale;
    int param=0;

    public Sa2ts(SaNode saRoot) {
        visit((SaProg) saRoot);
    }

    public Ts getTableGlobale() {
       return tableGlobale;
    }

    @Override
    public Object visit(SaDecTab node) {
        if(tableLocale!=null) {
            if (tableGlobale.variables.size() < param){
                tableLocale.addVar(node.getNom(), node.getTaille());
            }
            else{
                tableLocale.addParam(node.getNom());
            }
        }
        else {
            tableGlobale.addVar(node.getNom(),node.getTaille());
        }
        return super.visit(node);
    }

    @Override
    public Object visit(SaDecFonc node) {
        if(node.getParametres()==null){
            param=0;
        }
        else {
            param = node.getParametres().length();
        }
        tableLocale = new Ts();
        tableGlobale.addFct(node.getNom(),param,tableLocale,node);
        return super.visit(node);
    }

    @Override
    public Object visit(SaDecVar node) {
        if(tableLocale!=null) {
            if (tableLocale.variables.size() < param){
                tableLocale.addParam(node.getNom());
            }
            else{
                tableLocale.addVar(node.getNom(),1);
            }
        }
        else {
            tableGlobale.addVar(node.getNom(),1);
        }
        return super.visit(node);
    }

    @Override
    public Object visit(SaVarSimple node) {
        return super.visit(node);
    }

    @Override
    public Object visit(SaAppel node) {
        return super.visit(node);
    }

    @Override
    public Object visit(SaVarIndicee node) {
        return super.visit(node);
    }
}
