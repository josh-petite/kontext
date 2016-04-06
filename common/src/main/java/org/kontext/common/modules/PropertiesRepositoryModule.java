package org.kontext.common.modules;

import com.google.inject.AbstractModule;
import org.kontext.common.repositories.PropertiesRepository;
import org.kontext.common.repositories.PropertiesRepositoryImpl;

public class PropertiesRepositoryModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(PropertiesRepository.class).to(PropertiesRepositoryImpl.class);
    }
}
