package ca.johnwu.UWSectionFinder;
import java.io.*;
import java.util.*;
import java.net.*;

// Begin class Query

public class Query {

	// Defining global private objects

	private URL url;
	private HttpURLConnection connection;

	private BufferedReader in;
	private BufferedWriter out;

	private ArrayList<String> result = new ArrayList<String>();

	private String url_base = "https://api.uwaterloo.ca/v2";
	private String apikey = "ff289c3bebd946bed86f24c043c94eab";
	private String filename = "text.txt";
	private String data = "";
	private String err_course = "";
	private String err_catalog = "";
    public String getScore(String name, InputStream i) throws IOException{
        BufferedReader br= new BufferedReader(new InputStreamReader(i));
      try{
      String current= "";
      
      while ((current = br.readLine()) != null) {
              System.out.println(current);
              if (name.contains(current.substring(0,current.length()-4))){
              //if (current.substring(0,current.length()-4).contains(name)){
                      return current.substring(current.length()-3);
              }
      }
      
      } catch (IOException e){
              e.printStackTrace();
      }
      br.close();
      return "N/A";

      
}
	/**
	 * Constructor Query()
	 * Allows a Query object to be constructed with no parameters
	 * No parameters
	 */

	public Query() {

	}

	/**
	 * Method Get(String request)
	 * Retrieves the class data with the given request and
	 * makes a request to display the result
	 * Returns: void
	 * @param String request - the request to be made to the API
	 */

	public void Get(String request) throws IOException {

		// Establishing a connection to the UW API link

		url = new URL (url_base.concat(request.concat(".json?key=".concat(apikey))));
		in = new BufferedReader(new InputStreamReader(url.openStream()));
		connection = (HttpURLConnection)url.openConnection();
		connection.setRequestMethod("GET");
		connection.connect();

		// Defining local variables for later use

		int index_tut;
		int index_lab;
		int index_tst;
		int index_oln;
		Boolean error = false;

		String s;

		// Retrieving and filtering out relevant data to keep

		s = in.readLine();
		s = s.replace("\"","\"").trim();

		loop1: while (s != null) {
			if ((s.startsWith("section")) || (s.startsWith("start_time")) || (s.startsWith("end_time")) || (s.startsWith("weekdays")) || (s.startsWith("building"))) {
				data = data.concat(s+" ");
			}
			else if (s.startsWith("room")) {
				data = data.concat(s+", ");
			}
			else if (s.startsWith("instructors")) {
				s = in.readLine();
				s = s.replace("\"", "").trim();
				if (s.length() == 0) {
					data = data.concat("instructors:Staff, ");
				}
				else {
					data = data.concat("instructors:".concat(s+", "));
				}
			}
			else if (s.startsWith("start_date")) {
				data = data.concat(s+" ");
			}
			else if ((s.startsWith("message")) && (s.contains("No data returned"))) {
				error = true;
				break loop1;
			}

			s = in.readLine();
			if (s != null) {
				s = s.replace("\"", "").trim();
			}
		}

		in.close();	// Closes the InputStream

		if (error) {
			result.add("Error: There are no sections found for ".concat(err_course.concat(err_catalog.concat("."))));
			returnError();
		}
		else {

			// Removes all tutorials

			index_tut = data.indexOf("section:TUT");

			if (index_tut >= 0) {
				data = data.substring(0, index_tut);
			}

			// Removes all labs

			index_lab = data.indexOf("section:LAB");

			if (index_lab >= 0) {
				data = data.substring(0, index_lab);
			}

			// Removes all tests

			index_tst = data.indexOf("section:TST");

			if (index_tst >= 0) {
				data = data.substring(0, index_tst);
			}

			// Removes all online sections

			index_oln = data.indexOf("section:OLN");

			if (index_oln >= 0) {
				data = data.substring(0, index_oln);
			}

			// Basically tokenizes the data for split soon

			data = data.replace(", ", "&");

			if (data.length() == 0) {
				result.add("Error: "+err_course+err_catalog+" is not offered offline.");
			}
			else {

				// Request made to process the data

				Process(data);

			}

		}
	}

	/**
	 * Method Process(String data)
	 * Given data, splits the data and stores it into an array of strings
	 * Then, restructures the data so that each element of the result
	 * array contains all the needed information for one section
	 * Returns: void
	 * @param String data - the data of all the sections to be processed
	 */

	private void Process(String data) {

		String classes[] = data.split("&");

		String section = "";
		for (int i = 0; i < classes.length; i++) {
			if ((classes[i].contains("start_date")) || (classes[i].contains("end_date"))) {	        
				classes[i] = classes[i].substring(classes[i].indexOf(":")+1);
			}
			else {
				classes[i] = classes[i].substring(classes[i].indexOf(":")+1);
				if (classes[i].equals("null")) {
					classes[i] = "TBD";
				}
			}
		}

		for (int i = 0; i < classes.length; i = i) {

			// (2.4.4) Updated code to reflect temporary change in structure of response

			if (classes[i].startsWith("LEC")) {
				if (classes[i+4].equals("null")) {
					section = classes[i];
					result.add(section.concat(" ".concat(classes[i+3].concat(" ".concat(classes[i+1].concat("-".concat(classes[i+2])))))));
				//	result.add(classes[i+9].concat(" ".concat(classes[i+10].concat(" ".concat(classes[i+11])))));
					result.add(classes[i+5].concat(" ".concat(classes[i+6].concat(" ".concat(classes[i+7])))));
				}
				//	i = i+12;
					i = i+8;
			}
			else {
				if (classes[i+3].equals("null")) { 
					result.add(section.concat(" ".concat(classes[i+2].concat(" ".concat(classes[i].concat("-".concat(classes[i+1])))))));
				//	result.add(classes[i+8].concat(" ".concat(classes[i+9].concat(" ".concat(classes[i+10])))));
					result.add(classes[i+4].concat(" ".concat(classes[i+5].concat(" ".concat(classes[i+6])))));
				}
				//	i = i+11;
					i = i+7;
			}
		}
	}

	/**
	 * Method requestData(String course, int catalog)
	 * requestData converts the given inputs (course and catalog) to
	 * make a request to get the data indicated by the inputs
	 * Returns: void
	 * @param: String course - the course name
	 * @param: String catalog - the course catalog number
	 */		

	public void requestData(String course, String catalog) throws IOException {
		err_course = course;
		err_catalog = catalog;
		Get("/courses/".concat(course.concat("/".concat(catalog.concat("/schedule")))));
	}

	/**
	 * Method getData()
	 * getData() returns the result array containing information of
	 * all the sections
	 * Returns: ArrayList<String>
	 * No parameters
	 */

	public ArrayList<String> getData() {
		return result;
	}

	/**
	 * Method printData()
	 * printData() outputs the result array onto the display in an
	 * organized format
	 * Returns: void
	 * No parameters
	 */	

	public void printData() {
		for (int i = 0; i < result.size(); i++) {
			System.out.println(result.get(i));
		}	
	}

	/**
	 * Method setFileName()
	 * setFileName() is a mutator method to change the stored
	 * file name for output purposes
	 * Returns: void
	 * @param: String fname - the desired new file name for output
	 */	

	public void setFileName(String fname) {
		filename = fname;
	}

	/**
	 * Method outputDataToFile()
	 * printData() outputs the result array into a .txt file with the stored
	 * filename in an organized format
	 * Returns: void
	 * No parameters
	 */	

	public void outputDataToFile() throws IOException {
		out = new BufferedWriter (new FileWriter(filename));
		for (int i = 0; i < result.size(); i++) {
			out.write(result.get(i));
			out.newLine();
		}
		out.close();
	}

	/**
	 * Method returnError()
	 * returnError() returns an error message inside an ArrayList<String>
	 * Returns: ArrayList<String>
	 * No parameters
	 */	

	public ArrayList<String> returnError() {
		return result;
	}

}