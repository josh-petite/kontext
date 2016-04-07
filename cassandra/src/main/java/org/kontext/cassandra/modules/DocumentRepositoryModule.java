package org.kontext.cassandra.modules;

import org.kontext.cassandra.documents.DocumentRepository;
import org.kontext.cassandra.documents.DocumentRepositoryImpl;

import com.google.inject.AbstractModule;

public class DocumentRepositoryModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(DocumentRepository.class).to(DocumentRepositoryImpl.class);
    }
}
