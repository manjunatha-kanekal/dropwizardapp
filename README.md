# dropwizardapp

How to start the dropwizardapp application
---

1. Run `mvn clean install` to build your application
1. Start application with `java -jar target/dropwizardapp-1.0.jar server config.yml`
1. To check that your application is running enter url `http://localhost:8080`

# Resources
1. `/register`  
	Type: Post  
    URL: `http://localhost:8080/register`  
    Request body:  
    ```json
    {
      "fullName": "Test User",
      "phone": "1234567890"
    }
    ```
    
2. `/find`  
    Type: Get  
    URL: `http://localhost:8080/find?phone=1234567890`  
    

3. `/all`  
    Type: Get  
    URL: `http://localhost:8080/all`
    

4. `/login`  
    Type: Post  
    URL: `http://localhost:8080/login`  
    Request body: 
    ```json
    {
      "phone": "8553538684"
    }
    ```  
    
    
5. `/txn/spend`  
    Type: Post  
    URL: `http://localhost:8080/txn/spend`  
    Request body: 
    ```json
    {
      "amount": "900",
      "description" : "1st Transaction",
      "timestamp" : "{{current_timestamp}}"
    }
    ```  
    
    
6.  `/topup`  
    Type: Post  
    URL: `http://localhost:8080/topup`  
    Request body:   
    ```json
    {
      "phone": "8553538684",
      "credit": "900"
    }
    ```   
    
# Postman collection
[![Run in Postman](https://run.pstmn.io/button.svg)](https://app.getpostman.com/run-collection/5c89b3e06500c825d5a6)