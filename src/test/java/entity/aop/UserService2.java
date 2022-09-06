package entity.aop;

public class UserService2 implements IUserService {
    @Override
    public String queryUserInfo() {
        return "eqwrrt";
    }

    @Override
    public String register(String userName) {
        return "your name is " + userName;
    }

    @Override
    public int addInfo() {
        return 3214342;
    }
}
