package acs.logic.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.opencsv.CSVWriter;

public class AwsS3 {
	private String ACCESS_KEY_ID = "AKIATMLKY7FE3DVIVKFL";
	private String ACCESS_SEC_ID = "liQCDiZLYhioYSFwTbyWHdKOP4JWrtbTVWtjn442";
	private String bucketName = "avrahamwepark";
	String line = "";
	private HashMap<String, Double> dataToSave;

	AWSCredentials credentials = new BasicAWSCredentials(ACCESS_KEY_ID, ACCESS_SEC_ID);

	AmazonS3 s3client = AmazonS3ClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(credentials))
			.withRegion(Regions.US_EAST_2).build();

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

	// delete file
	public void deleteFile(String fileName) {
		// Object = Folder
		try {
			s3client.deleteObject(bucketName, fileName);
		} catch (AmazonS3Exception e) {
			System.err.println(e.getErrorMessage());
			System.exit(1);
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
	public void uploadFile(String keyName, String fileName) {
		try {
			s3client.putObject(bucketName, keyName, new File(fileName));
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
			System.err.println("Failed to download! " + e.getErrorMessage());
			System.exit(1);
		} catch (FileNotFoundException e) {
			System.err.println("Failed to download! " + e.getMessage());
			System.exit(1);
		} catch (IOException e) {
			System.err.println("Failed to download! " + e.getMessage());
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

		double currentAvgOfW;
		double currentAvgOfW_q;
		double currentAvgOfLambda;
		double currentAvgOfServers;

		try {
			BufferedReader br = new BufferedReader(new FileReader(file)); // Open CSV file
			line = br.readLine(); // Read the headers (W,Wq,Lambda,Servers)
			line = "";
			while ((line = br.readLine()) != null && totalCount < 100) { // read line from CSV file
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
			}

			currentAvgOfW = countW / totalCount;
			currentAvgOfW_q = countW_q / totalCount;
			currentAvgOfLambda = countLambda / totalCount;
			currentAvgOfServers = countServers;

			while (line != null) { // read line from CSV file
				totalCount = totalCount + 1;
				line = line.replaceAll("\\uFEFF", ""); // fix the bug i faced
				String[] data = line.split(",");

				for (int i = 0; i < data.length; i++) {

					if (i == 0)
						currentAvgOfW = calcuateWeightedAvg(currentAvgOfW, Double.parseDouble(data[i].trim()));
					else if (i == 1)
						currentAvgOfW_q = calcuateWeightedAvg(currentAvgOfW_q, Double.parseDouble(data[i].trim()));
					else if (i == 2)
						currentAvgOfLambda = calcuateWeightedAvg(currentAvgOfLambda,
								Double.parseDouble(data[i].trim()));
				}
				line = br.readLine();
			}

			this.dataToSave.put("W", currentAvgOfW);
			this.dataToSave.put("Q", currentAvgOfW_q);
			this.dataToSave.put("Lambda", currentAvgOfLambda);
			this.dataToSave.put("Servers", currentAvgOfServers);

			br.close(); // Close CSV file
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public double calcuateWeightedAvg(double currentAvg, double otherResult) {
		if (otherResult < 0)
			return currentAvg;
		return ((currentAvg * 100) + otherResult) / 101;
	}

	// read CSV file to our map
	public void writeDataToCsvFile(String fileName, String[] row) {
		CSVWriter writer;
		try {
			writer = new CSVWriter(new FileWriter(fileName, true), CSVWriter.DEFAULT_SEPARATOR,
					CSVWriter.NO_QUOTE_CHARACTER, CSVWriter.DEFAULT_ESCAPE_CHARACTER, CSVWriter.RFC4180_LINE_END);

			writer.writeNext(row);
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
