package github.ag777.util.net;

import github.ag777.util.lang.ObjectUtils;
import github.ag777.util.lang.collection.ListUtils;
import github.ag777.util.lang.collection.MapUtils;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 一个通过链式调用流畅地构建、解析和操作 URI 的多功能工具类。
 * <p>
 * 该类旨在成为数据与 URI 之间的桥梁，它不仅能构建 URI，还能轻松解析和修改 URI。
 * 特别地，它在处理查询参数时提供了强大的类型转换能力。
 * </p>
 *
 * <h3>核心特性:</h3>
 * <ul>
 *     <li><b>智能设值:</b> 可使用 {@code setParam(key, Object)} 传入任意类型的值，工具类会自动处理转换。</li>
 *     <li><b>类型安全取值:</b> 可使用 {@code getIntParam(key)}, {@code getStringParamList(key)} 等方法直接获取指定类型的值或列表。</li>
 *     <li><b>灵活的 Null 处理:</b> 能正确解析和构建值为空的参数 (如 {@code ?a=&b})。</li>
 *     <li><b>链式调用:</b> 所有修改操作都返回自身，便于连续操作。</li>
 * </ul>
 *
 * <h3>使用示例:</h3>
 * <pre>{@code
 * // 1. 构建 URI
 * List<Integer> ids = List.of(101, 102);
 * URI uri = UriBuilder.create()
 *         .scheme("https")
 *         .host("api.example.com")
 *         .path("/items")
 *         .setParam("ids", ids)       // 传入 List<Integer>
 *         .setParam("page", 1)
 *         .setParam("isPublished", true) // 传入 boolean
 *         .build();
 * // 结果: https://api.example.com/items?ids=101&ids=102&page=1&isPublished=true
 *
 * // 2. 解析和读取 URI
 * UriBuilder builder = UriBuilder.of(uri);
 * Integer page = builder.getIntParam("page", 0);
 * List<String> idList = builder.getStringParamList("ids");
 * }</pre>
 *
 * @author ag777
 * @version 2.0
 * @since 2025.07
 */
public class UriBuilder {

    private String scheme;
    private String userInfo;
    private String host;
    private int port = -1;
    private String path;
    private final Map<String, Object> queryParams = new LinkedHashMap<>();
    private String fragment;

    private UriBuilder() {
    }

    public static UriBuilder create() {
        return new UriBuilder();
    }

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

    public static UriBuilder of(String uriString) {
        return of(URI.create(uriString));
    }

    // --- 解析和构建 --- 

    private static void parseQuery(String query, Map<String, Object> params) {
        if (query == null || query.isEmpty()) {
            return;
        }
        Stream.of(query.split("&"))
                .forEach(pair -> {
                    int idx = pair.indexOf('=');
                    String key = decode(idx == -1 ? pair : pair.substring(0, idx));
                    String value = idx == -1 || pair.substring(idx + 1).isEmpty() ? null : decode(pair.substring(idx + 1));
                    addValueToMap(key, value, params);
                });
    }

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
                .flatMap(this::paramEntryToQueryStream)
                .collect(Collectors.joining("&"));
    }

    @SuppressWarnings("unchecked")
    private Stream<String> paramEntryToQueryStream(Map.Entry<String, Object> entry) {
        String key = encode(entry.getKey());
        Object value = entry.getValue();
        if (value instanceof List) {
            return ((List<Object>) value).stream()
                    .map(v -> key + "=" + (v == null ? "" : encode(ObjectUtils.toStr(v))));
        } else {
            return Stream.of(key + "=" + (value == null ? "" : encode(ObjectUtils.toStr(value))));
        }    }

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
     * 设置一个查询参数，替换该键已有的任何值。
     * <p>
     * 值可以是任意类型，工具类会自动进行转换：
     * <ul>
     *     <li>集合或数组会被转换为多值参数。</li>
     *     <li>其他类型会通过 {@code Object.toString()} 转换为字符串。</li>
     * </ul>
     *
     * @param key   参数的键。不能为空。
     * @param value 参数的值，可以为 null。
     * @return 当前 {@code UriBuilder} 实例，用于链式调用。
     */
    public UriBuilder setParam(String key, Object value) {
        Objects.requireNonNull(key, "键不能为空");
        this.queryParams.put(key, value);
        return this;
    }

    /**
     * 添加一个查询参数。如果键已存在，其值将被转换为列表以容纳多个值。
     * <p>
     * 值可以是任意类型，处理方式与 {@link #setParam(String, Object)} 相同。
     *
     * @param key   参数的键。不能为空。
     * @param value 参数的值，可以为 null。
     * @return 当前 {@code UriBuilder} 实例，用于链式调用。
     */
    public UriBuilder addParam(String key, Object value) {
        Objects.requireNonNull(key, "键不能为空");
        addValueToMap(key, value, this.queryParams);
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

    // --- Getters --- 
    /**
     * 获取一个键对应的所有参数，并转换为指定类型的列表。
     * 
     * @param key 参数的键。
     * @param clazz 目标类型。
     * @return 包含所有转换后值的列表。如果键不存在或无法转换，则返回 null。
     */
    @SuppressWarnings("unchecked")
    public <T>List<T> getListParam(String key, Class<T> clazz) {
        Object o = this.queryParams.get(key);
        if (o == null) {
            return null;
        }
        if (o instanceof List) {
            return ((List<Object>) o).stream()
                    .map(clazz::cast)
                    .collect(Collectors.toList());
        } else if (o.getClass().isArray()){
            return Arrays.stream((Object[]) o)
                    .map(clazz::cast)
                    .collect(Collectors.toList());
        }
        return ListUtils.of(clazz.cast(o));
    }
    
    /**
     * 获取单个查询参数的字符串形式。
     * <p>如果一个键对应多个值，此方法返回列表中的第一个值。</p>
     *
     * @param key 参数的键。
     * @return 参数的字符串值，如果键不存在则返回 null。
     */
    public String getStringParam(String key) {
        return MapUtils.getStr(this.queryParams, key);
    }

    /**
     * 获取单个查询参数的字符串形式，如果不存在则返回默认值。
     *
     * @param key          参数的键。
     * @param defaultValue 默认值。
     * @return 参数的字符串值。
     */
    public String getStringParam(String key, String defaultValue) {
        return MapUtils.getStr(this.queryParams, key, defaultValue);
    }

    /**
     * 获取单个查询参数的 Integer 形式。
     *
     * @param key 参数的键。
     * @return 参数的 Integer 值，如果键不存在或无法转换则返回 null。
     */
    public Integer getIntParam(String key) {
        return MapUtils.getInt(this.queryParams, key);
    }

    /**
     * 获取单个查询参数的 int 形式，如果不存在则返回默认值。
     *
     * @param key          参数的键。
     * @param defaultValue 默认值。
     * @return 参数的 int 值。
     */
    public int getIntParam(String key, int defaultValue) {
        return MapUtils.getInt(this.queryParams, key, defaultValue);
    }

    /**
     * 获取单个查询参数的 Long 形式。
     *
     * @param key 参数的键。
     * @return 参数的 Long 值，如果键不存在或无法转换则返回 null。
     */
    public Long getLongParam(String key) {
        return MapUtils.getLong(this.queryParams, key);
    }

    /**
     * 获取单个查询参数的 long 形式，如果不存在则返回默认值。
     *
     * @param key          参数的键。
     * @param defaultValue 默认值。
     * @return 参数的 long 值。
     */
    public long getLongParam(String key, long defaultValue) {
        return MapUtils.getLong(this.queryParams, key, defaultValue);
    }

    /**
     * 获取单个查询参数的 Double 形式。
     *
     * @param key 参数的键。
     * @return 参数的 Double 值，如果键不存在或无法转换则返回 null。
     */
    public Double getDoubleParam(String key) {
        return MapUtils.getDouble(this.queryParams, key);
    }

    /**
     * 获取单个查询参数的 double 形式，如果不存在则返回默认值。
     *
     * @param key          参数的键。
     * @param defaultValue 默认值。
     * @return 参数的 double 值。
     */
    public double getDoubleParam(String key, double defaultValue) {
        return MapUtils.getDouble(this.queryParams, key, defaultValue);
    }

    /**
     * 获取单个查询参数的 Boolean 形式。
     *
     * @param key 参数的键。
     * @return 参数的 Boolean 值，如果键不存在或无法转换则返回 null。
     */
    public Boolean getBooleanParam(String key) {
        return MapUtils.getBoolean(this.queryParams, key);
    }

    /**
     * 获取单个查询参数的 boolean 形式，如果不存在则返回默认值。
     *
     * @param key          参数的键。
     * @param defaultValue 默认值。
     * @return 参数的 boolean 值。
     */
    public boolean getBooleanParam(String key, boolean defaultValue) {
        return MapUtils.getBoolean(this.queryParams, key, defaultValue);
    }

    /**
     * 获取一个键对应的所有参数，并转换为字符串列表。
     *
     * @param key 参数的键。
     * @return 包含所有转换后值的列表。如果键不存在，返回一个空列表。
     */
    public List<String> getStringParamList(String key) {
        return getParamList(key, ObjectUtils::toStr);
    }

    /**
     * 获取一个键对应的所有参数，并转换为 Integer 列表。
     *
     * @param key 参数的键。
     * @return 包含所有转换后值的列表。如果键不存在或某项无法转换，则该项会被忽略。
     */
    public List<Integer> getIntParamList(String key) {
        return getParamList(key, ObjectUtils::toInt);
    }

    /**
     * 获取一个键对应的所有参数，并转换为 Long 列表。
     *
     * @param key 参数的键。
     * @return 包含所有转换后值的列表。如果键不存在或某项无法转换，则该项会被忽略。
     */
    public List<Long> getLongParamList(String key) {
        return getParamList(key, ObjectUtils::toLong);
    }

    /**
     * 获取一个键对应的所有参数，并转换为 Double 列表。
     *
     * @param key 参数的键。
     * @return 包含所有转换后值的列表。如果键不存在或某项无法转换，则该项会被忽略。
     */
    public List<Double> getDoubleParamList(String key) {
        return getParamList(key, ObjectUtils::toDouble);
    }

    /**
     * 获取所有查询参数的只读副本。
     *
     * @return 包含所有查询参数的 Map 副本。
     */
    public Map<String, Object> getQueryParams() {
        return new LinkedHashMap<>(this.queryParams); // 返回副本
    }

    // --- 内部辅助方法 ---

    @SuppressWarnings("unchecked")
    private static void addValueToMap(String key, Object value, Map<String, Object> params) {
        Object processedValue = processValue(value);
        Object existingValue = params.get(key);

        if (existingValue == null) {
            params.put(key, processedValue);
        } else if (existingValue instanceof List) {
            if (processedValue instanceof List) {
                ((List<Object>) existingValue).addAll((List<Object>) processedValue);
            } else {
                ((List<Object>) existingValue).add(processedValue);
            }
        } else {
            List<Object> values = new ArrayList<>();
            values.add(existingValue);
            if (processedValue instanceof List) {
                values.addAll((List<Object>) processedValue);
            } else {
                values.add(processedValue);
            }
            params.put(key, values);
        }
    }

    private static Object processValue(Object value) {
        if (value == null) {
            return null;
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
        return ObjectUtils.toStr(value);
    }

    @SuppressWarnings("unchecked")
    private <T> List<T> getParamList(String key, java.util.function.Function<Object, T> converter) {
        Object value = this.queryParams.get(key);
        if (value instanceof List) {
            return ((List<Object>) value).stream()
                    .map(converter)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        } else if (value != null) {
            T converted = converter.apply(value);
            return converted == null ? new ArrayList<>() : List.of(converted);
        }
        return new ArrayList<>();
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

