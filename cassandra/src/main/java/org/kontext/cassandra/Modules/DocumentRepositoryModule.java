package org.kontext.cassandra.Modules;

import com.google.inject.AbstractModule;
import org.kontext.cassandra.DocumentRepository;
import org.kontext.cassandra.DocumentRepositoryImpl;

public class DocumentRepositoryModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(DocumentRepository.class).to(DocumentRepositoryImpl.class);
    }
}
