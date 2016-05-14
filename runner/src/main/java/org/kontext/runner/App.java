package org.kontext.runner;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;

import org.kontext.cassandra.documents.DocumentRepository;
import org.kontext.cassandra.modules.DataSourceManagerModule;
import org.kontext.cassandra.modules.DocumentRepositoryModule;
import org.kontext.common.modules.PropertiesRepositoryModule;
import org.kontext.crawler.ContentCrawler;
import org.kontext.crawler.modules.ControllerModule;

import java.util.ArrayList;
import java.util.List;

public class App {
    public static void main(String[] args) {
        // TODO: Externalize this somehow so the injector is referencable globally
        // ...or whatever the best approach might be so we don't have to repeat this work
        List<AbstractModule> modules = new ArrayList<>();
        modules.add(new PropertiesRepositoryModule());
        modules.add(new DocumentRepositoryModule());
        modules.add(new ControllerModule());
        modules.add(new DataSourceManagerModule());

        Injector injector = Guice.createInjector(modules);
        DocumentRepository documentRepository = injector.getInstance(DocumentRepository.class);

        try {
            ContentCrawler c = injector.getInstance(ContentCrawler.class);
            c.crawl();
        } catch (Exception e) {
            e.printStackTrace();
        }

        documentRepository.read(null);
        documentRepository.purge(null);
    }
}
