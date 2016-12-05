package Util;


import java.util.ArrayList;
import java.util.Scanner;
import java.util.Stack;

public class CalExpress implements GameConstants{

    /**
     * 将字符串转化成List
     */
    //检查字符串是否正确：有用到四个数且仅用了一次，没有出现其他不合法字符
    private static String  getStringList(ArrayList<String> strings, String exp, ArrayList<String> numlist){
//        ArrayList<String> strings = new ArrayList<String>();
        String num = "";
        for (int i = 0; i < exp.length(); i++) {
            //跳过空白字符
            if(Character.isWhitespace(exp.charAt(i)))continue;

            //处理数字
            if(Character.isDigit(exp.charAt(i))){
                num = num + exp.charAt(i);
            }else{
                if(!num .equals("") ){
                    //检查数字
                    if(numlist.isEmpty())return overuse+"";//设置只能使用给出的数一次
                    int index=numlist.indexOf(num);
                    if(index==-1){
                        return no_use_number_given+"";//设置只能使用给出的数
                    }
                    //number is right
                    numlist.remove(index);
                    strings.add(num);
                }
                //check the char
                char c=exp.charAt(i);
                if(!checkChar(c))return op_error+"";

                //char is ok;
                strings.add(exp.charAt(i) + "");
                num = "";
            }
        }
        if(!num .equals("") ){
            //检查数字
            if(numlist.isEmpty())return overuse+"";//设置只能使用给出的数一次
            int index=numlist.indexOf(num);
            if(index==-1){
                return no_use_number_given+"";//设置只能使用给出的数
            }
            //number is right
            numlist.remove(index);

            strings.add(num);
        }

        if(!numlist.isEmpty())return lackuse+"";//没有使用到所有数
        return TRUE;
    }


    private static boolean checkChar(char c){
        return c == '+' || c == '-' || c == '*' || c == '/' || c == '(' || c == ')';
    }


    /**
     * 将中缀表达式转化为后缀表达式
     */
    private static ArrayList<String> getPostOrder(ArrayList<String> inOrderList){

        ArrayList<String> result = new ArrayList<String>();
        Stack<String> stack = new Stack<String>();
        for (String anInOrderList : inOrderList) {
            if (Character.isDigit(anInOrderList.charAt(0))) {
                result.add(anInOrderList);
            } else {
                switch (anInOrderList.charAt(0)) {
                    case '(':
                        stack.push(anInOrderList);
                        break;
                    case ')':
                        while (!stack.peek().equals("(")) {
                            result.add(stack.pop());
                        }
                        stack.pop();
                        break;
                    default:
                        while (!stack.isEmpty() && compare(stack.peek(), anInOrderList)) {
                            result.add(stack.pop());
                        }
                        stack.push(anInOrderList);
                        break;
                }
            }
        }
        while(!stack.isEmpty()){
            result.add(stack.pop());
        }
        return result;
    }



    public static Integer calculate(String exp,ArrayList<String> numlist) throws Exception{

        //将表达式分解到该strings
        ArrayList<String>strings=new ArrayList();
        String checkResult=getStringList(strings,exp, numlist);
        if ( !checkResult.equals(TRUE)) {
            //返回整数的错误代码
           return Integer.parseInt(checkResult);
        }
        ArrayList<String>postOrder=getPostOrder(strings);
        Stack stack = new Stack();
        for (String aPostOrder : postOrder) {
            if (Character.isDigit(aPostOrder.charAt(0))) {
                stack.push(Integer.parseInt(aPostOrder));
            } else {
                Integer back = (Integer) stack.pop();
                Integer front = (Integer) stack.pop();
                Integer res = 0;
                switch (aPostOrder.charAt(0)) {
                    case '+':
                        res = front + back;
                        break;
                    case '-':
                        res = front - back;
                        break;
                    case '*':
                        res = front * back;
                        break;
                    case '/':
                        if (front % back != 0) return 999;
                        res = front / back;
                        break;
                }
                stack.push(res);
            }
        }
        return (Integer)stack.pop();
    }

    /**
     * 比较运算符等级
     * @param peek
     * @param cur
     * @return
     */
    private static boolean compare(String peek, String cur){
        if("*".equals(peek) && ("/".equals(cur) || "*".equals(cur) ||"+".equals(cur) ||"-".equals(cur))){
            return true;
        }else if("/".equals(peek) && ("/".equals(cur) || "*".equals(cur) ||"+".equals(cur) ||"-".equals(cur))){
            return true;
        }else if("+".equals(peek) && ("+".equals(cur) || "-".equals(cur))){
            return true;
        }else if("-".equals(peek) && ("+".equals(cur) || "-".equals(cur))){
            return true;
        }
        return false;
    }

    public static void main(String[] args) throws Exception {
        Scanner scanner=new Scanner(System.in);
        while (true){
            String exp=scanner.nextLine();
            int i= calculate(exp,null);
            System.out.println(i);
        }
    }

}
