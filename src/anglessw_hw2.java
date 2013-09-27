import java.util.Arrays;
import java.util.Scanner;
import java.lang.Thread;

/**
 * Steven Angles
 * CSE381 - Fall 2013
 * Homework 2
 * 
 * anglessw_hw2.java
 * Calculates the Minimum Scalar Product of two vectors
 * with a brute force approach of permutating the vector
 * over multiple threads.
*/
public class anglessw_hw2 {
	/* String variables for user I/O */
	private static final String ENTER_SIZE = "Enter vector size: ";
	private static final String ENTER_VECTOR_ONE = "Enter data for vector 1: ";
	private static final String ENTER_VECTOR_TWO = "Enter data for vector 2: ";
	private static final String ENTER_THREAD_COUNT = "Enter number of threads to use: ";
	private static final String LOWEST_SCALAR_PRODUCT = "Lowest scalar product is: ";

	/* Variables for passing to new thread objects */
	private static int vectorSize;
	private static int numThreads;
	private static int[] vectorOne = null;
	private static int[] vectorTwo = null;
	
	/* Final tallies for computing best thread output */
	private static int finalMinSum;
	private static int[] finalMinVector = null;
	
	public static void main(String[] args) throws InterruptedException {
		getInput();
		runThreads();
		printResults();
	}
	
	/**Gets necessary input from user
	*/
	private static void getInput() {
		Scanner keyboard = new Scanner(System.in);

		System.out.print(ENTER_SIZE);
		vectorSize = keyboard.nextInt();
		vectorOne = new int[vectorSize];
		vectorTwo = new int[vectorSize];

		System.out.println(ENTER_VECTOR_ONE);
		for (int i=0; (i<vectorSize); i++) {
			vectorOne[i] = keyboard.nextInt();
		}

		System.out.println(ENTER_VECTOR_TWO);
		for (int i=0; (i<vectorSize); i++) {
			vectorTwo[i] = keyboard.nextInt();
		}

		System.out.println(ENTER_THREAD_COUNT);
		numThreads = keyboard.nextInt();
		
		keyboard.close();
	}
	
	/**Runs the specified threads and computes the best result
	*/
	private static void runThreads() throws InterruptedException {
		Helper[] threads = new Helper[numThreads];
		int numPerThread = vectorSize / numThreads;
		for (int i=0; (i<numThreads); i++) {
			int start = i*numPerThread;
			int end = (start+numPerThread);
			threads[i] = new Helper(vectorOne, vectorTwo, start, end);
			threads[i].start();
		}
		
		for (Helper thread : threads) {
			thread.join();
		}

		for (int i=0; (i<threads.length); i++) {
			int minSum = threads[i].getMinSum();
			int[] minVector = threads[i].getMinVector();
			if (i == 0) {
				finalMinSum = minSum;
				finalMinVector = Arrays.copyOf(minVector, minVector.length);
			} else if (minSum < finalMinSum) {
				finalMinSum = minSum;
				finalMinVector = Arrays.copyOf(minVector, minVector.length);
			}
		}
	}
	
	/**Prints result of run to the user
	*/
	private static void printResults() {
		System.out.println(LOWEST_SCALAR_PRODUCT);
		System.out.println("vec1: " + Arrays.toString(finalMinVector));
		System.out.println("vec2: " + Arrays.toString(vectorTwo));
		System.out.println("Scalar product: " + finalMinSum);
	}

	/**Thread helper class that handles computation
	*/
	private static class Helper extends Thread {
		private int[] one = null;
		private int[] two = null;
		private int start;
		private int end;
		private int sum = 0;
		private int[] minVector = null;
		private boolean iterated = false;

		public Helper(int[] vectorOne, int[] vectorTwo, int startIndex, int endIndex) {
			this.one   = vectorOne;
			this.two   = vectorTwo;
			this.start = startIndex;
			this.end   = endIndex;
		}
		
		public int[] getMinVector() {
			return minVector;
		}
		
		public int getMinSum() {
			return sum;
		}
		
		/**Recursive permutation generation method
		*/
		private void genPerm(int[] vector, int anchor) {
			if (anchor > (vector.length-1)) {
				int tmpSum = calcSum(vector);
				if (!iterated) {
					sum = tmpSum;
					minVector = Arrays.copyOf(vector,vector.length);
					iterated = true;
				} else if (tmpSum < sum) {
					sum = tmpSum;
					minVector = Arrays.copyOf(vector,vector.length);
				}
				return;
			}

			for (int i=anchor; (i<vector.length); i++) {
				swap(vector, i, anchor);
				genPerm(vector, anchor+1);
				swap(vector, anchor, i);
			}
		}
		
		/**
		 * Calculates the scalar product of the input
		 * and the thread's second vector.
		 * @param vector
		 * 			The int array to calculate the scalar product against.
		 * @return The computed scalar product.
		 */
		private int calcSum(int[] vector) {
			int tmpSum = 0;
			for (int i=0; (i<vector.length); i++) {
				tmpSum += vector[i]*two[i];
			}
			return tmpSum;
		}
		
		/**
		 * Convenience method for swapping two values of an array.
		 * @param arr
		 * 			The int array with values to swap.
		 * @param idx1
		 * 			The first index of the value to swap.
		 * @param idx2
		 * 			The second index of the value to swap.
		 */
		private void swap(int[] arr, int idx1, int idx2) {
			int temp = arr[idx1];
			arr[idx1] = arr[idx2];
			arr[idx2] = temp;
		}
		
		/**
		 * Run method that divides the array 
		 * appropriately and calculates a subset
		 * of the permutations.
		 */
		public void run() {
			int[] tmpOne = Arrays.copyOf(one, one.length);
			for (int i=start; i<end; i++) {
				swap(tmpOne, 0, i);
				genPerm(tmpOne, 1);
				swap(tmpOne, i, 0);
			}
		}
	}
}
