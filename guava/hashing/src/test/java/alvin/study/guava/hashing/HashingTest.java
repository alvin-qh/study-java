package alvin.study.guava.hashing;

import alvin.study.guava.hashing.model.Person;
import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Collections;

import static org.assertj.core.api.BDDAssertions.then;

/**
 * 测试散列计算工具类
 *
 * <p>
 * Guava 提供了简化散列计算的工具类 {@link Hashing}, 用于对数据进行 Hash 计算, 包括简单数据, 对象, 流和分片数据
 * </p>
 *
 * <p>
 * 通过不同的散列方法, 可以获取到不同的散列函数, 由 {@link com.google.common.hash.HashFunction HashFunction} 类型对象表示, 其中:
 * <ul>
 * <li>
 * {@link com.google.common.hash.HashFunction#bits() HashFunction.bits()} 方法返回该散列函数结果的字节数
 * </li>
 * <li>
 * {@link com.google.common.hash.HashFunction#hashString(CharSequence, java.nio.charset.Charset)
 * HashFunction.hashString(CharSequence, Charset)} 方法用于计算字符串值的散列
 * <ul>
 * <li>
 * 类似的方法还有 {@code hashBytes}, {@code hashInt}, {@code hashLong}, {@code hashUnencodedChars} 以及 {@code hashObject}
 * 方法, 可以对不同类型数据求散列
 * </li>
 * <li>
 * {@link com.google.common.hash.HashFunction#hashObject(Object, com.google.common.hash.Funnel)
 * HashFunction.hashObject(Object, Funnel)} 方法用于计算对象的的散列, 其第一个参数为对象引用, 第二个参数为一个 {@code Funnel} 类型
 * 对象, 用来描述一个对象如何进行序列化, 以便进行散列计算. 具体可以参考: {@link Person#makeFunnel()} 方法
 * </li>
 * </ul>
 * </li>
 * <li>
 * {@link com.google.common.hash.HashFunction#newHasher() HashFunction.newHasher()} 方法返回一个
 * {@link com.google.common.hash.Hasher Hasher} 类型对象, 用于对复合类型数据或分片数据计算散列, 其中:
 * <ul>
 * <li>
 * {@link com.google.common.hash.Hasher#putBytes(byte[]) Hasher.putBytes(byte[])} 方法用于添加一条 {@code byte[]} 数据,
 * 可以持续添加各种类型数据
 * </li>
 * <li>
 * 类似方法还有 {@code putByte}, {@code putShort}, {@code putInt}, {@code putLong}, {@code putFloat},
 * {@code putString}, {@code putUnencodedChars} 以及 {@code putObject} 方法
 * </li>
 * <li>
 * 数据添加完毕后, 通过 {@link com.google.common.hash.Hasher#hash() Hasher.hash()} 方法可以获取散列计算结果
 * </li>
 * </ul>
 * </li>
 * </ul>
 * </p>
 *
 * <p>
 * 散列计算结果为 {@link HashCode} 类型对象, 保存了散列计算结果, 其中:
 * <ul>
 * <li>
 * {@link HashCode#asBytes()} 方法将散列值以字节数组形式返回
 * </li>
 * <li>
 * {@link HashCode#asInt()} 方法取散列值前 32 位 (4 字节) 以 {@code int} 形式返回, 要求是
 * {@link com.google.common.hash.HashFunction#bits() HashFunction.bits()} 返回值不小于 {@code 32}
 * </li>
 * <li>
 * {@link HashCode#asLong()} 方法取散列值前 64 位 (8 字节) 以 {@code long} 形式返回, 要求是
 * {@link com.google.common.hash.HashFunction#bits() HashFunction.bits()} 返回值不小于 {@code 64}
 * </li>
 * <li>
 * {@link HashCode#padToLong()} 方法对于散列值不小于 8 字节时, 返回 {@code asLong} 方法的结果, 否则用 {@code 0} 补齐 8 字节后返回
 * </li>
 * <li>
 * {@link HashCode#toString()} 将散列值转为 16 进制字符串返回
 * </li>
 * </ul>
 * </p>
 */
@SuppressWarnings("deprecation")
class HashingTest {
    /**
     * 测试通过 MD5 计算散列值
     *
     * <p>
     * MD5 散列函数返回长度 {@code 128bit} 的散列结果
     * </p>
     */
    @Test
    void md5_shouldCalculateHashCodeWithMD5() {
        // 获取散列函数
        var hashFn = Hashing.md5();
        // 确认散列函数计算结果的长度
        then(hashFn.bits()).isEqualTo(128);

        // 对字符串输入计算散列值
        var hashCode = hashFn.hashString("HelloWorld", Charsets.UTF_8);
        then(hashCode).hasToString("68e109f0f40ca72a15e05cc22786f8e6");

        // 对对象输入计算散列值
        var person = new Person(1L, "Alvin", LocalDate.of(1981, 3, 17), "Shanxi Xi'an");
        hashCode = hashFn.hashObject(person, Person.makeFunnel());
        then(hashCode).hasToString("ccac6eea503e5c4ffba468e927a25365");

        // 获取一个 Hasher 对象, 用于将数据分段计算, 最后得到整体数据的散列值
        hashCode = hashFn.newHasher()
                .putLong(person.getId())
                .putString(person.getName(), Charsets.UTF_8)
                .putString(person.getBirthday().toString(), Charsets.UTF_8)
                .putString(person.getLocation(), Charsets.UTF_8)
                .hash();
        then(hashCode).hasToString("ccac6eea503e5c4ffba468e927a25365");
    }

    /**
     * 测试通过 SHA 计算散列值
     *
     * <p>
     * 通过 {@code shaXXX()} 系列方法获取 SHA 系列散列函数, 包括:
     * <ul>
     * <li>{@link Hashing#sha1()} 散列函数, 可以返回长度 {@code 160bit} 的散列值</li>
     * <li>{@link Hashing#sha256()} 散列函数, 可以返回长度 {@code 256bit} 的散列值</li>
     * <li>{@link Hashing#sha384()} 散列函数, 可以返回长度 {@code 384bit} 的散列值</li>
     * <li>{@link Hashing#sha512()} 散列函数, 可以返回长度 {@code 512bit} 的散列值</li>
     * </ul>
     * </p>
     *
     * <p>
     * 散列值结果长度越长, 安全性越高, 碰撞的几率越小, 但同时计算的迭代次数会增高, 计算量会大幅度增加
     * </p>
     */
    @Test
    void sha1_shouldCalculateHashCodeWithSHA1() {
        // 获取散列函数
        var hashFn = Hashing.sha1();
        // 确认散列函数计算结果的长度
        then(hashFn.bits()).isEqualTo(160);

        // 对字符串输入计算散列值
        var hashCode = hashFn.hashString("HelloWorld", Charsets.UTF_8);
        then(hashCode).hasToString("db8ac1c259eb89d4a131b253bacfca5f319d54f2");

        // 对对象输入计算散列值
        var person = new Person(1L, "Alvin", LocalDate.of(1981, 3, 17), "Shanxi Xi'an");
        hashCode = hashFn.hashObject(person, Person.makeFunnel());
        then(hashCode).hasToString("d61c874b8ddb8b674871fadeeb7984e96b9679c2");
    }

    /**
     * 测试计算 HMAC 散列值
     *
     * <p>
     * 通过 {@code hmacXXXX} 系列方法可以计算 HMAC 系列散列值, 包括:
     * <ul>
     * <li>{@link Hashing#hmacMd5(byte[])}, 通过 MD5 加盐方式计算散列值, 返回长度 {@code 128bit} 的散列结果</li>
     * <li>{@link Hashing#hmacSha1(byte[])}, 通过 SHA1 加盐方式计算散列值, 返回长度 {@code 160bit} 的散列结果</li>
     * <li>{@link Hashing#hmacSha256(byte[])}, 通过 SHA256 加盐方式计算散列值, 返回长度 {@code 256bit} 的散列结果</li>
     * <li>{@link Hashing#hmacSha512(byte[])}, 通过 SHA512 加盐方式计算散列值, 返回长度 {@code 512bit} 的散列结果</li>
     * </ul>
     * </p>
     *
     * <p>
     * HMAC 散列值相当于加盐的散列值计算, 对于同一组数据, 用不同的盐值 (Key 值), 计算出的散列值也不同, 可以有效地防止对散列结果的查询式攻击
     * (即将算好的散列值保存, 用来进行攻击)
     * </p>
     */
    @Test
    void hmacSha512_shouldCalculateHashCodeWithHmacSha512() {
        var key = "password".getBytes(Charsets.UTF_8);

        // 设定密钥, 获取散列函数
        var hashFn = Hashing.hmacSha512(key);
        // 确认散列函数计算结果的长度
        then(hashFn.bits()).isEqualTo(512);

        // 对字符串输入计算散列值
        var hashCode = hashFn.hashString("HelloWorld", Charsets.UTF_8);
        then(hashCode).hasToString(
            "6be352acddb34f12af6547ca0d4dd38051bd69303d36888df031cc7f1fa3562a97f16edbb76cdf5a0a3528091fc7dd3ee77bc2466495e876586fc1ad4d83c5a6");

        // 对对象输入计算散列值
        var person = new Person(1L, "Alvin", LocalDate.of(1981, 3, 17), "Shanxi Xi'an");
        hashCode = hashFn.hashObject(person, Person.makeFunnel());
        then(hashCode).hasToString(
            "89638c96ff035e25b738036e08bc29e6e7286510173eb3eb26aa9d6f7a2e9ce9a1248478b0c8373cafb837c83a964d7ff93e9df259a7c4f1aaf62bda9ae35b30");
    }

    /**
     * 计算一个临时散列值
     *
     * <p>
     * 通过 {@link Hashing#goodFastHash(int)} 可以获取一个计算指定长度散列值的散列函数, 例如本例中通过
     * {@code Hashing.goodFastHash(256)} 计算一个长度为 {@code 256bit} 的散列值
     * </p>
     *
     * <p>
     * 散列的长度可以为 {@code 32bit, 128bit, (128 * n)bit}, 即除 {@code 32bit} 外, 其它长度必须为 128 的整数倍, 例如要求散列长度为
     * {@code 129bit}, 但实际散列长度为 {@code 256bit}
     * </p>
     *
     * <p>
     * 该散列函数内部是通过 Murmur3 算法计算散列的, 包括 Murmur3_32 以及 Murmur3_128 两种算法, 参考
     * {@link #murmur3_128_shouldCalculateHashCodeWithMurmur3_128()} 范例, 对于要求散列值长度大于 {@code 128bit} 的情况,
     * 则是将多个 Murmur3_128 散列函数组合, 对计算结果进行合并
     * </p>
     *
     * <p>
     * 注意, 该方法的计算效率很高, 但会使用当前时间值作为初始化运算种子, 所以即便对于相同的数据, 每个 Java 进程的散列运算都是不同的,
     * 故而不应使用该方法计算永久性的数据标识, 尽可以作为临时值使用 (例如在 {@link java.util.Map Map} 对象中做 Key)
     * </p>
     *
     * <p>
     * 散列值结果长度越长, 安全性越高, 碰撞的几率越小, 但同时计算的迭代次数会增高, 计算量会大幅度增加
     * </p>
     */
    @Test
    void goodFastHash_shouldCalculateHashCodeWithGoodFastHash() {
        // 获取散列函数
        var hashFn = Hashing.goodFastHash(256);
        // 确认散列函数计算结果的长度
        then(hashFn.bits()).isEqualTo(256);

        // 对字符串输入计算散列值
        var hashCode1 = hashFn.hashString("HelloWorld", Charsets.UTF_8);
        // 对相同字符串输入再次计算散列值
        var hashCode2 = hashFn.hashString("HelloWorld", Charsets.UTF_8);
        // 确认两次计算的散列值相同
        then(hashCode1).hasToString(hashCode2.toString());

        var person = new Person(1L, "Alvin", LocalDate.of(1981, 3, 17), "Shanxi Xi'an");
        // 对对象计算散列值
        hashCode1 = hashFn.hashObject(person, Person.makeFunnel());
        // 对相同对象再次计算散列值
        hashCode2 = hashFn.hashObject(person, Person.makeFunnel());
        // 确认两次计算的散列值相同
        then(hashCode1).hasToString(hashCode2.toString());
    }

    /**
     * 测试 MurmurHash3 函数
     *
     * <p>
     * {@link Hashing#murmur3_128(int)} 方法返回一个通过 Murmur 算法计算散列的函数, 用于计算一个 128 位的散列值
     * </p>
     *
     * <p>
     * Murmur 算法通过一个比较简单的方式计算散列值, 由于不使用加密计算, 所以计算速度较 MD 系列或者 SHA 系列要快很多
     * (约为 MD5 算法的 10 倍)
     * </p>
     *
     * <p>
     * Murmur3 是 Murmur 算法的最新版本, 通过了雪崩测试 (Avalanche Test) 以及卡方测试 (Chi-Squared Test),
     * 具备良好的分布性和数据敏感性. 例如在布隆过滤器中, 为了追求计算 bit 位的分散性, 对数据的散列就是通过 Murmur3-128 算法计算
     * </p>
     *
     * <p>
     * 特别注意的是: Murmur3-128 算法比 Murmur3-32 ({@link Hashing#murmur3_32()}) 算法要快, 这是因为现代处理器的特性
     * </p>
     */
    @Test
    void murmur3_128_shouldCalculateHashCodeWithMurmur3_128() {
        // 获取散列函数
        var hashFn = Hashing.murmur3_128();
        // 确认散列函数计算结果的长度
        then(hashFn.bits()).isEqualTo(128);

        // 对字符串输入计算散列值
        var hashCode = hashFn.hashString("HelloWorld", Charsets.UTF_8);
        then(hashCode).hasToString(
            "53700d7578c99c7192cfb44e0ddd7db2");

        var person = new Person(1L, "Alvin", LocalDate.of(1981, 3, 17), "Shanxi Xi'an");
        // 对对象输入计算散列值
        hashCode = hashFn.hashObject(person, Person.makeFunnel());
        then(hashCode).hasToString("cdfd1405bf4f7eac60c3cb296f80f4fa");
    }

    /**
     * 测试字符串指纹函数
     *
     * <p>
     * {@link Hashing#fingerprint2011()} 方法返回一个 Fingerprint2011 散列函数 (64位)
     * </p>
     *
     * <p>
     * Fingerprint2011 一般不认为适用于计算散列 (尽管确实是), 主要是生成字符串的一个"指纹", 用于作为字符串的摘要标识. Fingerprint2011
     * 会产生一个高质量散列值, 很少会发生冲突
     * </p>
     *
     * <p>
     * 所谓指纹标识即: 两个字符串的指纹不同, 则两个字符串一定不同, 反之则两个字符串大概率相同. 利用指纹可以加快长字符串的比较和检索效率
     * </p>
     *
     * <p>
     * 类似的指纹计算函数还包括 {@link Hashing#farmHashFingerprint64()} 方法返回的散列函数
     * </p>
     */
    @Test
    void fingerprint2011_shouldCalculateHashCodeWithAdler32() {
        // 获取散列函数
        var hashFn = Hashing.fingerprint2011();
        // 确认散列函数计算结果的长度
        then(hashFn.bits()).isEqualTo(64);

        // 对字符串输入计算散列值
        var hashCode = hashFn.hashString("HelloWorld", Charsets.UTF_8);
        then(hashCode.asLong()).isEqualTo(-6909930713299836517L);

        var person = new Person(1L, "Alvin", LocalDate.of(1981, 3, 17), "Shanxi Xi'an");
        // 对对象输入计算散列值
        hashCode = hashFn.hashObject(person, Person.makeFunnel());
        then(hashCode.asLong()).isEqualTo(7442922871322564914L);
    }

    /**
     * 测试 CRC32 (Cyclic Redundancy Check) 验证码函数
     *
     * <p>
     * {@link Hashing#crc32()} 方法返回一个循环冗余验证码计算 (散列) 函数 (32位); 另外 {@link Hashing#crc32c()}
     * 方法返回的计算函数针对于 Intel SSE 4.2 指令集进行优化, 可以显著的提升计算效率
     * </p>
     *
     * <p>
     * CRC32 算法一般不认为是一种散列算法, 其主要作用是作为数据校验码, 即数据传输前计算验证码, 数据接收 (或存储) 后再计算一次验证码,
     * 两次计算结果相同即表示数据再传输和存储过程中未发生错误或被篡改
     * </p>
     *
     * <p>
     * CRC32 算法的计算结果不能作为数据识别或去重的标识, 因为 CRC32 的计算结果发生冲突的概率比较大, 但 CRC32 具有非常高的运算效率,
     * 且主流的硬件平台都提供了对应的硬件加速指令, 所以主要用于数据校验
     * </p>
     */
    @Test
    void crc32_shouldCalculateHashCodeWithCRC32() {
        // 获取验证码计算函数
        var hashFn = Hashing.crc32();
        // 确认验证码计算结果的长度
        then(hashFn.bits()).isEqualTo(32);

        // 对字符串输入计算验证码
        var hashCode = hashFn.hashString("HelloWorld", Charsets.UTF_8);
        then(hashCode.asInt()).isEqualTo(2004290681);

        var person = new Person(1L, "Alvin", LocalDate.of(1981, 3, 17), "Shanxi Xi'an");
        // 对对象输入计算验证码
        hashCode = hashFn.hashObject(person, Person.makeFunnel());
        then(hashCode.asInt()).isEqualTo(234783673);
    }

    /**
     * 测试 Adler32 验证码函数
     *
     * <p>
     * {@link Hashing#adler32()} 方法返回一个验证码计算 (散列) 函数 (32位)
     * </p>
     *
     * <p>
     * Adler32 和 CRC32 类似, 都是对数据求校验码的计算方法, Adler32 的算法更为简单, 计算结果容易被伪造, 所以不具备安全性, 但和 CRC32
     * 相比, Adler32 的运算速度非常快 (约 CRC32 的三到五倍)
     * </p>
     *
     * <p>
     * 在数据安全性要求较低的场景, 可以先用 Adler32 计算验证码, 如果 Adler32 无法达到安全性和容错性的要求, 再换用 CRC32
     * </p>
     */
    @Test
    void adler32_shouldCalculateHashCodeWithAdler32() {
        // 获取验证码计算函数
        var hashFn = Hashing.adler32();
        // 确认验证码计算结果的长度
        then(hashFn.bits()).isEqualTo(32);

        // 对字符串输入计算验证码
        var hashCode = hashFn.hashString("HelloWorld", Charsets.UTF_8);
        then(hashCode.asInt()).isEqualTo(357958653);

        var person = new Person(1L, "Alvin", LocalDate.of(1981, 3, 17), "Shanxi Xi'an");
        // 对对象输入计算验证码
        hashCode = hashFn.hashObject(person, Person.makeFunnel());
        then(hashCode.asInt()).isEqualTo(1883834422);
    }

    /**
     * 测试 SipHash 散列函数
     *
     * <p>
     * 通过 {@link Hashing#sipHash24(long, long)} 方法获取 SipHash 散列计算函数, 可以计算一个长度 {@code 64bit} 的散列值,
     * 其两个参数为两个 {@code 64bit} 长度的密钥
     * </p>
     *
     * <p>
     * SipHash24 的 {@code 24} 表示算法的压缩轮次为 {@code 2}, 终结轮次为 {@code 4}, 即算法不同阶段的迭代次数, {@code 24}
     * 的组合具备强 PRF (伪随机数) 定义, 具备较高的安全性, 参考
     * <a href="https://en.wikipedia.org/wiki/SipHash">https://en.wikipedia.org/wiki/SipHash</a>
     * </p>
     *
     * <p>
     * SipHash 属于加密散列, 具有一个 {@code 128bit} 的密钥, 但又比 {@code HMAC} 这类散列算法效率高很多
     * </p>
     *
     * <p>
     * SipHash 的主要作用是用于 {@code HashTable} 这类结构. 当根据数据计算出散列值, 存入 {@code HashTable} 中时,
     * 无法避免将会产生冲突, 如果数据散列不够分散, 这类冲突将会对 {@code HashTable} 带来很大的压力
     * (将算法退化为线性算法或导致大量的存储区重建工作). SipHash 一方面在计算结果上较短且足够分散, 另外具备密钥, 另攻击者很难推断出来
     * {@code HashTable} 的 Key 分布规律, 从而可以保护 {@code HashTable} 在使用时不会受到恶意的数据攻击
     * </p>
     */
    @Test
    void sipHash_shouldCalculateHashCodeWithSipHash24() {
        // 获取散列函数
        var hashFn = Hashing.sipHash24(0xdeadbeefaffe0022L, 0xdeadbeefaccf2200L); // cspell: disable-line
        // 确认散列计算结果的长度
        then(hashFn.bits()).isEqualTo(64);

        // 对字符串输入计算散列值
        var hashCode = hashFn.hashString("HelloWorld", Charsets.UTF_8);
        then(hashCode.asLong()).isEqualTo(-8061863767188984683L);

        var person = new Person(1L, "Alvin", LocalDate.of(1981, 3, 17), "Shanxi Xi'an");
        // 对对象输入计算散列值
        hashCode = hashFn.hashObject(person, Person.makeFunnel());
        then(hashCode.asLong()).isEqualTo(-9031241806438730239L);
    }

    /**
     * 将一组具备相同字节数的散列值按顺序进行合并
     *
     * <p>
     * {@link Hashing#combineOrdered(Iterable)} 方法按输入顺序将一系列长度相同的散列值进行合并, 所得结果仍是一个 {@link HashCode}
     * 散列值对象, 且长度和被组合的散列值长度一致
     * </p>
     *
     * <p>
     * 本方法的意义在于: 如果通过组合方式得到了一致的散列值结果, 则可以认为用于组合的散列值以及组合顺序都是一致的.
     * 所以可以对多次分批计算的散列值进行合并
     * </p>
     *
     * <p>
     * {@code combineOrdered} 方法组合多个散列码的算法是按迭代逐位异或, 即在每次迭代中,
     * 用之前计算的结果和下一个散列码的每一个字节进行异或操作, 所以对于这一系列散列码的运算, 如果顺序不同, 则结果也不同
     * </p>
     */
    @Test
    void combineOrdered_shouldCombineSeriesHashCodesToOne() {
        var hashFn = Hashing.md5();

        // 获取一组长度相同的散列值计算结果集合
        var hashCodes = Lists.<HashCode>newArrayList();
        for (var i = 0; i < 3; i++) {
            var eachHashCode = hashFn.hashString(String.format("%c", 'A' + i).repeat(5), Charsets.UTF_8);
            then(eachHashCode.bits()).isEqualTo(hashFn.bits());

            hashCodes.add(eachHashCode);
        }

        // 将该组散列集合进行合并
        var hashCode = Hashing.combineOrdered(hashCodes);
        // 确认合并结果的长度和合并前散列值的长度一致
        then(hashCode.bits()).isEqualTo(hashFn.bits());
        then(hashCode).hasToString("a5578e4f6fc1f50d5c91e66b5cb66742");

        // 将散列值结果中的某两项交换
        Collections.swap(hashCodes, 0, hashCodes.size() - 1);

        // 重新对交换后的散列值集合进行合并
        hashCode = Hashing.combineOrdered(hashCodes);
        then(hashCode.bits()).isEqualTo(hashFn.bits());
        // 确认由于参与计算的散列值集合顺序不同, 合并的结果也不同
        then(hashCode).hasToString("dd2f1e7f6f29cd0ddca1d6d3ece66782");
    }

    /**
     * 将一组具备相同字节数的散列值进行合并, 且和顺序无关
     *
     * <p>
     * {@link Hashing#combineUnordered(Iterable)} 方法将一系列长度相同的散列值进行合并, 所得结果仍是一个 {@link HashCode}
     * 散列值对象, 且长度和被组合的散列值长度一致
     * </p>
     *
     * <p>
     * 本方法的意义在于: 如果通过组合方式得到了一致的散列值结果, 则可以认为用于组合的散列值是一致的
     * </p>
     *
     * <p>
     * {@code combineOrdered} 方法组合多个散列码的算法是按迭代逐位相加, 即在每次迭代中,
     * 用之前计算的结果和下一个散列码的每一个字节进行相加操作, 所以对于这一系列散列码的运算, 只要散列码相同, 不受顺序影响
     * </p>
     */
    @Test
    void combineUnordered_shouldCombineSeriesHashCodesToOne() {
        var hashFn = Hashing.md5();

        // 获取一组长度相同的散列值计算结果集合
        var hashCodes = Lists.<HashCode>newArrayList();
        for (var i = 0; i < 3; i++) {
            var eachHashCode = hashFn.hashString(String.format("%c", 'A' + i).repeat(5), Charsets.UTF_8);
            then(eachHashCode.bits()).isEqualTo(128);

            hashCodes.add(eachHashCode);
        }

        // 将该组散列集合进行合并
        var hashCode = Hashing.combineUnordered(hashCodes);
        // 确认合并结果的长度和合并前散列值的长度一致
        then(hashCode.bits()).isEqualTo(128);
        then(hashCode).hasToString("65d71627592943b9d8f9e4e3d624232c");

        Collections.swap(hashCodes, 0, hashCodes.size() - 1);

        // 将散列值结果中的某两项交换
        hashCode = Hashing.combineUnordered(hashCodes);
        then(hashCode.bits()).isEqualTo(128);
        // 确认参与计算的散列值集合顺序不影响合并结果
        then(hashCode).hasToString("65d71627592943b9d8f9e4e3d624232c");
    }

    /**
     * 通过一致性哈希算法计算桶编号
     *
     * <p>
     * 通过 {@link Hashing#consistentHash(HashCode, int)} 方法可以根据一个桶的总数计算给定哈希值的桶索引
     * </p>
     *
     * <p>
     * 一致性哈希算法可以在总的桶数发生变化时, 尽可能不影响之前计算的桶索引, 减少因桶总数发生变化导致的命中不到问题
     * </p>
     *
     * <p>
     * 一致性哈希的定义参考:
     * <a href="https://en.wikipedia.org/wiki/Consistent_hashing">https://en.wikipedia.org/wiki/Consistent_hashing</a>;
     * 具体算法参考:
     * <a href=
     * "https://www.codeproject.com/Articles/56138/Consistent-hashing">https://www.codeproject.com/Articles/56138/Consistent-hashing</a>
     * </p>
     */
    @Test
    void consistentHash_shouldCalculateBucketIndexByConsistentHash() {
        var hashFn = Hashing.sipHash24();

        // 计算一组散列值, 存入一个集合
        var hashCodes = Lists.<HashCode>newArrayList();
        for (var i = 0; i < 3; i++) {
            hashCodes.add(hashFn.hashString(String.format("%c", 'A' + i).repeat(5), Charsets.UTF_8));
        }

        // 在桶数量为 10 时计算散列集合值每项的映射值
        var buckets = 10;
        var nodes1 = Lists.<Integer>newArrayList();
        for (var hashCode : hashCodes) {
            nodes1.add(Hashing.consistentHash(hashCode, buckets));
        }

        // 在桶数量为 8 时计算散列集合值每项的映射值
        buckets = 8;
        var nodes2 = Lists.<Integer>newArrayList();
        for (var hashCode : hashCodes) {
            nodes2.add(Hashing.consistentHash(hashCode, buckets));
        }

        // 确认桶数量小幅度修改时, 不影响映射值的计算结果
        then(nodes1).containsExactlyElementsOf(nodes2);

        // 在桶数量为 12 时计算散列集合值每项的映射值
        buckets = 12;
        nodes2 = Lists.newArrayList();
        for (var hashCode : hashCodes) {
            nodes2.add(Hashing.consistentHash(hashCode, buckets));
        }

        // 确认桶数量小幅度修改时, 不影响映射值的计算结果
        then(nodes1).containsExactlyElementsOf(nodes2);
    }

    /**
     * 组合多个散列函数
     *
     * <p>
     * 通过 {@link Hashing#concatenating(Iterable)} 方法可以将多个散列函数组合起来, 产生一个由这些散列函数计算的散列值按顺序拼接的结果,
     * 整个结果的长度为每个散列函数结果长度之和
     * </p>
     *
     * <p>
     * 通过该方法, 可以用已知散列函数组合成一个更为复杂的散列函数, 减少冲突的可能性. 当然,
     * 组合结果的运算时长也是所有参与组合散列函数运算时长之和
     * </p>
     */
    @Test
    void concatenating_shouldCombineMoreHashFunctions() {
        // 定义多个散列函数
        var hashFn1 = Hashing.sipHash24();
        var hashFn2 = Hashing.md5();
        var hashFn3 = Hashing.sha256();

        // 将多个散列函数组合为一个
        var hashFn = Hashing.concatenating(hashFn1, hashFn2, hashFn3);
        // 确认合并后的散列函数计算的散列值长度为所有参与组合的散列函数散列值长度之和
        then(hashFn.bits()).isEqualTo(hashFn1.bits() + hashFn2.bits() + hashFn3.bits());

        // 对字符串输入计算散列值
        var hashCode = hashFn.hashString("HelloWorld", Charsets.UTF_8);
        then(hashCode).hasToString(
            "9a1b31c53def8ce568e109f0f40ca72a15e05cc22786f8e6872e4e50ce9990d8b041330c47c9ddd11bec6b503ae9386a99da8584e9bb12c4");

        var person = new Person(1L, "Alvin", LocalDate.of(1981, 3, 17), "Shanxi Xi'an");
        // 对对象输入计算散列值
        hashCode = hashFn.hashObject(person, Person.makeFunnel());
        then(hashCode).hasToString(
            "33227b6b0652fbdcccac6eea503e5c4ffba468e927a2536566c029029975d82cf86b0b698ec5aecc7c9313e748164d8e8cb89e7f1e322c8c");
    }
}
