/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package struct;

/**
 * Data structure used to store the index of a sample and his expression value
 * @author mmendez
 */
public class Index_Label_Expression implements Comparable<Index_Label_Expression> {

    public int index;
    public String label;
    public double expression;

    public Index_Label_Expression(int idx, String label, double expression) {
        this.index = idx;
        this.label = label;
        this.expression = expression;
    }

    public Index_Label_Expression(Index_Label index_label, double expression) {
        this.index = index_label.index;
        this.label = index_label.label;
        this.expression = expression;
    }
    
    @Override
    public int compareTo(Index_Label_Expression other) {
        double value1 = other.expression;
        double value2 = this.expression;
        if (value1 > value2) {
            return -1;
        } else if (value1 == value2) {
            return 0;
        } else {
            return 1;
        }
    }

    @Override
    public String toString() {
        return "idx: " + index + " label: " + label + " expression: " + expression;
    }

    /*
     * getters
     */
    
    public Index_Label getIndex_Label(){
        return new Index_Label(this.index, this.label);
    }
//    public int getIndex() {
//        return index;
//    }
//
//    public double getExpression() {
//        return expression;
//    }
//    
//    public String getLabel(){
//        return label;
//    }

    
}