import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class TestSh {
    public static void main(String[] args){
        int k=0;
        RestTemplate restTemplate = new RestTemplate();
        while (true) {
            try {
                Thread.sleep(500);
                ResponseEntity<String> txt = restTemplate.getForEntity("http://www.hy.sh.cn", String.class);
               String body =txt.getBody();
               if (body!=null || txt.getStatusCode()== HttpStatus.OK){
                   System.out.println(body);
                   break;
               }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
            System.out.println("连接次数："+k++);
        }
    }
}
