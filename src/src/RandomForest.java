/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package src;

import com.beust.jcommander.JCommander;
import comparator.RowResGiniComparator;
import java.io.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import struct.MutableInt;

/**
 *
 * @author mmendez
 */
public class RandomForest {

    /**
     * Check the user inputs, initialize global attributes, load the data and
     * run regularized random forest and save the results.
     *
     * @param args the command line arguments and options
     */
    public static void main(String[] args) throws IOException {
//        System.out.println("original grrf");
        long time;
        Map<Integer, MutableInt> rowCount = new HashMap<Integer, MutableInt>();
        time = System.currentTimeMillis();

        System.out.println("---------------------------------------");
        System.out.println("Start");

        OptionsParser options = checkCmdParameters(args);
        String fileName = options.files.get(0).substring(options.files.get(0).lastIndexOf("/") + 1);

        new Conf(options);
        new Data(options);

        Forest forest = new Forest(options.prows);
//        System.out.println("si: "+Conf.count_split_info);
        System.out.println("selected rows: " + Conf.KNOWN_VARIABLES.size());
        saveRes(forest.getForestRes(), options.outdir + options.run_name + "RRF" + "--" + options.penalty + "--" + fileName, options.sort, options);
        
        System.out.println("Elapsed time: " + Conf.TimeElapsed(time));
        System.out.println("---------------------------------------");
        
        if (options.save_forest) {
            fileName = fileName.substring(0, fileName.lastIndexOf('.') + 1) + "json";
            String outfile = options.outdir + options.run_name + "RRF" + "--" + options.penalty + "--" + fileName;
            Conf.writeToFile(outfile, forest.getTreeToJSON());            
        }

        
    }

    /**
     * Write the results in a file.
     *
     * @param fres an instance of ForestRes
     * @param nomFic the name on the output file
     * @param sort 0 = sort by gain, 1 = sort by row name
     * @param options user input
     */
    private static void saveRes(ForestRes fres, String nomFic, int sort, OptionsParser options) {
    DecimalFormat df = new DecimalFormat("0.00");

        try {

            FileWriter fw = new FileWriter(nomFic, false);

            // le BufferedWriter output auquel on donne comme argument le FileWriter fw cree juste au dessus
            BufferedWriter output = new BufferedWriter(fw);

            String type = "RRF";
            String f_name = options.files.get(0);
            f_name = f_name.substring(f_name.lastIndexOf("/") + 1);

            /*
             * Writting general infornation in the header
             */
            output.write("name\t" + options.complete_name + "\n");
            output.write("type\t" + type + "\n");
            output.write("nb_of_tree\t" + Conf.NB_OF_TREE + "\n");
            output.write("file\t" + f_name + "\n");

            output.write("penalty\t" + options.penalty + "\n\n");

            output.write("accuracy\t" + fres.getAccuracy() + "\n" + "\n");
            output.write("Var_Name\tDeltaGini\tMean\tS_deviation\tUsed\tclassificationStats" + "\n");

            System.out.println("ACCURACY\t" + fres.getAccuracy() + "%" + "\n");


            String sep = "\t";
            List<Integer> keys = new ArrayList<Integer>(fres.getResMap().keySet());

            switch (sort) {
                case 0:
                    Collections.sort(keys, new RowResGiniComparator(fres.getResMap()));
                    break;
                case 1:
                    Collections.sort(keys);
                    break;
            }

            for (Integer id : keys) {

                StringBuilder sb = new StringBuilder();
                sb.append(Data.annotations[id]);
                sb.append(sep);
                sb.append(df.format(fres.getResMap().get(id).getGiniD()));
//                sb.append(df.format(fres.getResMap().get(id).getGiniD()/fres.getResMap().get(id).getUsedCount()));
                sb.append(sep);
                sb.append(df.format(fres.getResMap().get(id).getMean()));
                sb.append(sep);
                sb.append(df.format(fres.getResMap().get(id).getStandardDeviation()));
                sb.append(sep);
                sb.append(fres.getResMap().get(id).getUsedCount());
                sb.append(sep);
                sb.append(fres.getResMap().get(id).getClassificationStats());


                output.write(sb.toString() + "\n");
            }
//            output.write("\n\n");
//
//            output.write("sample_correlation\nsamples_name");
//            for (int i = 0; i < Data.sil.size(); i++) {
//                output.write("\t" + Data.sil.name.get(i));
//            }
//            
//            for (Entry<String, int[]> e: fres.getDrops_in().entrySet()) {
//                output.write("\n" + e.getKey());
//                for (int i = 0; i < e.getValue().length; i++) {
//                    output.write("\t" + e.getValue()[i]);
//                }
//            }
//            
//            output.write("\noriginal");
//            for (int i = 0; i < Data.sil.size(); i++) {
//                output.write("\t" + Data.sil.label.get(i));
//            }
//
//            output.write("\nprediction");
//            for (int i = 0; i < fres.getPredicted_label().length; i++) {
//                output.write("\t" + fres.getPredicted_label()[i]);
//            }
//
//            output.write("\npercentage");
//            for (int i = 0; i < fres.getPrediction_percentage().length; i++) {
//                output.write("\t" + fres.getPrediction_percentage()[i]);
//            }

            output.write("\n");
            output.flush();
            output.close();

            System.out.println("File generated : " + nomFic);
        } catch (IOException ioe) {
            System.out.print("Erreur : ");
        }
        

    }

    /**
     * Set the right options according to the user input
     *
     * @param args
     * @return
     */
    private static OptionsParser checkCmdParameters(String[] args) {
        OptionsParser cmd = new OptionsParser();
        JCommander jc = new JCommander(cmd, args);
        if (cmd.help) {
            jc.usage();
            return null;
        }

        if (cmd.files.size() > 1) {
            System.out.println("too many parameters");
            jc.usage();
            return null;
        }

        if (!cmd.outdir.equals("")) {
            if (!cmd.outdir.startsWith("/")) {
                cmd.outdir = System.getProperty("user.dir") + "/" + cmd.outdir;
            }
            if (!cmd.outdir.endsWith("/")) {
                cmd.outdir = cmd.outdir + "/";
            }

            File f = new File(cmd.outdir);
            if (!f.isDirectory()) {
                System.out.println("The directory doesn't exist " + cmd.outdir);
                System.exit(1);
            }

        }

        if (!cmd.path.isEmpty()) {
            File file = new File(cmd.path);
            FileReader fr = null;
            try {
                fr = new FileReader(file);
            } catch (FileNotFoundException ex) {
                System.out.println("Cannot find dataSet : " + file.getName());
                System.exit(1);

            }
            BufferedReader br = new BufferedReader(fr);

            String nextLine;

            try {
                int i = 0;
                String name = "";
                while ((nextLine = br.readLine()) != null) {
                    if (i == 0) {
                        if (cmd.run_name.equals("noname")) {
                            cmd.run_name = nextLine;
                        }
                    }
                    if (i == 1) {
                        cmd.complete_name = nextLine;
                    }
                    if (i == 2) {
                        cmd.labelsA = Arrays.asList(nextLine.split("\t"));
                    }
                    if (i == 3) {
                        cmd.labelsB = Arrays.asList(nextLine.split("\t"));
                    }
                    i++;
                }
            } catch (IOException ex) {
                Logger.getLogger(Data.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        
        if (!cmd.labels.isEmpty()) {
            File file = new File(cmd.labels);
            FileReader fr = null;
            try {
                fr = new FileReader(file);
            } catch (FileNotFoundException ex) {
                System.out.println("Cannot find label file : " + file.getName());
                System.exit(1);

            }
            BufferedReader br = new BufferedReader(fr);


            try {
                
                cmd.labels = br.readLine();
                
            } catch (IOException ex) {
                Logger.getLogger(Data.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        
        
        return cmd;
    }

    
}

