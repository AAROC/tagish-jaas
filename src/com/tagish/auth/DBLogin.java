// $Id: DBLogin.java 2 2008-09-03 19:06:36Z celdredge $
package com.tagish.auth;

import java.util.Map;
import java.util.*;
import java.sql.*;
import javax.security.auth.*;
import javax.security.auth.callback.*;
import javax.security.auth.login.*;

/**
 * Simple database based authentication module.
 *
 * @author Andy Armstrong, <A HREF="mailto:andy@tagish.com">andy@tagish.com</A>
 * @version 1.0.3
 */
public class DBLogin extends SimpleLogin
{
	protected String                dbDriver;
	protected String                dbURL;
	protected String                dbUser;
	protected String                dbPassword;
	protected String                userTable;
        protected String                userColumn;
        protected String                passwordColumn;

	protected synchronized Vector validateUser(String username, char password[]) throws LoginException
	{
		ResultSet rsu = null;
		Connection con = null;
		PreparedStatement psu = null;

		try
		{
                    if(username == null || username.isEmpty()){
                        throw new FailedLoginException("User cannot be empty!");
                    }
			Class.forName(dbDriver);
			if (dbUser != null)
			   con = DriverManager.getConnection(dbURL, dbUser, dbPassword);
			else
			   con = DriverManager.getConnection(dbURL);

			psu = con.prepareStatement("SELECT " + userColumn +"," + passwordColumn + " FROM " + userTable +
									   " WHERE UserName=?");
			psu.setString(1, username);
			rsu = psu.executeQuery();                        
			if (!rsu.next()) throw new FailedLoginException("Unknown user");
                        rsu.close();
                        psu.close();
			psu = con.prepareStatement("SELECT " + userColumn +"," + passwordColumn + " FROM " + userTable +
									   " WHERE UserName=? AND Password=PASSWORD(?)");
			psu.setString(1, username);
                        psu.setString(2, new String(password));
			rsu = psu.executeQuery();
			if (!rsu.next()) throw new FailedLoginException("Wrong Password");
			Vector p = new Vector();
			p.add(new TypedPrincipal(username, TypedPrincipal.USER));
			return p;
		}
		catch (ClassNotFoundException e)
		{
			throw new LoginException("Error reading user database (" + e.getMessage() + ")");
		}
		catch (SQLException e)
		{
			throw new LoginException("Error reading user database (" + e.getMessage() + ")");
		}
		finally
		{
			try {
				if (rsu != null) rsu.close();
				if (psu != null) psu.close();
				if (con != null) con.close();
			} catch (Exception e) { }
		}
	}

	public void initialize(Subject subject, CallbackHandler callbackHandler, Map sharedState, Map options)
	{
		super.initialize(subject, callbackHandler, sharedState, options);

		dbDriver = getOption("dbDriver", null);
		if (dbDriver == null) throw new Error("No database driver named (dbDriver=?)");
		dbURL = getOption("dbURL", null);
		if (dbURL == null) throw new Error("No database URL specified (dbURL=?)");
		dbUser = getOption("dbUser", null);
		dbPassword = getOption("dbPassword", null);
		if ((dbUser == null && dbPassword != null) || (dbUser != null && dbPassword == null))
		   throw new Error("Either provide dbUser and dbPassword or encode both in dbURL");

		userTable    = getOption("userTable",    "user");
		userColumn    = getOption("userColumn",    "UserName");
		passwordColumn    = getOption("passwordColumn",    "Password");
	}
}
