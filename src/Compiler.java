import c3a.C3a;
import sa.Sa2Xml;
import sa.SaNode;
import sc.lexer.Lexer;
import sc.node.Start;
import sc.parser.Parser;
import ts.Ts;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;


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
                //if (file.equals("C:\\Users\\Laura\\Desktop\\Fac\\S6\\Compilation\\compilationl3-public\\test\\input\\add1.l")) {
                br = new PushbackReader(new FileReader(file));
                baseName = removeSuffix(file, ".l");

                try {
                  //  System.out.println(file);
                    Parser p = new Parser(new Lexer(br));
                    Start tree = p.parse();
                    //System.out.println("[SC]");
                    tree.apply(new Sc2Xml(baseName));

                    //System.out.println("[SA]");
                    Sc2sa sc2sa = new Sc2sa();
                    tree.apply(sc2sa);
                    SaNode saRoot = sc2sa.getRoot();
                    new Sa2Xml(saRoot, baseName);
                    //System.out.println("Fin de l'arbre ");

                    //System.out.println("[TABLE SYMBOLES]");
                    Ts table = new Sa2ts(saRoot).getTableGlobale();
                    table.afficheTout(baseName);

                    //System.out.println("[C3A]");
	                C3a c3a = new Sa2c3a(saRoot, table).getC3a();
	                c3a.affiche(baseName);
                    if(!compareTest(baseName)) System.out.println("False");
	    /*
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


    public static boolean compareTest(String baseName) throws FileNotFoundException {
        boolean same=false;
        List<String> fileNames = new ArrayList<>();
        File folder = new File("test\\c3a-ref");
        File[] listOfFiles = folder.listFiles();
        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isFile() && listOfFiles[i].getName().endsWith(".c3a")) {
                fileNames.add(listOfFiles[i].getAbsolutePath());
            }
        }
        String newBaseName=baseName.substring(74)+".c3a";
        for (String file : fileNames) {
            String newfile=file.substring(76);
            if(newfile.equals(newBaseName)) {
                same = compareFiles(file,baseName);
            }
        }
        return same;
    }

    public static boolean compareFiles(String path1, String path2){
        path2=path2+".c3a";
        try{
            List<String> listF1 = Files.readAllLines(Paths.get(path1));
            List<String> listF2 = Files.readAllLines(Paths.get(path2));
            return listF1.containsAll(listF2) && listF2.containsAll(listF1);
        }catch(IOException ie) {
            System.out.println("File different : " + path1.substring(76));
        }
        return false;
    }



}