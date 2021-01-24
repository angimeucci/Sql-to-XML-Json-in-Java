import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import java.beans.XMLEncoder;
import java.math.BigDecimal;
import java.io.*;
import java.net.*;
import java.util.*;
public class JavaWebServerStXaJ implements Runnable{ 
	
	static final File WEB_ROOT = new File("../");
	static final String DEFAULT_FILE = "Sito/index.html";
	static final String FILE_NOT_FOUND = "Sito/404.html";
	static final String METHOD_NOT_SUPPORTED = "not_supported.html";
	// port to listen connection
	static final int PORT = 8080;
	
	// verbose mode
	static final boolean verbose = true;
	
	// Client Connection via Socket Class
	private Socket connect;
        
	public JavaWebServerStXaJ(Socket c) {
		connect = c;
	}
	
	public static void main(String[] args) {
                /*File directory = new File("./");
                System.out.println(directory.getAbsolutePath());*/
		try {
			ServerSocket serverConnect = new ServerSocket(PORT);
			System.out.println("Server started.\nListening for connections on port : " + PORT + " ...\n");
			
			// we listen until user halts server execution
			while (true) {
				JavaWebServerStXaJ myServer = new JavaWebServerStXaJ(serverConnect.accept());
				
				if (verbose) {
					System.out.println("Connecton opened. (" + new Date() + ")");
				}
				
				// create dedicated thread to manage the client connection
				Thread thread = new Thread(myServer);
				thread.start();
			}
		} catch (IOException e) {
			System.err.println("Server Connection error : " + e.getMessage());
		}
	}
	@Override
	public void run() {
		// we manage our particular client connection
		BufferedReader in = null; PrintWriter out = null; BufferedOutputStream dataOut = null;
		String fileRequested = null;
		
		try {
			// we read characters from the client via input stream on the socket
			in = new BufferedReader(new InputStreamReader(connect.getInputStream()));
			// we get character output stream to client (for headers)
			out = new PrintWriter(connect.getOutputStream());
			// get binary output stream to client (for requested data)
			dataOut = new BufferedOutputStream(connect.getOutputStream());
			
			// get first line of the request from the client
			String input = in.readLine();
			// we parse the request with a string tokenizer
			StringTokenizer parse = new StringTokenizer(input);
			String method = parse.nextToken().toUpperCase(); // we get the HTTP method of the client
			// we get file requested
			fileRequested = parse.nextToken().toLowerCase();
			
			// we support only GET and HEAD methods, we check
			                  if (!method.equals("GET")  &&  !method.equals("HEAD")) {
                            if (verbose) {
                                    System.out.println("501 Not Implemented : " + method + " method.");
                            }

                            // we return the not supported file to the client
                            File file = new File(WEB_ROOT, METHOD_NOT_SUPPORTED);
                            int fileLength = (int) file.length();
                            String contentMimeType = "text/html";
                            //read content to return to client
                            byte[] fileData = readFileData(file, fileLength);

                            // we send HTTP Headers with data to client
                            out.println("HTTP/1.1 501 Not Implemented");
                            out.println("Server: Java HTTP Server from SSaurel : 1.0");
                            out.println("Date: " + new Date());
                            out.println("Content-type: " + contentMimeType);
                            out.println("Content-length: " + fileLength);
                            out.println(); // blank line between headers and content, very important !
                            out.flush(); // flush character output stream buffer
                            // file
                            dataOut.write(fileData, 0, fileLength);
                            dataOut.flush();
                            
                        } else if(fileRequested.contains("punti-vendita.xml")){
                            
                            ObjectMapper mapper = new ObjectMapper();
                            puntiVendita p = mapper.readValue(new File("Sito/DESErializzazione/puntiVendita.json"), puntiVendita.class);
                            XmlMapper xmlMapper = new XmlMapper();
                            xmlMapper.writeValue(new File("puntiVendita.xml"), p);
                            File fileinxml = new File("puntiVendita.xml");
                            File file = new File(WEB_ROOT, FILE_NOT_FOUND);
                            int fileLength = (int) file.length();
                            String content = "application/xml";
                            String xml = xmlMapper.writeValueAsString(p);

                            out.println("HTTP/1.1 200 OK");
                            out.println("Location: " + fileRequested);
                            out.println("Server: Java HTTP Server from SSaurel : 1.0");
                            out.println("Date: " + new Date());
                            out.println("Content-type: " + content);
                            out.println("Content-length: " + xml.length());
                            out.println(); // blank line between headers and content, very important !
                            out.flush(); // flush character output stream buffer

                            out.write(xml);
                            out.flush();

                            if (verbose) System.out.println("File " + fileRequested + " not found");
                          
                        }else if (fileRequested.endsWith("/db")){
                            EsempioDB database = new EsempioDB();
                            Vector v = new Vector();
                            v = database.takevalue();
                            System.out.println("Current saved elements: "+ v.get(1) + " " + v.get(2)); 
                            
                        }else if (fileRequested.endsWith("/db/persona-xml")){
                            EsempioDB database = new EsempioDB();
                            Vector v = new Vector();
                            v = database.takevalue();
                            
                            XmlMapper xmlMapper = new XmlMapper();
                            xmlMapper.writeValue(new File("dbxml.xml"), v);
                            File fileinxml = new File("dbxml.xml");
                            File file = new File(WEB_ROOT, FILE_NOT_FOUND);
                            int fileLength = (int) file.length();
                            String content = "application/xml";
                            String xml = xmlMapper.writeValueAsString(v);
                            
                            out.println("HTTP/1.1 200 OK");
                            out.println("Location: " + fileRequested);
                            out.println("Server: Java HTTP Server from SSaurel : 1.0");
                            out.println("Date: " + new Date());
                            out.println("Content-type: " + "application/xml");
                            out.println("Content-length: " + content);
                            out.println(); // blank line between headers and content, very important !
                            out.flush(); // flush character output stream buffer

                            out.write(xml);
                            out.flush();

                            if (verbose) System.out.println("File " + fileRequested + " not found");
                          
                        }else if (fileRequested.endsWith("/db/persona-json")) {
                            EsempioDB database = new EsempioDB();
                            Vector v = new Vector();
                            v = database.takevalue();
                            try {
                                File fileJSON = new File(WEB_ROOT, "dbjson.json");
                                ObjectMapper objectMapper = new ObjectMapper();
                                objectMapper.writeValue(new FileOutputStream("dbjson.json"), v);
                                File fileinjson = new File("dbjson.json");
                                int fileLength = (int) fileJSON.length();
                                byte[] fileData = readFileData(fileJSON, fileLength);

                                dataOut.write(fileData, 0, fileLength);
                                dataOut.flush();
                                
                                out.println("HTTP/1.1 200 OK");
                                out.println("Location: " + fileRequested);
                                out.println("Server: Java HTTP Server from SSaurel : 1.0");
                                out.println("Date: " + new Date());
                                out.println("Content-type: " + "application/xml");
                                out.println("Content-length: " + "application/json");
                                out.println(); // blank line between headers and content, very important !
                                out.flush(); // flush character output stream buffer

                                out.write(fileJSON.toString());
                                out.flush();

                                if (verbose) System.out.println("File " + fileRequested + " not found");
                            }catch (Throwable e){e.getMessage();} 
                            
                        } else {
                            // GET or HEAD method
                            if (fileRequested.endsWith("/")) {
                                    fileRequested += DEFAULT_FILE;
                            }

                            File file = new File(WEB_ROOT, fileRequested);
                            int fileLength = (int) file.length();
                            String content = getContentType(fileRequested);

                            if (method.equals("GET")) { // GET method so we return content
                                    byte[] fileData = readFileData(file, fileLength);
                                    
                                    // send HTTP Headers
                                    out.println("HTTP/1.1 200 OK");
                                    out.println("Server: Java HTTP Server from SSaurel : 1.0");
                                    out.println("Date: " + new Date());
                                    out.println("Content-type: " + content);
                                    out.println("Content-length: " + fileLength);
                                    out.println(); // blank line between headers and content, very important !
                                    out.flush(); // flush character output stream buffer

                                    dataOut.write(fileData, 0, fileLength);
                                    dataOut.flush();
                            }

                            if (verbose) {
                                    System.out.println("File " + fileRequested + " of type " + content + " returned");
                            }
				
			}
			
		} catch (FileNotFoundException fnfe) {
			try {
				fileNotFound(out, dataOut, fileRequested);
			} catch (IOException ioe) {
				System.err.println("Error with file not found exception : " + ioe.getMessage());
			}
			
		} catch (IOException ioe) {
			System.err.println("Server error : " + ioe);
		} finally {
			try {
				in.close();
				out.close();
				dataOut.close();
				connect.close(); // we close socket connection
			} catch (Exception e) {
				System.err.println("Error closing stream : " + e.getMessage());
			} 
			
			if (verbose) {
				System.out.println("Connection closed.\n");
			}
		}
        }
	private byte[] readFileData(File file, int fileLength) throws IOException {
		FileInputStream fileIn = null;
		byte[] fileData = new byte[fileLength];
		
		try {
			fileIn = new FileInputStream(file);
			fileIn.read(fileData);
		} finally {
			if (fileIn != null) 
				fileIn.close();
		}
		return fileData;
	}
	private String getContentType(String fileRequested) {
		if (fileRequested.endsWith(".htm")  ||  fileRequested.endsWith(".html"))
			return "text/html";
		else
			return "text/plain";
	}
	
	private void fileNotFound(PrintWriter out, OutputStream dataOut, String fileRequested) throws IOException {
            if(!(fileRequested.endsWith("/") || fileRequested.endsWith(".html") || fileRequested.endsWith(".xml"))){
                File file = new File(WEB_ROOT, FILE_NOT_FOUND);
		int fileLength = (int) file.length();
		String content = "text/html";
		byte[] fileData = readFileData(file, fileLength);
		
                
		out.println("HTTP/1.1 301 Moved Permanently");
		out.println("Server: Java HTTP Server from SSaurel : 1.0");
		out.println("Date: " + new Date());
		out.println("Content-type: " + content);
		out.println("Content-length: " + fileLength);
                out.println("Location: " + fileRequested + "/");
		out.println(); // blank line between headers and content, very important !
		out.flush(); // flush character output stream buffer
		
		dataOut.write(fileData, 0, fileLength);
		dataOut.flush();
		
		if (verbose) {
			System.out.println("File " + fileRequested + " not found");
                }            
            }else{
                File file = new File(WEB_ROOT, FILE_NOT_FOUND);
		int fileLength = (int) file.length();
		String content = "text/html";
		byte[] fileData = readFileData(file, fileLength);
		
                
		out.println("HTTP/1.1 404 File Not Found");
		out.println("Server: Java HTTP Server from SSaurel : 1.0");
		out.println("Date: " + new Date());
		out.println("Content-type: " + content);
		out.println("Content-length: " + fileLength);
		out.println(); // blank line between headers and content, very important !
		out.flush(); // flush character output stream buffer
		
		dataOut.write(fileData, 0, fileLength);
		dataOut.flush();
		
		if (verbose) {
			System.out.println("File " + fileRequested + " not found");
		}
            }
	}
}


