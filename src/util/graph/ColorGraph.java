package util.graph;

import util.intset.*;
import java.util.*;

public class ColorGraph {
    public Graph G;
    public int R;
    public int K;
    private Stack<Integer> pile;
    public IntSet enleves;
    public IntSet deborde;
    public int[] couleur;
    public Node[] int2Node;
    static int NOCOLOR = -1;

    public ColorGraph(Graph G, int K, int[] phi) {
        this.G = G;
        this.K = K;
        pile = new Stack<>();
        R = G.nodeCount();
        couleur = new int[R];
        enleves = new IntSet(R);
        deborde = new IntSet(R);
        int2Node = G.nodeArray();
        for (int v = 0; v < R; v++) {
            int preColor = phi[v];
            if (preColor >= 0 && preColor < K)
                couleur[v] = phi[v];
            else
                couleur[v] = NOCOLOR;
        }
    }

    /*-------------------------------------------------------------------------------------------------------------*/
    /* associe une couleur à tous les sommets se trouvant dans la pile */
    /*-------------------------------------------------------------------------------------------------------------*/

    public void selection() {
        IntSet intSet = new IntSet(K);
        for(int index = 0 ; index < intSet.getSize() ; index++) {
            intSet.add(index);
        }
        while(pile.size() != 0){
            int newPile = pile.pop();
            IntSet intSetVoisins = couleursVoisins(newPile);
            int counter = 0 ;
            IntSet intSetCopy  = intSet.copy();
            IntSet intSetMinus = intSetCopy.minus(intSetVoisins);
            for(int index = 0 ; index < intSetVoisins.getSize(); index++){
                if(intSetVoisins.isMember(index))
                    counter++ ;
            }
            if(counter != K && couleur[newPile]==NOCOLOR){
                couleur[newPile] = choisisCouleur(intSetMinus);
            }
        }
    }

    /*-------------------------------------------------------------------------------------------------------------*/
    /* récupère les couleurs des voisins de t */
    /*-------------------------------------------------------------------------------------------------------------*/

    public IntSet couleursVoisins(int t) {
        IntSet intSetVoisins = new IntSet(K);
        NodeList listVoisins = int2Node[t].succs;
        while(listVoisins != null){
            Node headNode =listVoisins.head ;
            for(int index = 0 ; index < int2Node.length ; index++){
                if(int2Node[index].equals(headNode))
                    if(couleur[index] != -1)
                        intSetVoisins.add(couleur[index]);
            }
            listVoisins = listVoisins.tail;
        }
        return intSetVoisins ;
    }

    /*-------------------------------------------------------------------------------------------------------------*/
    /* recherche une couleur absente de colorSet */
    /*-------------------------------------------------------------------------------------------------------------*/

    public int choisisCouleur(IntSet voisins) {
        for(int index = 0 ; index < voisins.getSize() ; index++){
            if(voisins.isMember(index)) {
                return index ;
            }
        }
        return -1 ;
    }

    /*-------------------------------------------------------------------------------------------------------------*/
    /* calcule le nombre de voisins du sommet t */
    /*-------------------------------------------------------------------------------------------------------------*/

    public int nbVoisins(int t) {
        List<Node> listHeads = new ArrayList<>();
        NodeList nodeList = int2Node[t].succ();
        int counter = 0;
        while(nodeList != null){
            listHeads.add(nodeList.head);
            nodeList = nodeList.tail;
        }
        for(int i = 0 ; i < int2Node.length; i++){
            for(Node e : listHeads){
                if(e == int2Node[i]){
                    if(!enleves.isMember(i)){
                        counter++;
                    }
                }
            }
        }
        return counter;
    }

    /*-------------------------------------------------------------------------------------------------------------*/
    /* simplifie le graphe d'interférence g                                                                        */
    /* la simplification consiste à enlever du graphe les temporaires qui ont moins de k voisins                   */
    /* et à les mettre dans une pile                                                                               */
    /* à la fin du processus, le graphe peut ne pas être vide, il s'agit des temporaires qui ont au moins k voisin */
    /*-------------------------------------------------------------------------------------------------------------*/

    public void simplification() {
        boolean modified = true;
        int counter = 0;
        for(int color : couleur){
            if(color == -1){
                counter++;
            }
        }
        int num = R - (R - counter);
        while (pile.size() != num && modified ) {
            modified = false;
            for (int index = 0; index < int2Node.length; index++) {
                if (enleves.isMember(index))
                    continue;
                final int i = nbVoisins(index);
                if (i < K && couleur[index] == NOCOLOR) {
                    pile.push(index);
                    enleves.add(index);
                    modified = true ;
                }

            }
        }
    }

    /*-------------------------------------------------------------------------------------------------------------*/
    /*-------------------------------------------------------------------------------------------------------------*/

    public void debordement() {
        while( pile.size() != R ){
            for(int index = 0 ; index < R ; index++){
                if(!enleves.isMember(index)){
                    pile.push(index);
                    deborde.add(index);
                    enleves.add(index);
                    simplification();
                }
            }

        }
    }

    /*-------------------------------------------------------------------------------------------------------------*/
    /*-------------------------------------------------------------------------------------------------------------*/
    public void coloration() {
        this.simplification();
        this.debordement();
        this.selection();
    }

    void affiche() {
        System.out.println("vertex\tcolor");
        for (int i = 0; i < R; i++) {
            System.out.println(i + "\t" + couleur[i]);
        }
    }


}
