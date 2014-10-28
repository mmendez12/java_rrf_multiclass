/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package struct;

import java.util.ArrayList;
/**
 * Data structure used to store sample's name, their indexes and labels.
 * @author mmendez
 */
public class Sample_Index_Label {

    public ArrayList<String> name;
    public ArrayList<Integer> index;
    public ArrayList<String> label;

    public Sample_Index_Label() {
        this.name = new ArrayList<String>();
        this.index = new ArrayList<Integer>();
        this.label = new ArrayList<String>();
    }
    
    public int size(){
        return name.size();
    }
    
    public void add(String name, int index, String label){
        this.name.add(name);
        this.index.add(index);
        this.label.add(label);
    }

}