# Spring Boot Learning

BackendFrontend Folder for the final product


Two types of Models:
User (Used for Login)
Contacts (Data Stored)

Types of Users:
Admin (can perform all crud operations on Contacts)
User  (Can only View contacts and add a new one)

APIs:
CRUD of Contacts with appropriate permissions 
Basic CRUD of Users

Database:
Inmemory H2 Database
Liquibase for intialization of an ADMIN user

Security:
Stateless
JWT
Oauth2 with Google and github
Manual endpoint /api/login (For Postman login testing)
 
+ UnitTesting + testing of APIs on POSTMAN 
Jacoco test coverage = 65% (For now)
