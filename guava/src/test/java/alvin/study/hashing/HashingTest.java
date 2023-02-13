package alvin.study.hashing;

import static org.assertj.core.api.BDDAssertions.then;

import java.time.LocalDate;
import java.util.Collections;

import org.junit.jupiter.api.Test;

import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;

@SuppressWarnings("deprecation")
class HashingTest {
    @Test
    void md5_shouldCalculateHashCodeWithMD5() {
        var hashFn = Hashing.md5();
        then(hashFn.bits()).isEqualTo(128);

        var hashCode = hashFn.hashString("HelloWorld", Charsets.UTF_8);
        then(hashCode).hasToString("68e109f0f40ca72a15e05cc22786f8e6");

        var person = new Person(1L, "Alvin", LocalDate.of(1981, 3, 17), "Shanxi Xi'an");
        hashCode = hashFn.hashObject(person, Person.makeFunnel());
        then(hashCode).hasToString("ccac6eea503e5c4ffba468e927a25365");
    }

    @Test
    void sha1_shouldCalculateHashCodeWithSHA1() {
        var hashFn = Hashing.sha1();
        then(hashFn.bits()).isEqualTo(160);

        var hashCode = hashFn.hashString("HelloWorld", Charsets.UTF_8);
        then(hashCode).hasToString("db8ac1c259eb89d4a131b253bacfca5f319d54f2");

        var person = new Person(1L, "Alvin", LocalDate.of(1981, 3, 17), "Shanxi Xi'an");
        hashCode = hashFn.hashObject(person, Person.makeFunnel());
        then(hashCode).hasToString("d61c874b8ddb8b674871fadeeb7984e96b9679c2");
    }

    @Test
    void sha256_shouldCalculateHashCodeWithSHA256() {
        var hashFn = Hashing.sha256();
        then(hashFn.bits()).isEqualTo(256);

        var hashCode = hashFn.hashString("HelloWorld", Charsets.UTF_8);
        then(hashCode).hasToString("872e4e50ce9990d8b041330c47c9ddd11bec6b503ae9386a99da8584e9bb12c4");

        var person = new Person(1L, "Alvin", LocalDate.of(1981, 3, 17), "Shanxi Xi'an");
        hashCode = hashFn.hashObject(person, Person.makeFunnel());
        then(hashCode).hasToString("66c029029975d82cf86b0b698ec5aecc7c9313e748164d8e8cb89e7f1e322c8c");
    }

    @Test
    void goodFastHash_shouldCalculateHashCodeWithGoodFastHash() {
        var hashFn = Hashing.goodFastHash(256);
        then(hashFn.bits()).isEqualTo(256);

        var hashCode1 = hashFn.hashString("HelloWorld", Charsets.UTF_8);
        var hashCode2 = hashFn.hashString("HelloWorld", Charsets.UTF_8);
        then(hashCode1).hasToString(hashCode2.toString());

        var person = new Person(1L, "Alvin", LocalDate.of(1981, 3, 17), "Shanxi Xi'an");
        hashCode1 = hashFn.hashObject(person, Person.makeFunnel());
        hashCode2 = hashFn.hashObject(person, Person.makeFunnel());
        then(hashCode1).hasToString(hashCode2.toString());
    }

    @Test
    void hmacSha512_shouldCalculateHashCodeWithHmacSha512() {
        var key = "password".getBytes(Charsets.UTF_8);

        var hashFn = Hashing.hmacSha512(key);
        then(hashFn.bits()).isEqualTo(512);

        var hashCode = hashFn.hashString("HelloWorld", Charsets.UTF_8);
        then(hashCode).hasToString(
            "6be352acddb34f12af6547ca0d4dd38051bd69303d36888df031cc7f1fa3562a97f16edbb76cdf5a0a3528091fc7dd3ee77bc2466495e876586fc1ad4d83c5a6");

        var person = new Person(1L, "Alvin", LocalDate.of(1981, 3, 17), "Shanxi Xi'an");
        hashCode = hashFn.hashObject(person, Person.makeFunnel());
        then(hashCode).hasToString(
            "89638c96ff035e25b738036e08bc29e6e7286510173eb3eb26aa9d6f7a2e9ce9a1248478b0c8373cafb837c83a964d7ff93e9df259a7c4f1aaf62bda9ae35b30");
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
        var hashFn = Hashing.murmur3_128();
        then(hashFn.bits()).isEqualTo(128);

        var hashCode = hashFn.hashString("HelloWorld", Charsets.UTF_8);
        then(hashCode).hasToString(
            "53700d7578c99c7192cfb44e0ddd7db2");

        var person = new Person(1L, "Alvin", LocalDate.of(1981, 3, 17), "Shanxi Xi'an");
        hashCode = hashFn.hashObject(person, Person.makeFunnel());
        then(hashCode).hasToString("cdfd1405bf4f7eac60c3cb296f80f4fa");
    }

    /**
     * 测试指纹 Hash 函数
     *
     * <p>
     * {@link Hashing#fingerprint2011()} 方法返回一个 Fingerprint2011 散列函数 (64位)
     * </p>
     *
     * <p>
     * Fingerprint2011 一般不认为适用于计算散列 (尽管确实是), 主要是生成字符串的一个"指纹", 用于作为字符串的唯一标识. Fingerprint2011
     * 会产生一个高质量散列值, 很少会发生冲突
     * </p>
     *
     * <p>
     * Fingerprint2011 是一种针对 {@code 32} 字节以内字符串的 Murmur2 形式, 以及一种针对更长的字符串的 CityHash 形式.
     * 自始至终都可能是其中之一. 这种组合的主要优势是: CityHash 为短字符串提供了一堆特殊情况, 不需要复制且结果永远不会是 {@code 0} 或
     * {@code 1}
     * </p>
     */
    @Test
    void fingerprint2011_shouldCalculateHashCodeWithAdler32() {
        var hashFn = Hashing.fingerprint2011();
        then(hashFn.bits()).isEqualTo(64);

        var hashCode = hashFn.hashString("HelloWorld", Charsets.UTF_8);
        then(hashCode).hasToString("9ba1336ba0fe1aa0");

        var person = new Person(1L, "Alvin", LocalDate.of(1981, 3, 17), "Shanxi Xi'an");
        hashCode = hashFn.hashObject(person, Person.makeFunnel());
        then(hashCode).hasToString("32d9cfcbea924a67");
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
        var hashFn = Hashing.crc32();
        then(hashFn.bits()).isEqualTo(32);

        var hashCode = hashFn.hashString("HelloWorld", Charsets.UTF_8);
        then(hashCode.asInt()).isEqualTo(2004290681);

        var person = new Person(1L, "Alvin", LocalDate.of(1981, 3, 17), "Shanxi Xi'an");
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
        var hashFn = Hashing.adler32();
        then(hashFn.bits()).isEqualTo(32);

        var hashCode = hashFn.hashString("HelloWorld", Charsets.UTF_8);
        then(hashCode.asInt()).isEqualTo(357958653);

        var person = new Person(1L, "Alvin", LocalDate.of(1981, 3, 17), "Shanxi Xi'an");
        hashCode = hashFn.hashObject(person, Person.makeFunnel());
        then(hashCode.asInt()).isEqualTo(1883834422);
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

        var hashCodes = Lists.<HashCode>newArrayList();
        for (var i = 0; i < 3; i++) {
            var eachHashCode = hashFn.hashString(String.format("%c", 'A' + i).repeat(5), Charsets.UTF_8);
            then(eachHashCode.bits()).isEqualTo(128);

            hashCodes.add(eachHashCode);
        }

        var hashCode = Hashing.combineOrdered(hashCodes);
        then(hashCode.bits()).isEqualTo(128);
        then(hashCode).hasToString("a5578e4f6fc1f50d5c91e66b5cb66742");

        Collections.swap(hashCodes, 0, hashCodes.size() - 1);

        hashCode = Hashing.combineOrdered(hashCodes);
        then(hashCode.bits()).isEqualTo(128);
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

        var hashCodes = Lists.<HashCode>newArrayList();
        for (var i = 0; i < 3; i++) {
            var eachHashCode = hashFn.hashString(String.format("%c", 'A' + i).repeat(5), Charsets.UTF_8);
            then(eachHashCode.bits()).isEqualTo(128);

            hashCodes.add(eachHashCode);
        }

        var hashCode = Hashing.combineUnordered(hashCodes);
        then(hashCode.bits()).isEqualTo(128);
        then(hashCode).hasToString("65d71627592943b9d8f9e4e3d624232c");

        Collections.swap(hashCodes, 0, hashCodes.size() - 1);

        hashCode = Hashing.combineUnordered(hashCodes);
        then(hashCode.bits()).isEqualTo(128);
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
     * 一致性哈希的定义参考: {@link https://en.wikipedia.org/wiki/Consistent_hashing}; 具体算法参考:
     * {@link https://www.codeproject.com/Articles/56138/Consistent-hashing}
     * </p>
     */
    @Test
    void consistentHash_shouldCalculateBucketIndexByConsistentHash() {
        var hashFn = Hashing.md5();

        var hashCodes = Lists.<HashCode>newArrayList();
        for (var i = 0; i < 3; i++) {
            var eachHashCode = hashFn.hashString(String.format("%c", 'A' + i).repeat(5), Charsets.UTF_8);
            then(eachHashCode.bits()).isEqualTo(128);

            hashCodes.add(eachHashCode);
        }

        var buckets = 5;
        var nodes1 = Lists.<Integer>newArrayList();
        for (var hashCode : hashCodes) {
            nodes1.add(Hashing.consistentHash(hashCode, buckets));
        }

        buckets = 3;
        var nodes2 = Lists.<Integer>newArrayList();
        for (var hashCode : hashCodes) {
            nodes2.add(Hashing.consistentHash(hashCode, buckets));
        }

        then(nodes1).containsExactlyElementsOf(nodes2);

        buckets = 4;
        nodes2 = Lists.<Integer>newArrayList();
        for (var hashCode : hashCodes) {
            nodes2.add(Hashing.consistentHash(hashCode, buckets));
        }

        then(nodes1).containsExactlyElementsOf(nodes2);
    }
}
