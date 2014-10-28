/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package src;

import com.beust.jcommander.Parameter;
import java.util.ArrayList;
import java.util.List;

/**
 * Parser for command-line options 
 * @author mmendez
 */
public class OptionsParser {

    /**
     *
     */
    @Parameter(description = "file", arity = 1, required = true)
    public List<String> files = new ArrayList<String>();
    
    /**
     *
     */
    @Parameter(names = {"-ir", "init.rows"}, description = "initialyze a set of variable for Regularized Random Forest", variableArity = true)
    public List<String> known_variables = new ArrayList<String>();
    
    /**
     *
     */
    @Parameter(names = {"-eol","--end.of.line"}, description = "end of line character")
    public boolean eol = false;
     
    /**
     *
     */
    @Parameter(names = {"-h","--help"}, description = "Usage")
    public boolean help;
    
    /**
     *
     */
    @Parameter(names = {"-tr","--tree"}, description = "number of tree")
    public int nb_of_tree = 1000;
    
    /**
     *
     */
    @Parameter(names = {"-p","--penalty"}, description = "define the penalty when using Regularized Random Forest")
    public double penalty = 1.0;
    

    
    /**
     *
     */
    @Parameter(names = {"-dir","--directory"}, description = "specify directory where to store the result file")
    public String outdir = "" ;
    
    /**
     *
     */
    @Parameter(names = {"-s","--sort"}, description = "sort the results by :\n\t0 : gain\n\t1 : original row order")
    public int sort = 0;
    
    /**
     *
     */
    @Parameter(names = {"-mbs","--min.bin.size"}, description = "Stop to build the tree when the bin size in a child is <= than the user specification")
    public int max_bin_size = 2;
    
    /**
     *
     */
    @Parameter(names = {"-name"}, description = "specify the name of the run, will be used to create the result file name")
    public String run_name = "noname";

    /**
     *
     */
    @Parameter(names = {"-pf", "param.file"}, description = "path of the file that contain parameters")
    public String path = "";
    
    @Parameter(names = {"-l", "labels"}, description = "path of the file that contain the class labels")
    public String labels = "";
    
    /**
     *
     */
    @Parameter(names = {"-cn", "complete.name"}, description = "name of the CL")
    public String complete_name = "";
    
//    @Parameter(names = {"-ic", "importance.coefficient"}, description = "Controls the weight of the normalized importance")
//    public double importance_coefficient = 0.01;
    
     @Parameter(names = {"-prows", "perfect.rows"}, description = "exclude perfect rows from the mtry")
     public boolean prows = false;
     
     @Parameter(names = {"-cpc", "classification.per.class"}, description = "Indicates for each selected feature how accurates was the classification of each class when the feature was used to split a node")
     public boolean cpc = false;
     
     @Parameter(names = {"-raw", "raw.importance.score"}, description = "Throw the oob samples in the trees while randomly permuting the value of the variable m for each oob samples.")
     public boolean raw = false;
     
    @Parameter(names = {"-la","--label.A"}, description = "define the pattern in the name of the sample that will be used to tag the sample as A", variableArity = true)
    public List<String> labelsA = new ArrayList<String>();
    
    @Parameter(names = {"-lb","--label.B"}, description = "define the pattern in the name of the sample that will be used to tag the sample as B", variableArity = true)
    public List<String> labelsB = new ArrayList<String>();
    
    @Parameter(names = {"-sf", "--save.forest"}, description = "Save the forest")
    public boolean save_forest = false;
    
    
}
