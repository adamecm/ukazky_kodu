package cv05;


import java.util.Arrays;
import java.util.Random;

/**
 * Testovani razeni
 * @author Libor Vasa 
 */

public class SortingTest {
	public static boolean crashed = false;
	
	public static void main(String[] args) {		
		ISortingAlgorithm algorithm;
		String[] name = {"Insertion sort","QuickSort","MergeSort","JavaSort"};
		int velikostDat= 100;
		
		/*
		algorithm = new Mergesort();
		int[] data = {17,9,456889,47,35,1,0,75,9};
		int[] data2 = Arrays.copyOf(data, data.length);
		
		System.out.println(Arrays.toString(data));
		algorithm.onlySort(data);
		System.out.println(Arrays.toString(data));
		
		System.out.println(Arrays.toString(data2));
		System.out.println("bubbleh");
		bubbleh(data2);
		System.out.println(Arrays.toString(data2));
		//*/
		
		for (int i = 0; i <= 3; i++) {
			if (i==0) {
				algorithm = new InsertSort();
			}
			else if (i==1) {
				algorithm = new QuickSort();
			}
			else if (i==2) {
				algorithm = new Mergesort();
			}
			else{
				algorithm = new JavaSort();
			}
			
			try {
				System.out.println(name[i]+":");
				if (testCorrectness(algorithm)) {
					testCounts(algorithm);
				} 
			}
			catch (StackOverflowError e) {
				crashed = true;
				System.out.println("Doslo k preteceni zasobniku, nejspise pri mereni porovnani QuickSortu u jiz serazeneho pole.");
			}
			finally {
				if (crashed) {
					System.out.println("Experiment probehne znovu bez mereni porovnani pro jiz serazene pole.");
					System.out.println(name[i]+" again without counting compares:");
					testCounts(algorithm);
				}
				crashed = false;
				timeMeasure(algorithm,velikostDat);
				System.out.println();
				}
			
		}//*/
		System.out.println("Experiment finished");
	}


	/**
	 * Otestuje pocty porovani zadaneho razeni
	 * @param algorithm testovany algoritmus razeni 
	 */
	private static void testCounts(ISortingAlgorithm algorithm) {
		int MIN_LENGTH = 100;
		int MAX_LENGTH = 100000;
		int TEST_COUNT = 100;
		int inSorted = 0;
		for (int length = MIN_LENGTH; length < MAX_LENGTH; length *= 2) {
			int minComp = Integer.MAX_VALUE;
			int maxComp = 0;
			for (int test = 0; test < TEST_COUNT; test++) {
				int[] data = generateData(length);
				algorithm.sort(data);
				//pokud quicksort probehne pro jiz serazene pole, dojde k preteceni zasobniku
				//algorithm.sort(data); 
				if (algorithm.comparesInLastSort() > maxComp) {
					maxComp = algorithm.comparesInLastSort();
				}
				if (algorithm.comparesInLastSort() < minComp) {
					minComp = algorithm.comparesInLastSort();
				}
				//pokud quicksort probehne pro jiz serazene a velke pole
				//dojde pravdepodobne k preteceni zasobniku
				if (crashed==false) {
					algorithm.sort(data);
					inSorted = algorithm.comparesInLastSort();
				}
								
				
			}
				if (!crashed) {
					System.out.println("Length: " + length + ", Min:" + minComp + ", Max:" + maxComp+", In sorted array:"+inSorted);
				}
				else if (crashed) {
					System.out.println("Length: " + length + ", Min:" + minComp + ", Max:" + maxComp);
				}
				
		}
		
	}
	/**
	 * Otestuje spravnost zadaneho razeni
	 * @param algorithm testovany algoritmus razeni
	 */
	private static boolean testCorrectness(ISortingAlgorithm algorithm) {
		for (int i = 0; i < 100; i++) {
			int[] data = generateData(100);
			int[] dataCopy = new int[data.length];
			for (int j = 0; j < data.length; j++) {
				dataCopy[j] = data[j];
			}
			algorithm.sort(data);
			Arrays.sort(dataCopy);
			for (int j = 0; j < data.length; j++) {
				if (data[j] != dataCopy[j]) { //zde bylo i u dataCopy nahrazeno za j,
					//aby byly porovnávány prvky na stejných pozicích v obou polích
					System.out.println("Algorithm failed, terminating.");
					return false;
				}
			}			
		}
		System.out.println("Algorithm passed test, continuing.");
		return true;
	}

	/**
	 * Vygeneruje pole o zadane velikosti c nahodnych cisel z intervalu <0; c)
	 * @param c velikost vygenerovaneho pole a horni hranice generovanych hodnot
	 * @return vygenerovane pole nahodnych cisel
	 */
	private static int[] generateData(int c) {
		int[] result = new int[c];
		Random rnd = new Random();
		for (int i = 0; i < c; i++) {
			result[i] = rnd.nextInt(c);
		}
	
		/*
		//kontrola poctu porovnoani pro pole unikatnich prvku
		for (int i = 0; i < c; i++) {
			result[i] = i+1;
		}*/
		return result;
	}
	/**
	 * Metoda ktera meri cas serazeni nahodneho pole danym algortimem a pak nasledne cas "serazeni"
	 * jiz serazeneho pole pomoci System.nanoTime(), vysledek je vypsan v µs
	 * @param algorithm je konkretni dany algoritmus, napr. quicksort nebo javasort
	 * @param lengthOfData urcuje jak dlouhe ma byt pole nahodnych dat
	 */
	private static void timeMeasure(ISortingAlgorithm algorithm, int lengthOfData) {
		long start = 0;
		long end = 0;
		long time = 0;
		long timeS = 0;
		
		int[] data = generateData(lengthOfData);
		start = System.nanoTime();
		algorithm.onlySort(data);
		end = System.nanoTime();
		time = end - start;
		
		start = System.nanoTime();
		algorithm.onlySort(data);
		end = System.nanoTime();
		timeS = end - start;
		
		
		System.out.println("Time needed:");
		System.out.format("Unsorted array: %.2f µs\n",(double)time/1000);
		System.out.format("Unsorted array: %.2f µs\n",(double)timeS/1000);
		}
	/*
	public static void bubbleh(int[] arr) {
		int n = arr.length;
	    
	    for (int i = 0; i < n; i++) {
	        for (int j = 1; j < (n - i); j++) {

	            if (arr[j - 1] > arr[j]) {
	            	int temp = arr[j - 1];
	                arr[j - 1] = arr[j];
	                arr[j] = temp;
	            }

	        }
	    }
		
	}*/
}