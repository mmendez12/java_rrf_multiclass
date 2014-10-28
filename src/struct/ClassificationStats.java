/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package struct;

/**
 *
 * @author mmendez
 */
public class ClassificationStats {
    private String label;
    private int correct;
    private int incorrect;
    
    public ClassificationStats(String label){
        this.label = label;
        this.correct = 0;
        this.incorrect = 0;
    }
    
    public void addStat(boolean cc){
        if (cc){
            this.correct++;
        } else {
            this.incorrect++;
        }
    }
    
    /**
     * The total number of time that this variable is used to classify a sample.
     * @return  correct + incorrect count
     */
    public int used(){
        return this.correct + this.incorrect;
    }

    public double percentageOfCorrectClassification(){
        if (this.correct == 0) {
            return 0.0f;
        }
        return (this.correct * 100.0f)/used();
    }
    
    public String getLabel() {
        return label;
    }

    public int getCorrect() {
        return correct;
    }

    public int getIncorrect() {
        return incorrect;
    }
    
    
    
    
}
