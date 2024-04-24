package top.tolan;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import tk.mybatis.spring.annotation.MapperScan;

@MapperScan("top.tolan.**.mapper")
@SpringBootApplication
public class WechatApplication {

    public static void main(String[] args) {
        SpringApplication.run(WechatApplication.class, args);
        System.out.println(
            "      _             _   _                                                          \n" +
            "  ___| |_ __ _ _ __| |_(_)_ __   __ _   ___ _   _  ___ ___ ___  ___ ___            \n" +
            " / __| __/ _` | '__| __| | '_ \\ / _` | / __| | | |/ __/ __/ _ \\/ __/ __|         \n" +
            " \\__ \\ || (_| | |  | |_| | | | | (_| | \\__ \\ |_| | (_| (_|  __/\\__ \\__ \\    \n" +
            " |___/\\__\\__,_|_|   \\__|_|_| |_|\\__, | |___/\\__,_|\\___\\___\\___||___/___/   \n" +
            "                                |___/                                              \n"
        );
    }

}
