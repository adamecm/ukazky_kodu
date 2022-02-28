package ppa1;

/**
 * program Ppa1u05a pro generovani funkce dvou promennych 
 */

/**
 * @author adamecm
 * trida programu Ppa1u05a pocitajiciho hodnoty pro vizualizaci funkce dvou promennych
 */
import java.util.Locale;
import java.util.Scanner;
public class Ppa1u05a {
/**
 * metoda pro vypocet hodnot funkce
 * @param x
 * @param y
 * @param t
 * @return
 */
	public static double funkce(double x, double y, double t) {
		double funkce = Math.sin(Math.sqrt((x*x)+(y*y))-(2*t*Math.PI));
			return funkce;
	}
	/**
	 * @param args
	 * telo programu provadejici vypocty hodnot funkce pomoci for cyklu
	 */
	public static void main(String[] args) {
		Locale.setDefault(Locale.US);
		Scanner s = new Scanner(System.in);
		
		// TODO Auto-generated method stub
		
		System.out.println("Zadejte x1:");
		double x1 = s.nextDouble();
		
		System.out.println("Zadejte x2:");
		double x2 = s.nextDouble();
		
		System.out.println("Zadejte Xs:");
		double Xs = s.nextDouble();
		
		System.out.println("Zadejte y1:");
		double y1 = s.nextDouble();
		
		System.out.println("Zadejte y2:");
		double y2 = s.nextDouble();
	
		System.out.println("Zadejte Ys:");
		double Ys = s.nextDouble();
		
		System.out.println("Zadejte Ts:");
		double Ts = s.nextDouble();
		
		double krokX = Math.abs((x2 - x1))/(Xs-1);
		double krokY = Math.abs((y2 - y1))/(Ys-1);
		double krokT = 1 / Ts;
		
	
		double e = 0.00001;		
		System.out.println("x, y, z, t");
		for (double k = 0; k < 1; k += krokT) {
						
			for (double j = y1; j <= (y2+e); j += krokY) {
				
				for (double i = x1; i <= (x2+e); i += krokX) {
					System.out.format("%f,%f,%f,%f\n",i,j,funkce(i,j,k),k);
				}			
			}
		}
		
	}
	
}

