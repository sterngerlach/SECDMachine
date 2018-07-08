
/* SECDMachine */
/* AbstractionExpressionAST.java */

public class AbstractionExpressionAST extends BaseAST
{
    private String mVariableName;
    private BaseAST mExpression;
    
    public String getVariableName()
    {
        return this.mVariableName;
    }
    
    public BaseAST getExpression()
    {
        return this.mExpression;
    }
    
    public AbstractionExpressionAST(
        String variableName, BaseAST lambdaExpression, String lambdaExpressionString)
    {
        super(ASTType.AbstractionExpression, lambdaExpressionString);
        this.mVariableName = variableName;
        this.mExpression = lambdaExpression;
    }
    
    @Override
    public AbstractionExpressionAST clone()
    {
        AbstractionExpressionAST clonedAST = null;
        
        try {
            clonedAST = (AbstractionExpressionAST)super.clone();
            clonedAST.mVariableName = this.mVariableName;
            clonedAST.mExpression = this.mExpression.clone();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return clonedAST;
    }
}
