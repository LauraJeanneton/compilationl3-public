package fg;

import nasm.*;
import util.graph.Graph;
import util.graph.Node;
import util.graph.NodeList;

import java.io.IOException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;



public class Fg implements NasmVisitor <Void> {
    public Nasm nasm;
    public Graph graph;
    Map< NasmInst, Node> inst2Node;
    Map< Node, NasmInst> node2Inst;
    Map< String, NasmInst> label2Inst;


    public Fg(Nasm nasm){
        this.nasm = nasm;
        this.inst2Node = new HashMap< NasmInst, Node>();
        this.node2Inst = new HashMap< Node, NasmInst>();
        this.label2Inst = new HashMap< String, NasmInst>();
        this.graph = new Graph();

        createAll();
    }

    public void createAll(){
        for(NasmInst nasmInst : nasm.listeInst) {
            if (nasmInst.label != null) label2Inst.put(nasmInst.label.toString(), nasmInst);
            Node node = graph.newNode();
            inst2Node.put(nasmInst, node);
            node2Inst.put(node, nasmInst);
        }
        for(NasmInst nasmInst : nasm.listeInst) nasmInst.accept(this);
    }


    public void affiche(String baseFileName){
        String fileName;
        PrintStream out = System.out;

        if (baseFileName != null){
            try {
                baseFileName = baseFileName;
                fileName = baseFileName + ".fg";
                out = new PrintStream(fileName);
            }

            catch (IOException e) {
                System.err.println("Error: " + e.getMessage());
            }
        }

        for(NasmInst nasmInst : nasm.listeInst){
            Node n = this.inst2Node.get(nasmInst);
            out.print(n + " : ( ");
            for(NodeList q=n.succ(); q!=null; q=q.tail) {
                out.print(q.head.toString());
                out.print(" ");
            }
            out.println(")\t" + nasmInst);
        }

    }



    void createNode(NasmInst inst) {
        Node node = graph.newNode();
        inst2Node.put(inst, node);
        node2Inst.put(node, inst);
        if (inst.label != null) {
            label2Inst.put(inst.label.toString(),inst);
        }
        for (NasmInst nasmInst : nasm.listeInst) {
            nasmInst.accept(this);
        }
    }



    void createNextArc(NasmInst inst) {
        Node node = inst2Node.get(inst);
        int index = nasm.listeInst.indexOf(inst);
        if(index < nasm.listeInst.size() - 1) {
            graph.addEdge(node, inst2Node.get(nasm.listeInst.get(index + 1)));
        }
    }

    void createNextArcLabel(NasmInst inst) {
        NasmLabel label = (NasmLabel) inst.address;
        Node node = inst2Node.get(inst);
        String value = label.val;
        if (label2Inst.containsKey(value)) {
            graph.addEdge(node, inst2Node.get(label2Inst.get(value))
            );
        }
    }


    public Void visit(NasmAdd inst){
        createNextArc(inst);
        return null;
    }

    public Void visit(NasmCall inst){
        createNextArcLabel(inst);
        return null;

    }

    public Void visit(NasmDiv inst){
        createNextArc(inst);
        return null;
    }

    public Void visit(NasmJe inst){
        createNextArcLabel(inst);
        createNextArc(inst);
        return null;
    }

    public Void visit(NasmJle inst){
        createNextArcLabel(inst);
        createNextArc(inst);
        return null;
    }

    public Void visit(NasmJne inst){
        createNextArcLabel(inst);
        createNextArc(inst);
        return null;
    }
    public Void visit(NasmMul inst){
        createNextArc(inst);
        return null;
    }

    public Void visit(NasmOr inst){
        createNextArc(inst);
        return null;
    }

    public Void visit(NasmCmp inst){
        createNextArc(inst);
        return null;
    }

    public Void visit(NasmInst inst){
        createNextArc(inst);
        return null;
    }
    public Void visit(NasmJge inst){
        createNextArcLabel(inst);
        createNextArc(inst);
        return null;
    }
    public Void visit(NasmJl inst){
        createNextArcLabel(inst);
        createNextArc(inst);
        return null;
    }

    public Void visit(NasmNot inst){
        createNextArc(inst);
        return null;
    }

    public Void visit(NasmPop inst){
        createNextArc(inst);
        return null;
    }

    public Void visit(NasmRet inst){
        createNextArc(inst);
        return null;
    }

    public Void visit(NasmXor inst){
        createNextArc(inst);
        return null;
    }

    public Void visit(NasmAnd inst){
        createNextArc(inst);
        return null;
    }

    public Void visit(NasmJg inst){
        createNextArcLabel(inst);
        createNextArc(inst);
        return null;
    }

    public Void visit(NasmJmp inst){
        createNextArcLabel(inst);
        return null;
    }

    public Void visit(NasmMov inst){
        createNextArc(inst);
        return null;
    }

    public Void visit(NasmPush inst){
        createNextArc(inst);
        return null;
    }

    public Void visit(NasmSub inst){
        createNextArc(inst);
        return null;
    }

    public Void visit(NasmEmpty inst){
        createNextArc(inst);
        return null;
    }



    public Void visit(NasmAddress operand){return null;}
    public Void visit(NasmConstant operand){return null;}
    public Void visit(NasmLabel operand){return null;}
    public Void visit(NasmRegister operand){return null;}




}