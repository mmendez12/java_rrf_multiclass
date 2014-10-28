package struct;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * A mutable integer
 * @author mmendez
 */
public class MutableInt {

    private int value;
    
    public MutableInt(){
        this.value = 0;
    }
    
    public MutableInt(int value){
        this.value = value;
    } 
    
    public void add(int value){
        this.value += value; 
    }

    public int get() {
        return value;
    }

    public void inc() {
        value++;
    }
    
    public void dec() {
        value--;
    }

    public void reset() {
        value = 0;
    }
    
    public int toInt(){
        return (int) value;
    }
    
    @Override
    public String toString(){
        return "val: "+value;
    }
}
