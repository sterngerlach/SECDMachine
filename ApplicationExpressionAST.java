
/* SECDMachine */
/* ApplicationExpressionAST.java */

public class ApplicationExpressionAST extends BaseAST
{
    private BaseAST mLeftExpression;
    private BaseAST mRightExpression;
    
    public BaseAST getLeftExpression()
    {
        return this.mLeftExpression;
    }
    
    public BaseAST getRightExpression()
    {
        return this.mRightExpression;
    }
    
    public ApplicationExpressionAST(
        BaseAST leftExpression, BaseAST rightExpression, String expressionString)
    {
        super(ASTType.ApplicationExpression, expressionString);
        this.mLeftExpression = leftExpression;
        this.mRightExpression = rightExpression;
    }
    
    @Override
    public ApplicationExpressionAST clone()
    {
        ApplicationExpressionAST clonedAST = null;
        
        try {
            clonedAST = (ApplicationExpressionAST)super.clone();
            clonedAST.mLeftExpression = this.mLeftExpression.clone();
            clonedAST.mRightExpression = this.mRightExpression.clone();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return clonedAST;
    }
}
