package org.kontext.runner;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import org.kontext.cassandra.DocumentRepository;
import org.kontext.cassandra.Modules.DocumentRepositoryModule;
import org.kontext.cassandra.Modules.PropertiesRepositoryModule;
import org.kontext.crawler.Controller;

import java.util.ArrayList;

public class App {
    public static void main(String[] args) {
        ArrayList<AbstractModule> modules = new ArrayList<>();
        modules.add(new PropertiesRepositoryModule());
        modules.add(new DocumentRepositoryModule());

        Injector injector = Guice.createInjector(modules);
        DocumentRepository repository = injector.getInstance(DocumentRepository.class);

        Controller c = new Controller();

        try {
            repository.init();
            c.start();
        } catch (Exception e) {
            e.printStackTrace();
        }

        repository.read();
        repository.purge();
    }
}
