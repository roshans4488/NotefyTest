package core;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import org.hibernate.validator.constraints.NotEmpty;
import org.hibernate.validator.constraints.Email;

public class Person {

	@NotEmpty(message="Please provide email.")
	@Email
	@NotNull
    private String email;

	
	@Size(min=6, max=15)
	@NotNull
	@NotEmpty(message="Please provide password.")
    private String password;

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String toString() {
        return "Person(Email: " + this.email + ", Password: " + this.password + ")";
    }

}
