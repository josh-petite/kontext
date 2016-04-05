package org.kontext.cassandra.Modules;

import com.google.inject.AbstractModule;
import org.kontext.cassandra.PropertiesRepository;
import org.kontext.cassandra.PropertiesRepositoryImpl;

public class PropertiesRepositoryModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(PropertiesRepository.class).to(PropertiesRepositoryImpl.class);
    }
}
