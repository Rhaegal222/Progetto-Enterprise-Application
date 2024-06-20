package it.unical.inf.ea.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration
public class ClockConfig {
    /*
    Rappresenta un'idea astratta di un orologio che può essere utilizzato per ottenere l'ora corrente.
    Fornisce metodi per ottenere l'ora corrente, la data e l'ora, e altre funzionalità correlate.
     */

    @Bean
    public Clock clock() {
        return Clock.systemDefaultZone();
    }
    /*
    Clock.systemDefaultZone() ritorna un'istanza di Clock che utilizza il fuso orario di sistema predefinito.
     */
}
