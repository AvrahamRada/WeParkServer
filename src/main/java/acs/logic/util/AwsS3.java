package acs.logic.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.services.s3.model.S3ObjectSummary;

public class AwsS3 {
	private String ACCESS_KEY_ID = "AKIATMLKY7FE3DVIVKFL";
	private String ACCESS_SEC_ID = "liQCDiZLYhioYSFwTbyWHdKOP4JWrtbTVWtjn442";
	private String bucketName = "avrahamwepark";
	String line = "";
	private HashMap<String, Double> dataToSave;
	
	AWSCredentials credentials = new BasicAWSCredentials(ACCESS_KEY_ID,ACCESS_SEC_ID);
	
	AmazonS3 s3client = AmazonS3ClientBuilder
			.standard()
			.withCredentials(new AWSStaticCredentialsProvider(credentials))
			.withRegion(Regions.US_EAST_2)
			.build();
	
	public AwsS3() {
		dataToSave = new HashMap<>();
	}

	public HashMap<String, Double> getDataToSave() {
		return dataToSave;
	}

	public void setDataToSave(HashMap<String, Double> dataToSave) {
		this.dataToSave = dataToSave;
	}

	// Create bucket
	public void createBucket(String bucketNamee) {
		try {
			s3client.createBucket(bucketNamee);
		} catch (AmazonS3Exception e) {
			System.err.println(e.getErrorMessage());
		}
	}
	
	// delete bucket from AWS S3 Amazon
	public void deleteBucket(String bucketNamee) {
			try {
				s3client.deleteBucket(bucketNamee);
			} catch (AmazonS3Exception e) {
				System.err.println(e.getErrorMessage());
			}
	}

	// print the list of buckets we have on AWS S3 Amazon
 	public void listOfBuckets() {
		try {
			List<Bucket> buckets = s3client.listBuckets();
			System.err.println("Your Amazon S3 buckets are:");
			for (Bucket b : buckets) {
				System.err.println("* " + b.getName());
			}
		} catch (AmazonS3Exception e) {
			System.err.println(e.getErrorMessage());
		}
	}

	// Upload CSV file to AWS S3 Amazon
	public void uploadFile(String bucketNamee, String keyName, String filePath) {
		try {
			s3client.putObject(bucketNamee, keyName, new File(filePath));
		} catch (AmazonS3Exception e) {
			System.err.println(e.getErrorMessage());
			System.exit(1);
		}
	}
	
	// Download CSV File from AWS S3 Amazon
	public void downloadCSV(String file) {
		try {
			System.out.println("Downloading " + file + " CSV File....\n");
		    com.amazonaws.services.s3.model.S3Object o = s3client.getObject(this.bucketName, file);
		    S3ObjectInputStream s3is = o.getObjectContent();
		    FileOutputStream fos = new FileOutputStream(new File(file));
		    byte[] read_buf = new byte[1024];
		    int read_len = 0;
		    while ((read_len = s3is.read(read_buf)) > 0) {
		        fos.write(read_buf, 0, read_len);
		    }
		    s3is.close();
		    fos.close();
			System.out.println("Finished Downloading " + file + " CSV File....\n");

		} catch (AmazonServiceException e) {
		    System.err.println("Failed to download! "+ e.getErrorMessage());
		    System.exit(1);
		} catch (FileNotFoundException e) {
		    System.err.println("Failed to download! "+ e.getMessage());
		    System.exit(1);
		} catch (IOException e) {
		    System.err.println("Failed to download! "+ e.getMessage());
		    System.exit(1);
		}
	}
	
	// read CSV file to our map
	public void readCSVFileToOurMap(String file) {
		double countW = 0;
		double countW_q = 0;
		double countLambda = 0;
		double countServers = 0;
		double totalCount = 0;
		
		try {
			BufferedReader br = new BufferedReader(new FileReader(file)); // Open CSV file
			line = br.readLine(); // Read the headers (W,Wq,Lambda,Servers)
			line = line.replaceAll("\\uFEFF", ""); // fix the bug i faced
//			String[] headers = line.split(",");
//			String W = headers[0].trim();
//			String Wq = headers[1].trim();
//			String Lambda = headers[2].trim();
//			String Servers = headers[3].trim();
			line = "";
			while ((line = br.readLine()) != null) { // read line from CSV file

				totalCount = totalCount + 1;
				
				line = line.replaceAll("\\uFEFF", ""); // fix the bug i faced
				String[] data = line.split(",");
				for (int i = 0; i < data.length; i++) {
					
					if (i == 0)
						countW = countW + Double.parseDouble(data[i].trim());
					else if (i == 1)
						countW_q = countW_q + Double.parseDouble(data[i].trim());
					else if (i == 2)
						countLambda = countLambda + Double.parseDouble(data[i].trim());
					else
						countServers = countServers + Double.parseDouble(data[i].trim());
						
				}
				
				this.dataToSave.put("W", countW / totalCount);
				this.dataToSave.put("Q", countW_q / totalCount);
				this.dataToSave.put("Lambda", countLambda / totalCount);
				this.dataToSave.put("Servers", countServers / totalCount);
				
//				this.dataToSave.put(W, countW / totalCount);
//				this.dataToSave.put(Wq, countW_q / totalCount);
//				this.dataToSave.put(Lambda, countLambda / totalCount);
//				this.dataToSave.put(Servers, countServers / totalCount);
			}
			
			br.close(); // Close CSV file
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	
//	// read CSV file to our map
//	public void readCSVFileToOurMap(String file) {
//		double count;
//		try {
//			BufferedReader br = new BufferedReader(new FileReader(file));
//			while ((line = br.readLine()) != null) { // read line from CSV file
//				count = 0;
//				line = line.replaceAll("\\uFEFF", ""); // fix the bug i faced
//				String[] data = line.split(",");
//				for (int i = 1; i < data.length; i++) {
//					count = count + Double.parseDouble(data[i].trim());
//				}
////				System.err.println("**** average of " + data[0].trim() + " :" + (count/data.length-1));
//				this.dataToSave.put(data[0].trim(), count / (data.length - 1));
//			}
//			br.close();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}
	
//	// Giron - upload
//	public void uploadCSV(String file) {
//	     try {
//	    	 s3client.putObject(bucketName, file, new File(file));
//	     } catch (AmazonServiceException e) {
//	         System.err.println(e.getErrorMessage());
//	         System.exit(1);
//	     }
//	}

//	// list Of Files
//	public void listOfFiles(String bucketName) {
//		try {
//			ObjectListing ol = s3.listObjects(bucketName);
//			List<S3ObjectSummary> objects = ol.getObjectSummaries();
//			for (S3ObjectSummary os : objects) {
//				System.err.println("* " + os.getKey());
//			}
//		} catch (AmazonS3Exception e) {
//			System.err.println(e.getErrorMessage());
//		}
//	}
	
//	// delete file
//	public void deleteFile(String bucketName, String objectKey) {
//		// Object = Folder
//			try {
//				s3.deleteObject(bucketName, objectKey);
//			} catch (AmazonS3Exception e) {
//				System.err.println(e.getErrorMessage());
//				System.exit(1);
//			}
//	}

//	// delete folder
//	public void deleteFolder(String bucketName, String objectKey) {
//		ObjectListing objects = s3.listObjects(bucketName, objectKey);
//		for (S3ObjectSummary objectSummary : objects.getObjectSummaries()) {
//			try {
//				s3.deleteObject(bucketName, objectSummary.getKey());
//			} catch (AmazonS3Exception e) {
//				System.err.println(e.getErrorMessage());
//			}
//		}
//
//	}
	

	
	// Giron - download CSV file
//	public void downloadCSV(String file) {
//		final AmazonS3 s3 = AmazonS3ClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(awsCredentials)).withRegion(Regions.US_WEST_2).build();
//		try {
//			System.out.println("Downloading " + file + " CSV File....\n");
//		    com.amazonaws.services.s3.model.S3Object o = s3.getObject(recommendationBucketString, file);
//		    S3ObjectInputStream s3is = o.getObjectContent();
//		    FileOutputStream fos = new FileOutputStream(new File(file));
//		    byte[] read_buf = new byte[1024];
//		    int read_len = 0;
//		    while ((read_len = s3is.read(read_buf)) > 0) {
//		        fos.write(read_buf, 0, read_len);
//		    }
//		    s3is.close();
//		    fos.close();
//		} catch (AmazonServiceException e) {
//		    System.err.println("Failed to download! "+ e.getErrorMessage());
//		    System.exit(1);
//		} catch (FileNotFoundException e) {
//		    System.err.println("Failed to download! "+ e.getMessage());
//		    System.exit(1);
//		} catch (IOException e) {
//		    System.err.println("Failed to download! "+ e.getMessage());
//		    System.exit(1);
//		}
//	}
//	// @Async annotation ensures that the method is executed in a different background thread 
//    // but not consume the main thread.
//	@Async
//    public void uploadFile(final MultipartFile multipartFile) {
//        LOGGER.info("File upload in progress...");
//        try {
//            final File file = convertMultiPartFileToFile(multipartFile);
//            uploadFileToS3Bucket(bucketName, file);
//            LOGGER.info("File upload is completed.");
//            file.delete();  // To remove the file locally created in the project folder.
//        } catch (final AmazonServiceException ex) {
//            LOGGER.info("File upload is failed.");
//            LOGGER.error("Error= {} while uploading file.", ex.getMessage());
//        }
//    }
// 
//    private File convertMultiPartFileToFile(final MultipartFile multipartFile) {
//        final File file = new File(multipartFile.getOriginalFilename());
//        try (final FileOutputStream outputStream = new FileOutputStream(file)) {
//            outputStream.write(multipartFile.getBytes());
//        } catch (final IOException ex) {
//            LOGGER.error("Error converting the multi-part file to file= ", ex.getMessage());
//        }
//        return file;
//    }
// 
//    private void uploadFileToS3Bucket(final String bucketName, final File file) {
//        final String uniqueFileName = LocalDateTime.now() + "_" + file.getName();
//        LOGGER.info("Uploading file with name= " + uniqueFileName);
//        final PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, uniqueFileName, file);
//        amazonS3.putObject(putObjectRequest);
//    }
}
