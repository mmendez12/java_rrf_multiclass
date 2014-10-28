package src;

import struct.MutableInt;
import java.util.*;
import java.util.Map.Entry;
import struct.TreeInfo;

/**
 * Stores results for each tree and row
 * @author mmendez
 */
public class ForestRes {

    /**
     * Stores results for each row used to split a node.
     */
    private Map<Integer, RowRes> row_res;
    
    /**
     * Counts the nb of trees generated. starts from 0.
     */
    private int treeNum;
    
    /**
     * For each tree keep track of the number of well classified oob sample.
     */
    private int[] correct_classification_per_tree;
    
    /**
     * Number of time a row is used to split a node.
     */
    private Map<Integer, MutableInt> usedRowCount;
    
    /**
     * For each sample, stores the number of time that oob samples drops in a Leaf.
     * 
     */
    private Map<String, int[]> drops_in;
    
 
    /**
     * How well the forest can classify samples.
     */
    private double accuracy;
    
    /**
     * For each Label, stores the label of the node in which it falls the more often.
     */
    private String[] predicted_label;
    
    /**
     * A ratio between the number of time the sample is well classified or not.
     */
    private double[] prediction_percentage;

    /**
     * Init.
     * 
     */
    public ForestRes() {
        this.row_res = new LinkedHashMap<Integer, RowRes>();
//        this.treeList = new LinkedList<Tree>();
        this.treeNum = -1;
        this.correct_classification_per_tree = new int[Conf.NB_OF_TREE];
        this.usedRowCount = new LinkedHashMap<Integer, MutableInt>();
        this.drops_in = new HashMap<String, int[]>();
        this.predicted_label = new String[Data.sil.size()];
        this.prediction_percentage = new double[Data.sil.size()];
        this.accuracy = 0.0;
        
        for (String label: Data.label_indexs.keySet())
            this.drops_in.put(label, new int[Data.sil.size()]);
    }

    /**
     * Run through the tree and combined results with previous trees.
     * 
     * @param tree the root of this tree
     * @param usedRow the indexes of the rows used to create child node
     */
    public void addRes(Tree tree, List<Integer> usedRow) {
        if (tree.isLeaf()) {
            return;
        }

        if (row_res.containsKey(tree.getRow())) {
            row_res.get(tree.getRow()).addGain(tree.getGain());
            row_res.get(tree.getRow()).addUsedCount();
        } else {
            row_res.put(tree.getRow(), new RowRes(tree.getGain()));
            row_res.get(tree.getRow()).addUsedCount();
        }

        usedRow.add(tree.getRow());

        if (usedRowCount.containsKey(tree.getRow())) {
            usedRowCount.get(tree.getRow()).inc();
        } else {
            usedRowCount.put(tree.getRow(), new MutableInt());
        }

        addRes(tree.getLeftChild(), usedRow);
        addRes(tree.getRightChild(), usedRow);



    }

    /**
     * Keep a trace of the tree created.
     * 
     * @param tree the root of this tree
     */
//    public void addTree(Tree tree) {
//        treeList.add(tree);
//    }

    /**
     * Throw the out of the bag samples in the tree
     * @param tree the root node
     */
    public void oobCall(Tree tree) {
        treeNum++;

        // throw vector in the tree
        //run through bootstrapCount

        for (Integer labelIndex: TreeInfo.oob_index) {
          
                oobProcess(labelIndex, tree);
        }
    }

    /**
     * See oobCall(Tree)
     * 
     * Find the leaf node and prepare the calculation of the accuracy
     * @param labelIndex index of this label
     * @param tree A tree node
     */
    private void oobProcess(int labelIndex, Tree tree) {
        if (!tree.isLeaf()) {
            if (Data.m[tree.getRow()][labelIndex] <= tree.getCutOffVal()) {
                oobProcess(labelIndex, tree.getLeftChild());
            } else {
                oobProcess(labelIndex, tree.getRightChild());
            }
        } else { // if dt is leaf
            if (tree.getLabel().equals(Data.sil.label.get(labelIndex))) {
                correct_classification_per_tree[treeNum]++;
            }
            drops_in.get(tree.getLabel())[labelIndex]++;

        }
    }

    /**
     * Throw the out of the bag samples in the tree and permute expression value to 
     * get information about the variation in the accuracy when perturbing the data.
     * 
     * @param tree the last tree that has been grown
     * @param usedRow used rows to build this tree
     */
    public void oobPermutedCall(Tree tree, List<Integer> usedRow) {

        // throw vector in the tree
        for (Integer i: TreeInfo.oob_index) {
                float sample[] = Data.getExpressionBySample(i);
                for (int j = 0; j < Data.annotations.length; j++) {
                    if (!usedRow.contains(j)) {
                        continue;
                    }
                    // permute value with a random value in the same var
                    sample[j] = Data.m[j][(int) (Math.random() * Data.sil.size())];
//                    // throw vector in the tree
                    oobPermutedProcess(i, tree, j, sample);

                }
        }

        int oobCorrectCorrelation = correct_classification_per_tree[treeNum];

        for (Entry<Integer, RowRes> mapEntry : row_res.entrySet()) {
            if (!usedRow.contains(mapEntry.getKey())) {
                continue;
            }
            mapEntry.getValue().storeClassificationDifference(oobCorrectCorrelation);
        }
    }

    /**
     * Find the leaf and collect the data
     * @param i sample index
     * @param tree the current node
     * @param row row index
     * @param sample the expression value for this sample
     */
    private void oobPermutedProcess(int i, Tree tree, int row, float[] sample) {
        if (!tree.isLeaf()) {
            if (sample[tree.getRow()] <= tree.getCutOffVal()) {
                oobPermutedProcess(i, tree.getLeftChild(), row, sample);
            } else {
                oobPermutedProcess(i, tree.getRightChild(),row, sample);
            }
        } else if (tree.getLabel().equals(Data.sil.label.get(i))) {
                row_res.get(row).addPermutedCC();
        }
    }
    
    /**
     * Check how many of the oob samples fall in a node labeled as the sample.
     */
    public void calculateAccuracy() {
        String majorityLabel = "NA";
        int majorityDrops, nb_of_oob, nb_of_non_oob_samples = 0;
        for (int i = 0; i < Data.sil.size(); i++) {
            majorityDrops = 0; nb_of_oob = 0;
            
            for (Entry<String, int[]> e: drops_in.entrySet()){
                nb_of_oob += e.getValue()[i];
                if (e.getValue()[i] > majorityDrops){
                    majorityDrops = e.getValue()[i];
                    majorityLabel = e.getKey();
                }
            }
            
            predicted_label[i] = majorityLabel;
            try {
                prediction_percentage[i] = (majorityDrops / (double) nb_of_oob) * 100;
            } catch (Exception e){
                prediction_percentage[i] = 0.0;
                System.out.println("division by zero: " + (double)nb_of_oob);
            }

            if (majorityLabel.equals(Data.sil.label.get(i))) {
                accuracy++;
            } else if (majorityLabel.equals("NA")) {
                nb_of_non_oob_samples++;
            }
        }
        
        accuracy = (accuracy / (double) (Data.sil.size() - nb_of_non_oob_samples)) * 100;
    }
    
        void variableToLabelStats(Tree tree) {
            for (int i = 0; i < Data.sil.size(); i++) {
                variableToLabelStatsProcess(tree, i);
            }
        }
        
        boolean variableToLabelStatsProcess(Tree tree, int sampleIndex){
            boolean cc = false; //correct classification
            if (!tree.isLeaf()) {
                if (Data.m[tree.getRow()][sampleIndex] <= tree.getCutOffVal()) {
                    cc = variableToLabelStatsProcess(tree.getLeftChild(), sampleIndex);
                } else {
                    cc = variableToLabelStatsProcess(tree.getRightChild(), sampleIndex);
                }
                row_res.get(tree.getRow()).classificationStat(Data.sil.label.get(sampleIndex), cc);
            } 
            if (tree.isLeaf() && tree.getLabel().equals(Data.sil.label.get(sampleIndex))) {
                cc = true;
            }
            return cc;

       }
    

    /*
     * getters
     */
    /**
     *
     * @return
     */
    public Map<Integer, RowRes> getResMap() {
        return row_res;
    }

    /**
     *
     * @return
     */
    public Map<Integer, MutableInt> getUsedRowCount() {
        return this.usedRowCount;
    }

    /**
     * 
     * @return 
     */
    public Map<String, int[]> getDrops_in() {
        return drops_in;
    }
   
    /**
     *
     * @return
     */
    public double getAccuracy() {
        return accuracy;
    }

    /**
     *
     * @return
     */
    public String[] getPredicted_label() {
        return predicted_label;
    }

    /**
     *
     * @return
     */
    public double[] getPrediction_percentage() {
        return prediction_percentage;
    }


    
}
