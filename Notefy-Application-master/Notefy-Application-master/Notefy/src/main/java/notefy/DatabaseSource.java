package notefy;

import org.springframework.jdbc.datasource.DriverManagerDataSource;

public  class DatabaseSource {
	
	
	public static DriverManagerDataSource dataSource() {
	    DriverManagerDataSource driverManagerDataSource = new DriverManagerDataSource();
	    driverManagerDataSource.setDriverClassName("com.mysql.jdbc.Driver");
	    driverManagerDataSource.setUrl("jdbc:mysql://localhost:3306/notefy");
	    driverManagerDataSource.setUsername("root");
	    driverManagerDataSource.setPassword("notefyapp");
	  
	    return driverManagerDataSource;
	}
	
}
