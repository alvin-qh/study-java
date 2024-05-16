package alvin.study.testing.faker;

import com.github.javafaker.Faker;
import org.junit.jupiter.api.Test;

import java.util.Locale;

import static org.assertj.core.api.BDDAssertions.then;

/**
 * 通过 {@link Faker} 类型对象可以伪造各类测试数据
 */
class FakerTest {
    /**
     * 测试 {@link Faker} 内置伪造器的使用
     *
     * <p>
     * 内置伪造器包括:
     *
     * <pre>
     * Address
     * Ancient
     * Animal
     * App
     * Aqua Teen Hunger Force
     * Artist
     * Avatar
     * Back To The Future
     * Aviation
     * Basketball
     * Beer
     * Bojack Horseman
     * Book
     * Bool
     * Business
     * ChuckNorris
     * Cat
     * Code
     * Coin
     * Color
     * Commerce
     * Company
     * Crypto
     * DateAndTime
     * Demographic
     * Disease
     * Dog
     * DragonBall
     * Dune
     * Educator
     * Esports
     * EnglandFootBall
     * File
     * Finance
     * Food
     * Friends
     * FunnyName
     * GameOfThrones
     * Gender
     * Hacker
     * HarryPotter
     * Hipster
     * HitchhikersGuideToTheGalaxy
     * Hobbit
     * HowIMetYourMother
     * IdNumber
     * Internet
     * Job
     * Kaamelott
     * LeagueOfLegends
     * Lebowski
     * LordOfTheRings
     * Lorem
     * Matz
     * Music
     * Name
     * Nation
     * Number
     * Options
     * Overwatch
     * PhoneNumber
     * Photography
     * Pokemon
     * Princess Bride
     * Relationship Terms
     * RickAndMorty
     * Robin
     * RockBand
     * Shakespeare
     * Sip
     * SlackEmoji
     * Space
     * StarCraft
     * StarTrek
     * Stock
     * Superhero
     * Team
     * TwinPeaks
     * University
     * Weather
     * Witcher
     * Yoda
     * Zelda
     * </pre>
     * </p>
     */
    @Test
    void faker_shouldGenerateSomeValues() {
        // 产生 Faker 对象, 指定数据的地域
        var faker = new Faker(Locale.CHINA);

        // 产生一个伪造的艺术家名字
        var artistName = faker.artist().name();
        then(artistName).isNotEmpty();

        // 产生一个伪造的地址
        var address = faker.address();

        // 地址的城市名
        then(address.cityName()).isNotEmpty();
        // 地址的街道名
        then(address.streetAddress()).isNotEmpty();
        // 地址的楼号
        then(address.buildingNumber()).isNotEmpty();
    }

    /**
     * 测试产生一个符合指定正则表达式的数据
     *
     * <p>
     * 通过 {@link com.github.javafaker.service.FakeValuesService FakeValuesService} 对象可以根据一个正则表达式产生数据
     * </p>
     *
     * <p>
     * {@link Faker#regexify(String)} 内部调用了 {@link com.github.javafaker.service.FakeValuesService FakeValuesService}
     * 对象
     * </p>
     *
     * <p>
     * 另外, 生成数据使用的正则表达式和匹配数据的正则表达式略有不同, 例如不能加 {@code ^} 或 {@code $} 字符等
     * </p>
     */
    @Test
    void regexify_shouldGenerateFakeValueByRegexp() {
        // 产生 Faker 对象, 指定数据的地域
        var faker = new Faker(Locale.CHINA);

        // 产生一个符合指定正则表达式的数据
        var value = faker.regexify("\\d{3}\\-\\d{7}");
        // 确认产生的数据符合预期的正则表达式
        then(value).matches("^\\d{3}-\\d{7}$");
    }

    /**
     * 测试产生随机数服务
     *
     * <p>
     * 通过 {@link com.github.javafaker.service.RandomService RandomService} 对象可以产生各类随机数
     * </p>
     *
     * <p>
     * {@link Faker#random()} 方法返回 {@link com.github.javafaker.service.RandomService RandomService} 对象
     * </p>
     */
    @Test
    void random_shouldGenerateRandomValue() {
        // 产生 Faker 对象, 指定数据的地域
        var faker = new Faker(Locale.CHINA);
        // 获取随机数服务对象
        var service = faker.random();

        // 产生指定范围内的随机整数 ([1..10])
        var value = service.nextInt(1, 10);
        // 确认产生的随机数符合预期
        then(value).isGreaterThanOrEqualTo(1).isLessThanOrEqualTo(10);

        // 产生长度为 10 的随机 16 进制字符串
        var hex = service.hex(10);
        // 确认产生的字符串符合预期
        then(hex).matches("^[A-F0-9]{10}$");
    }

    /**
     * 通过一个字符串 Key 获取对应的伪造值
     *
     * <p>
     * 通过 {@link com.github.javafaker.service.FakeValuesService FakeValuesService} 对象可以解析一个字符串 Key
     * </p>
     *
     * <p>
     * {@link Faker#resolve(String)} 内部调用了 {@link com.github.javafaker.service.FakeValuesService FakeValuesService}
     * 对象
     * </p>
     *
     * <p>
     * 例如 {@code faker.resolve("internet.avatar")} 相当于调用 {@code faker.internet().avatar()} 方法
     * </p>
     */
    @Test
    void resolve_shouldResolvePropertyByStringKey() {
        // 产生 Faker 对象, 指定数据的地域
        var faker = new Faker(Locale.CHINA);

        // 根据属性 Key 解析伪造内容
        var result = faker.resolve("internet.avatar");

        // 确认伪造数据符合预期
        then(result).endsWith("jpg");
    }
}
