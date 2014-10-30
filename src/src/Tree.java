/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package src;

import java.util.*;
import org.json.simple.JSONObject;
import struct.Index_Label;
import struct.Index_Label_Expression;
import struct.Sample_Index_Label;
import struct.TreeInfo;


/**
 * Class representing a Tree.
 *
 * Once an instance of this class is created, use the grow() method to grow a
 * tree
 *
 * @author mmendez
 */
public abstract class Tree {

    protected Tree leftChild;
    protected Tree rightChild;
    protected boolean isLeaf = false;
    protected int row;
    protected String label;
    protected double gain;
    protected double cut_off_val;

    /**
     * Calls findBestRow on 2 rows_index and determines which row is the best.
     */
    protected abstract void calculateTheGiniCriteriaForEachRows();

    /**
     * Run through the given rows and try to find the one who best split the
     * data into different groups.
     *
     * @param rows index of the rows to be tested
     * @param si
     * @param penalty
     * @return Information about the best split
     */
    protected SplitInfo findBestRow(Collection<Integer> rows, SplitInfo si, boolean penalty) {

        if (penalty) {
            return findBestRowWithPenalty(rows, si);
        }

        for (Integer row_index : rows) {
            List<Index_Label_Expression> ile = new LinkedList<Index_Label_Expression>();
            for (Index_Label tree_index_label : TreeInfo.tree_index_label) {
                ile.add(new Index_Label_Expression(tree_index_label, Data.m[row_index][tree_index_label.index]));
            }

            SplitInfo current_split_info = new SplitInfo(ile, row_index);

            if (current_split_info.getGain() > si.getGain()) {
                si = current_split_info;
            }
        }
        return si;
    }
    
    private SplitInfo findBestRowWithPenalty(Collection<Integer> rows, SplitInfo si) {

        for (Integer row_index : rows) {
            List<Index_Label_Expression> ile = new LinkedList<Index_Label_Expression>();
            for (Index_Label tree_index_label : TreeInfo.tree_index_label) {
                ile.add(new Index_Label_Expression(tree_index_label, Data.m[row_index][tree_index_label.index]));
            }

            SplitInfo current_split_info = new SplitInfo(ile, row_index);
            current_split_info.penalty(Conf.PENALTY[row_index]);
            
            if (current_split_info.getGain() > si.getGain()) {
                si = current_split_info;
            }
        }
        return si;
    }

    /**
     * Creates a Child node or a Leaf.
     */
    protected abstract void splitDataSetAndCreateChildNodes();

    /**
     * Grow a tree
     *
     * Select a row, split it and creates child nodes
     * 
     */
    public void grow() {
        calculateTheGiniCriteriaForEachRows();
        splitDataSetAndCreateChildNodes();
    }

    /**
     * Stratified bootstrap.
     *
     * we first get the group (A or B) with the minimum number of samples, then
     * randomly select with replacement the same number of sample from A and B.
     *
     * Also,the same sample can not be selected every time and at least one
     * sample has to be out of the bag for each group.
     *
     * @param sil
     * @return
     */
    protected List<Index_Label> bootstrap(Sample_Index_Label sil) {

        int smallest_list_size;
        List<Index_Label> il = new ArrayList<Index_Label>();

        // stratified bootstrap
        Map<String, List<Integer>> label_indexs = new HashMap<String, List<Integer>>(Data.label_indexs);
        for (String l : label_indexs.keySet()) {
            Collections.shuffle(label_indexs.get(l));
        }

        smallest_list_size = Integer.MAX_VALUE;
        for (List l : label_indexs.values()) 
            if (l.size() < smallest_list_size)
                smallest_list_size = l.size();

        for (int i = 0; i < smallest_list_size; i++) {
            for (String l : label_indexs.keySet()) {
                int randint = (int) (Math.random() * (label_indexs.get(l).size() - 1));
                il.add(new Index_Label(label_indexs.get(l).get(randint), l));
            }
        }

        Collections.shuffle(il);
        
//        List<Index_Label> il = new ArrayList<Index_Label>();
//        
//        for (int i = 0; i < 9; i++) {
//            il.add(new Index_Label(sil.index.get(i)-1, sil.label.get(i)));
//            
//        }

        return il;

    }

    /**
     * Use to decide if a child node or a leaf is going to be created.
     *
     * A leaf is created if the total nb of label (bin size) is smaller or equal
     * than the the minimum bin size defined by the user (default value is 2) or
     * if the bin does not contain label A or label B (bin is pure)
     *
     * @param label - The number of labels A and B of a potential child;
     * @return True is the stop conditions are verified
     */
    protected static boolean checkStopCondition(Map<String, Integer> label) {
        // check bin size
        int nb_of_labels = 0;
        for (Integer count : label.values()) {
            nb_of_labels += count;
        }

        if (nb_of_labels <= Conf.MIN_BIN_SIZE)
            return true;
        
        return label.size() == 1;
    }

    /**
     * Randomly selects rows.
     *
     * The number of row to select is based on the mtry (default: sqrt(initial
     * nb of rows))
     *
     * @param length Number of rows in the initial dataset
     * @return
     */
    protected List<Integer> randomRows(int length) {
        List<Integer> randomRows = new ArrayList<Integer>();
        for (int i = 0; i < length; i++) {
            randomRows.add(i);
        }
        if(randomRows.size() - Conf.NB_OF_RANDOM_ROW > Conf.NB_OF_RANDOM_ROW){
            randomRows.removeAll(Conf.KNOWN_VARIABLES);
            Collections.shuffle(randomRows);
            randomRows = randomRows.subList(0, Conf.NB_OF_RANDOM_ROW);
        } else {
            randomRows.removeAll(Conf.KNOWN_VARIABLES);
        }
        return randomRows;
    }

    /**
     * Determines if the current Tree is a leaf or not.
     *
     * A leaf has only 1 field, label
     *
     * @return True if this Tree is a leaf
     */
    public boolean isLeaf() {
        return isLeaf;
    }

    /**
     *
     * @return The left child
     */
    public Tree getLeftChild() {
        return leftChild;
    }

    /**
     *
     * @return
     */
    public Tree getRightChild() {
        return rightChild;
    }

    /**
     *
     * @return
     */
    public int getRow() {
        return row;
    }

    /**
     *
     * @return
     */
    public double getGain() {
        return gain;
    }

    /**
     *
     * @return
     */
    public double getCutOffVal() {
        return cut_off_val;
    }

    /**
     *
     * @return A or B
     */
    public String getLabel() {
        return label;
    }
    
    public JSONObject toJSON(){
        JSONObject obj = new JSONObject();
        if(!this.isLeaf()){
            obj.put("gene", Data.annotations[this.row]);
            obj.put("exp", this.cut_off_val);
            obj.put("left", this.leftChild.toJSON());
            obj.put("right", this.rightChild.toJSON());
            obj.put("rule", "mineq-left");
        } else {
            obj.put("label", this.label);
        }
        return obj;
    }


}
