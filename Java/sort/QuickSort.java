package cv05;

public class QuickSort implements ISortingAlgorithm{
	int end;
	int start;
	int compares = 0;
	boolean counting = true;
	
	/**
	 * metoda split prevzata z prednasky cislo 4
	 * @param data
	 * @param left
	 * @param right
	 * @return
	 */
	public int split(int[] data, int left, int right){
		int pivot = data[right];
		
		if (counting) {
			while(true){
				//greater than vrati true pokud prvni vetsi nez druhy
				while (greaterThan(pivot,data[left])&&(left<right))
					left++;
				
				if (left<right){
					data[right] = data[left];
					right--;
				}
				else break;
			
				while (greaterThan(data[right],pivot)&&(left<right))
					right--;
				
				if (left<right){
					data[left] = data[right];
					left++;
				} 
				else break;
			}
		}
		else {
			while(true){
				while ((pivot>data[left])&&(left<right))
					left++;
					
				if (left<right){
					data[right] = data[left];
					right--;
				}
				else break;
				
				while ((data[right]>pivot)&&(left<right))
					right--;
					
				if (left<right){
					data[left] = data[right];
					left++;
				} 
				else break;
			}
		}
		
		data[left] = pivot;
		return(left);
	}
	
	boolean greaterThan(int i, int v) {
		compares++;
		return i > v;
	}
	
	/**
	 * metoda quicksort prevzata z prednasky cislo 4
	 * @param data
	 * @param start
	 * @param end
	 */
	public void quickSort(int[] data, int start, int end){
		if (end<=start) return;
		int splitted = split(data, start, end);
		quickSort(data, start, splitted-1);
		quickSort(data, splitted+1, end);
	}
	

	@Override
	public void sort(int[] data) {
		compares= 0;	
		counting = true;
		quickSort(data, 0, data.length-1);
	}

	@Override
	public int comparesInLastSort() {
		return compares;
	}
	@Override
	public void onlySort(int[] data) {
		counting = false;
		quickSort(data, 0, data.length-1);
	}
	
}
