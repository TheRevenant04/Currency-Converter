# Currency-Converter
A simple **Currency Converter** application created using JavaFX.

## Description
* **Currency-Converter** is an application that converts between different currencies of the world.
* The application uses the https://free.currencyconverterapi.com API to get real time currency rate data. 
* The currency data is updated every **60 minutes**. 
* The application supports **162** currencies.

## Screenshots
![Home Screen](/src/Resources/Images/Home.png)
![Conversion Demo](/src/Resources/Images/Conversion.png)
![Error Dialog](/src/Resources/Images/NoConnection.png)

## Requirements
* jdk-13.0.2
* openjfx-11.0.2
* jackson-core-2.11.3
* jackson-annotations-2.11.3
* jackson-databind-2.11.3

## How to Use?
**Note**: The JAR file only runs on windows OS.
The application can be used in two ways described below.
### 1.Executing the JAR 
1. Clone the project.
   >git clone https://github.com/TheRevenant04/Currency-Converter.git
2. Navigate to the JAR file. It is located in the **out/artifacts/CurrencyConverter_jar** folder.

3. Double click the **CurrencyConverter.jar** file.<br>
   OR<br>
   Alternatively, execute the following command on cmd or powershell:
   >(path to JAR file)\>java -jar CurrencyConverter.jar 

### 2.Building and running the project locally
1. Clone the project.
   >git clone https://github.com/TheRevenant04/Currency-Converter.git

2. Obtain a free API key from  https://free.currencyconverterapi.com.

3. Setup a javaFX project with the project **requirements** and source files cloned from the repo according to your IDE.

4. Create a **JSON** folder in the **Resources** folder of the project and create a **APIKey.json** file. Add the following in the file.
   >{<br>
        "API_Key" : "**your API key**"<br>
    }
    
5. Run the **CurrencyJSONRetriever.java** which makes an API call to obtain currency data and creates the **currencies.json** file that stores the currency data.(This step needs to be performed only when the project is being run for the first time.

6. Run the **Driver.java** to run the application.

