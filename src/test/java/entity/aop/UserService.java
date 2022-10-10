package entity.aop;

import springframework.beans.factory.annotation.Autowired;
import springframework.beans.factory.annotation.Value;

import java.util.Random;

public class UserService implements IUserService {

    @Autowired
    Blog blog;

    @Value("312")
    int res;

    public String queryUserInfo() {
        try {
            Thread.sleep(new Random(1).nextInt(100));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "xxx，100001，深圳";
    }

    public String register(String userName) {
        try {
            Thread.sleep(new Random(1).nextInt(100));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "注册用户：" + userName + " success！";
    }

    @Override
    public int addInfo() {
        System.out.println("增加一条记录");
        return 11;
    }

    public int resss() {
        return 100;
    }
}
