/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package src;

import au.com.bytecode.opencsv.CSVReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import struct.Sample_Index_Label;

/**
 * Loads and stores data informations
 *
 * @author mmendez
 */
public final class Data {


    /**
     * Expression matrice, m[row][col]
     */
    public static float[][] m;
    
    /**
     *  List of row identifier (the first column in the dataset)
     */
    public static String[] annotations;

    /**
     * list of labels (A or B), samples name and indexes as they
     * appear in the dataset. 
     * 
     * The index is the original index of the sample in the dataset,
     * before filtering, it is used to get the expression values.
     */
    public static Sample_Index_Label sil = new Sample_Index_Label();
    
    /**
     * Stores the sample index per label.
     */
    public static Map<String, List<Integer>> label_indexs = new HashMap<String, List<Integer>>();
    
    /**
     * Load the expression table into the memory
     *
     * @param options command-line options
     */
    public Data(OptionsParser options) throws FileNotFoundException, IOException {
        
        int row_count = 0;
        int eol = options.eol ? 1 : 0;
        Set<String> labels = new TreeSet<String>();
        String[] nextLine = null;
        String[] header = null;
        String[] samples_name = null;
        ArrayList<Integer> samples_to_keep = new ArrayList<Integer>();
        HashMap<Integer, Integer> old_to_new_indexLabel = new HashMap<Integer, Integer>();
        File file = new File(options.files.get(0));
        FileReader fr = null;
        
        CSVReader csvReader;

        try {
            fr = new FileReader(file);
            csvReader = new CSVReader(fr, '\t');

            header = csvReader.readNext();
//            System.out.println(header[1]);
            samples_name = new String[header.length];
            System.arraycopy(header, 0, samples_name, 0, header.length);
//            System.out.println(samples_name[1]);
            while ((csvReader.readNext()) != null) {
                row_count++;
            }

        } catch (FileNotFoundException ex) {
            System.out.println("Cannot find dataSet : " + file.getName());
            System.exit(1);

        } catch (IOException ex) {
            Logger.getLogger(Data.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        if (!options.labelsA.isEmpty()) { // what is not A is B
            for (int i = 1; i < header.length - eol; i++) {
                if (Conf.containsSubstring(options.labelsA, header[i])) {
                    header[i] = "A";
//                    count ++;
                } else if (Conf.containsSubstring(options.labelsB, header[i])) {
                    header[i] = "B";
                } else {
                    header[i] = "skip";
                }
            } 
        }
        
        if (!options.labels.isEmpty()) { 
            header = options.labels.split(",");
        }
        
        
        
        int count = 1;
        for (int i = 1; i < header.length - eol; i++){
            if(header[i].equals("skip")) continue;
            old_to_new_indexLabel.put(i, count);
            count++;
        }

        //get labels
        for (int i = 1; i < header.length - eol; i++){
            if(header[i].equals("skip")) continue;
            labels.add(header[i]);
            samples_to_keep.add(i);
        }

        
        //init label_indexs
        for (String label : labels)
            label_indexs.put(label, new ArrayList<Integer>());
        
        if (label_indexs.size() <= 1) {
            System.out.println("at least 2 classes should be defined");
            System.exit(0);
        }
        
        for (int i = 1; i < header.length - eol; i++) {
            if(header[i].equals("skip")) continue;
            sil.add(samples_name[i], old_to_new_indexLabel.get(i), header[i]);
            label_indexs.get(header[i]).add(old_to_new_indexLabel.get(i)-1);
        }
        
        Conf.PENALTY = new double[row_count];
        Arrays.fill(Conf.PENALTY, options.penalty);
        m = new float[row_count][sil.size()];

        // read expression table
        try {
            fr = new FileReader(file);
            csvReader = new CSVReader(fr, '\t');

            annotations = new String[row_count];
//            System.out.println(row_count);
            row_count = 0;
            
            try {
                // skip header
                csvReader.readNext();
            } catch (IOException ex) {
                Logger.getLogger(Data.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            // read expression able
            while ((nextLine = csvReader.readNext()) != null) {
                annotations[row_count] = nextLine[0];
                
//                if(annotations[row_count].equals("prow")){
//                    System.out.println("");
//                }
                for (int i = 0; i < samples_to_keep.size(); i++) {
                    m[row_count][i] = Float.parseFloat(nextLine[samples_to_keep.get(i)]);
                }

                
                if (options.known_variables.contains(annotations[row_count])) {
                    Conf.KNOWN_VARIABLES.add(row_count);
                }

                row_count++;
            }
//            System.out.println(row_count);
            // filter array if gene mean exp ==0
//            ArrayList<Integer> keep = new ArrayList<Integer>();
//            for (int i = 0; i < m.length; i++) {
//
//                double result = 0.;
//                for(double m2: m[i]){
//                    result += m2;
//                }
//                if(result > 0.){
//                    keep.add(i);
//                }
//            }
//            row_count = keep.size();
            Conf.setNB_OF_RANDOM_ROW(row_count);
            Conf.PENALTY = new double[row_count];
            Arrays.fill(Conf.PENALTY, options.penalty);
    

        } catch (FileNotFoundException ex) {
            System.out.println("Cannot find dataSet : " + file.getName());
            System.exit(1);
        } catch (IOException ex) {
            Logger.getLogger(Data.class.getName()).log(Level.SEVERE, null, ex);
        }
    }


    /**
     * Get the expression values based on a sample and not based on a row.
     * 
     * @param sample_index index of the column (sample) to be return
     * @return an array of expression values
     */
    public static float[] getExpressionBySample(int sample_index) {

        float[] expression = new float[m.length];

        for (int i = 0; i < m.length; i++) {
            expression[i] = m[i][sample_index];
        }

        return expression;
    }
}
