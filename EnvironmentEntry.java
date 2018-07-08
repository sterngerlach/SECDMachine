
/* SECDMachine */
/* EnvironmentEntry.java */

public class EnvironmentEntry implements Cloneable
{
    public BaseAST mKey;
    public BaseAST mValue;
    
    @Override
    public EnvironmentEntry clone()
    {
        EnvironmentEntry clonedEntry = null;
        
        try {
            clonedEntry = (EnvironmentEntry)super.clone();
            clonedEntry.mKey = this.mKey.clone();
            clonedEntry.mValue = this.mValue.clone();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return clonedEntry;
    }
    
    @Override
    public String toString()
    {
        StringBuilder stringBuilder = new StringBuilder();
        
        stringBuilder.append("<");
        stringBuilder.append(this.mKey.getExpressionString());
        stringBuilder.append(", ");
        stringBuilder.append(this.mValue.getExpressionString());
        stringBuilder.append(">");
        
        return stringBuilder.toString();
    }
}
