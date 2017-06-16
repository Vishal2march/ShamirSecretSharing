package cy;

import java.math.BigInteger;

public class ShamirSecret {
	BigInteger s = new BigInteger("12");
	BigInteger a1 = new BigInteger("2");
	BigInteger a2 = new BigInteger("3");
	BigInteger p = new BigInteger("23");
	BigInteger[] y = new BigInteger[5];
	
	public void txtShare(){
		
		for(int i =1; i<= 5;i++){
			int c=i-1;
			BigInteger x = new BigInteger(""+i);
			y[c] =  (s.add((a1.multiply(x)).add((a2.multiply(x.multiply(x)))))).mod(p);
			System.out.println("( "+i+" , "+y[i]+" )");
		}
	}
	
	public void txtRecon(){
		
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
