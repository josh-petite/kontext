package org.kontext.crawler.modules;

import com.google.inject.AbstractModule;
import org.kontext.crawler.BasicController;
import org.kontext.crawler.Controller;

public class ControllerModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(Controller.class).to(BasicController.class);
    }
}
