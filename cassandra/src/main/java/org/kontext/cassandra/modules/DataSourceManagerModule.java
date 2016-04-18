package org.kontext.cassandra.modules;

import com.google.inject.AbstractModule;

import org.kontext.common.CassandraManager;
import org.kontext.data.DataSourceManager;

public class DataSourceManagerModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(DataSourceManager.class).to(CassandraManager.class);
    }
}
