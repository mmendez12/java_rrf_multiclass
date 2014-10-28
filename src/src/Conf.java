package src;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Global variables and methods
 * @author mmendez
 */
public class Conf {
    public static int count_split_info = 0;
    /**
     * Stop condition:
     * Defines the minimum number of sample than can be used to grow a tree.
     */
    public static int MIN_BIN_SIZE;
    
    /**
     * Penalty to apply to the gain of a row which is not in the F vector
     * when trying to find the best row to split a node.
     */
    public static double[] PENALTY;
    
    /**
     * mtry:
     * Defines the number of row to test when splitting a node.
     */
    public static int NB_OF_RANDOM_ROW;
    
    /**
     * F Vector:
     * Stores the index of the row previously used to split a node.
     * Can be empty or initialize by the user before building the forest
     * 
     */
    public static Set<Integer> KNOWN_VARIABLES;
    
    /**
     * The number of tree in the forest.
     */
    public static int NB_OF_TREE;
    
    /**
     * Controls the weight of the normalized importance score.
     */
    public static double importance_coefficient;
    
    /**
     * Rows to exclude because they are perfect.
     */
    
    public static List<Integer> to_exclude;
    
    /**
     * Calculates the classification accuracy for each features and for each class.
     */
    public static boolean CLASSIFICATION_PER_CLASS;
    
    /**
     * Throw the oob samples in the trees while randomly permuting the value of the variable m for each oob samples.
     */
    public static boolean RAW;
    static boolean SAVE_FOREST;
    
    /**
     * Initialize the attributes based on the user input.
     * @param options
     */
    public Conf(OptionsParser options) {
        MIN_BIN_SIZE = options.max_bin_size;
        KNOWN_VARIABLES = new HashSet<Integer>();
        to_exclude = new ArrayList<Integer>();
        NB_OF_TREE = options.nb_of_tree;
        CLASSIFICATION_PER_CLASS = options.cpc;
        RAW = options.raw;
        SAVE_FOREST = options.save_forest;
//        importance_coefficient = options.importance_coefficient;
    }

    /**
     * Initialize the nb of random row to test when splitting a node.
     * @param nb_of_row the total nb of row in the expression data
     */
    public static void setNB_OF_RANDOM_ROW(int nb_of_row) {

        NB_OF_RANDOM_ROW = (int) (Math.sqrt(nb_of_row));
        if (NB_OF_RANDOM_ROW < 2)
            NB_OF_RANDOM_ROW = 2;
    }

    /**
     * Given a certain time that's elapsed, return a string representation of
     * that time in hr,min,s
     *
     * @param timeinms	the beginning time in milliseconds
     * @return	the hr,min,s formatted string representation of the time
     */
    public static String TimeElapsed(long timeinms) {
        int s = (int) (System.currentTimeMillis() - timeinms) / 1000;
        int h = (int) Math.floor(s / ((double) 3600));
        s -= (h * 3600);
        int m = (int) Math.floor(s / ((double) 60));
        s -= (m * 60);
        return "" + h + "hr " + m + "m " + s + "s";
    }

   
    /**
     * Searches in a list l if an elements contains the substring s.
     * @param l String list
     * @param s the substring to find
     * @return true if any element of l contains the substring s
     */
    public static boolean containsSubstring(List<String> l, String s){
        for(String key : l)
            if(s.contains(key))
                return true;
        return false;
    }

    
    public static void writeToFile(String output_file, String content){
    FileOutputStream fop = null;
		File file;
		
 
		try {
 
			file = new File(output_file);
			fop = new FileOutputStream(file);
 
			// if file doesnt exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			}
 
			// get the content in bytes
			byte[] contentInBytes = content.getBytes();
 
			fop.write(contentInBytes);
			fop.flush();
			fop.close();
 
			System.out.println("Tree saved to JSON: " + output_file);
 
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (fop != null) {
					fop.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
    }
    
    
}

