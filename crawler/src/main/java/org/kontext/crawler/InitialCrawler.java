package org.kontext.crawler;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;
import org.kontext.cassandra.DocumentRepository;
import org.kontext.cassandra.modules.DocumentRepositoryModule;
import org.kontext.common.modules.PropertiesRepositoryModule;
import java.util.ArrayList;
import java.util.Set;
import java.util.regex.Pattern;

public class InitialCrawler extends WebCrawler {
    private final static Pattern FILTERS =
            Pattern.compile(".*(\\.(css|js|gif|jpg|png|mp3|mp3|zip|gz|php))$");

    private DocumentRepository documentRepository;

    public InitialCrawler() {
        // this is AMAZINGLY GROSS, but due to the way this crawler works I have no choice
        // adding anything to the constructor makes its built in factory explode which offers me no chance
        // to inject any dependencies. this is a testing nightmare as well as just very annoying
        // I'd rather do this in the runner instead of here, but there is no other hook sadly
        // we should look at another crawler or write our own
        ArrayList<AbstractModule> modules = new ArrayList<>();
        modules.add(new PropertiesRepositoryModule());
        modules.add(new DocumentRepositoryModule());
        Injector injector = Guice.createInjector(modules);
        documentRepository = injector.getInstance(DocumentRepository.class);
        documentRepository.init();
    }

    /**
     * This method receives two parameters. The first parameter is the page
     * in which we have discovered this new url and the second parameter is
     * the new url. You should implement this function to specify whether
     * the given url should be crawled or not (based on your crawling logic).
     * In this example, we are instructing the crawler to ignore urls that
     * have css, js, git, ... extensions and to only accept urls that start
     * with "http://www.ics.uci.edu/". In this case, we didn't need the
     * referringPage parameter to make the decision.
     */
    @Override
    public boolean shouldVisit(Page referringPage, WebURL url) {
        String href = url.getURL().toLowerCase();
        return !FILTERS.matcher(href).matches();
//                && href.startsWith("http://en.wikipedia.org");
    }

    /**
     * This function is called when a page is fetched and ready
     * to be processed by your program.
     */
    @Override
    public void visit(Page page) {
        String url = page.getWebURL().getURL();
        System.out.println("URL: " + url);

        if (page.getParseData() instanceof HtmlParseData) {
            HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();
            String text = htmlParseData.getText();
            String html = htmlParseData.getHtml();
            Set<WebURL> links = htmlParseData.getOutgoingUrls();

            System.out.println("Text length: " + text.length());
            System.out.println("Html length: " + html.length());
            System.out.println("Number of outgoing links: " + links.size());

            documentRepository.storeDocument(html, text, links.size());
        }
    }
}
