# ms3Interview

A)
The purpose of this repository is to hold the software responsible for consuming a CSV file, parsing the data, and inserting valid records into SQLite. There are 10 columns in the CSV file and the database alike labeled (A,B,C,D,E,F,G,H,I,J). Each record in the CSV is what I would like to call a PersonPayment and it consists of attributes: firstName, lastName, email, sex, dataImage, paymentCard, paymentAmount, flag1, flag2, and location. Bad records not matching the 10 column format are written to ms3Interview-bad.csv. Logs are written to: ms3Interview.log which contains the number of received, successful, and failed records respectively.

B)
The steps of running this software is described below:
Fork a copy of the ms3Interview project and import into NetBeans(the IDE used to create the project).
Perform clean & build.
Please run the program and wait until the Output Window reads "BUILD SUCCESSFUL". This means all data has been processed.
Next, open sqlite. Your sqlite should be available under C:\sqlite. You can enter "sqlite3" to get the application up and running. 

Once you open sqlite3, please copy paste and press enter for each individual command for sqlite3 under "Commands".

--Commands--
.mode column
.headers on
.width 14 14 14 14 14 14 14 14 14 14

Next, type in sqlite3 ".open ms3Interview.db".
Then, type in sqlite3 ".tables" to make sure ValidPersonPayments table is there.

To view the formatted data inside ValidPersonPayments, please perform the following.
Copy, paste, and then press enter for each sql command.
Then, expand the sqlite3 window to view the data.

		ALTER TABLE ValidPersonPayments RENAME to ValidPersonPayments_Old;
		
		CREATE TABLE IF NOT EXISTS ValidPersonPayments(
                FIRST_NAME_A VARCHAR(30),
                LAST_NAME_B VARCHAR(30),
                EMAIL_C VARCHAR(50),
                SEX_D VARCHAR(1),
                DATA_IMAGE_E BLOB,
                PAYMENT_CARD_F VARCHAR(20),
                PAYMENT_AMOUNT_G DECIMAL(3,2),
                FLAG1_H INTEGER,
                FLAG2_I INTEGER,
                LOCATION_J VARCHAR(100)
                );
		
		INSERT INTO ValidPersonPayments(FIRST_NAME_A, LAST_NAME_B, EMAIL_C, SEX_D, DATA_IMAGE_E, PAYMENT_CARD_F, PAYMENT_AMOUNT_G, FLAG1_H, FLAG2_I, LOCATION_J)
		SELECT * FROM ValidPersonPayments_Old;
		
		SELECT * FROM ValidPersonPayments;

C)
Approach, Design Choices, and Assumptions.

When reading about the task at hand, I haven't parsed a CSV file before in Java in the past, so I turned to StackOverflow.
I noticed developers were using BufferedReader or Apache Common's CSVParser class. I decided to go with BufferedReader and split() from java.lang.String class. Java 8's Stream API made reading the CSV file simple without any loops. I imagined each record of the CSV to be represented as a List of Strings and later a List of PersonPayments. 

Making the PersonPayments.java class and resulting ValidPersonPayments.java and InValidPersonPayments.java classes came about when I noticed common elements of each record: firstName, lastName, email, sex, dataImage, paymentCard, paymentAmount, flag1, flag2, and location. With records missing data in different places across the file, I required all columns to be full with data to be Valid and InValid otherwise. When looking at determineRecordValiditiy() with checks non-nulls, column G starting with the "$", and Male or Female for the sex, we come up with some basicConditions for the validity of a record. Add to that, the check for a double amount for paymentAmount and now we can determine valid records for each record in the CSV. The mapper function is the brains of the application. It parses, determines the validity of a record, adds valid or invalid records to their respective and even checks for anomalies like: missing columns for a record(a list size less than 10) and corrects for null paymentAmounts.
Doing JDBC stuff was fun because I haven't done it in awhile, then there was the cross of choosing String or char[] for dataImage, and converting PersonPayment objects back into String for bad data.


I used PreparedStatement for parametric data insertion and lambda to make writeValidRecordsToDB() more manageable.
In creating the ValidPersonPayments table I assumed the first 3 columns and column 6 wouldn't have really Strings, so I stuck with reasonable memory allocations for each field. I assumed every paymentAmount was a double, a location can be something could be something long like "hangakoauauotamateaturipukakapikimaungahoronukupokaiwhenuakitanatahu" in New Zealand, so I stuck with 100. I also assumed the format for the CSV would not change and there will be 10 columns for good data, bad data aside. I used code from Baeldung for convertToCSV() and regex code from StackOverflow on line 156 for the mapper fucntion.

I tried a few attempts at forming a unique key for the data but I found it a challenge to find only one null among several different columns to uniquely identify a record, so I left it out.

