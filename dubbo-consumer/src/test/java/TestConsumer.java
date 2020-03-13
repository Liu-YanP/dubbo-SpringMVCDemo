import com.liu.entity.User;
import com.liu.service.ITestService;
import com.liu.service.IUserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.io.IOException;

@RunWith(SpringRunner.class)
@ContextConfiguration("classpath:/applicationContext.xml")
public class TestConsumer {

    @Autowired
    ApplicationContext applicationContext;

    @Test
    public void testCon() throws IOException {
        System.out.println("启动成功！");
        ITestService testService = (ITestService) applicationContext.getBean("testService");
        String result = testService.sayHello("刘延平");
        System.out.println(result);
        System.in.read();
    }

    @Resource
    private IUserService userService;

    @Test
    public void testGetUserByName(){
        User user = userService.getUserByName("刘延平");
        System.out.println(user);
    }
}
