package cv05;

import java.util.Arrays;
/**
 * trida vola metodu Arrays.sort() ktera je soucasti Javy
 * @author ADMIN
 *
 */
public class JavaSort implements ISortingAlgorithm{

	@Override
	public void sort(int[] data) {
		Arrays.sort(data);
	}
	
	/**
	 * vraci 0 jelikoz vnitrni pocet porovnani v metode je neznamy
	 */
	@Override
	public int comparesInLastSort() {
		return 0;
	}
	
	@Override
	public void onlySort(int[] data) {
		Arrays.sort(data);
	}
}
