package com.example.clients;

import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientHttpServiceGroupConfigurer;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.registry.AbstractHttpServiceRegistrar;
import org.springframework.web.service.registry.ImportHttpServices;

@SpringBootApplication
public class ClientsApplication {

    public static void main(String[] args) {
        SpringApplication.run(ClientsApplication.class, args);
    }

    @Bean
    RestClient.Builder restClientBuilder() {
        return RestClient.builder();
    }


    @Bean
    ApplicationRunner runner(CatFactClient client) {
        return _ -> System.out.println(client.fact());
    }
}


@Configuration
//@ImportHttpServices(group = "cats", types = {CatFactClient.class})
@Import(ClientsConfiguration.MyRegistrar.class)
class ClientsConfiguration {

    static class MyRegistrar extends AbstractHttpServiceRegistrar {

        @Override
        protected void registerHttpServices(GroupRegistry registry, AnnotationMetadata importingClassMetadata) {
            registry.forGroup("cats").register(CatFactClient.class);
        }
    }

    @Bean
    RestClientHttpServiceGroupConfigurer configurer() {
        return groups -> groups.filterByName("cats").forEachClient(
                (group, clientBuilder) -> clientBuilder
                        .defaultHeaders(httpHeaders ->
                                httpHeaders.setBasicAuth("user", "password"))
        );
    }
}

interface CatFactClient {

    @GetExchange("https://catfact.ninja/fact")
    CatFact fact();
}

record CatFact(String fact, int length) {
}