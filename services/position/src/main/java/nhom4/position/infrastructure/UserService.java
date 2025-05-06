package nhom4.position.infrastructure;

//import lombok.Value;
import org.springframework.beans.factory.annotation.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import nhom4.position.domain.core.vo.Driver;


@Service
public class UserService {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);

    @Value("${BOOKING_UC_URL:http://UC-SERVICE}")
    private String ucServiceUrl;

    @Autowired
    private RestTemplate restTemplate;

    //Tim tai xe
    public Driver findById(Integer id){
        try {
            Driver driver = this.restTemplate.getForObject(ucServiceUrl+"/users/"+id, Driver.class);
            if (driver!=null){
                driver.setId(id);
                return driver;
            }
        }
        catch (RestClientException e){
            LOGGER.error("Loi khi goi UC Service" + e.getMessage(), e);
        }
        return  fallback(id);
    }

    private Driver fallback(Integer id){
        LOGGER.info("Co loi, suw dung thong tin fix cung cho id: {}",id);
        Driver driver = new Driver();
        driver.setId(id);
        driver.setUserName("Default");
        driver.setMobile("0000");
        return driver;
    }

}
