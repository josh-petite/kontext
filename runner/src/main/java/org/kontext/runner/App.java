package org.kontext.runner;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import org.kontext.cassandra.DocumentRepository;
import org.kontext.cassandra.modules.DocumentRepositoryModule;
import org.kontext.common.modules.PropertiesRepositoryModule;
import org.kontext.crawler.Controller;
import org.kontext.crawler.modules.ControllerModule;

import java.util.ArrayList;

public class App {
    public static void main(String[] args) {
        ArrayList<AbstractModule> modules = new ArrayList<>();
        modules.add(new PropertiesRepositoryModule());
        modules.add(new DocumentRepositoryModule());
        modules.add(new ControllerModule());

        Injector injector = Guice.createInjector(modules);
        DocumentRepository repository = injector.getInstance(DocumentRepository.class);

        try {
            repository.init();
            Controller c = injector.getInstance(Controller.class);
            c.start();
        } catch (Exception e) {
            e.printStackTrace();
        }

        repository.read();
        repository.purge();
    }
}
