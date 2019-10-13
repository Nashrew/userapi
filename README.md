Instuctions to build, run, and use

Requirements

    - Java 8
    - Maven (3.5.2 used in development)
    
Optional
    
    - Lombok
        - The User class uses Lombok's @Data annotation to avoid the need for creating getters and setters.
            If this causes probelms in your IDE you may need to install the lombok plugin and ensure the option
            'Enable annotation processors' is turned on (IntelliJ, not sure if an option is required for eclipse). 
        - If you don't want to install plugins or change settings there are two options:
            - There are commented out getters in the User class that you can use which should make your IDE happy
            - You could ignore the IDE errors, I beleive the application will still run since Lombok is in the 
                classpath
        
Command line:

    Primary:
        - The easiest way to run the app is to run 'mvn spring-boot:run' from the userapi directory.
        - Tests can be run from the command line with 'mvn test' from the userapi directory

    Alternative:
        Build:
         - Navigate to the userapi directory, run 'mvn package' to create a jar in the /target/ directory

        Run:
         - Navigate to the userapi/target directory, run 'java -jar {jarname}'.

Eclipse / IntelliJ:

    If the project is correctly imported to an IDE as a maven project, you should be able to run the
    UserApiApplication class to launch the spring boot app, and run tests from within the test classes.

Authorization:

    Basic authentication is provided with spring security and a simple implementation of JWT. 
        The only username/password which can be authenticated is 'developer'/'dev'. This was done in the
        interest of time, and because of a lack of 'real' users in the database.
    
    To obtain a token, POST { "username":"developer", "password":"dev" } to /auth/login. That token can 
    be used for access to the user entity endpoints.

Usage:

    API documentation can be found when the app is running at /swagger-ui.html 
    H2 Database console can be accessed at /h2 - you can log in as sa with no password
    
Additional notes:

    This was a fun project. It was built largely upon the invoiceapi project (also on my github), with 
        several additions and enhancements along the way. Namely: spring security / jwt, database 
        constraints, and much more robust test coverage
        
    Although this project may not need them, some things like paging and put/patch are a result of 
        being very low effort to convert
    
    
