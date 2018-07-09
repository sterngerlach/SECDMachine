
/* SECDMachine */
/* SECDMachine.java */

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class SECDMachine
{
    private final APObject mAPObject;
    private final SECDEntry mInitialDump;
    private SECDEntry mSECD;
    
    public SECDMachine()
    {
        this.mAPObject = new APObject();
        this.mInitialDump = new SECDEntry();
        this.mInitialDump.mStack = new ArrayList<>();
        this.mInitialDump.mEnvironment = new ArrayList<>();
        this.mInitialDump.mControl = new ArrayList<>();
        this.mInitialDump.mDump = new ArrayList<>();
    }
    
    public <T> void execute(BaseAST expressionAST, Consumer<String> stringBuilder)
    {
        this.mSECD = new SECDEntry();
        this.mSECD.mStack = new ArrayList<>();
        this.mSECD.mEnvironment = new ArrayList<>();
        this.mSECD.mControl = new ArrayList<>();
        this.mSECD.mDump = new ArrayList<>();
        
        switch (expressionAST.getType()) {
            case LambdaExpression:
                LambdaExpressionAST lambdaExpressionAST = (LambdaExpressionAST)expressionAST;
                this.mSECD.mControl.add(lambdaExpressionAST.getExpression());
                break;
            default:
                this.mSECD.mControl.add(expressionAST);
                break;
        }
        
        this.mSECD.mDump.add(this.mInitialDump);
        
        // Append the current status
        stringBuilder.accept(this.mSECD.toString() + System.lineSeparator());
        
        boolean controlEmpty = false;
        
        while (true) {
            if (this.mSECD.mControl.isEmpty()) {
                // Condition 1
                controlEmpty = true;
                
                // Dump cannot be empty
                if (this.mSECD.mDump.isEmpty())
                    throw new IllegalStateException();
                
                // <S', E', C', D'>
                SECDEntry secdEntry = this.mSECD.mDump.get(0);
                // hd S
                BaseAST stackHead = this.mSECD.mStack.get(0);
                
                // S <= hd S : S'
                // E <= E'
                // C <= C'
                // D <= D'
                this.mSECD.mStack.clear();
                this.mSECD.mStack = secdEntry.mStack;
                this.mSECD.mStack.add(0, stackHead);
                this.mSECD.mEnvironment = secdEntry.mEnvironment;
                this.mSECD.mControl = secdEntry.mControl;
                this.mSECD.mDump = secdEntry.mDump;
            } else {
                // Condition 2
                controlEmpty = false;
                
                // hd C
                BaseAST controlHead = this.mSECD.mControl.get(0);
                ASTType controlHeadType = controlHead.getType();
                
                switch (controlHeadType) {
                    case VariableExpression:
                    {
                        // Condition 2a
                        
                        // locationEXE
                        EnvironmentEntry foundEnvEntry =
                            this.mSECD.mEnvironment.stream()
                                .filter(envEntry -> LambdaExpressionParser.astEqual(envEntry.mKey, controlHead))
                                .findFirst()
                                .orElse(null);
                        BaseAST locationEXE =
                            foundEnvEntry == null ? controlHead : foundEnvEntry.mValue;
                        
                        // S <= location EXE : S
                        // E <= E
                        // C <= tl C
                        // D <= D
                        this.mSECD.mStack.add(0, locationEXE);
                        this.mSECD.mControl.remove(0);
                        
                        break;
                    }
                    case AbstractionExpression:
                    {
                        // Condition 2b
                        
                        // hd C
                        AbstractionExpressionAST abstractionAST = (AbstractionExpressionAST)controlHead;
                        // bv (hd C)
                        VariableExpressionAST boundVariable = new VariableExpressionAST(abstractionAST.getVariableName());
                        // body (hd C)
                        BaseAST abstractionBody = abstractionAST.getExpression().clone();
                        // unitlist(body (hd C))
                        List<BaseAST> bodyUnitList = new ArrayList<>();
                        bodyUnitList.add(abstractionBody);
                        
                        // constructClosure((E, bv (hd C)), unitlist(body (hd C)))
                        String expressionString = ClosureObject.createStringRepresentation(
                            this.mSECD.mEnvironment, boundVariable, bodyUnitList);
                        
                        ClosureObject closureObject = new ClosureObject(expressionString);
                        closureObject.mEnvironment = this.mSECD.mEnvironment.stream()
                            .map(envEntry -> envEntry.clone())
                            .collect(Collectors.toList());
                        closureObject.mBoundVariable = boundVariable;
                        closureObject.mBody = bodyUnitList;
                        
                        // S <= constructClosure((E, bv (hd C)), unitlist(body (hd C))) : S
                        // E <= E
                        // C <= tl C
                        // D <= D
                        this.mSECD.mStack.add(0, closureObject);
                        this.mSECD.mControl.remove(0);
                        
                        break;
                    }
                    case APObject:
                    {
                        // Condition 2c
                        
                        // hd S
                        BaseAST stackHead = this.mSECD.mStack.get(0);
                        
                        if (stackHead.getType() == ASTType.ClosureObject) {
                            // Condition 2c1
                            
                            // hd S
                            ClosureObject closureObject = (ClosureObject)stackHead;
                            // E1
                            List<EnvironmentEntry> closureEnvironment = closureObject.mEnvironment;
                            // bv X
                            VariableExpressionAST boundVariable = closureObject.mBoundVariable;
                            // body X
                            BaseAST closureBody = closureObject.mBody.get(0);
                            
                            // assoc(bv X, 2nd S)
                            EnvironmentEntry envEntry = new EnvironmentEntry();
                            envEntry.mKey = boundVariable;
                            envEntry.mValue = this.mSECD.mStack.get(1);
                            
                            // tl(tl S)
                            List<BaseAST> remainingStack = this.mSECD.mStack.stream()
                                .map(stackEntry -> stackEntry.clone())
                                .skip(2)
                                .collect(Collectors.toList());
                            // tl C
                            List<BaseAST> controlTail = this.mSECD.mControl.stream()
                                .map(controlEntry -> controlEntry.clone())
                                .skip(1)
                                .collect(Collectors.toList());
                            // <tl(tl S), E, tl C, D>
                            SECDEntry secdEntry = this.mSECD.clone();
                            secdEntry.mStack = remainingStack;
                            secdEntry.mControl = controlTail;
                            
                            // S <= nil
                            // E <= derive(assoc(bv X, 2nd S)) : E1
                            // C <= unitlist(body X')
                            // D <= <tl(tl S), E, tl C, D>
                            this.mSECD.mStack = new ArrayList<>();
                            this.mSECD.mEnvironment = closureEnvironment;
                            this.mSECD.mEnvironment.add(0, envEntry);
                            this.mSECD.mControl = new ArrayList<>();
                            this.mSECD.mControl.add(closureBody);
                            this.mSECD.mDump = new ArrayList<>();
                            this.mSECD.mDump.add(secdEntry);
                        } else {
                            // Condition 2c2
                            
                            // 1st S
                            BaseAST stackFirst = this.mSECD.mStack.get(0);
                            // 2nd S
                            BaseAST stackSecond = this.mSECD.mStack.get(1);
                            
                            // (1st S)(2nd S)
                            String expressionString =
                                "(" + stackFirst.getExpressionString() + " " +
                                stackSecond.getExpressionString() + ")";
                            ApplicationExpressionAST applicationAST = new ApplicationExpressionAST(
                                stackFirst, stackSecond, expressionString);
                            
                            // tl(tl S)
                            List<BaseAST> remainingStack = this.mSECD.mStack.stream()
                                .map(stackEntry -> stackEntry.clone())
                                .skip(2)
                                .collect(Collectors.toList());
                            
                            // S <= (1st S)(2nd S) : tl(tl S)
                            // E <= E
                            // C <= tl C
                            // D <= D
                            this.mSECD.mStack = remainingStack;
                            this.mSECD.mStack.add(0, applicationAST);
                            this.mSECD.mControl.remove(0);
                        }
                        
                        break;
                    }
                    case ApplicationExpression:
                    {
                        // Condition 2d
                        
                        // hd C
                        ApplicationExpressionAST applicationAST = (ApplicationExpressionAST)controlHead;
                        // operand X
                        BaseAST operandAST = applicationAST.getRightExpression();
                        // operator X
                        BaseAST operatorAST = applicationAST.getLeftExpression();
                        
                        // S <= S
                        // E <= E
                        // C <= operand X : operator X : 'ap' : tl C
                        // D <= D
                        this.mSECD.mControl.remove(0);
                        this.mSECD.mControl.add(0, this.mAPObject);
                        this.mSECD.mControl.add(0, operatorAST);
                        this.mSECD.mControl.add(0, operandAST);
                        
                        break;
                    }
                    default:
                    {
                        throw new IllegalStateException();
                    }
                }
            }
            
            // Append the current status
            stringBuilder.accept(this.mSECD.toString() + System.lineSeparator());
            
            // Done
            if (this.mSECD.mControl.isEmpty() &&
                this.isInitialDump())
                break;
        }
    }
    
    private boolean isInitialDump()
    {
        return
            this.mSECD.mDump.get(0).mStack.size() == 0 &&
            this.mSECD.mDump.get(0).mEnvironment.size() == 0 && 
            this.mSECD.mDump.get(0).mControl.size() == 0 && 
            this.mSECD.mDump.get(0).mDump.size() == 0;
    }
}
