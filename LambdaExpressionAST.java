
/* SECDMachine */
/* LambdaExpressionAST.java */

public class LambdaExpressionAST extends BaseAST
{
    private BaseAST mExpression;
    
    public BaseAST getExpression()
    {
        return this.mExpression;
    }
    
    public LambdaExpressionAST(BaseAST lambdaExpression, String lambdaExpressionString)
    {
        super(ASTType.LambdaExpression, lambdaExpressionString);
        this.mExpression = lambdaExpression;
    }
    
    @Override
    public LambdaExpressionAST clone()
    {
        LambdaExpressionAST clonedAST = null;
        
        try {
            clonedAST = (LambdaExpressionAST)super.clone();
            clonedAST.mExpression = this.mExpression.clone();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return clonedAST;
    }
}
