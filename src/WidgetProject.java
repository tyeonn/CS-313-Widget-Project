import java.io.*;

/** 
 * WidgetProject will store widgets and sell them to customers. If there is no inventory left, order is placed on backorder until restocked.
 * @author Tyeon
 *
 */
public class WidgetProject {
	
	public static void main(String[] args) {
		
		//Text file to be read
		String fileName = "transactions.txt";
		String line = null;

		try {

			FileReader fr = new FileReader(fileName);
			BufferedReader br = new BufferedReader(fr);

			while ((line = br.readLine()) != null) {
				
				if (line.charAt(0) == 'R' || line.charAt(0) == 'S') {
					
					//Makes a widget if the file is read correctly
					Widgets inventory = new Widgets(line);
					
				} else {
					
					System.out.println("Error in reading line.");
					
				}
				
			}

			br.close();

		} catch (IOException ioe) {

			ioe.printStackTrace();

		}
		
	}
	
}
