
/* SECDMachine */
/* ClosureObject.java */

import java.util.List;
import java.util.stream.Collectors;

public class ClosureObject extends BaseAST implements Cloneable
{
    public List<EnvironmentEntry> mEnvironment;
    public VariableExpressionAST mBoundVariable;
    public List<BaseAST> mBody;

    public ClosureObject(String expressionString)
    {
        super(ASTType.ClosureObject, expressionString);
    }

    @Override
    public ClosureObject clone()
    {
        ClosureObject clonedObject = null;

        try {
            clonedObject = (ClosureObject)super.clone();
            clonedObject.mEnvironment = this.mEnvironment.stream()
                .map(envEntry -> envEntry.clone())
                .collect(Collectors.toList());
            clonedObject.mBoundVariable = this.mBoundVariable.clone();
            clonedObject.mBody = this.mBody.stream()
                .map(astEntry -> astEntry.clone())
                .collect(Collectors.toList());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return clonedObject;
    }
    
    public static String createStringRepresentation(
        List<EnvironmentEntry> environmentEntries,
        VariableExpressionAST boundVariable,
        List<BaseAST> bodyUnitList)
    {
        StringBuilder stringBuilder = new StringBuilder();
        
        stringBuilder.append("closure(");
        stringBuilder.append("[");
        stringBuilder.append(String.join(", ",
            environmentEntries.stream()
                .map(envEntry -> envEntry.toString())
                .toArray(String[]::new)));
        stringBuilder.append("], ");
        stringBuilder.append(boundVariable.getExpressionString());
        stringBuilder.append(", [");
        stringBuilder.append(String.join(", ",
            bodyUnitList.stream()
                .map(astEntry -> astEntry.getExpressionString())
                .toArray(String[]::new)));
        stringBuilder.append("])");
        
        return stringBuilder.toString();
    }
}
