package com.example.mcalculator;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.StringTokenizer;

class ExpressionParser {
    private static String operators = "+-*/";
    private static String delimiters = "() " + operators;
    public static boolean flag = true;
    private static boolean isDelimiter(String token) {
        if (token.length() != 1) return false;
        for (int i = 0; i < delimiters.length(); i++) {
            if (token.charAt(0) == delimiters.charAt(i)) return true;
        }
        return false;
    }

    private static boolean isOperator(String token) {
        if (token.equals("u-")) return true;
        for (int i = 0; i < operators.length(); i++) {
            if (token.charAt(0) == operators.charAt(i)) return true;
        }
        return false;
    }

    private static boolean isFunction(String token) {
        if (token.equals("sqrt") || token.equals("cube") || token.equals("pow10")) return true;
        return false;
    }

    private static int priority(String token) {
        if (token.equals("(")) return 1;
        if (token.equals("+") || token.equals("-")) return 2;
        if (token.equals("*") || token.equals("/")) return 3;
        return 4;
    }

    public static List<String> parse(String infix) {
        List<String> postfix = new ArrayList<String>();
        Deque<String> stack = new ArrayDeque<String>();
        StringTokenizer tokenizer = new StringTokenizer(infix, delimiters, true);
        String prev = "";
        String curr = "";
        while (tokenizer.hasMoreTokens()) {
            curr = tokenizer.nextToken();
            if (!tokenizer.hasMoreTokens() && isOperator(curr)) {
                System.out.println("Некорректное выражение.");
                flag = false;
                return postfix;
            }
            if (curr.equals(" ")) continue;
            if (isFunction(curr)) stack.push(curr);
            else if (isDelimiter(curr)) {
                if (curr.equals("(")) stack.push(curr);
                else if (curr.equals(")")) {
                    while (!stack.peek().equals("(")) {
                        postfix.add(stack.pop());
                        if (stack.isEmpty()) {
                            System.out.println("Скобки не согласованы.");
                            flag = false;
                            return postfix;
                        }
                    }
                    stack.pop();
                    if (!stack.isEmpty() && isFunction(stack.peek())) {
                        postfix.add(stack.pop());
                    }
                }
                else {
                    if (curr.equals("-") && (prev.equals("") || (isDelimiter(prev)  && !prev.equals(")")))) {
                    curr = "u-";
                    }
                    else {
                        while (!stack.isEmpty() && (priority(curr) <= priority(stack.peek()))) {
                            postfix.add(stack.pop());
                        }
                    }
                    stack.push(curr);
                }
            }

            else {
                postfix.add(curr);
            }
            prev = curr;
        }

        while (!stack.isEmpty()) {
            if (isOperator(stack.peek())) postfix.add(stack.pop());
            else {
                System.out.println("");
                flag = false;
                return postfix;
            }
        }
        return postfix;
    }
}
class Ideone {
    public static Double calc(List<String> postfix) {
        Deque<Double> stack = new ArrayDeque<Double>();
        for (String x : postfix) {
            if (x.equals("sqrt")) stack.push(Math.sqrt(stack.pop()));
            else if (x.equals("cube")) {
                Double tmp = stack.pop();
                stack.push(tmp * tmp * tmp);
            } else if (x.equals("pow10")) stack.push(Math.pow(10, stack.pop()));
            else if (x.equals("+")) stack.push(stack.pop() + stack.pop());
            else if (x.equals("-")) {
                Double b = stack.pop(), a = stack.pop();
                stack.push(a - b);
            } else if (x.equals("*")) stack.push(stack.pop() * stack.pop());
            else if (x.equals("/")) {
                Double b = stack.pop(), a = stack.pop();
                stack.push(a / b);
            } else if (x.equals("u-")) stack.push(-stack.pop());
            else stack.push(Double.valueOf(x));
        }
        return stack.pop();
    }
}
    public class MainActivity extends AppCompatActivity {
    TextView resultField;
    EditText numberField;
    Double operand = null;
    String lastOperation = "=";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        resultField =(TextView) findViewById(R.id.resultField);
        numberField = (EditText) findViewById(R.id.numberField);
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString("OPERATION", lastOperation);
        if(operand!=null)
            outState.putDouble("OPERAND", operand);
        super.onSaveInstanceState(outState);
    }
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        lastOperation = savedInstanceState.getString("OPERATION");
        operand= savedInstanceState.getDouble("OPERAND");
        resultField.setText(operand.toString());
    }
    public void onClearClick(View view){

        resultField.setText("");
        numberField.setText("");

    }
        public void onClickSin(View view){
            double num=Double.valueOf( numberField.getText().toString());

            resultField.setText(Math.sin(num)+"");

        }
        public void onClickCos(View view){
double num=Double.valueOf( numberField.getText().toString());

            resultField.setText(Math.cos(num)+"");
        }
        public void onClickProc(View view){
            double num=Double.valueOf( numberField.getText().toString());

            resultField.setText(num/100+"");
        }
        public void onNumberClick(View view){

            Button button = (Button)view;
            numberField.append(button.getText());

        }
    public void onOperationClick(View view){

        Button button = (Button)view;
        String op = button.getText().toString();
        if(!op.equals("=")){
            String number = numberField.getText().toString();
            numberField.setText(number+op);
        }
        else {
            String s = numberField.getText().toString();
            ExpressionParser n = new ExpressionParser();
            List<String> expression = n.parse(s);
            boolean flag = n.flag;
            if (flag) {
                for (String x : expression) System.out.print(x + " ");

                resultField.setText(Ideone.calc(expression).toString());
            }
        }
        }

    }

