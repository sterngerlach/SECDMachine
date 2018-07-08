
/* SECDMachine */
/* Token.java */

public class Token implements Cloneable
{
    private TokenType mTokenType;
    private String mTokenString;
    
    public TokenType getTokenType()
    {
        return this.mTokenType;
    }
    
    public String getTokenString()
    {
        return this.mTokenString;
    }
    
    public Token(TokenType tokenType, String tokenString)
    {
        this.mTokenType = tokenType;
        this.mTokenString = tokenString;
    }
    
    @Override
    public Token clone() throws CloneNotSupportedException
    {
        Token clonedToken = null;
        
        try {
            clonedToken = (Token)super.clone();
            clonedToken.mTokenType = this.mTokenType;
            clonedToken.mTokenString = this.mTokenString;
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return clonedToken;
    }
}
