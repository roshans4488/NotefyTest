package notefy;


import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.servlet.configuration.EnableWebMvcSecurity;
import org.springframework.security.web.csrf.CsrfFilter;

@Configuration
@EnableWebMvcSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	
	DataSource dataSource;
	/*
	public DriverManagerDataSource dataSource() {
	    DriverManagerDataSource driverManagerDataSource = new DriverManagerDataSource();
	    driverManagerDataSource.setDriverClassName("com.mysql.jdbc.Driver");
	    driverManagerDataSource.setUrl("jdbc:mysql://localhost:3306/notefy");
	    driverManagerDataSource.setUsername("root");
	    driverManagerDataSource.setPassword("notefyapp");
	   // dataSource = driverManagerDataSource;
	    return driverManagerDataSource;
	}
	
*/
	
	
	@Override
    public void configure(WebSecurity web) throws Exception {
       web.ignoring().antMatchers("/notefy/**");  
       web.ignoring().antMatchers("/bootstrap/**");
       web.ignoring().antMatchers("/JQuery/**");
     //  web
      // .ignoring()
         // .antMatchers("/resources/**");
    }
	
	@Override
    protected void configure(HttpSecurity http) throws Exception {
        http
        	//.addFilterAfter(new CsrfTokenGeneratorFilter(), CsrfFilter.class)
            .authorizeRequests()
                .antMatchers("/login","/register").permitAll()
            	//.antMatchers("/**").hasRole("USER")
                .anyRequest().authenticated()
                .and()
            .formLogin()
               .loginPage("/login")
               	.usernameParameter("username").passwordParameter("password")
                .permitAll()
                .and()
            .logout()
                .permitAll()
                .and()
				.csrf().disable();
       
    }
	

	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		dataSource = DatabaseSource.dataSource();
		auth.jdbcAuthentication().dataSource(dataSource)
			.usersByUsernameQuery("select username,password, enabled from users where username=?")
			.authoritiesByUsernameQuery("select username, role from user_roles where username=?");
	}	
	


	
	
/*
	 @Autowired
	    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
	        auth
	            .inMemoryAuthentication()
	                .withUser("roshans.4488@gmail.com").password("password").roles("USER");
	        auth
	        .inMemoryAuthentication()		
	        		.withUser("priya.pawar30@gmail.com").password("password").roles("USER");
	        
	        auth
	        .inMemoryAuthentication()		
	        		.withUser("mariovinay@gmail.com").password("password").roles("USER");
	        
	        auth
	        .inMemoryAuthentication()		
	        		.withUser("meghna1888@gmail.com").password("password").roles("USER");
	        
	        auth
	        .inMemoryAuthentication()		
	        		.withUser("hadli.rashmi@gmail.com").password("password").roles("USER");
	        
	    }
	
	*/	
}