
import com.beust.jcommander.JCommander;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import src.Conf;
import src.Data;
import src.Forest;
import src.OptionsParser;
import src.SplitInfo;
import struct.Index_Label_Expression;
import struct.MutableInt;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author mickael
 */
public class test_split {
    
    
    public static void main(String[] args) throws IOException{
        
        System.out.println("---------------------------------------");
        System.out.println("Start");

        OptionsParser options = checkCmdParameters(args);
        String fileName = options.files.get(0).substring(options.files.get(0).lastIndexOf("/") + 1);

        new Conf(options);
        new Data(options);

        
    for (int i = 0; i < 10; i++) {

                List<Index_Label_Expression> ile = new LinkedList<Index_Label_Expression>();
                for (int j = 0; j < Data.sil.size(); j++) {
                    ile.add(new Index_Label_Expression(j, Data.sil.label.get(j), Data.m[i][j]));
                }

                SplitInfo si = new SplitInfo(ile, i);

                System.out.print(si.getGain());
                System.out.println(" " + si.getGain_idx());
            }
}

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
