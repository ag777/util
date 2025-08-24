package github.ag777.util.net;

import github.ag777.util.lang.ObjectUtils;
import github.ag777.util.lang.StringUtils;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 一个通过链式调用流畅地构建、解析和操作 URI 的多功能工具类。
 * <p>
 * 该类提供了灵活的参数设置接口（接受 Object 类型），同时在内部将所有参数统一存储为字符串列表
 * ({@code List<String>})，以保证类型明确和行为一致。
 * </p>
 *
 * <h3>核心特性:</h3>
 * <ul>
 *     <li><b>灵活设值:</b> 可使用 {@code setParam(key, Object)} 传入任意类型的值，工具类会自动将其转换为字符串。</li>
 *     <li><b>类型明确:</b> 内部所有参数都以 {@code List<String>} 形式存储，API 只返回字符串或字符串列表。</li>
 *     <li><b>多值支持:</b> 通过 {@code addParam} 或传入集合/数组来轻松管理多值参数。</li>
 *     <li><b>链式调用:</b> 所有修改操作都返回自身，便于连续操作。</li>
 * </ul>
 *
 * <h3>使用示例:</h3>
 * <pre>{@code
 * // 1. 使用不同类型的值构建 URI
 * List<Integer> ids = List.of(101, 102);
 * URI uri = UriBuilder.create()
 *         .scheme("https")
 *         .host("api.example.com")
 *         .path("/items")
 *         .setParam("ids", ids)       // 传入 List<Integer>
 *         .setParam("page", 1)
 *         .setParam("isPublished", true) // 传入 boolean
 *         .addParam("filter", "new")
 *         .build();
 * // 结果: https://api.example.com/items?ids=101&ids=102&page=1&isPublished=true&filter=new
 *
 * // 2. 解析和读取 URI
 * UriBuilder builder = UriBuilder.of(uri);
 * String page = builder.getParam("page"); // "1"
 * List<String> idList = builder.getParamList("ids"); // ["101", "102"]
 * }</pre>
 *
 * @author ag777
 * @version  2025/08/24 20:33
 */
public class UriBuilder {

    /**
     * URI scheme，例如 "http" 或 "https"。
     */
    private String scheme;
    /**
     * URI 用户信息部分。
     */
    private String userInfo;
    /**
     * URI 主机名。
     */
    private String host;
    /**
     * URI 端口号，-1 表示未设置。
     */
    private int port = -1;
    /**
     * URI 路径部分。
     */
    private String path;
    /**
     * 存储查询参数的 Map。每个键都映射到一个字符串值的列表。
     */
    private final Map<String, List<String>> queryParams = new LinkedHashMap<>();
    /**
     * URI 片段部分（#后的内容）。
     */
    private String fragment;

    private UriBuilder() {
    }

    /**
     * 创建一个新的、空的 {@code UriBuilder} 实例。
     *
     * @return 一个新的 {@code UriBuilder}。
     */
    public static UriBuilder create() {
        return new UriBuilder();
    }

    /**
     * 从一个已有的 {@link URI} 创建 {@code UriBuilder} 实例。
     *
     * @param uri 要解析的 URI。
     * @return 一个用给定 URI 组件初始化的新 {@code UriBuilder}。
     * @throws NullPointerException 如果 uri 为 null。
     */
    public static UriBuilder of(URI uri) {
        Objects.requireNonNull(uri, "URI 不能为空");
        UriBuilder builder = new UriBuilder();
        builder.scheme = uri.getScheme();
        builder.userInfo = uri.getUserInfo();
        builder.host = uri.getHost();
        builder.port = uri.getPort();
        builder.path = uri.getPath();
        builder.fragment = uri.getFragment();
        parseQuery(uri.getRawQuery(), builder.queryParams);
        return builder;
    }

    /**
     * 从一个 URI 字符串创建 {@code UriBuilder} 实例。
     *
     * @param uriString 要解析的字符串。
     * @return 一个从解析后的 URI 初始化的新 {@code UriBuilder}。
     * @throws IllegalArgumentException 如果 uriString 无效。
     */
    public static UriBuilder of(String uriString) {
        return of(URI.create(uriString));
    }

    // --- 解析和构建 --- 

    /**
     * 解析查询参数。
     *
     * @param query 查询参数。
     * @param params 参数的 Map。
     */
    private static void parseQuery(String query, Map<String, List<String>> params) {
        if (query == null || query.isEmpty()) {
            return;
        }
        params.clear();
        Stream.of(query.split("&"))
                .forEach(pair -> {
                    int idx = pair.indexOf('=');
                    String key = decode(idx == -1 ? pair : pair.substring(0, idx));
                    String value = idx == -1 || pair.substring(idx + 1).isEmpty() ? null : decode(pair.substring(idx + 1));
                    params.computeIfAbsent(key, k -> new ArrayList<>()).add(value);
                });
    }

    /**
     * 构建最终的 {@link URI} 实例。
     *
     * @return 构建完成的 {@link URI}。
     * @throws IllegalStateException 如果在构建过程中 URI 语法不正确。
     */
    public URI build() {
        try {
            return new URI(scheme, userInfo, host, port, path, buildQuery(), fragment);
        }
        catch (URISyntaxException e) {
            throw new IllegalStateException("构建 URI 失败", e);
        }
    }

    private String buildQuery() {
        if (queryParams.isEmpty()) {
            return null;
        }
        return this.queryParams.entrySet().stream()
                .flatMap(entry -> {
                    List<String> values = entry.getValue();
                    // 防御性检查：如果与键关联的列表为 null，则视为空流，不生成任何参数
                    if (values == null) {
                        return Stream.empty();
                    }
                    return values.stream()
                            .map(value -> encode(entry.getKey()) + "=" + (value == null ? "" : encode(value)));
                })
                .collect(Collectors.joining("&"));
    }

    // --- Setters --- 

    /**
     * 设置 scheme 组件 (例如 "http", "https")。
     *
     * @param scheme scheme 名称。
     * @return 当前 {@code UriBuilder} 实例，用于链式调用。
     */
    public UriBuilder scheme(String scheme) {
        this.scheme = scheme;
        return this;
    }

    /**
     * 设置 user-info 组件。
     *
     * @param userInfo 用户信息。
     * @return 当前 {@code UriBuilder} 实例，用于链式调用。
     */
    public UriBuilder userInfo(String userInfo) {
        this.userInfo = userInfo;
        return this;
    }

    /**
     * 设置 host 组件。
     *
     * @param host 主机名。
     * @return 当前 {@code UriBuilder} 实例，用于链式调用。
     */
    public UriBuilder host(String host) {
        this.host = host;
        return this;
    }

    /**
     * 设置 port 组件。
     *
     * @param port 端口号，或 -1 表示不设置。
     * @return 当前 {@code UriBuilder} 实例，用于链式调用。
     */
    public UriBuilder port(int port) {
        this.port = port;
        return this;
    }

    /**
     * 设置 path 组件。
     *
     * @param path 路径。
     * @return 当前 {@code UriBuilder} 实例，用于链式调用。
     */
    public UriBuilder path(String path) {
        this.path = path;
        return this;
    }

    /**
     * 设置 fragment 组件。
     *
     * @param fragment 片段。
     * @return 当前 {@code UriBuilder} 实例，用于链式调用。
     */
    public UriBuilder fragment(String fragment) {
        this.fragment = fragment;
        return this;
    }

    /**
     * 设置查询参数。
     *
     * @param query 查询参数。
     * @return 当前 {@code UriBuilder} 实例，用于链式调用。
     */
    public UriBuilder query(String query) {
        this.queryParams.clear();
        parseQuery(query, this.queryParams);
        return this;
    }

    /**
     * 检查是否存在指定的查询参数。
     *
     * @param key 参数的键。
     * @return 如果参数存在，则返回 {@code true}，否则返回 {@code false}。
     */
    public boolean containsParam(String key) {
        if (this.queryParams.containsKey(key)) {
            String value = getParam(key);
            return !StringUtils.isEmpty(value);
        }
        return false;
    }

    /**
     * 计算一个查询参数的值。
     *
     * @param key 参数的键。
     * @param remappingFunction 计算函数。
     * @return 当前 {@code UriBuilder} 实例，用于链式调用。
     */
    public UriBuilder computeParam(String key, BiFunction<String, String, String> remappingFunction) {
        Objects.requireNonNull(key, "键不能为空");
        String oldValue = getParam(key);

        String newValue = remappingFunction.apply(key, oldValue);
        if (newValue == null) {
            // delete mapping
            if (oldValue != null || this.queryParams.containsKey(key)) {
                // something to remove
                this.queryParams.remove(key);
                return null;
            } else {
                // nothing to do. Leave things as they were.
                return null;
            }
        } else {
            // add or replace old mapping
            this.queryParams.put(key, convertValueToStrings(newValue));
            return this;
        }
    }

    /**
     * 计算一个查询参数的值，如果参数存在。
     *
     * @param key 参数的键。
     * @param remappingFunction 计算函数。
     * @return 当前 {@code UriBuilder} 实例，用于链式调用。
     */
    public UriBuilder computeParamIfPresent(String key, BiFunction<String, String, String> remappingFunction) {
        if (this.queryParams.containsKey(key)) {
            computeParam(key, remappingFunction);
        }
        return this;
    }

    /**
     * 计算一个查询参数的值，如果参数不存在。
     *
     * @param key 参数的键。
     * @param remappingFunction 计算函数。
     * @return 当前 {@code UriBuilder} 实例，用于链式调用。
     */
    public UriBuilder computeParamIfAbsent(String key, Function<String, String> remappingFunction) {
        if (!this.queryParams.containsKey(key)) {
            this.queryParams.put(key, convertValueToStrings(remappingFunction.apply(key)));
        }
        return this;
    }

    /**
     * 设置一个查询参数，替换该键已有的任何值。
     * <p>
     * 值可以是任意类型，工具类会自动进行字符串转换：
     * <ul>
     *     <li>集合或数组会被转换为多个字符串值。</li>
     *     <li>其他类型会通过 {@code ObjectUtils.toStr()} 转换为单个字符串。</li>
     * </ul>
     *
     * @param key   参数的键。不能为空。
     * @param value 参数的值，可以为 null。
     * @return 当前 {@code UriBuilder} 实例，用于链式调用。
     */
    public UriBuilder setParam(String key, Object value) {
        Objects.requireNonNull(key, "键不能为空");
        this.queryParams.put(key, convertValueToStrings(value));
        return this;
    }

    /**
     * 设置一个查询参数，如果条件为 true，则替换该键已有的任何值。
     *
     * @param condition 条件。
     * @param key 参数的键。不能为空。
     * @param value 参数的值，可以为 null。
     * @return 当前 {@code UriBuilder} 实例，用于链式调用。
     */
    public UriBuilder setParam(boolean condition, String key, Object value) {
        if (condition) {
            setParam(key, value);
        }
        return this;
    }

    /**
     * 添加一个或多个查询参数。
     * <p>
     * 如果传入的是集合或数组，其所有元素都将被添加为该键的值。
     * 否则，将添加转换后的单个字符串值。
     *
     * @param key   参数的键。不能为空。
     * @param value 参数的值，可以为 null。
     * @return 当前 {@code UriBuilder} 实例，用于链式调用。
     */
    public UriBuilder addParam(String key, Object value) {
        Objects.requireNonNull(key, "键不能为空");
        this.queryParams.computeIfAbsent(key, k -> new ArrayList<>()).addAll(convertValueToStrings(value));
        return this;
    }

    /**
     * 添加一个或多个查询参数，如果条件为 true，则添加该键的值。
     *
     * @param condition 条件。
     * @param key 参数的键。不能为空。
     * @param value 参数的值，可以为 null。
     * @return 当前 {@code UriBuilder} 实例，用于链式调用。
     */
    public UriBuilder addParam(boolean condition, String key, Object value) {
        if (condition) {
            addParam(key, value);
        }
        return this;
    }

    /**
     * 替换指定键的参数值。
     *
     * @param key 参数的键。
     * @param valueConverter 值转换器，如果为 null，则移除该键。
     * @return 当前 {@code UriBuilder} 实例，用于链式调用。
     */
    public UriBuilder replaceParam(String key, Function<String, Object> valueConverter) {
        Objects.requireNonNull(key, "键不能为空");
        if (valueConverter == null) {
            return this;
        }
        Object newVal = valueConverter.apply(getParam(key));
        if (newVal == null) {
            this.queryParams.remove(key);
        } else {
            this.queryParams.put(key, convertValueToStrings(newVal));
        }
        return this;
    }

    /**
     * 移除指定键的所有查询参数。
     *
     * @param key 要移除的参数的键。
     * @return 当前 {@code UriBuilder} 实例，用于链式调用。
     */
    public UriBuilder removeParam(String key) {
        this.queryParams.remove(key);
        return this;
    }

    /**
     * 移除指定键的查询参数，如果条件为 true。
     *
     * @param condition 条件。
     * @param key 要移除的参数的键。
     * @return 当前 {@code UriBuilder} 实例，用于链式调用。
     */
    public UriBuilder removeParam(boolean condition, String key) {
        if (condition) {
            removeParam(key);
        }
        return this;
    }

    /**
     * 移除所有值为 null 的查询参数。
     *
     * @return 当前 {@code UriBuilder} 实例，用于链式调用。
     */
    public UriBuilder clearNullParams() {
        this.queryParams.entrySet().removeIf(entry -> entry.getValue().isEmpty() || entry.getValue().stream().allMatch(Objects::isNull));
        return this;
    }

    // --- Getters --- 

    /**
     * 获取 URI 的 scheme 部分。
     *
     * @return URI 的 scheme 部分。
     */
    public String scheme() {
        return scheme;
    }

    /**
     * 获取 URI 的用户信息部分。
     *
     * @return URI 的用户信息部分。
     */
    public String userInfo() {
        return userInfo;
    }

    /**
     * 获取 URI 的主机名。
     *
     * @return URI 的主机名。
     */
    public String host() {
        return host;
    }

    /**
     * 获取 URI 的端口号。
     *
     * @return URI 的端口号。
     */
    public int port() {
        return port;
    }

    /**
     * 获取 URI 的路径部分。
     *
     * @return URI 的路径部分。
     */
    public String path() {
        return path;
    }

    /**
     * 获取 URI 的查询参数部分。
     *
     * @return URI 的查询参数部分。
     */
    public String query() {
        return buildQuery();
    }

    /**
     * 获取 URI 的片段部分。
     *
     * @return URI 的片段部分。
     */
    public String fragment() {
        return fragment;
    }

    /**
     * 获取单个查询参数的值。
     * <p>如果一个键对应多个值，此方法只返回列表中的第一个值。</p>
     *
     * @param key 参数的键。
     * @return 参数的字符串值。如果键不存在或列表为空，则返回 {@code null}。
     */
    public String getParam(String key) {
        List<String> values = this.queryParams.get(key);
        return (values == null || values.isEmpty()) ? null : values.get(0);
    }

    /**
     * 获取一个键对应的整数参数值。
     *
     * @param key 参数的键。
     * @return 参数的整数值。如果键不存在或无法转换为整数，则返回 {@code null}。
     */
    public Integer getIntParam(String key) {
        String value = getParam(key);
        return ObjectUtils.toInt(value);
    }

    /**
     * 获取一个键对应的整数参数值，如果键不存在或无法转换为整数，则返回默认值。
     *
     * @param key 参数的键。
     * @param defaultValue 默认值。
     * @return 参数的整数值。如果键不存在或无法转换为整数，则返回默认值。
     */
    public int getIntParam(String key, int defaultValue) {
        String value = getParam(key);
        return ObjectUtils.toInt(value, defaultValue);
    }

    /**
     * 获取一个键对应的长整数参数值。
     *
     * @param key 参数的键。
     * @return 参数的长整数值。如果键不存在或无法转换为长整数，则返回 {@code null}。
     */
    public long getLongParam(String key) {
        String value = getParam(key);
        return ObjectUtils.toLong(value);
    }

    /**
     * 获取一个键对应的长整数参数值，如果键不存在或无法转换为长整数，则返回默认值。
     *
     * @param key 参数的键。
     * @param defaultValue 默认值。
     * @return 参数的长整数值。如果键不存在或无法转换为长整数，则返回默认值。
     */
    public long getLongParam(String key, long defaultValue) {
        String value = getParam(key);
        return ObjectUtils.toLong(value, defaultValue);
    }

    /**
     * 获取一个键对应的浮点数参数值。
     *
     * @param key 参数的键。
     * @return 参数的浮点数值。如果键不存在或无法转换为浮点数，则返回 {@code null}。
     */
    public double getDoubleParam(String key) {
        String value = getParam(key);
        return ObjectUtils.toDouble(value);
    }

    /**
     * 获取一个键对应的浮点数参数值，如果键不存在或无法转换为浮点数，则返回默认值。
     *
     * @param key 参数的键。
     * @param defaultValue 默认值。
     * @return 参数的浮点数值。如果键不存在或无法转换为浮点数，则返回默认值。
     */
    public double getDoubleParam(String key, double defaultValue) {
        String value = getParam(key);
        return ObjectUtils.toDouble(value, defaultValue);
    }

    /**
     * 获取一个键对应的浮点数参数值。
     *
     * @param key 参数的键。
     * @return 参数的浮点数值。如果键不存在或无法转换为浮点数，则返回 {@code null}。
     */
    public float getFloatParam(String key) {
        String value = getParam(key);
        return ObjectUtils.toFloat(value);
    }

    /**
     * 获取一个键对应的浮点数参数值，如果键不存在或无法转换为浮点数，则返回默认值。
     *
     * @param key 参数的键。
     * @param defaultValue 默认值。
     * @return 参数的浮点数值。如果键不存在或无法转换为浮点数，则返回默认值。
     */
    public float getFloatParam(String key, float defaultValue) {
        String value = getParam(key);
        return ObjectUtils.toFloat(value, defaultValue);
    }

    /**
     * 获取一个键对应的布尔参数值。
     *
     * @param key 参数的键。
     * @return 参数的布尔值。如果键不存在或无法转换为布尔值，则返回 {@code null}。
     */
    public boolean getBooleanParam(String key) {
        String value = getParam(key);
        return ObjectUtils.toBoolean(value);
    }

    /**
     * 获取一个键对应的布尔参数值，如果键不存在或无法转换为布尔值，则返回默认值。
     *
     * @param key 参数的键。
     * @param defaultValue 默认值。
     * @return 参数的布尔值。如果键不存在或无法转换为布尔值，则返回默认值。
     */
    public boolean getBooleanParam(String key, boolean defaultValue) {
        String value = getParam(key);
        return ObjectUtils.toBoolean(value, defaultValue);
    }

    /**
     * 获取一个键对应的所有参数值的列表。
     *
     * @param key 参数的键。
     * @return 包含所有值的字符串列表。如果键不存在，返回一个空的只读列表。
     */
    public List<String> getParamList(String key) {
        return this.queryParams.getOrDefault(key, Collections.emptyList());
    }

    /**
     * 获取所有查询参数的只读副本。
     *
     * @return 包含所有查询参数的 Map ({@code Map<String, List<String>>}) 副本。
     */
    public Map<String, List<String>> getQueryParams() {
        return new LinkedHashMap<>(this.queryParams);
    }

    // --- 内部辅助方法 ---

    private static List<String> convertValueToStrings(Object value) {
        if (value == null) {
            return new ArrayList<>(Collections.singletonList(null));
        }
        if (value instanceof Collection) {
            return ((Collection<?>) value).stream()
                    .map(ObjectUtils::toStr)
                    .collect(Collectors.toList());
        }
        if (value.getClass().isArray()) {
            return Arrays.stream((Object[]) value)
                    .map(ObjectUtils::toStr)
                    .collect(Collectors.toList());
        }
        List<String> list = new ArrayList<>();
        list.add(ObjectUtils.toStr(value));
        return list;
    }

    private static String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }

    private static String decode(String value) {
        return URLDecoder.decode(value, StandardCharsets.UTF_8);
    }

    @Override
    public String toString() {
        return build().toString();
    }
}


