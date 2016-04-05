package org.kontext.cassandra.Modules;

import com.google.inject.AbstractModule;
import org.kontext.cassandra.ApplicationPropertiesRepository;
import org.kontext.cassandra.ApplicationPropertiesRepositoryImpl;

public class ApplicationPropertiesRepositoryModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(ApplicationPropertiesRepository.class).to(ApplicationPropertiesRepositoryImpl.class);
    }
}
