package apps;



import java.io.*;



import java.util.*;

import java.util.regex.*;



import structures.Stack;



public class Expression {



	/**

	 * Expression to be evaluated

	 */

	String expr;                

    

	/**

	 * Scalar symbols in the expression 

	 */

	ArrayList<ScalarSymbol> scalars;   

	

	/**

	 * Array symbols in the expression

	 */

	ArrayList<ArraySymbol> arrays;

    

    /**

     * String containing all delimiters (characters other than variables and constants), 

     * to be used with StringTokenizer

     */

    public static final String delims = " \t*+-/()[]";

    

    /**

     * Initializes this Expression object with an input expression. Sets all other

     * fields to null.

     * 

     * @param expr Expression

     */

    public Expression(String expr) {

        this.expr = expr;

    }



    /**

     * Populates the scalars and arrays lists with symbols for scalar and array

     * variables in the expression. For every variable, a SINGLE symbol is created and stored,

     * even if it appears more than once in the expression.

     * At this time, values for all variables are set to

     * zero - they will be loaded from a file in the loadSymbolValues method.

     */

    public void buildSymbols() {

    		scalars = new ArrayList<ScalarSymbol>();

    		arrays = new ArrayList<ArraySymbol>();

    		int i=0;

    		String temp = "";

    		this.expr = this.expr.replaceAll(" ","");

    		for(i=0;i<this.expr.length();i++) {

        	char ch = this.expr.charAt(i);

        	if(ch=='+'||ch=='-'||ch=='*'||ch=='/'||ch=='['||ch==']'||ch=='('||ch==')'|| Character.isDigit(ch)) {	
    		
        		if(ch=='[') {

        			if(arrays.contains(new ArraySymbol(temp))) {

        				temp ="";

        				continue;
    	
        			}

    	 

        			arrays.add(new ArraySymbol(temp));

        			temp="";

    	

        		}

        		else if(Character.isDigit(ch)) {

        			continue;

        		}

        		else if(ch=='+'||ch=='-'||ch=='*'||ch=='/') {    

        			if(temp.isEmpty()) {
        				
        				continue;

        			}

        			if(scalars.contains(new ScalarSymbol(temp))) {

        				temp="";

        				continue;
        				
        			}

    	

        			scalars.add(new ScalarSymbol(temp));	

        			temp = "";

    	

        		}
        		
        		else {
    		
        			continue;

        		}

    	

        	}	

    	

        	else {

        		temp+=ch;

        	}

    		}

    	

    	if(!temp.isEmpty()) {

    		if(!scalars.contains(new ScalarSymbol(temp))) {

    			scalars.add(new ScalarSymbol(temp));	

    		}
    		
    	

    	}

    	

    }

    	

    	

    

    /**

     * Loads values for symbols in the expression

     * 

     * @param sc Scanner for values input

     * @throws IOException If there is a problem with the input 

     */

    public void loadSymbolValues(Scanner sc) 

    throws IOException {

        while (sc.hasNextLine()) {

            StringTokenizer st = new StringTokenizer(sc.nextLine().trim());

            int numTokens = st.countTokens();

            String sym = st.nextToken();

            ScalarSymbol ssymbol = new ScalarSymbol(sym);

            ArraySymbol asymbol = new ArraySymbol(sym);

            int ssi = scalars.indexOf(ssymbol);

            int asi = arrays.indexOf(asymbol);

            if (ssi == -1 && asi == -1) {

            	continue;

            }

            int num = Integer.parseInt(st.nextToken());

            if (numTokens == 2) { // scalar symbol

                scalars.get(ssi).value = num;

            } else { // array symbol

            	asymbol = arrays.get(asi);

            	asymbol.values = new int[num];

                // following are (index,val) pairs

                while (st.hasMoreTokens()) {

                    String tok = st.nextToken();

                    StringTokenizer stt = new StringTokenizer(tok," (,)");

                    int index = Integer.parseInt(stt.nextToken());

                    int val = Integer.parseInt(stt.nextToken());

                    asymbol.values[index] = val;              

                }

            }

        }

    }

    

    

    /**

     * Evaluates the expression, using RECURSION to evaluate subexpressions and to evaluate array 

     * subscript expressions.

     * 

     * @return Result of evaluation

     */

    public float evaluate() {

    	return evaluate(this.expr);

    }


    private float evaluate(String sub) {

    	int f=0; int l=0;

    		Stack<String> temp = new Stack<String>();

    		for(int i=0;i<sub.length();i++) {
    		
    			char ch = sub.charAt(i) ;

    			if(ch=='(') {

    				temp.push(Character.toString(ch)); 	

    				if(temp.size()==1) {

    					f=i;

    				}

    		}

    		else if(ch==')') {

    			if(temp.size()==1) {

    				l=i;

    			}

    		else {

    			temp.pop();
    	
    		}

    	}

    	}

    	if(f==0 && l==0){



    		String str = "";

    		int num;

    		int first=0; int last=0;

    	

    		String name ="";

    		for(int i =0;i<sub.length();i++) {

    			Stack<String> brackets = new Stack<String>();

    			char ch2=sub.charAt(i);

    			if(ch2=='[') {

    				for(int j=0;j<sub.length();j++) {

    					char ch = sub.charAt(j) ;

    					if(ch=='[') {

    						brackets.push(Character.toString(ch)); 	

    						if(brackets.size()==1) {

    							first=j;

    						}

    					}

    					else if(ch==']') {
 	    		
    						if(brackets.size()==1) {
 	    		
    							last=j;

    						}

    					else {

    						brackets.pop();

    					}

    				}

    			}

 	    	int indexVal = (int) evaluate(sub.substring(first+1,last));
 	    	int num2 =0;
 	    	for(int m=0;m<arrays.size();m++) {

 	    		if(arrays.get(l).name.equals(str)) {
 	    			num2 =arrays.get(m).values[indexVal];
 	    			name = arrays.get(m).name;
 	    			i=0;
 	    			break;    			
 
 	    		}

 	    	}

 	    	String temp2="";

 	    	if(l==sub.length()-1) {

 	    		temp2 = name + sub.substring(first);

 	    	}

 	    	else {

 	    		temp2 = name+ sub.substring(first,last+1);

 	    	}

 	    	

 	    	ArrayList<String> temp4 = new ArrayList<String>();	

 	    	String track = "";

 	    	for(int n=0;n<sub.length();n++){

 	    		char ch3 = sub.charAt(n);

 	    		if(n>=first &&n<=last) {

 	    			if(i==first && !track.isEmpty()) {

 	    				temp4.add(track);

 	    				track="";

 	    				track+=ch3;

 	    			}

 	    	else if(i==last) {

 	    		track+=ch3;

 	    		temp4.add(track);
 	    		
 	    		track="";

 	    	}

 	    	else {

 	    		track+=ch3;

 	    	}

 	    		}

 	    	else if(ch3=='+'||ch3=='-'||ch3=='*'||ch3=='/') {

 	    		temp4.add(track);
 	    	
 	    		temp4.add(Character.toString(ch3));

 	    		track="";

 	    	}

 	    	else {
 	    		
 	    		track+=ch3;
 	    		
 	    		}

 	    	}

 	    	if(!track.isEmpty()) {

 	    		temp4.add(track);

 	    	}

 	    	for(int j=0;j<temp4.size();j++) {

 	    		if(temp4.get(j).equals(temp2)) {

 	    			temp4.set(j,Integer.toString((int)num2));

 	    		}

 	    	}

 	    	

 	    		sub="";

 	    		for(int k=0;k<temp4.size();k++) {

 	    			sub+=temp4.get(k);

 	    		}

 	    			str ="";
 	    		
    			}



 	    	

 	        

    	else if(ch2=='+'||ch2=='-'||ch2=='*'||ch2=='/'||Character.isDigit(ch2)||ch2=='['||ch2==']') {

    		if(!str.isEmpty()) {

    			for(int j=0;j<scalars.size();j++) {

    				if(scalars.get(j).name.equals(str)) {

    					num=scalars.get(j).value;

    					String rep = Integer.toString(num);

    					sub=sub.replaceAll(str,rep);

    					str= "";        	

    				}

    			}

    		}

        	 
        	  }

    	    	

    	else {

    		str+=ch2;

    	}

    	

    	}

    		if(!str.isEmpty()) {

    			for(int j=0;j<scalars.size();j++) {

    				if(scalars.get(j).name.equals(str)) {

    					num=scalars.get(j).value;

    					String rep = Integer.toString(num);

    					sub=sub.replaceAll(str,rep);

    					str= "";        	

    				}

    			}

	    	

    	}

    		str="";

        	ArrayList<String> split = new ArrayList<String>();

        	for(int i=0;i<sub.length();i++) {

        		char ch2=sub.charAt(i);

        		if(ch2=='+'||ch2=='-'||ch2=='*'||ch2=='/') {

        			split.add(str);

        			split.add(ch2+"");

        			str = "";

        		}

    	

    	else {

    		str+=ch2;

    	}

        	}

    	if(!str.isEmpty()) {

    		split.add(str);	

    	}

    	float one=0;

    	float two=0;

    	float result =0;

    	for(int i=0;i<split.size();i++) {

    		if(split.get(i).equals('/'+"")) {

    			one = Float.parseFloat(split.get(i-1));
    	
    			two = Float.parseFloat(split.get(i+1));

    			result = one/two;

    			split.set(i, Float.toString(result));

    			split.remove(i+1);

    			split.remove(i-1);

		    	i=0;

    		}

    	else if(split.get(i).equals('*'+"")) {

    		one = Float.parseFloat(split.get(i-1));

    		two = Float.parseFloat(split.get(i+1));
    	
    		result = one*two;
    	
    		split.set(i, Float.toString(result));

    		split.remove(i+1);
    	
    		split.remove(i-1);

    		i=0;

    		}

    	

    	

    	}

    		one =0;

    		two =0;

    		for(int i=0;i<split.size();i++) {

    			if(split.get(i).equals('-'+"")) {

    				one = Float.parseFloat(split.get(i-1));

    				two = Float.parseFloat(split.get(i+1));

    				result = one-two;

    				split.set(i, Float.toString(result));

    				split.remove(i+1);

    				split.remove(i-1);

    				i=0;

    			}

    			else if(split.get(i).equals('+'+"")) {

    					one = Float.parseFloat(split.get(i-1));

    					two = Float.parseFloat(split.get(i+1));

    					result = one+two;

    					split.set(i, Float.toString(result));

    					split.remove(i+1);
    	
    					split.remove(i-1);
    					
    					i=0;



    			}

    	

    	

    	}

    	

    	float answer = Float.parseFloat(split.get(0));

    	return answer;

    	}

    	else {

    		String temp2="";

    		if(l==sub.length()-1) {

    			temp2 = sub.substring(f);

    		}

    	else {

    		temp2 = sub.substring(f,l+1);

    	}

    		float num = evaluate(sub.substring(f+1,l));	// a-((b-c)) , a=2,b=3,c=2

    	// a-(1)

    	// 2 : - : 1

    	// f=2,l=8 -> evaluate (3,8) -> 1 

    		ArrayList<String> temp3 = new ArrayList<String>();	

    		String track = "";
    		
    		for(int i=0;i<sub.length();i++){

    			char ch3 = sub.charAt(i);

    			if(i>=f &&i<=l) {

    				if(i==f && !track.isEmpty()) {

    					temp3.add(track);

    					track="";

    					track+=ch3;

    				}

    				else if(i==l) {
    		
    					track+=ch3;

    					temp3.add(track);
    			
    					track="";

    				}

    			else {

    				track+=ch3;

    			}

    		}

    			else if(ch3=='+'||ch3=='-'||ch3=='*'||ch3=='/') {

    					temp3.add(track);

    					temp3.add(Character.toString(ch3));

    					track="";

    			}

    			else {

    				track+=ch3;
    				
    			}

    		}

    	if(!track.isEmpty()) {

    		temp3.add(track);

    	}

    	for(int j=0;j<temp3.size();j++) {

    		if(temp3.get(j).equals(temp2)) {

    			temp3.set(j,Integer.toString((int)num));

    	

    		}

    	}

    	

    	sub="";

    	for(int k=0;k<temp3.size();k++) {

    	sub+=temp3.get(k);

    	}

    	return evaluate(sub);

    	}

    	

     

    }



    /**

     * Utility method, prints the symbols in the scalars list

     */

    public void printScalars() {

        for (ScalarSymbol ss: scalars) {

            System.out.println(ss);

        }

    }

    

    /**

     * Utility method, prints the symbols in the arrays list

     */

    public void printArrays() {

    	for (ArraySymbol as: arrays) {

    	System.out.println(as);

    	}

    }



}


