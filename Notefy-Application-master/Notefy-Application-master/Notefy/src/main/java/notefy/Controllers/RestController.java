package notefy.Controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller

public class RestController {
	
	@RequestMapping("/alternateindex")
	  @ResponseBody
	  public String index() {
	    return "login.html";
	  }
	
}
