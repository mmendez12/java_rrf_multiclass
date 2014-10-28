/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package src;

import struct.MutableInt;
import struct.Index_Label_Expression;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import struct.Index_Label;

/**
 * Calculates the best split for a row and stores the results.
 * @author mmendez
 */
public class SplitInfo {
    private double gain;
    private double gini;
    private int row;
    private int gain_idx;
    private double cut_off_val;
    private List<Index_Label> left_index;
    private List<Index_Label> right_index;
    private Map<String, Integer> count_label_left;
    private Map<String, Integer> count_label_right;

    /**
     * Init attributes.
     */
    public SplitInfo() {
        left_index = new LinkedList<Index_Label>();
        right_index = new LinkedList<Index_Label>();
        gain = Double.MIN_VALUE;
    }   

    /**
     * Try to find the best split in a row.
     * 
     * @param ile_l index label expression
     * @param row
     */
    public SplitInfo(List<Index_Label_Expression> ile_l, int row) {
        
        this();
        this.row = row;
        Conf.count_split_info++;
        Map<String, MutableInt> label_left = new HashMap<String, MutableInt>();
        Map<String, MutableInt> label_right = new HashMap<String, MutableInt>();
        count_label_left = new LinkedHashMap<String, Integer>();
        count_label_right = new LinkedHashMap<String, Integer>();
        HashMap<String, MutableInt> label_count = new HashMap<String, MutableInt>();
        Collections.shuffle(ile_l);
        Collections.sort(ile_l);

        // get number of label A and B
        for (Index_Label_Expression ile: ile_l)
            if (label_count.containsKey(ile.label)) {
                label_count.get(ile.label).inc();
            } else {
                label_count.put(ile.label, new MutableInt(1));
            }

        // calculate gini the gini index
        gini = gini_index(label_count);

        label_right.putAll(label_count);
        // for each possible split :
        for (int i = 0; i < ile_l.size() - 1; i++) {
            //take the i-th elemement from label_right and add it to label_left
            if (label_left.containsKey(ile_l.get(i).label)) {
                label_left.get(ile_l.get(i).label).inc();
            } else {
                label_left.put(ile_l.get(i).label, new MutableInt(1));
            }
            if (label_right.get(ile_l.get(i).label).toInt() == 1) {
                label_right.remove(ile_l.get(i).label);
            } else{
                label_right.get(ile_l.get(i).label).dec();
            }
            

            // Then calculate gini gain
            double giniL = gini_index(label_left);
            double giniR = gini_index(label_right);
            
            double current_gain = gini;
            current_gain -= giniL * (i + 1) / ile_l.size();
            current_gain -= giniR * (ile_l.size() -i -1) / ile_l.size();

            if (current_gain > gain) {
                gain = current_gain;
                gain_idx = i;
                
                count_label_left = new LinkedHashMap<String, Integer>();
                count_label_right = new LinkedHashMap<String, Integer>();
                for (Entry<String, MutableInt> entry : label_left.entrySet()) {
                    count_label_left.put(entry.getKey(), entry.getValue().get());
                }
                for (Entry<String, MutableInt> entry : label_right.entrySet()) {
                    count_label_right.put(entry.getKey(), entry.getValue().get());
                }
            }
        }

        cut_off_val = ile_l.get(gain_idx).expression;

        for (int i = 0; i <= gain_idx; i++)
            left_index.add(ile_l.get(i).getIndex_Label());

        for (int i = gain_idx + 1; i < ile_l.size(); i++)
            right_index.add(ile_l.get(i).getIndex_Label());
    
    }
    
    /**
     * Calculates the gini index impurity criteria
     * @param label_count map with key = label and value = nb of labels
     * @return the gini index
     * 
     */

    private double gini_index(Map<String, MutableInt> label_count) {
    double gini_index = 1.0;
    int len_samples = 0;
    
    for (MutableInt count : label_count.values()) {
        len_samples += count.get();
    }
    
    for (MutableInt count : label_count.values()) {
        if (count.get() == 0) {
            continue;
        }
        gini_index -= Math.pow(((double) (count.get()) / (len_samples)), 2);
    }
    
    return gini_index;
    }

    /*
     * getters
     */
    /**
     *
     * @return
     */
    public double getGain() {
        return gain;
    }
    
    void penalty(double penalty) {
        gain *=  penalty;
    }

    /**
     *
     * @return
     */
    public int getGain_idx() {
        return gain_idx;
    }

    /**
     *
     * @return
     */
    public double getGini() {
        return gini;
    }

    /**
     *
     * @return
     */
    public double getCut_off_val() {
        return cut_off_val;
    }

    /**
     * Retrieve the index_label
     * @return
     */
    public List<Index_Label> getLeft_index_label() {
        return left_index;
    }

    /**
     * 
     * @return
     */
    public List<Index_Label> getRight_index_label() {
        return right_index;
    }

    /**
     *
     * @return
     */
    public Map<String, Integer> getCount_left() {
        return count_label_left;
    }

    /**
     *
     * @return
     */
    public Map<String, Integer> getCount_right() {
        return count_label_right;
    }
    
    /**
     *
     * @return
     */
    public String getLabelLeft(){
        return getLabel(count_label_left);
    }
    
    /**
     *
     * @return
     */
    public String getLabelRight(){
        return getLabel(count_label_right);
    }
    
    public int getRow() {
        return row;
    }

    /**
     * Defines if a Node is labeled as A or B.
     * @param label_count The number of labels A and the number of labels B
     * @return The max between A and B or randomly A or B if the counts for A and B is the same
     */
    private String getLabel(Map<String, Integer> label_count) {
        int max = Collections.max(label_count.values());
        String label = "";
        
        for (Entry<String, Integer> e : label_count.entrySet()) {
            if (e.getValue() == max) {
                label = e.getKey();
            }
        }
        
       return label;

    }




}
