package alvin.study.quarkus.web;

import io.quarkus.runtime.Quarkus;
import io.quarkus.runtime.annotations.QuarkusMain;

/**
 * 定义 Quarkus 的主方法类
 */
@QuarkusMain
public class Main {
    public static void main(String[] args) {
        Quarkus.run(args);
    }
}
