import c3a.C3a;
import c3a.C3aEval;
import fg.Fg;
import fg.FgSolution;
import ig.Ig;
import nasm.Nasm;
import sa.Sa2Xml;
import sa.SaEval;
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
                if(!listOfFiles[i].getName().contains("tab1"))
                    fileNames.add(listOfFiles[i].getAbsolutePath());
            }
        }

        try {
            for (String file : fileNames) {
               // if(file.contains("affect")){
                br = new PushbackReader(new FileReader(file));
                baseName = removeSuffix(file, ".l");
                try {
                    Parser p = new Parser(new Lexer(br));
                //    System.out.print("[BUILD SC] ");
                    Start tree = p.parse();
                //    System.out.println("[PRINT SC]");
                    tree.apply(new Sc2Xml(baseName));

               //     System.out.print("[BUILD SA] ");
                    Sc2sa sc2sa = new Sc2sa();
                    tree.apply(sc2sa);
                    SaNode saRoot = sc2sa.getRoot();
               //     System.out.println("[PRINT SA]");
                    new Sa2Xml(saRoot, baseName);
                    if(!compareTest(baseName,".sa")) System.out.println("False sa : " + baseName);

                 //   System.out.print("[BUILD TS] ");
                    Ts table = new Sa2ts(saRoot).getTableGlobale();
                 //   System.out.println("[PRINT TS]");
                    table.afficheTout(baseName);
                    if(!compareTest(baseName,".ts")) System.out.println("False ts : " + baseName);


//                    SaEval saEval = new SaEval(saRoot, table);
//                    saEval.affiche(baseName);
                  //  if(!compareTest(baseName,".saout")) System.out.println("False saout : " + baseName);


                    C3a c3a = new Sa2c3a(saRoot, table).getC3a();
                    c3a.affiche(baseName);
                    if(!compareTest(baseName,".c3a"))System.out.println("False c3a: " +baseName);

                    C3aEval c3aEval = new C3aEval(c3a, table);
                    c3aEval.affiche(baseName);
                    if(!compareTest(baseName,".c3aout"))System.out.println("False c3aout: " +baseName);


                    //System.out.print("[BUILD PRE NASM] ");
                    Nasm nasm = new C3a2nasm(c3a, table).getNasm();
                    //System.out.println("[PRINT PRE NASM] ");
                    nasm.affichePre(baseName);
                    //if(!compareTest(baseName,".pre-nasm"))System.out.println("False prenasm: " +baseName);

             //       System.out.print("[BUILD FG] ");
                    Fg fg = new Fg(nasm);
                 //   System.out.print("[PRINT FG] ");
                    fg.affiche(baseName);
                   // if(!compareTest(baseName,".fg"))System.out.println("False fg: " +baseName);
//
//
//                    System.out.println("[SOLVE FG]");
//                    FgSolution fgSolution = new FgSolution(nasm, fg);
//                    fgSolution.affiche(baseName);
//
//                    System.out.print("[BUILD IG] ");
//                    Ig ig = new Ig(fgSolution);
//                    System.out.print("[PRINT IG] ");
//                    ig.affiche(baseName);
//                    System.out.println("[ALLOCATE REGISTERS]");
//                    ig.allocateRegisters();
//                    System.out.println("[PRINT NASM]");
//                    nasm.affiche(baseName);

                } catch (Exception e) {
                    System.out.println("ERROR : " + baseName);
                }
               //   } ////
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


    public static boolean compareTest(String baseName,String extension) throws FileNotFoundException {
        boolean same=false;
        List<String> fileNames = new ArrayList<>();
        String path;
        if(extension.equals(".pre-nasm")) path = "test\\prenasm-ref";
        else path = "test\\"+extension.substring(1)+"-ref";
        File folder = new File(path);
        File[] listOfFiles = folder.listFiles();
        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isFile() && listOfFiles[i].getName().endsWith(extension)) {
                fileNames.add(listOfFiles[i].getAbsolutePath());
            }
        }
        String newBaseName=baseName.substring(55)+extension;
        for (String file : fileNames) {
            int toSub = 54+ (extension.length()-1) ;
            if (extension.equals(".pre-nasm")) toSub= toSub-1;
            String newfile=file.substring(toSub);
            // System.out.println(newBaseName + " " + newfile);
            if(newfile.equals(newBaseName)) {
                same = compareFiles(file,baseName,extension);
            }
        }
        return same;
    }

    public static boolean compareFiles(String path1, String path2,String extension){
        path2=path2+extension;
        try{
            List<String> listF1 = Files.readAllLines(Paths.get(path1));
            List<String> listF2 = Files.readAllLines(Paths.get(path2));
            return listF1.containsAll(listF2) && listF2.containsAll(listF1);
        }catch(IOException ie) {
            System.out.println("File different : " + path1.substring(55));
        }
        return false;
    }



}