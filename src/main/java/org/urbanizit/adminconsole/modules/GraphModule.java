/*
 * Created by IntelliJ IDEA.
 * User: nicolasger
 * Date: 16/08/12
 * Time: 00:22
 */
package org.urbanizit.adminconsole.modules;

import com.google.inject.AbstractModule;
import org.urbanizit.adminconsole.core.ConfigurationService;
import org.urbanizit.adminconsole.core.services.PropertiesConfigurationService;

/**
 * @author Nicolas Geraud
 */
public class GraphModule extends AbstractModule {
    protected void configure() {
        bind(ConfigurationService.class).to(PropertiesConfigurationService.class);
    }
}
