import sa.Sa2Xml;
import sa.SaExp;
import sa.SaNode;
import sc.parser.*;
import sc.lexer.*;
import sc.node.*;
import ts.Ts;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
//import sa.*;
//import ts.*;
//import c3a.*;
//import nasm.*;
//import fg.*;

public class Compiler {
    public static void main(String[] args) {
        PushbackReader br = null;
        String baseName = null;

        List<String> fileNames = new ArrayList<>();
        File folder = new File("test\\input");
        File[] listOfFiles = folder.listFiles();

        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isFile() && listOfFiles[i].getName().endsWith(".l")) {
                fileNames.add(listOfFiles[i].getAbsolutePath());
            }
        }

        try {
            for (String file : fileNames) {
         //       if (file.equals("C:\\Users\\Laura\\Desktop\\S6\\Compilation\\compilationl3-public\\test\\input\\tab1.l")) {
                    br = new PushbackReader(new FileReader(file));
                    baseName = removeSuffix(file, ".l");

                    try {
                        System.out.println(file);
                        Parser p = new Parser(new Lexer(br));
                        Start tree = p.parse();
                        System.out.println("[SC]");
                        tree.apply(new Sc2Xml(baseName));

                        System.out.println("[SA]");
                        Sc2sa sc2sa = new Sc2sa();
                        tree.apply(sc2sa);
                        SaNode saRoot = sc2sa.getRoot();
                        new Sa2Xml(saRoot, baseName);
                        System.out.println("Fin de l'arbre ");


					System.out.println("[TABLE SYMBOLES]");
					Ts table = new Sa2ts(saRoot).getTableGlobale();
					table.afficheTout(baseName);
/*
	    System.out.println("[C3A]");
	    C3a c3a = new Sa2c3a(saRoot, table).getC3a();
	    c3a.affiche(baseName);

	    System.out.println("[NASM]");
	    Nasm nasm = new C3a2nasm(c3a, table).getNasm();
	    nasm.affiche(baseName);

	    System.out.println("[FLOW GRAPH]");
	    Fg fg = new Fg(nasm);
	    fg.affiche(baseName);

	    System.out.println("[FLOW GRAPH SOLVE]");
	    FgSolution fgSolution = new FgSolution(nasm, fg);
	    fgSolution.affiche(baseName);*/


                    } catch (Exception e) {
                        System.out.println("Catch");
                        System.out.println(e.getMessage());
                    }
              //  } ////
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    public static String removeSuffix(final String s, final String suffix) {
        if (s != null && suffix != null && s.endsWith(suffix)) {
            return s.substring(0, s.length() - suffix.length());
        }
        return s;
    }

}
