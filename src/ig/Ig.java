package ig;

import fg.*;
import nasm.*;
import util.graph.*;
import util.intset.*;

import java.io.*;
import java.util.Arrays;

public class Ig {
	public Graph graph;
	public FgSolution fgs;
	public int regNb;
	public Nasm nasm;
	public Node[] int2Node;
	public ColorGraph colorGraph;


	public Ig(FgSolution fgs) {
		this.fgs = fgs;
		this.graph = new Graph();
		this.nasm = fgs.nasm;
		this.regNb = this.nasm.getTempCounter();
		this.int2Node = new Node[regNb];
		this.construction();
		colorGraph = new ColorGraph(graph, 4, getPrecoloredTemporaries());
		colorGraph.coloration();
	}

	public void construction() {
		for (int index = 0; index < int2Node.length; index++) {
			int2Node[index] = graph.newNode();
		}
		for (NasmInst inst : nasm.listeInst) {
			IntSet intSetIn = fgs.in.get(inst);
			IntSet intSetOut = fgs.out.get(inst);

			for (int i = 0; i < intSetIn.getSize(); i++) {
				for (int j = i + 1; j < intSetIn.getSize(); j++) {
					if (intSetIn.isMember(i) && intSetIn.isMember(j))
						graph.addNOEdge(int2Node[i], int2Node[j]);
				}
			}
			for (int i = 0; i < intSetOut.getSize(); i++) {
				for (int j = i + 1; j < intSetOut.getSize(); j++) {
					if (intSetOut.isMember(i) && intSetOut.isMember(j))
						graph.addNOEdge(int2Node[i], int2Node[j]);
				}
			}

		}
	}

	public int[] getPrecoloredTemporaries() {
		int[] couleurs = new int[regNb];
		Arrays.fill(couleurs, -1);
		for (NasmInst inst : nasm.listeInst) {
			NasmOperand[] operand = {inst.destination,inst.source};
			for (NasmOperand op: operand) {
				if (op != null) {
					NasmRegister register = null;
					if (op.isGeneralRegister()) {
						register = (NasmRegister) op;
					}
					else if (op instanceof NasmAddress) {
						NasmAddress address = (NasmAddress) op;
						if (address.base != null && address.base.isGeneralRegister() ) {
							register = (NasmRegister) address.base;
						}
						if (address.offset != null && address.offset.isGeneralRegister()) {
							register = (NasmRegister) address.offset;
						}
					}
					if (register!=null && register.color != Nasm.REG_UNK && register.color != Nasm.REG_ESP && register.color != Nasm.REG_EBP)
						couleurs[register.val] = register.color;
				}
			}
		}
		return couleurs;
	}

	public void allocateRegisters() {
		for (NasmInst inst : nasm.listeInst) {
			NasmOperand[] operands = {inst.source,inst.destination};
			for (NasmOperand op: operands) {
				if (op != null) {
					NasmRegister register = null;
					if (op.isGeneralRegister()) {
						register = (NasmRegister) op;
					}
					else if (op instanceof NasmAddress) {
						NasmAddress address = (NasmAddress) op;
						if (address.base.isGeneralRegister() && address.base != null) {
							register = (NasmRegister) address.base;
						}
						if (address.offset != null && address.offset.isGeneralRegister()) {
							register = (NasmRegister) address.offset;
						}
					}
					assert register != null;
					if (register.color == Nasm.REG_UNK)
						register.color = colorGraph.couleur[register.val];
				}
			}
		}
	}


	public void affiche(String baseFileName) {
		String fileName;
		PrintStream out = System.out;

		if (baseFileName != null) {
			try {
				fileName = baseFileName + ".ig";
				out = new PrintStream(fileName);
			} catch (IOException e) {
				System.err.println("Error: " + e.getMessage());
			}
		}

		for (int i = 0; i < regNb; i++) {
			Node n = this.int2Node[i];
			out.print(n + " : ( ");
			for (NodeList q = n.succ(); q != null; q = q.tail) {
				out.print(q.head.toString());
				out.print(" ");
			}
			out.println(")");
		}
	}
}




    
