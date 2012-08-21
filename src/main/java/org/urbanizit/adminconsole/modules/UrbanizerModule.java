/*
 * Created by IntelliJ IDEA.
 * User: nicolasger
 * Date: 15/08/12
 * Time: 23:09
 */
package org.urbanizit.adminconsole.modules;

import com.google.inject.AbstractModule;
import org.urbanizit.adminconsole.core.GraphService;
import org.urbanizit.adminconsole.core.services.Neo4jService;

/**
 * @author Nicolas Geraud
 */
public class UrbanizerModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(GraphService.class).to(Neo4jService.class);
    }
}
