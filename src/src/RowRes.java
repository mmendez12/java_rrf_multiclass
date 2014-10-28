/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package src;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import struct.ClassificationStats;


/**
 * Stores row informations to calculate the accuracy mean and stdev.
 * @author mmendez
 */
public class RowRes {

    /**
     * Decrease of impurity when selecting this row to split a node
     */
    private double gain;

    /**
     * Stores the sum of the absolute difference between the number of oob
     * samples correctly classified in this tree and number of oob samples
     * correctly classified with permuted values
     */
    private double s1;
    
    /**
     * Stores the sum of the squared absolute difference between the number of
     * oob samples correctly classified in this tree and number of oob samples
     * correctly classified with permuted values
     */
    private double s2;
    
    /**
     * Number of well classified oob samples when permuting values of this row
     */
    private int permuted_correct_classification;
    
    /**
     * Number of time that this row is used to split a node
     */
    private int usedCount;
    
    private Map<String, ClassificationStats> classificationStat;
    
    /**
     *init
     */
    public RowRes() {
        usedCount = 0;
        gain = 0.0;
        s1 = 0.0;
        permuted_correct_classification = 0;
        classificationStat = new HashMap<String, ClassificationStats>();
    }

    /**
     *
     * @param giniD
     */
    public RowRes(double giniD) {
        this();
        this.gain = giniD;
    }

    /**
     *
     * @param tree_correct_classification
     */
    public void storeClassificationDifference(int tree_correct_classification) {
        int difference = Math.abs(tree_correct_classification - permuted_correct_classification);
        s1 += difference;
        s2 += Math.pow(difference, 2);
        permuted_correct_classification = 0;
    }

    /**
     *
     * @return
     */
    public double getStandardDeviation() {

        double dividende, diviseur, s0 = usedCount;
        
        dividende = s0 * s2 - Math.pow(s1, 2);
        diviseur = s0 * (s0 - 1);

        if (diviseur == 0.0) {
            return 0.0;
        } else {
            return Math.sqrt(dividende / diviseur);

        }
    }
    
    /**
     * Investigate on the usage of the variable when classifying all the samples.
     * @param label
     * @param cc 
     */
    public void classificationStat(String label, boolean cc) {
        if (!this.classificationStat.containsKey(label)){
            classificationStat.put(label, new ClassificationStats(label));
        }
        classificationStat.get(label).addStat(cc);
    }

    /**
     *
     * @return
     */
    public double getMean() { // RAW_SCORE
        return s1 / usedCount;
    }

    /**
     *
     * @param gain
     */
    public void addGain(double gain) {
        this.gain += gain;
    }

    public String getClassificationStats(){
        DecimalFormat df = new DecimalFormat("0.00");
        StringBuilder sb = new StringBuilder();
        
        if(classificationStat.isEmpty()){
            return "Not seen";
        }
        
        for (ClassificationStats cs : classificationStat.values()) {
            sb.append(cs.getLabel());
            sb.append(":");
            sb.append(df.format(cs.percentageOfCorrectClassification()));
            sb.append("%,");
        }
        sb.deleteCharAt(sb.length()-1);
        
        return sb.toString();
    }
    
    
    /**
     *
     */
    public void addPermutedCC() {
        permuted_correct_classification++;
    }

    /**
     *
     * @param cc
     */
    public void addPermutedCC(int cc) {
        this.permuted_correct_classification += cc;
    }

    /**
     *
     */
    public void addUsedCount() {
        usedCount++;
    }

    /**
     *
     * @param usedCount
     */
    public void addUsedCount(int usedCount) {
        this.usedCount += usedCount;
    }

    /**
     *
     * @param s1
     */
    public void addS1(double s1) {
        this.s1 += s1;
    }

    /**
     *
     * @param s2
     */
    public void addS2(double s2) {
        this.s2 += s2;
    }

    /*
     * Getters
     */
    /**
     *
     * @return
     */
    public int getUsedCount() {
        return usedCount;
    }

    /**
     *
     * @return
     */
    public double getS1() {
        return s1;
    }

    /**
     *
     * @return
     */
    public double getS2() {
        return s2;
    }
    
    /**
     *
     * @return
     */
    public double getGiniD() {// Gini importance if divided by the used count
        return gain;
    }

    

}
