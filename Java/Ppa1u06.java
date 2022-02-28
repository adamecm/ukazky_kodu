/**
 * program Ppa1u06
 */
package ppa1;

/**
 * @author adamecm
 * program Ppa1u06 zjistujici zda je zadane cislo kladne a zda je souctem prvocisel
 */
//import java.util.Locale;
import java.util.Scanner;
public class Ppa1u06 {
	static Scanner sc = new Scanner(System.in); 
	/**
	 * metoda pocitajici zda je zadane cislo prvocislem
	 * @param cislo
	 * @return
	 */
	public static boolean jePrvocislo(int cislo) {
		int delitel = 0;
		if (cislo<=1) {
			return false;
		}
		for (int i = 2; i <= (cislo-1); i++) {
			if (cislo%i==0) {
				delitel++;
			}
		}
		if (delitel>0) {
			return false;
		}
		return true;
	}
	/**
	 * metoda pocitajici zda je dane cislo soucet dvou prvocisel
	 * @param cislo
	 * @return
	 */
	public static int jeSoucetPrvocisel (int cislo) {
		for (int i = 2; i<=cislo; i++) {
			//for (int j = cislo-1; j>=1; j--) {
			int j = cislo-i;	
			if (jePrvocislo(i)&&jePrvocislo(j)) {
					//if (i+j==cislo) {
						return i;
				//	}
					
				//}
			}
		}
		return 0;
	}
	/**
	 * metoda nacitajici cislo pomoci Scanneru
	 * @param sc
	 * @return
	 */
	public static int nactiPrirozeneCislo(Scanner sc) {
		int a;
		while(true) {
			a = sc.nextInt();
			if (a>0) {
				return a;
			}
			
		}
		
	}
	/**
	 * main programu, zjistuje kladnost cisla nasledne moznost ho rozlozit na 2 prvocisla
	 * @param args
	 */
	public static void main(String[] args) {
		
		//int a = 0;
		System.out.println("Zadej prirozene cislo: ");
		
		int a = nactiPrirozeneCislo(sc);
		
	
		
			
		
		//System.out.println(jeSoucetPrvocisel(11));
		
		
		if(jeSoucetPrvocisel(a)>0) {
			System.out.format("Lze rozlozit: %d + %d",jeSoucetPrvocisel(a),(a-jeSoucetPrvocisel(a)));
		}
		else {
			System.out.println("Nelze rozlozit.");
		}
		
		
	}

}
