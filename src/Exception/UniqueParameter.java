/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Exception;

import com.beust.jcommander.IParameterValidator;
import com.beust.jcommander.ParameterException;

/**
 *
 * @author mmendez
 */
public class UniqueParameter implements IParameterValidator {

    @Override
    public void validate(String name, String value)
            throws ParameterException {
        int n = Integer.parseInt(value);
        if (n < 0) {
            throw new ParameterException("Parameter " + name + " should be positive (found " + value + ")");
        }
    }
    
}
