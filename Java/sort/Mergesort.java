package cv05;

/**
 * kod tridy prevzaty z prednasky 4
 *
 */
public class Mergesort implements ISortingAlgorithm{
	int end;
	int start;
	int compares = 0;
	boolean counting = true;
	
	int[] bitonic(int[] data, int start, int mid, int end){
		int[] result = new int[end-start+1];
		for (int i = start;i<=mid;i++)
		result[i-start] = data[i];
		for (int i = mid+1;i<=end;i++)
		result[end - start + mid + 1 - i] = data[i];
		return result;
		}

	void mergeBitonic(int[] data, int start, int[] bitonic){
		int i = 0;
		int j = bitonic.length-1;
		if (counting) {
			for (int k = 0;k<bitonic.length;k++) {
				//greater than vrati true pokud prvni vetsi nez druhy
				data[start+k] = greaterThan(bitonic[j],bitonic[i])?
				bitonic[i++]:bitonic[j--];
			}
		}
		else {
			for (int k = 0;k<bitonic.length;k++) {
				//greater than vrati true pokud prvni vetsi nez druhy
				if (bitonic[j]>bitonic[i]) {
					data[start+k] = bitonic[i++];
				}
				else {
					data[start+k] = bitonic[j--];
				}
			}
		}
		
	}
	
	public void mergeSort(int[] data, int start, int end){
		if (end==start)
		return;
		int mid = (start + end)/2;
		mergeSort(data, start, mid);
		mergeSort(data, mid+1, end);
		
		int[] temp = bitonic(data, start, mid, end);
		mergeBitonic(data, start, temp);
		}
	
	void mergeSort(int[] data){
		mergeSort(data, 0, data.length-1);
	}
	
	boolean greaterThan(int i, int v) {
		compares++;
		return i > v;
	}	
	
	
	
	@Override
	public void sort(int[] data) {
		counting=true;
		start = 0;
		end = data.length-1;
		compares= 0;	
		mergeSort(data, start, end);
	}

	@Override
	public int comparesInLastSort() {
		return compares;
	}
	
	@Override
	public void onlySort(int[] data) {
		counting=false;
		mergeSort(data, 0, data.length-1);
	}
}
