package uhf;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class APIcontroller {

    @RequestMapping("/")
    public String index() {
        return "The applications is set up!";
    }


}
