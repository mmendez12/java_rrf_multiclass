/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package struct;

import java.util.List;
import src.SplitInfo;

/**
 *
 * @author mmendez
 */
public class TreeInfo {
    
    /**
     * Returns the index of the out of bag samples.
     * 
     * bootstrapping is done with replacement so duplicates indexes are allowed
     */
    public static List<Index_Label> bootstrap_index_label;
    public static List<Integer> oob_index;
    public static SplitInfo split_info;
    public static List<Index_Label> tree_index_label;
    public static boolean isRoot;
}
