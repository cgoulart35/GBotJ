package com.stormerg.gbotj.services.logging;

import org.slf4j.Logger;

public interface LoggingService {

    Logger getLogger(final Class<?> clazz);

    void setGlobalLogLevel(final String level);

}
