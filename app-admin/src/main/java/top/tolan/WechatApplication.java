package top.tolan;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j

@SpringBootApplication
public class WechatApplication {

    public static void main(String[] args) {
        SpringApplication.run(WechatApplication.class, args);
        log.info(
            """
            
                 _             _   _
             ___| |_ __ _ _ __| |_(_)_ __   __ _   ___ _   _  ___ ___ ___  ___ ___
            / __| __/ _` | '__| __| | '_ \\ / _` | / __| | | |/ __/ __/ _ \\/ __/ __|
            \\__ \\ || (_| | |  | |_| | | | | (_| | \\__ \\ |_| | (_| (_|  __/\\__ \\__ \\
            |___/\\__\\__,_|_|   \\__|_|_| |_|\\__, | |___/\\__,_|\\___\\___\\___||___/___/
                                            |___/
            """
        );
        log.info("项目启动成功 ╰（*°▽°*）╯");
    }

}
