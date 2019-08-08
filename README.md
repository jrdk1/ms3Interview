# ms3Interview

The purpose of this repository is to hold the software responsible for consuming a CSV file, parsing the data, and inserting valid records into SQLite. There are 10 columns in the CSV file and the database alike labeled (A,B,C,D,E,F,G,H,I,J). Each record in the CSV is what I would like to call a PersonPayment and it consists of attributes: firstName, lastName, email, sex, dataImage, paymentCard, paymentAmount, flag1, flag2, and location. Bad records not matching the 10 column format are written to ms3Interview-bad.csv. Logs are written to: ms3Interview.log which contains the number of received, successful, and failed records respectively.

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
