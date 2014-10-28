/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package comparator;

import java.util.Comparator;
import java.util.Map;
import src.RowRes;

/**
 * Use to sort Forest results by Gini index
 * @author mmendez
 */
public class RowResGiniComparator  implements Comparator<Integer>{
    private Map<Integer,RowRes> variableRes;
    
    public RowResGiniComparator(Map<Integer,RowRes> variableRes){
        this.variableRes = variableRes;
    }
    
    @Override
    public int compare(Integer id1, Integer id2){
        RowRes v1 = variableRes.get(id1);
        RowRes v2 = variableRes.get(id2);
        
        // decreasing order
        if(v1.getGiniD() > v2.getGiniD()) return -1;
        if(v1.getGiniD() < v2.getGiniD()) return 1;
        return 0;
        
    }
}
