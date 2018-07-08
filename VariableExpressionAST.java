
/* SECDMachine */
/* VariableExpression.java */

public final class VariableExpressionAST extends BaseAST
{
    private String mVariableName;
    
    public String getVariableName()
    {
        return this.mVariableName;
    }
    
    public VariableExpressionAST(String variableName)
    {
        super(ASTType.VariableExpression, variableName);
        this.mVariableName = variableName;
    }
    
    @Override
    public VariableExpressionAST clone()
    {
        VariableExpressionAST clonedAST = null;
        
        try {
            clonedAST = (VariableExpressionAST)super.clone();
            clonedAST.mVariableName = this.mVariableName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return clonedAST;
    }
}
