import com.liu.service.ITestService;
import com.liu.serviceImpl.TestService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;

@RunWith(SpringRunner.class)
@ContextConfiguration("classpath:/applicationContext.xml")
public class TestProvider {
    @Autowired
    TestService testService;

    @Test
    public void testDubbo() throws IOException {
        System.out.println("服务已启动");
        testService.sayHello("刘延平");
        System.in.read();
    }
}
