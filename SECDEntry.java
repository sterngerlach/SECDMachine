
/* SECDMachine */
/* SECDEntry.java */

import java.util.List;
import java.util.stream.Collectors;

public class SECDEntry implements Cloneable
{
    public List<BaseAST> mStack;
    public List<EnvironmentEntry> mEnvironment;
    public List<BaseAST> mControl;
    public List<SECDEntry> mDump;
    
    public SECDEntry()
    {
    }
    
    @Override
    public SECDEntry clone()
    {
        SECDEntry clonedEntry = null;
        
        try {
            clonedEntry = (SECDEntry)super.clone();
            
            if (this.mStack != null)
                clonedEntry.mStack = this.mStack.stream()
                    .map(astEntry -> astEntry.clone())
                    .collect(Collectors.toList());
            else
                clonedEntry.mStack = null;
            
            if (this.mEnvironment != null)
                clonedEntry.mEnvironment = this.mEnvironment.stream()
                    .map(envEntry -> envEntry.clone())
                    .collect(Collectors.toList());
            else
                clonedEntry.mEnvironment = null;
            
            if (this.mControl != null)
                clonedEntry.mControl = this.mControl.stream()
                    .map(astEntry -> astEntry.clone())
                    .collect(Collectors.toList());
            else
                clonedEntry.mControl = null;
            
            if (this.mDump != null)
                clonedEntry.mDump = this.mDump.stream()
                    .map(secdEntry -> secdEntry.clone())
                    .collect(Collectors.toList());
            else
                clonedEntry.mDump = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return clonedEntry;
    }
    
    @Override
    public String toString()
    {
        StringBuilder stringBuilder = new StringBuilder();
        
        stringBuilder.append("S: [");
        
        if (this.mStack != null)
            stringBuilder.append(String.join(", ", 
                this.mStack.stream()
                    .map(stackEntry -> stackEntry.getExpressionString())
                    .toArray(String[]::new)));
        
        stringBuilder.append("], E: [");
        
        if (this.mEnvironment != null)
            stringBuilder.append(String.join(", ",
                this.mEnvironment.stream()
                    .map(envEntry -> envEntry.toString())
                    .toArray(String[]::new)));
        
        stringBuilder.append("], C: [");
        
        if (this.mControl != null)
            stringBuilder.append(String.join(", ",
                this.mControl.stream()
                    .map(controlEntry -> controlEntry.getExpressionString())
                    .toArray(String[]::new)));
        
        stringBuilder.append("], D: [");
        
        if (this.mDump != null)
            stringBuilder.append(String.join(", ",
                this.mDump.stream()
                    .map(dumpEntry -> dumpEntry.toString())
                    .toArray(String[]::new)));
        
        stringBuilder.append("]");
        
        return stringBuilder.toString();
    }
}
