/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package src;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import static src.Tree.checkStopCondition;
import struct.Index_Label;
import struct.TreeInfo;

/**
 *
 * @author mmendez
 */
public class Rtree extends Tree {

    /**
     * Creates a root node.
     *
     * The root node initialyse the bootstrap the oob index and the the column
     * index which are respectively the indexes of the samples used to split a
     * node, the indexes of the oob samples (use to calculate the acurracy), and
     * a copy of the bootstrap indexes. A sublist of column index is used to
     * create a left or right child.
     *
     */
    public Rtree() {
        TreeInfo.split_info = new SplitInfo();
        TreeInfo.tree_index_label = new LinkedList<Index_Label>();

        TreeInfo.bootstrap_index_label = bootstrap(Data.sil);
        TreeInfo.oob_index = new ArrayList<Integer>();

        for (Index_Label bootstrap_index_label : TreeInfo.bootstrap_index_label) {
            TreeInfo.tree_index_label.add(bootstrap_index_label);
        }

        // fill oob_index
        for (int i = 0; i < Data.sil.size(); i++) {
            TreeInfo.oob_index.add(i);
        }
        TreeInfo.oob_index.removeAll(TreeInfo.tree_index_label);
        TreeInfo.isRoot = true;


    }

    /**
     * Creates a Child
     *
     * @param col_index index of the columns to be read in this child
     */
    protected Rtree(List<Index_Label> col_index) {
        TreeInfo.split_info = new SplitInfo();
        TreeInfo.tree_index_label = col_index;
        TreeInfo.isRoot = false;
    }

    /**
     * Creates a Leaf
     *
     * @param label the label
     */
    protected Rtree(String label) {
        this.isLeaf = true;
        this.label = label;
        TreeInfo.isRoot = false;
    }

    @Override
    protected void calculateTheGiniCriteriaForEachRows() {

        SplitInfo best_row_within_random_rows;
        SplitInfo best_row_within_used_rows = findBestRow(Conf.KNOWN_VARIABLES, new SplitInfo(), false);
        List<Integer> randomRows = randomRows(Data.m.length);
        randomRows.removeAll(Conf.to_exclude);
        best_row_within_random_rows = findBestRow(randomRows, best_row_within_used_rows, true);

        if (best_row_within_random_rows.getGain() > best_row_within_used_rows.getGain()) {
            TreeInfo.split_info = best_row_within_random_rows;
        } else {
            TreeInfo.split_info = best_row_within_used_rows;
        }

        gain = TreeInfo.split_info.getGain();
        cut_off_val = TreeInfo.split_info.getCut_off_val();
        row = TreeInfo.split_info.getRow();
        Conf.KNOWN_VARIABLES.add(row);


    }

    @Override
    protected void splitDataSetAndCreateChildNodes() {
        SplitInfo si = TreeInfo.split_info;
        
        if (checkStopCondition(si.getCount_left())) //create a leaf
        {
            leftChild = new Rtree(si.getLabelLeft());
        } else {
            //create a child
            leftChild = new Rtree(si.getLeft_index_label());
            leftChild.grow();
        }

        if (checkStopCondition(si.getCount_right())) // create a leaf
        {
            rightChild = new Rtree(si.getLabelRight());
        
        } else {
            // create a child
            rightChild = new Rtree(si.getRight_index_label());
            rightChild.grow();
        }
    }
}
