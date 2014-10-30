package src;

import java.util.LinkedList;
import java.util.List;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import struct.Index_Label_Expression;

/**
 * Grow trees and collect the fruits.
 *
 * @author mmendez
 */
public class Forest {

    /**
     * grow the trees, collect results and prepare the calculation of the
     * accuracy.
     */
    private ForestRes forestRes;
    private JSONArray forestSave;
    /**
     * Exclude perfect rows and add them at the end
     * @param prows true will exclude perfect rows from the 
     * 
     */
    public Forest(boolean prows) {
        this.forestRes = new ForestRes();
        this.forestSave = new JSONArray();
        if (prows) {

            // find perfect rows
            for (int i = 0; i < Data.annotations.length; i++) {

                List<Index_Label_Expression> ile = new LinkedList<Index_Label_Expression>();
                for (int j = 0; j < Data.sil.size(); j++) {

                    ile.add(new Index_Label_Expression(j, Data.sil.label.get(j), Data.m[i][j]));
                }

                SplitInfo si = new SplitInfo(ile, i);
                if (si.getGain() == si.getGini()) {
                    Conf.to_exclude.add(i);
                }
            }
            System.out.println("perfect rows: " + Conf.to_exclude.size());
        }
        

        // run RRF
//        Conf.count_split_info = 0;
        for (int i = 0; i < Conf.NB_OF_TREE; i++) {
//            System.out.println("tree number: " + i);
            List<Integer> usedRowInThatTree = new LinkedList<Integer>(); // can be calculated in Treeinfo...
            Tree tree = new Rtree();
            tree.grow();

//            System.out.println(tree.toJSON().toJSONString());
            // Combine the results
            forestRes.addRes(tree, usedRowInThatTree);
            
            forestRes.oobCall(tree);
            
            if(Conf.RAW){
                forestRes.oobPermutedCall(tree, usedRowInThatTree);
            }

            if (Conf.CLASSIFICATION_PER_CLASS) {
                forestRes.variableToLabelStats(tree);
            }
            
            if (Conf.SAVE_FOREST){
                this.forestSave.add(tree.toJSON());
            }
            
        }
        
        forestRes.calculateAccuracy(); // oob-error estimate
        for (Integer to_exclude : Conf.to_exclude) {
            forestRes.getResMap().put(to_exclude, new RowRes());
        }
        
    }

    /**
     * A Forest res instance.
     *
     * @return the combined results for this forest
     */
    public ForestRes getForestRes() {
        return forestRes;
    }
    
    public String getTreeToJSON(){
        return this.forestSave.toJSONString();
    }
}
