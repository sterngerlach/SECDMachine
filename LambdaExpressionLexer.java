
/* SECDMachine */
/* LambdaExpressionLexer.java */

import java.util.ArrayList;
import java.util.List;

public class LambdaExpressionLexer
{
    public enum LexerState
    {
        None,
        Variable
    }
    
    public LambdaExpressionLexer()
    {
    }
    
    private boolean isVariableBeginChar(char currentChar)
    {
        return (
            ('a' <= currentChar && currentChar <= 'z') ||
            ('A' <= currentChar && currentChar <= 'Z') ||
            currentChar == '_');
    }
    
    private boolean isVariableChar(char currentChar)
    {
        return (
            ('0' <= currentChar && currentChar <= '9') ||
            ('a' <= currentChar && currentChar <= 'z') ||
            ('A' <= currentChar && currentChar <= 'Z') ||
            currentChar == '_');
    }
    
    public List<Token> lexicalAnalysis(String inputLambdaExpression)
    {
        List<Token> tokenList = new ArrayList<>();
        
        LexerState currentState = LexerState.None;
        String tokenString = "";
        char currentChar = '\0';
        
        inputLambdaExpression += ' ';
        
        for (int i = 0; i < inputLambdaExpression.length(); ++i) {
            currentChar = inputLambdaExpression.charAt(i);
            
            switch (currentState) {
                case None:
                    if (Character.isWhitespace(currentChar)) {
                        // Ignore white spaces
                        continue;
                    } else if (currentChar == '\\') {
                        // Lambda
                        tokenList.add(new Token(TokenType.Lambda, "\\"));
                    } else if (currentChar == '.') {
                        // Dot
                        tokenList.add(new Token(TokenType.Dot, "."));
                    } else if (currentChar == '(') {
                        // Left parenthesis
                        tokenList.add(new Token(TokenType.LeftParenthesis, "("));
                    } else if (currentChar == ')') {
                        // Right parenthesis
                        tokenList.add(new Token(TokenType.RightParenthesis, ")"));
                    } else if (this.isVariableBeginChar(currentChar)) {
                        // Variable
                        currentState = LexerState.Variable;
                        // Reprocess the same character
                        --i;
                    } else {
                        // Error
                        throw new IllegalArgumentException(
                            "Lambda expression contains an illegal character: " + currentChar);
                    }
                    break;
                case Variable:
                    if (this.isVariableChar(currentChar)) {
                        tokenString += currentChar;
                    } else {
                        // Add the new variable token
                        tokenList.add(new Token(TokenType.Variable, tokenString));
                        
                        // Clear the token string
                        tokenString = "";
                        
                        // Reset the lexer state
                        currentState = LexerState.None;
                        
                        // Reprocess the same character
                        --i;
                    }
                    break;
                default:
                    throw new IllegalStateException("Illegal lexer state");
            }
        }
        
        return tokenList;
    }
}
