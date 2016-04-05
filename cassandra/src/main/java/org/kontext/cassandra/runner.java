package org.kontext.cassandra;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.kontext.cassandra.Modules.ApplicationPropertiesRepositoryModule;

public class runner {
    public static void main(String[] args) {
        Injector injector = Guice.createInjector(new ApplicationPropertiesRepositoryModule());

        DocumentRepository repository = injector.getInstance(DocumentRepository.class);
        repository.storeDocument("", "");
        repository.read();
        //repository.delete();
    }
}
