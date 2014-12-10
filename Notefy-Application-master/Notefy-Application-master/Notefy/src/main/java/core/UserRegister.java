package core;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import org.hibernate.validator.constraints.NotEmpty;
import org.hibernate.validator.constraints.Email;

public class UserRegister {
	@NotEmpty(message="Please enter your email.")
	@Email
	@NotNull
    private String email;

	@Size(min=6, max=15)
	@NotNull
	@NotEmpty(message="Please enter your password.")
    private String password;
	
	@Size(min=6, max=15)
	@NotNull
	@NotEmpty(message="Please confirm your password.")
	private String confirmPassword;
	
	@NotEmpty(message="Please enter your name.")
	@Email
	@NotNull
    private String name;
	
    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    
    public String getConfirmPassword() {
        return this.confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }
    
    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    public String toString() {
        return "UserRegister(Email: " + this.email + ", Password: " + this.password + ", Confirm Password: " + this.confirmPassword +", Name: " + this.name +")";
    }
}