import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import org.dcm4che2.data.DicomObject;
import org.dcm4che2.data.Tag;
import org.dcm4che2.io.DicomInputStream;


public class start   {

	/**
	 * @param args
	 */
    	static Dcm2Txt dcm2txt = new Dcm2Txt();
	private static DicomInputStream dis;
	private static String sourcedir;
	private static String destdir;
	private static String destinationdir;
	private static String destinationfile;
	private static String directoryName;
	private static String toProcess;
	    
	public static void copyFile(String sFile, String dFile) throws IOException {
	 
		File sourceFile = new File(sFile);
		File destFile = new File(dFile);
		
		System.out.println("Copy "+sourceFile+"\n to "+destFile);
		
		if(!destFile.exists()) {
	        destFile.createNewFile();
	    }

	    FileChannel source = null;
	    FileChannel destination = null;
	    
	    try {
	        source = new FileInputStream(sourceFile).getChannel();
	        destination = new FileOutputStream(destFile).getChannel();

	        // previous code: destination.transferFrom(source, 0, source.size());
	        // to avoid infinite loops, should be:
	        long count = 0;
	        long size = source.size();              
	        while((count += destination.transferFrom(source, count, size-count))<size);
	    }
	    finally {
	        if(source != null) {
	            source.close();
	        }
	        if(destination != null) {
	            destination.close();
	        }
	    }
	}
	
	
	    public static  void listFilesForFolder(final File folder) {
	    	
		      	for (final File fileEntry : folder.listFiles()) {
		       
		      	if (fileEntry.isDirectory()) {
		         
		      		listFilesForFolder(fileEntry);
		      		
		        } else {
		           
		        	toProcess=folder+File.separator+fileEntry.getName();
		        	System.out.println("");
		        	System.out.println("Processing " + toProcess);
		        
		            DicomInputStream dis = null;
					try {
						
						dis = new DicomInputStream(fileEntry);
						
						DicomObject obj = null;
						
						try {
							obj = dis.readDicomObject();
							
							String patientname = obj.getString(Tag.PatientName);
				            String studydate = obj.getString(Tag.StudyDate);
				            String studyid = obj.getString(Tag.StudyID);
				            String patientid = obj.getString(Tag.PatientID);
				            System.out.println(patientname+"_"+studydate+"_"+patientid+"__"+studyid);
							
				            DateFormat df = new SimpleDateFormat("yyyyMMdd"); 

				            Date date;
				            
							try {
								date = df.parse(studydate);

					            String startDateString1 = df.format(date);
					            //System.out.println("Date in format yyyyMMdd: " + startDateString1);

					            DateFormat year = new SimpleDateFormat("yyyy");
					            DateFormat month = new SimpleDateFormat("MM");
					            DateFormat day = new SimpleDateFormat("dd");
					           
					            directoryName=destdir
					            		+File.separator+year.format(date)
					            		+File.separator+month.format(date)
					            		+File.separator+day.format(date);
					           
					            File theDir = new File(directoryName);

					            // if the directory does not exist, create it
					            if (!theDir.exists()) {
					            	
						            System.out.println("creating directory: " + directoryName);
						            boolean result = theDir.mkdirs();  
	
						             if(result) {    
						               System.out.println("DIR created");  
						             }
						             
					            } else {
					            	System.out.println("Directory already exists: " + directoryName);
					            }
					            
					            destinationdir=directoryName+File.separator+patientname+"_"+studyid;
					            
					            theDir = new File(destinationdir);
					            
					            if (!theDir.exists()) {
					            	
						            System.out.println("creating directory: " + destinationdir);
						            boolean result = theDir.mkdirs();  
	
						             if(result) {    
						               System.out.println("DIR created");  
						             }
					            
					            } else {
					            	System.out.println("Directory already exists: " + destinationdir);
					            }

					            try {
					            	destinationfile=destinationdir+File.separator+fileEntry.getName();
					            	copyFile(toProcess,destinationfile);
								} catch (Exception e) {
									// TODO Auto-generated catch block
									System.out.println("4 "+e.getMessage() + " fuck");
								}	
					            					            
							} catch (ParseException e) {
								// TODO Auto-generated catch block
								System.out.println("3 "+e.getMessage() + " fuck");
							}
				            
				            
						} catch (IOException e) {
							// TODO Auto-generated catch block
							//e.printStackTrace();
							System.out.println("2 "+e.getMessage() + " ignoring");
	
						} 
					} catch (IOException e) {
						// TODO Auto-generated catch block
						//e.printStackTrace();
						System.out.println("1 "+e.getMessage() + " ignoring");
					}		            
		            
		        }
		    }
		}
	    	    
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		configurationFile conf = new configurationFile("config.properties");
		
		sourcedir=conf.getProperty("sourcedir");
		destdir=conf.getProperty("destdir");
		
		final File folder = new File(sourcedir);
		System.out.println("- "+sourcedir);
		listFilesForFolder(folder);
		
		 
	}


}
