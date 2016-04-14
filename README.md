JAAS DB Authenticator
=====================

This JAAS collection has been modified to be used inside Shibboleth IdP v3 for the authentication using a DB as credentials back-end.

Compared to the original version the additional back-ends have been removed and some option added to the *DBLogin*. With this change the table in the DB and the columns with the corresponding username and password are configurable.

For the password check the library makes use of the *PASSWORD()* method available in *mysql*. Be sure the method is available for use with other DBs or the code has to be modified.

The authorisation part of the original package has been removed because not needed for the integration with Shibboleth.

For more complex scenario the *DBLogin* can be modified.
