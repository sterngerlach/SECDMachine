
/* SECDMachine */
/* MainFrame.java */

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class MainFrame extends JFrame
{
    private static final long serialVersionUID = 1L;
    
    private static final int DefaultWindowWidth = 640;
    private static final int DefaultWindowHeight = 480;
    private static final String DefaultWindowTitle = "SECD Machine Implementation";
    
    private JPanel mPanelCenter;
    private JLabel mLabelLambdaExpression;
    private JTextField mTextBoxLambdaExpression;
    private JButton mButtonExecute;
    private JLabel mLabelResult;
    private JTextArea mTextBoxResult;
    private JScrollPane mScrollPaneResult;
    
    public MainFrame()
    {
        this.initializeComponent();
        this.setEventHandler();
        
        this.setMinimumSize(new Dimension(
            MainFrame.DefaultWindowWidth, MainFrame.DefaultWindowHeight));
        this.setSize(MainFrame.DefaultWindowWidth, MainFrame.DefaultWindowHeight);
        this.setTitle(MainFrame.DefaultWindowTitle);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);
    }
    
    private void initializeComponent()
    {
        /* Center Panel */
        this.mPanelCenter = new JPanel();
        this.mPanelCenter.setLayout(new GridBagLayout());
        this.mPanelCenter.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        this.getContentPane().add(this.mPanelCenter, BorderLayout.CENTER);
        
        Font textBoxFont = new Font(Font.MONOSPACED, Font.PLAIN, 14);
        
        GridBagConstraints layoutConstraints = new GridBagConstraints();
        layoutConstraints.anchor = GridBagConstraints.WEST;
        
        /* Lambda Expression Label */
        layoutConstraints.gridx = 0;
        layoutConstraints.gridy = 0;
        layoutConstraints.gridwidth = 2;
        layoutConstraints.weightx = 0.0;
        layoutConstraints.weighty = 0.0;
        layoutConstraints.fill = GridBagConstraints.NONE;
        layoutConstraints.insets = new Insets(0, 0, 5, 0);
        
        this.mLabelLambdaExpression = new JLabel("Enter Lambda Expression: ");
        this.mPanelCenter.add(this.mLabelLambdaExpression, layoutConstraints);
        
        /* Lambda Expression TextBox */
        layoutConstraints.gridx = 0;
        layoutConstraints.gridy = 1;
        layoutConstraints.gridwidth = 1;
        layoutConstraints.weightx = 1.0;
        layoutConstraints.weighty = 0.0;
        layoutConstraints.fill = GridBagConstraints.HORIZONTAL;
        layoutConstraints.insets = new Insets(0, 0, 5, 5);
        
        this.mTextBoxLambdaExpression = new JTextField();
        this.mTextBoxLambdaExpression.setHorizontalAlignment(JTextField.LEFT);
        this.mTextBoxLambdaExpression.setFont(textBoxFont);
        this.mPanelCenter.add(this.mTextBoxLambdaExpression, layoutConstraints);
        
        /* Execute Button */
        layoutConstraints.gridx = 1;
        layoutConstraints.gridy = 1;
        layoutConstraints.weightx = 0.0;
        layoutConstraints.weighty = 0.0;
        layoutConstraints.fill = GridBagConstraints.NONE;
        layoutConstraints.insets = new Insets(0, 0, 5, 0);
        
        this.mButtonExecute = new JButton("Execute");
        this.mPanelCenter.add(this.mButtonExecute, layoutConstraints);
        this.getRootPane().setDefaultButton(this.mButtonExecute);
        
        /* Result Label */
        layoutConstraints.gridx = 0;
        layoutConstraints.gridy = 2;
        layoutConstraints.gridwidth = 2;
        layoutConstraints.weightx = 0.0;
        layoutConstraints.weighty = 0.0;
        layoutConstraints.fill = GridBagConstraints.NONE;
        layoutConstraints.insets = new Insets(0, 0, 5, 0);
        
        this.mLabelResult = new JLabel("Result: ");
        this.mPanelCenter.add(this.mLabelResult, layoutConstraints);
        
        /* Result TextBox */
        this.mTextBoxResult = new JTextArea();
        this.mTextBoxResult.setEditable(false);
        this.mTextBoxResult.setTabSize(2);
        this.mTextBoxResult.setFont(textBoxFont);
        
        /* Result ScrollPane */
        layoutConstraints.gridx = 0;
        layoutConstraints.gridy = 3;
        layoutConstraints.gridwidth = 2;
        layoutConstraints.weightx = 1.0;
        layoutConstraints.weighty = 1.0;
        layoutConstraints.fill = GridBagConstraints.BOTH;
        layoutConstraints.insets = new Insets(0, 0, 0, 0);
        
        this.mScrollPaneResult = new JScrollPane(this.mTextBoxResult);
        this.mPanelCenter.add(this.mScrollPaneResult, layoutConstraints);
    }
    
    private void setEventHandler()
    {
        this.mButtonExecute.addActionListener(e -> {
            this.onButtonExecuteClick();
        });
    }
    
    private void onButtonExecuteClick()
    {
        this.mTextBoxResult.setText("");
        
        String phaseName = "";
        
        try {
            phaseName = "lexical analysis";
            LambdaExpressionLexer lambdaExprLexer = new LambdaExpressionLexer();
            List<Token> tokenList = lambdaExprLexer.lexicalAnalysis(
                this.mTextBoxLambdaExpression.getText());
            
            this.mTextBoxResult.append("Token List: " + System.lineSeparator());
            this.mTextBoxResult.append(this.repeat("-", 50) + System.lineSeparator());
            tokenList.forEach(token ->
                this.mTextBoxResult.append(
                    token.getTokenType() + ": " + token.getTokenString() +
                    System.lineSeparator()));
            
            phaseName = "parsing";
            LambdaExpressionParser lambdaExprParser = new LambdaExpressionParser();
            BaseAST lambdaExprAST = lambdaExprParser.parse(tokenList);

            this.mTextBoxResult.append(System.lineSeparator());
            this.mTextBoxResult.append("Abstract Syntax Tree: " + System.lineSeparator());
            this.mTextBoxResult.append(this.repeat("-", 50) + System.lineSeparator());
            this.mTextBoxResult.append(LambdaExpressionParser.astToTreeString(lambdaExprAST));
            
            phaseName = "transformation process";
            this.mTextBoxResult.append(System.lineSeparator());
            this.mTextBoxResult.append("SECD Machine Transformation Process: " + System.lineSeparator());
            this.mTextBoxResult.append(this.repeat("-", 50) + System.lineSeparator());
            
            SECDMachine secdMachine = new SECDMachine();
            secdMachine.execute(lambdaExprAST, statusText -> this.mTextBoxResult.append(statusText));
        } catch (Throwable e) {
            this.mTextBoxResult.append(System.lineSeparator());
            this.mTextBoxResult.append("Exception occurred during " + phaseName + ": " + System.lineSeparator());
            this.mTextBoxResult.append(this.repeat("-", 50) + System.lineSeparator());
            this.mTextBoxResult.append(e.getClass().getName() + ": " + e.getMessage() + System.lineSeparator());
            
            StringWriter stringWriter = new StringWriter();
            PrintWriter printWriter = new PrintWriter(stringWriter);
            e.printStackTrace(printWriter);
            printWriter.flush();
            this.mTextBoxResult.append(stringWriter.toString());
        }
    }
    
    private String repeat(String str, int times)
    {
        return new String(new char[times]).replace("\0", str);
    }
}
