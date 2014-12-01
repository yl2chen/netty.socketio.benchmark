package com.nettysio.benchmark.sio.module;

import com.corundumstudio.socketio.Configuration;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.name.Named;
import com.google.inject.name.Names;

import java.io.IOException;
import java.util.Properties;

/**
 * Created by yulin on 11/11/14.
 */
public class ConfigModule extends AbstractModule {

    @Override
    protected void configure() {
        configureProperties();
    }

    @Provides
    Configuration provideSocketIoServerConfig(@Named("server.port") int port) {
        Configuration config = new Configuration();
        config.setPort(port);
        return config;
    }

    private void configureProperties() {
        Properties props;
        try {
            props = readProps("default.properties");
        } catch (IOException e) {
            throw new RuntimeException("Error loading property files", e);
        }
        Names.bindProperties(binder(), props);
    }

    private Properties readProps(String file) throws IOException {
        Properties props = new Properties();
        props.load(this.getClass().getClassLoader().getResourceAsStream(file));
        return props;
    }
}
