
/* SECDMachine */
/* LambdaExpressionParser.java */

import java.util.List;
import java.util.function.Consumer;

public class LambdaExpressionParser
{
    private List<Token> mTokenList;
    private int mNumOfTokens;
    private int mCurrentTokenIndex;
    
    public LambdaExpressionParser()
    {
    }
    
    private static String repeat(String str, int times)
    {
        return new String(new char[times]).replace("\0", str);
    }
    
    public static String astToLambdaExpression(BaseAST expressionAST)
    {
        if (expressionAST == null)
            return "";
        
        StringBuilder stringBuilder = new StringBuilder();        
        LambdaExpressionParser.astToLambdaExpressionInternal(expressionAST, stringBuilder);
        
        return stringBuilder.toString();
    }
    
    private static void astToLambdaExpressionInternal(
        BaseAST expressionAST, StringBuilder stringBuilder)
    {
        switch (expressionAST.getType()) {
            case VariableExpression:
            {
                VariableExpressionAST variableExprAST = (VariableExpressionAST)expressionAST;
                stringBuilder.append(variableExprAST.getVariableName());
                break;
            }
            case ApplicationExpression:
            {
                ApplicationExpressionAST applicationExprAST = (ApplicationExpressionAST)expressionAST;
                stringBuilder.append("(");
                LambdaExpressionParser.astToLambdaExpressionInternal(
                    applicationExprAST.getLeftExpression(), stringBuilder);
                stringBuilder.append(" ");
                LambdaExpressionParser.astToLambdaExpressionInternal(
                    applicationExprAST.getRightExpression(), stringBuilder);
                stringBuilder.append(")");
                break;
            }
            case AbstractionExpression:
            {
                AbstractionExpressionAST abstractionExprAST = (AbstractionExpressionAST)expressionAST;
                stringBuilder.append("(\\");
                stringBuilder.append(abstractionExprAST.getVariableName());
                stringBuilder.append(". ");
                LambdaExpressionParser.astToLambdaExpressionInternal(
                    abstractionExprAST.getExpression(), stringBuilder);
                stringBuilder.append(")");
                break;
            }
            case LambdaExpression:
            {
                LambdaExpressionAST lambdaExprAST = (LambdaExpressionAST)expressionAST;
                LambdaExpressionParser.astToLambdaExpressionInternal(
                    lambdaExprAST.getExpression(), stringBuilder);
                break;
            }
            case ClosureObject:
            {
                Consumer<EnvironmentEntry> envEntryToString = envEntry -> {
                    stringBuilder.append("<");
                    LambdaExpressionParser.astToLambdaExpressionInternal(
                        envEntry.mKey, stringBuilder);
                    stringBuilder.append(", ");
                    LambdaExpressionParser.astToLambdaExpressionInternal(
                        envEntry.mValue, stringBuilder);
                    stringBuilder.append(">");
                };
                
                ClosureObject closureObject = (ClosureObject)expressionAST;
                stringBuilder.append("closure(");
                stringBuilder.append("[");
                
                for (int i = 0; i < closureObject.mEnvironment.size(); ++i) {
                    envEntryToString.accept(closureObject.mEnvironment.get(i));
                    
                    if (i != closureObject.mEnvironment.size() - 1)
                        stringBuilder.append(", ");
                }
                
                stringBuilder.append("], ");
                LambdaExpressionParser.astToLambdaExpressionInternal(
                    closureObject.mBoundVariable, stringBuilder);
                
                stringBuilder.append(", ");
                stringBuilder.append("[");
                
                for (int i = 0; i < closureObject.mBody.size(); ++i) {
                    LambdaExpressionParser.astToLambdaExpressionInternal(
                        closureObject.mBody.get(i), stringBuilder);
                    
                    if (i != closureObject.mBody.size() - 1)
                        stringBuilder.append(", ");
                }
                
                stringBuilder.append("]");
                stringBuilder.append(")");
                break;
            }
            case APObject:
            {
                stringBuilder.append("\'ap\'");
                break;
            }
            default:
            {
                throw new IllegalStateException();
            }
        }
    }
    
    public static boolean astEqual(BaseAST leftAST, BaseAST rightAST)
    {
        if (leftAST == rightAST)
            return true;
        
        if (leftAST.getType() != rightAST.getType())
            return false;
        
        ASTType astType = leftAST.getType();
        
        switch (astType) {
            case VariableExpression:
            {
                VariableExpressionAST leftVarAST = (VariableExpressionAST)leftAST;
                VariableExpressionAST rightVarAST = (VariableExpressionAST)rightAST;
                
                if (leftVarAST.getVariableName().equals(rightVarAST.getVariableName()))
                    return true;
                break;
            }
            case ApplicationExpression:
            {
                ApplicationExpressionAST leftApplicationAST = (ApplicationExpressionAST)leftAST;
                ApplicationExpressionAST rightApplicationAST = (ApplicationExpressionAST)rightAST;
                
                if (LambdaExpressionParser.astEqual(
                    leftApplicationAST.getLeftExpression(),
                    rightApplicationAST.getLeftExpression()) &&
                    LambdaExpressionParser.astEqual(
                        leftApplicationAST.getRightExpression(),
                    rightApplicationAST.getRightExpression()))
                    return true;
                break;
            }
            case AbstractionExpression:
            {
                AbstractionExpressionAST leftAbstractionAST = (AbstractionExpressionAST)leftAST;
                AbstractionExpressionAST rightAbstractionAST = (AbstractionExpressionAST)rightAST;
                
                if (leftAbstractionAST.getVariableName().equals(
                    rightAbstractionAST.getVariableName()) &&
                    LambdaExpressionParser.astEqual(
                    leftAbstractionAST.getExpression(),
                    rightAbstractionAST.getExpression()))
                    return true;
                break;
            }
            case LambdaExpression:
            {
                LambdaExpressionAST leftLambdaExprAST = (LambdaExpressionAST)leftAST;
                LambdaExpressionAST rightLambdaExprAST = (LambdaExpressionAST)rightAST;
                
                if (LambdaExpressionParser.astEqual(
                    leftLambdaExprAST.getExpression(),
                    rightLambdaExprAST.getExpression()))
                    return true;
                break;
            }
            default:
            {
                throw new IllegalStateException();
            }
        }
        
        return false;
    }
    
    public static String astToTreeString(BaseAST expressionAST)
    {
        if (expressionAST == null)
            return "";
        
        StringBuilder stringBuilder = new StringBuilder();
        int treeDepth = 0;
        
        LambdaExpressionParser.astToTreeStringInternal(expressionAST, stringBuilder, treeDepth);
        
        return stringBuilder.toString();
    }
    
    private static void astToTreeStringInternal(
        BaseAST expressionAST, StringBuilder stringBuilder, int treeDepth)
    {
        switch (expressionAST.getType()) {
            case VariableExpression:
            {
                VariableExpressionAST variableExprAST = (VariableExpressionAST)expressionAST;
                stringBuilder.append(
                    LambdaExpressionParser.repeat("\t", treeDepth) +
                    variableExprAST.getClass().getName() +
                    ": " + variableExprAST.getVariableName() + System.lineSeparator());
                break;
            }
            case ApplicationExpression:
            {
                ApplicationExpressionAST applicationExprAST = (ApplicationExpressionAST)expressionAST;
                stringBuilder.append(
                    LambdaExpressionParser.repeat("\t", treeDepth) +
                    applicationExprAST.getClass().getName() +
                    System.lineSeparator());
                treeDepth++;
                LambdaExpressionParser.astToTreeStringInternal(
                    applicationExprAST.getLeftExpression(), stringBuilder, treeDepth);
                LambdaExpressionParser.astToTreeStringInternal(
                    applicationExprAST.getRightExpression(), stringBuilder, treeDepth);
                treeDepth--;
                break;
            }
            case AbstractionExpression:
            {
                AbstractionExpressionAST abstractionExprAST = (AbstractionExpressionAST)expressionAST;
                stringBuilder.append(
                    LambdaExpressionParser.repeat("\t", treeDepth) +
                    abstractionExprAST.getClass().getName() +
                    ": " + abstractionExprAST.getVariableName() + System.lineSeparator());
                treeDepth++;
                LambdaExpressionParser.astToTreeStringInternal(
                    abstractionExprAST.getExpression(), stringBuilder, treeDepth);
                treeDepth--;
                break;
            }
            case LambdaExpression:
            {
                LambdaExpressionAST lambdaExprAST = (LambdaExpressionAST)expressionAST;
                stringBuilder.append(
                    LambdaExpressionParser.repeat("\t", treeDepth) +
                    lambdaExprAST.getClass().getName() +
                    System.lineSeparator());
                treeDepth++;
                LambdaExpressionParser.astToTreeStringInternal(
                    lambdaExprAST.getExpression(), stringBuilder, treeDepth);
                treeDepth--;
                break;
            }
            default:
            {
                throw new IllegalStateException();
            }
        }
    }
    
    public BaseAST parse(List<Token> tokenList)
    {
        if (tokenList.isEmpty())
            return null;
        
        this.mTokenList = tokenList;
        this.mNumOfTokens = tokenList.size();
        this.mCurrentTokenIndex = 0;
        
        BaseAST lambdaExpressionAST = this.visitLambdaExpression();
        
        if (this.mCurrentTokenIndex < this.mTokenList.size())
            throw new IllegalArgumentException("Parsing failed");
        
        return lambdaExpressionAST;
    }
    
    private BaseAST visitLambdaExpression()
    {
        /*
         * <LambdaExpression> ::=
         *     <VariableExpression> |
         *     <ApplicationExpression> |
         *     <AbstractionExpression>
         * <VariableExpression> ::= Variable
         * <ApplicationExpression> ::=
         *     LeftParenthesis <LambdaExpression> <LambdaExpression> RightParenthesis
         * <AbstractionExpression> ::=
         *     LeftParenthesis Lambda Variable Dot <LambdaExpression> RightParenthesis
         */
        
        if (this.mCurrentTokenIndex > this.mNumOfTokens - 1)
            throw new IllegalStateException();
        
        BaseAST lambdaExpressionAST = null;
        Token currentToken = this.mTokenList.get(this.mCurrentTokenIndex);
        TokenType currentTokenType = currentToken.getTokenType();
        
        if (currentTokenType == TokenType.Variable) {
            // VariableExpression
            BaseAST variableExpressionAST = this.visitVariableExpression();
            // lambdaExpressionAST = new LambdaExpressionAST(variableExpressionAST);
            lambdaExpressionAST = variableExpressionAST;
        } else if (currentTokenType == TokenType.LeftParenthesis) {
            this.mCurrentTokenIndex++;
            
            if (this.mCurrentTokenIndex > this.mNumOfTokens - 1)
                throw new IllegalStateException();
            
            Token nextToken = this.mTokenList.get(this.mCurrentTokenIndex);
            TokenType nextTokenType = nextToken.getTokenType();
            
            this.mCurrentTokenIndex--;
            
            if (nextTokenType == TokenType.Lambda) {
                // AbstractionExpression
                BaseAST abstractionExpressionAST = this.visitAbstractionExpression();
                // lambdaExpressionAST = new LambdaExpressionAST(abstractionExpressionAST);
                lambdaExpressionAST = abstractionExpressionAST;
            } else {
                // ApplicationExpression
                BaseAST applicationExpressionAST = this.visitApplicationExpression();
                // lambdaExpressionAST = new LambdaExpressionAST(applicationExpressionAST);
                lambdaExpressionAST = applicationExpressionAST;
            }
        } else {
            throw new IllegalStateException();
        }
        
        return lambdaExpressionAST;
    }
    
    private BaseAST visitVariableExpression()
    {
        /*
         * <VariableExpression> ::= Variable
         */
        
        if (this.mCurrentTokenIndex > this.mNumOfTokens - 1)
            throw new IllegalStateException();
        
        VariableExpressionAST variableExpressionAST = null;
        
        // Variable
        Token currentToken = this.mTokenList.get(this.mCurrentTokenIndex);
        TokenType currentTokenType = currentToken.getTokenType();
        
        if (currentTokenType != TokenType.Variable)
            throw new IllegalStateException();
        
        // Create VariableExpressionAST
        variableExpressionAST = new VariableExpressionAST(currentToken.getTokenString());
        
        this.mCurrentTokenIndex++;
        
        return variableExpressionAST;
    }
    
    private BaseAST visitApplicationExpression()
    {
        /*
         * <ApplicationExpression> ::=
         *     LeftParenthesis <LambdaExpression> <LambdaExpression> RightParenthesis
         */
        
        if (this.mCurrentTokenIndex > this.mNumOfTokens - 1)
            throw new IllegalStateException();
        
        ApplicationExpressionAST applicationExpressionAST = null;
        
        // LeftParenthesis
        Token currentToken = this.mTokenList.get(this.mCurrentTokenIndex);
        TokenType currentTokenType = currentToken.getTokenType();
        
        if (currentTokenType != TokenType.LeftParenthesis)
            throw new IllegalStateException();
        
        this.mCurrentTokenIndex++;
        
        // LambdaExpression
        BaseAST leftExpressionAST = this.visitLambdaExpression();
        
        if (this.mCurrentTokenIndex > this.mNumOfTokens - 1)
            throw new IllegalStateException();
        
        // LambdaExpression
        BaseAST rightExpressionAST = this.visitLambdaExpression();
        
        if (this.mCurrentTokenIndex > this.mNumOfTokens - 1)
            throw new IllegalStateException();
        
        // RightParenthesis
        currentToken = this.mTokenList.get(this.mCurrentTokenIndex);
        currentTokenType = currentToken.getTokenType();
        
        if (currentTokenType != TokenType.RightParenthesis)
            throw new IllegalStateException();
        
        // Create ApplicationExpressionAST
        String expressionString = 
            "(" + leftExpressionAST.getExpressionString() + " " +
            rightExpressionAST.getExpressionString() + ")";
        applicationExpressionAST = new ApplicationExpressionAST(
            leftExpressionAST, rightExpressionAST, expressionString);
        
        this.mCurrentTokenIndex++;
        
        return applicationExpressionAST;
    }
    
    private BaseAST visitAbstractionExpression()
    {
        /*
         * <AbstractionExpression> ::=
         *     LeftParenthesis Lambda Variable Dot <LambdaExpression> RightParenthesis
         */
        
        if (this.mCurrentTokenIndex > this.mNumOfTokens - 1)
            throw new IllegalStateException();
        
        AbstractionExpressionAST abstractionExpressionAST = null;
        
        // LeftParenthesis
        Token currentToken = this.mTokenList.get(this.mCurrentTokenIndex);
        TokenType currentTokenType = currentToken.getTokenType();
        
        if (currentTokenType != TokenType.LeftParenthesis)
            throw new IllegalStateException();
        
        this.mCurrentTokenIndex++;
        
        // Lambda
        if (this.mCurrentTokenIndex > this.mNumOfTokens - 1)
            throw new IllegalStateException();
        
        currentToken = this.mTokenList.get(this.mCurrentTokenIndex);
        currentTokenType = currentToken.getTokenType();
        
        if (currentTokenType != TokenType.Lambda)
            throw new IllegalStateException();
        
        this.mCurrentTokenIndex++;
        
        // Variable
        if (this.mCurrentTokenIndex > this.mNumOfTokens - 1)
            throw new IllegalStateException();
        
        currentToken = this.mTokenList.get(this.mCurrentTokenIndex);
        currentTokenType = currentToken.getTokenType();
        
        if (currentTokenType != TokenType.Variable)
            throw new IllegalStateException();
        
        String variableName = currentToken.getTokenString();
        
        this.mCurrentTokenIndex++;
        
        // Dot
        if (this.mCurrentTokenIndex > this.mNumOfTokens - 1)
            throw new IllegalStateException();
        
        currentToken = this.mTokenList.get(this.mCurrentTokenIndex);
        currentTokenType = currentToken.getTokenType();
        
        if (currentTokenType != TokenType.Dot)
            throw new IllegalStateException();
        
        this.mCurrentTokenIndex++;
        
        // LambdaExpression
        BaseAST lambdaExpressionAST = this.visitLambdaExpression();
        
        if (this.mCurrentTokenIndex > this.mNumOfTokens - 1)
            throw new IllegalStateException();
        
        // RightParenthesis
        currentToken = this.mTokenList.get(this.mCurrentTokenIndex);
        currentTokenType = currentToken.getTokenType();
        
        if (currentTokenType != TokenType.RightParenthesis)
            throw new IllegalStateException();
        
        // Create AbstractionExpressionAST
        String expressionString =
            "(\\" + variableName + ". " +
            lambdaExpressionAST.getExpressionString() + ")";
        abstractionExpressionAST = new AbstractionExpressionAST(
            variableName, lambdaExpressionAST, expressionString);
        
        this.mCurrentTokenIndex++;
        
        return abstractionExpressionAST;
    }
}
