# CrusherClusteringService

## Prerequisite
* Install PostgreSQL database. You can find installer of various versions for your desired OS here-
https://www.enterprisedb.com/downloads/postgres-postgresql-downloads
* Create a databse named crusher_server
* Create two tables named email and cluster using the foloowing script-
https://drive.google.com/open?id=12_FCAWW4GBTa3jk54q1c2RMYRrBzj7p0
* Download the dataset from the following link-
https://drive.google.com/open?id=1gF6LtBKuf-Xeb5AcLAO6X6nyhdaLIWfW
* Unzip the dataset and save the directory path

## Configuration
Before running the software there are some parameters that needed to be configured. These parameters belong to the Constants file which is in the project directory src/main/java. Below the parameters are explained-

* __MATCHING_THRESHOLD__: When the similarity between two minhash signatures is greater than or equal to this value, they are clustered together. 
* __DATABASE_NAME__: Name of the database created
* __DATABASE_USER__: Name of the database user
* __DATABASE_PASSWORD__: Password for the database user
* __NUMBER_OF_HASH_FUNCTIONS__: It will decide the component number in a minhash signature. Typically it should be greater than or equal to 10.
* __SHINGLE_LENGTH__: Length of each shingle. It is used to convert an email into set of shingles.

## How to run the software
* Open the project with an IDE (Eclipse, IntelliJ IDEA). 
* Since it's a Maven project it'll download the dependencies automatically.
* Run the main function in Main class.

## How to use the software
The menu in the console looks like this - https://drive.google.com/open?id=1AYdYo2Df4dutah3ZFaJbMeZRjhGaFjVr
* Press '1' to import data.
* Press '2', to cluster all the imported emails.
* Press '3' to directly cluster a single email.
* Press '4' to write all the clusters in html files to view them in a browser.
* Press '0' to exit te program anytime.
