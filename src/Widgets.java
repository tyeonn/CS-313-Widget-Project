import java.io.*;
import java.text.*;
import java.util.*;

/**
 * Widgets will sort all the inventory depending on selling or stocking widgets.
 * @author Tyeon
 *
 */
public class Widgets {

	private int order = 0; //The order coming in
	private int stock = 0; //Stock to be placed in inventory
	private String backorder; // String to be placed in queue with the stock and price
	private double price = 0; //Price of the widget that popped from inventory
	private double totalCost = 0; //The total cost for me to get the inventory
	private static double maxPrice = 0; //The price I will use to sell to customers
	private String poppedLine = null; //The string popped from inventory stack
	private static Stack<String> inventoryStack = new Stack<String>(); //Stack that holds all the inventory
	private static Queue<String> backorderQueue = new LinkedList<String>(); //Queue that holds the backorders
	static String fileName = "Bookkeeping.txt"; //Text file to be written to
	static BufferedWriter bw = null;

	static {

		try {

			//This will write to the file
			bw = new BufferedWriter(new FileWriter(fileName));

		} catch (IOException ioe) {

			ioe.printStackTrace();

		}

	}

	/**
	 * Widgets will read in the line from transaction.txt and either stock or sell widgets
	 * @param line The string that is read from text file
	 */
	public Widgets(String line) {

		try {

			//When receiving widgets
			if (line.charAt(0) == 'R') {
				
				//Pushes widget into inventory stack
				inventoryStack.push(line.substring(2));
				
				System.out.println("\nPushed into inventory: "+inventoryStack.peek());
				
				//Checks if there are any orders on backorder. If there is, then sell this first.
				if (backorderQueue.peek() != null) {
					
					//string that is dequeued from backorder
					backorder = backorderQueue.remove();
					
					System.out.println("Popped from backorder: "+backorder);
					
					//Sells the backorder
					sell(backorder);
					
				}

			//When selling to customer
			} else {
				
				//Make order equal to the new order that came in
				order = Integer.parseInt(line.substring(2));
				System.out.println("\nNew order: "+order);
				
				//Sells new order
				sell(order);

			}

		} catch (NumberFormatException nfe) {
			
			System.out.println("The line does not consist of numbers.");
			
		}
		
	}

	/**
	 * This method will sell the backordered order
	 * @param backorder The string that was popped from the queue
	 */
	private void sell(String backorder) {
		
		int backorderSpace = backorder.indexOf(" "); //Sets this to be the space between the widget and price
		int order = Integer.parseInt(backorder.substring(0, backorderSpace)); //Sets order to be the widget to be sold
		double backorderPrice = Double.parseDouble(backorder.substring(backorderSpace + 1)); //Price that customer bought it for before
		int temp = order;
		
		// Loops until entire order is sold
		while (temp > 0) {

			//Pops from inventory if its not empty
			if (!inventoryStack.empty()) {
				
				poppedLine = inventoryStack.pop();
				
			//If there is no inventory, places left over order on backorder
			} else {
				
				System.out.println("The stack is empty. Placing "+temp+" widgets on backorder.");
				
				backorderQueue.add(temp + " " + backorderPrice);
				
				System.out.println("Pushed to backorder: "+backorderQueue.peek());
				
				break;
			}

			System.out.println("Popped from inventory: "+poppedLine);

			int space = poppedLine.indexOf(" "); //sets this to be the space between stock and price
			stock = Integer.parseInt(poppedLine.substring(0, space)); //stock is the widget
			price = Double.parseDouble(poppedLine.substring(space + 1)); //price of the inventory
			
			//Updates max price
			if (maxPrice < price) {
				maxPrice = price;
			}
			
			System.out.println("Backorder price: "+backorderPrice);
			System.out.println("Max price: "+maxPrice);

			//If the order is greater than the popped inventory, subtracts the stock from order
			if (temp >= stock) {
				
				temp -= stock;			
				
				//Writes to text file the widget I sold with the price I bought it for
				write(stock, price);
				
				//Updates the total cost that I bought the widgets for
				totalCost += (stock * price);
				
				System.out.println("Sold "+stock+" widgets. Left over order: "+temp);

			//If popped inventory is greater than the order then subtracts order from stock and places widget back into inventory
			} else {
				
				stock -= temp;
				
				System.out.println("Sold "+temp+" widgets. Left over inventory: "+ stock);
				
				//Writes to text file the widget I sold with the price that I bought it for
				write(temp, price);
				
				//Updates total cost that I bought the widgets for
				totalCost += (temp * price);
				
				//Sets temp to 0 since the entire order finished
				temp = 0;
				
				//Pushes left over stock back into inventory
				String pushLine = stock + " " + price;
				inventoryStack.push(pushLine);
				
				System.out.println("Pushed back into inventory: "+inventoryStack.peek());
				System.out.println("Max price: "+ maxPrice );

			}
			
			//Writes to text file the total cost for me to get the widgets
			write(totalCost);
			
			//If order is finished, writes to text file how much I sold it to the customer. Max price * 140%
			if (temp == 0) {
				
				writeCustomer(order, backorderPrice);
				
			//If entire order was not finished, writes to file the widgets that I sold for now
			} else {
				
				writeCustomer(stock, backorderPrice);
				
			}

		}

	}

	/**
	 * This method sells the new order that came in
	 * @param order The amount of widgets to sell
	 */
	private void sell(int order) {

		int temp = order;
		
		//Sells until the order is fulfilled
		while (temp > 0) {

			//If the inventory is not empty, pops widget from inventory
			if (!inventoryStack.empty()) {
				
				poppedLine = inventoryStack.pop();
				
			//Places order on backorder if there is no more stock
			} else {
				
				System.out.println("The stack is empty. Placing "+temp+" widgets on backorder.");
				
				backorderQueue.add(temp + " " + maxPrice);
				
				System.out.println("Pushed to backorder: "+backorderQueue.peek());
				
				break;
				
			}

			System.out.println("Popped from inventory: "+poppedLine);

			int space = poppedLine.indexOf(" ");
			stock = Integer.parseInt(poppedLine.substring(0, space)); //Widget to be sold
			price = Double.parseDouble(poppedLine.substring(space + 1)); //Price that I bought the widget for
			
			//Updates the max price
			if (maxPrice < price) {
				maxPrice = price;
			}
			System.out.println("Current price: "+price);
			System.out.println("Max price: "+ maxPrice );
			
			//
			if (temp >= stock) {
				
				temp -= stock;
				totalCost += (stock * price);
				write(stock, price);
				
				System.out.println("Sold "+stock+" widgets. Left over order: "+temp);
				
			} else {
				
				stock -= temp;
				
				System.out.println("Sold "+temp+" widgets. Left over inventory: "+ stock);
				
				write(temp, price);
				totalCost += (temp * price);
				temp = 0;
				String pushLine = stock + " " + price;
				inventoryStack.push(pushLine);
				
				System.out.println("Pushed back into inventory: " +inventoryStack.peek());
				System.out.println("Max price: "+maxPrice);

			}

		}

		if (totalCost != 0) {
			
			write(totalCost);
			
		}
		if (temp == 0) {
			
			writeCustomer(order, maxPrice);
			
		} else if (temp == order) {
			
			System.out.println("sold none");
			
		} else {
			
			writeCustomer(stock, maxPrice);
			
		}

	}

	/**
	 * Write method will write to the text file the total cost I paid for the widgets
	 * @param cost the cost for the widgets
	 */
	public void write(double cost) {
		
		try {
			//Puts the price in proper money format
			DecimalFormat df = new DecimalFormat("$#.00");
			bw = new BufferedWriter(new FileWriter(fileName, true));
			bw.write("Total cost: " + df.format(totalCost));
			bw.newLine();
			bw.close();

		} catch (IOException ioe) {

			ioe.printStackTrace();

		}
		
	}

	/**
	 * This write method will write to text file the bookkeeping records
	 * @param stock The amount of widget sold
	 * @param price The price I bought it for
	 */
	public void write(int stock, double price) {

		try {
			
			bw = new BufferedWriter(new FileWriter(fileName, true));
			bw.write("Bookkeeping: " + stock + " @ " + price);
			bw.newLine();
			bw.close();

		} catch (IOException ioe) {

			ioe.printStackTrace();

		}

	}

	/**
	 * This method will write to the text file how much I sold the widgets for to the customer
	 * @param stock
	 * @param price
	 */
	public void writeCustomer(int stock, double price) {

		try {
			
			DecimalFormat df = new DecimalFormat("$#.00");
			bw = new BufferedWriter(new FileWriter(fileName, true));
			//Sells to the customer the max price * 1.4
			bw.write("Sold to customer: " + stock + " widgets @ " + "(" + df.format(price) + " * 140%)" + "= "
					+ df.format(stock * price * 1.4));
			bw.newLine();
			bw.newLine();
			bw.close();

		} catch (IOException ioe) {

			ioe.printStackTrace();

		}
		
	}
	
}
