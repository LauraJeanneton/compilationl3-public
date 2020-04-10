package fg;

import nasm.*;
import util.graph.Node;
import util.graph.NodeList;
import util.intset.*;

import java.io.*;
import java.util.*;
import java.util.List;

public class FgSolution implements NasmVisitor<Void> {
	int iter = 0;
	public Nasm nasm;
	Fg fg;
	public Map<NasmInst, IntSet> use;
	public Map<NasmInst, IntSet> def;
	public Map<NasmInst, IntSet> in;
	public Map<NasmInst, IntSet> out;
	final int register;

	public FgSolution(Nasm nasm, Fg fg) {
		this.nasm = nasm;
		this.register = nasm.getTempCounter();
		this.fg = fg;
		this.use = new HashMap<>();
		this.def = new HashMap<>();
		this.in = new HashMap<>();
		this.out = new HashMap<>();
		init();
	}


	public void init(){
		for (NasmInst inst : nasm.listeInst) {
			inst.accept(this);
		}

		for (NasmInst inst : nasm.listeInst){
			IntSet intSet = new IntSet(register);
			in.put(inst,intSet);
			IntSet intSet1 = new IntSet(register);
			out.put(inst,intSet1);
		}

		List<NasmInst> listeInst = nasm.listeInst;
		boolean equals;
		do {
			iter++;
			Map<NasmInst, IntSet> inMap = new HashMap<>();
			Map<NasmInst, IntSet> outMap = new HashMap<>();
			for (NasmInst inst : listeInst) {
				inMap.put(inst, in.get(inst));
				outMap.put(inst, out.get(inst));

				IntSet outCopy = out.get(inst).copy();
				IntSet defCopy = def.get(inst).copy();
				IntSet minus = outCopy.minus(defCopy);
				IntSet useCopy = use.get(inst).copy();
				IntSet union = useCopy.union(minus);

				in.put(inst, union);

				Node node = fg.inst2Node.get(inst);
				NodeList nodeList = node.succ();
				List<Node> list = new ArrayList<>();
				while (nodeList != null) {
					list.add(nodeList.head);
					nodeList = nodeList.tail;
				}
				List<NasmInst> collectInst = new ArrayList<>();
				for (Node n : list) {
					collectInst.add(fg.node2Inst.get(n));
				}

				List<IntSet> intSetList = new ArrayList<>();
				for (NasmInst n : collectInst) {
					intSetList.add(in.get(n).copy());
				}
				if (intSetList.size() < 1) continue;
				IntSet outUnion = intSetList.get(0);
				for (int j = 1; j < intSetList.size(); j++) {
					outUnion.union(intSetList.get(j));
				}
				out.put(inst, outUnion);
			}

			Set<NasmInst> set = in.keySet();
			equals = true;
			for(NasmInst n : set){
				if(in.containsKey(n) && inMap.containsKey(n)){
					if(!in.get(n).equal(inMap.get(n))){
						equals = false;
					}
				}else {
					equals = false;
				}

				if(out.containsKey(n) && outMap.containsKey(n)){
					if(!out.get(n).equal(outMap.get(n))){
						equals = false;
					}
				}else{
					equals = false;
				}
			}
		} while (!equals);
	}



	public void affiche(String baseFileName) {
		String fileName;
		PrintStream out = System.out;

		if (baseFileName != null) {
			try {
				fileName = baseFileName + ".fgs";
				out = new PrintStream(fileName);
			} catch (IOException e) {
				System.err.println("Error: " + e.getMessage());
			}
		}

		out.println("iter num = " + iter);
		for (NasmInst nasmInst : this.nasm.listeInst) {
			out.println("use = " + this.use.get(nasmInst) + " def = " + this.def.get(nasmInst) + "\tin = " + this.in.get(nasmInst) + "\t \tout = " + this.out.get(nasmInst) + "\t \t" + nasmInst);
		}
	}

	@Override
	public Void visit(NasmAdd inst) {
		defAndUse(inst);
		return null;
	}

	private void defAndUse(NasmInst inst) {
		NasmOperand destination = inst.destination;
		NasmOperand source = inst.source;


		IntSet useSet = new IntSet(register);
		IntSet defSet = new IntSet(register);

		if (destination instanceof NasmAddress) {
			NasmAddress address = ((NasmAddress) destination);

			if (address.base != null && address.base.isGeneralRegister()) {
				NasmRegister register = ((NasmRegister) address.base);
				useSet.add(register.val);
				defSet.add(register.val);
			}

			if (address.offset != null && address.offset.isGeneralRegister()) {
				NasmRegister register = ((NasmRegister) address.offset);
				useSet.add(register.val);
				defSet.add(register.val);
			}
		} else {
			if (destination.isGeneralRegister()) {
				NasmRegister register = ((NasmRegister) destination);
				useSet.add(register.val);
				defSet.add(register.val);
			}
		}
		if (source instanceof NasmAddress) {
			addToIntSet(useSet, source);
		} else {
			if (source.isGeneralRegister()) {
				NasmRegister register = ((NasmRegister) source);
				useSet.add(register.val);
			}
		}
		use.put(inst, useSet);
		def.put(inst, defSet);
	}


	private void empty(NasmInst inst, Map<NasmInst, IntSet> inst2intSet) {
		IntSet intSet = new IntSet(register);
		inst2intSet.put(inst, intSet);
	}

	@Override
	public Void visit(NasmCall inst) {
		empty(inst,use);
		empty(inst,def);
		return null;
	}

	@Override
	public Void visit(NasmDiv inst) {
		empty(inst, def);
		IntSet intSet = new IntSet(register);
		addRegister(inst, inst.source, intSet, use);
		return null;
	}

	@Override
	public Void visit(NasmJe inst) {
		empty(inst,use);
		empty(inst,def);
		return null;
	}

	@Override
	public Void visit(NasmJle inst) {
		empty(inst,use);
		empty(inst,def);
		return null;
	}

	@Override
	public Void visit(NasmJne inst) {
		empty(inst,use);
		empty(inst,def);
		return null;
	}

	@Override
	public Void visit(NasmMul inst) {
		defAndUse(inst);
		return null;
	}

	@Override
	public Void visit(NasmOr inst) {
		empty(inst,use);
		empty(inst,def);
		return null;
	}

	@Override
	public Void visit(NasmCmp inst) {
		empty(inst, def);
		NasmOperand destination = inst.destination;
		NasmOperand source = inst.source;
		IntSet intSet = new IntSet(register);

		if (destination instanceof NasmAddress) {
			addToIntSet(intSet, destination);
		} else if (destination.isGeneralRegister()) {
			NasmRegister register = ((NasmRegister) destination);
			intSet.add(register.val);
		}
		if (source instanceof NasmAddress) {
			addToIntSet(intSet, source);
		} else if (source.isGeneralRegister()) {
			NasmRegister register = ((NasmRegister) source);
			intSet.add(register.val);
		}
		use.put(inst, intSet);
		return null;
	}

	private void addRegister(NasmInst inst, NasmOperand address, IntSet intSet, Map<NasmInst, IntSet> inst2intSet) {
		if (address instanceof NasmAddress) {
			addToIntSet(intSet, address);
		} else {
			if (address.isGeneralRegister()) {
				NasmRegister register = ((NasmRegister) address);
				intSet.add(register.val);
			}
		}
		inst2intSet.put(inst, intSet);
	}

	private void addToIntSet(IntSet useSet, NasmOperand dest) {
		NasmAddress address = ((NasmAddress) dest);
		if (address.base != null && address.base.isGeneralRegister()) {
			NasmRegister register = ((NasmRegister) address.base);
			useSet.add(register.val);
		}

		if (address.offset != null && address.offset.isGeneralRegister()) {
			NasmRegister register = ((NasmRegister) address.offset);
			useSet.add(register.val);
		}
	}

	@Override
	public Void visit(NasmInst inst) {
		empty(inst,use);
		empty(inst,def);
		return null;
	}

	@Override
	public Void visit(NasmJge inst) {
		empty(inst,use);
		empty(inst,def);
		return null;
	}

	@Override
	public Void visit(NasmJl inst) {
		empty(inst,use);
		empty(inst,def);
		return null;
	}

	@Override
	public Void visit(NasmNot inst) {
		empty(inst,use);
		empty(inst,def);
		return null;
	}

	@Override
	public Void visit(NasmPop inst) {
		empty(inst,use);
		IntSet intSet =  new IntSet(register);
		addRegister(inst, inst.destination, intSet, def);
		return null;
	}

	@Override
	public Void visit(NasmRet inst) {
		empty(inst,use);
		empty(inst,def);
		return null;
	}

	@Override
	public Void visit(NasmXor inst) {
		empty(inst,use);
		empty(inst,def);
		return null;
	}

	@Override
	public Void visit(NasmAnd inst) {
		empty(inst,use);
		empty(inst,def);
		return null;
	}

	@Override
	public Void visit(NasmJg inst) {
		empty(inst,use);
		empty(inst,def);
		return null;
	}

	@Override
	public Void visit(NasmJmp inst) {
		empty(inst,use);
		empty(inst,def);
		return null;
	}


	@Override
	public Void visit(NasmMov inst) {
		NasmOperand destination = inst.destination;
		NasmOperand source = inst.source;
		nasmMovSolution(inst, destination, def);
		nasmMovSolution(inst, source, use);
		return null;
	}

	private void nasmMovSolution(NasmMov inst, NasmOperand op, Map<NasmInst, IntSet> inst2intSet) {
		IntSet intSet = new IntSet(register);
		addRegister(inst, op, intSet, inst2intSet);

	}

	@Override
	public Void visit(NasmPush inst) {
		empty(inst, def);
		IntSet intSet =  new IntSet(register);
		addRegister(inst, inst.source, intSet, use);
		return null;
	}

	@Override
	public Void visit(NasmSub inst) {
		defAndUse(inst);
		return null;
	}

	@Override
	public Void visit(NasmEmpty inst) {
		empty(inst,use);
		empty(inst,def);
		return null;
	}

	@Override
	public Void visit(NasmAddress operand) {
		return null;
	}

	@Override
	public Void visit(NasmConstant operand) {
		return null;
	}

	@Override
	public Void visit(NasmLabel operand) {
		return null;
	}

	@Override
	public Void visit(NasmRegister operand) {
		return null;
	}
}


