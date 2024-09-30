package cn.taskflow.sample.utils;

import cn.feiliu.taskflow.open.exceptions.NotFoundException;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author SHOUSHEN.LUAN
 * @since 2024-09-30
 */
public class PropertiesReader {
    private Properties properties;

    public PropertiesReader(String filename) throws IOException {
        properties = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream(filename)) {
            if (input == null) {
                throw new NotFoundException("Sorry, unable to find " + filename);
            }
            properties.load(input);
        }
    }

    public String getProperty(String key) {
        return properties.getProperty(key);
    }

    public Boolean getBoolean(String key) {
        String value = getProperty(key);
        if (StringUtils.isNotBlank(value)) {
            return Boolean.parseBoolean(value);
        }
        return null;
    }

    public Long getLong(String key) {
        String value = getProperty(key);
        if (StringUtils.isNotBlank(value)) {
            return Long.parseLong(value);
        }
        return null;
    }

    public Integer getInt(String key) {
        String value = getProperty(key);
        if (StringUtils.isNotBlank(value)) {
            return Integer.parseInt(value);
        }
        return null;
    }

    public String getProperty(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }
}
