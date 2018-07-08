
/* SECDMachine */
/* BaseAST.java */

public class BaseAST implements Cloneable
{
    protected ASTType mType;
    protected String mExpressionString;
    
    public ASTType getType()
    {
        return this.mType;
    }
    
    public String getExpressionString()
    {
        return this.mExpressionString;
    }
    
    public BaseAST(ASTType astType, String expressionString)
    {
        this.mType = astType;
        this.mExpressionString = expressionString;
    }
    
    @Override
    public BaseAST clone()
    {
        BaseAST clonedAST = null;
        
        try {
            clonedAST = (BaseAST)super.clone();
            clonedAST.mType = this.mType;
            clonedAST.mExpressionString = this.mExpressionString;
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return clonedAST;
    }
}
