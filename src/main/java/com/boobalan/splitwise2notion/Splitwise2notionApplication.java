package com.boobalan.splitwise2notion;

import com.boobalan.splitwise2notion.common.TransactionData;
import com.boobalan.splitwise2notion.notion.*;
import com.boobalan.splitwise2notion.splitwise.Expense;
import com.boobalan.splitwise2notion.splitwise.ExpenseInfo;
import com.boobalan.splitwise2notion.splitwise.User;
import com.boobalan.splitwise2notion.splitwise.UserInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.security.oauth2.client.web.server.ServerOAuth2AuthorizedClientRepository;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.net.URI;
import java.time.*;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import static org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction.clientRegistrationId;
import static org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction.oauth2AuthorizedClient;

@SpringBootApplication
@Slf4j
public class Splitwise2notionApplication {

    public static void main(String[] args) {
        SpringApplication.run(Splitwise2notionApplication.class, args);
    }

//    public static void main(String[] args) {
//        ObjectMapper objectMapper = new ObjectMapper();
//
//        ObjectNode rootPropertiesNode = objectMapper.createObjectNode();
//
//
//        String propertyName = "Description";
//        String propertyValue = "101";
//
//        final ObjectNode propertyValueNode = getPropertyValueNode(objectMapper, propertyName, propertyValue);
//
//        rootPropertiesNode.set(propertyName, propertyValueNode);
//
//
//        try {
//            log.debug(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(rootPropertiesNode));
//        } catch (JsonProcessingException e) {
//            throw new RuntimeException(e);
//        }
//
//    }

//    public static void main(String[] args) {
//        final Map<String, String> getenv = System.getenv();
//        System.out.println(System.getProperty("SPLITWISE_CLIENT_ID"));
//    }

    static ObjectNode getPropertyValueNode(final ObjectMapper objectMapper, final String propertyName, final String propertyValue) {
        // for each property
        final ObjectNode propertyValueNode = objectMapper.createObjectNode();


        // populate
        switch (propertyName) {
            case "Source":
                propertyValueNode.putPOJO("select", Collections.singletonMap("name", propertyValue));
                break;
            case "Amount":
                propertyValueNode.put("number", new BigDecimal(propertyValue));
                break;
            case "Date":
//                String dateString = "2022-05-20";
                String dateString = ZonedDateTime.ofInstant(Instant.parse(propertyValue), ZoneId.systemDefault())
                        .toLocalDate().toString();
                propertyValueNode.putPOJO("date", Collections.singletonMap("start", dateString));
                break;
            case "Description":
                final ArrayNode titleNode = objectMapper.createArrayNode();
                final ObjectNode titleValueNode = objectMapper.createObjectNode();
                titleValueNode.put("type", "text");
                titleValueNode.putPOJO("text", Collections.singletonMap("content", propertyValue));
                titleNode.add(titleValueNode);
                propertyValueNode.set("title", titleNode);
        }
        return propertyValueNode;
    }

//    public static void main(String[] args) {
//        final ZonedDateTime zonedDateTime = ZonedDateTime.ofInstant(Instant.parse("2022-05-18T08:00:00Z"), ZoneId.systemDefault());
//        System.out.println(zonedDateTime.toLocalDate());
//    }

//    public static void main(String[] args) {
//        DateTimeFormatter format = DateTimeFormatter.ofPattern("MMM d yyyy  hh:mm a");
//        LocalDateTime localDateTime = LocalDateTime.now();
//        System.out.println(localDateTime);
//        ZoneId zoneId = ZoneId.of("Asia/Kolkata");
//        try {
//            String out1 = localDateTime.format(format);
//            System.out.printf("LEAVING:  %s (%s)%n", out1, zoneId);
//        } catch (DateTimeException exc) {
//            System.out.printf("%s can't be formatted!%n", zoneId);
//            throw exc;
//        }
//        ZonedDateTime zonedDateTime = ZonedDateTime.of(localDateTime, zoneId);
//        System.out.println(zonedDateTime);
//        try {
//            String out1 = zonedDateTime.format(format);
//            System.out.printf("LEAVING:  %s (%s)%n", out1, zoneId);
//        } catch (DateTimeException exc) {
//            System.out.printf("%s can't be formatted!%n", zoneId);
//            throw exc;
//        }
//        zoneId = ZoneId.of("Asia/Tokyo");
//        zonedDateTime = ZonedDateTime.of(localDateTime, zoneId);
//        System.out.println(zonedDateTime);
//        try {
//            String out1 = zonedDateTime.format(format);
//            System.out.printf("LEAVING:  %s (%s)%n", out1, zoneId);
//        } catch (DateTimeException exc) {
//            System.out.printf("%s can't be formatted!%n", zoneId);
//            throw exc;
//        }
//        Instant instant = Instant.now();
//        System.out.println(instant);
//        LocalDateTime localDateTime2 = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
//        System.out.println(ZoneId.systemDefault());
//        System.out.println(localDateTime2);
//
//    }

    @Bean
    WebClient webClient(
            ReactiveClientRegistrationRepository clientRegistrations,
            ServerOAuth2AuthorizedClientRepository authorizedClients) {
        ServerOAuth2AuthorizedClientExchangeFilterFunction oauth =
                new ServerOAuth2AuthorizedClientExchangeFilterFunction(
                        clientRegistrations,
                        authorizedClients);
        oauth.setDefaultOAuth2AuthorizedClient(false);
        return WebClient.builder()
                .filter(oauth)
                .build();
    }

}


@RequiredArgsConstructor
@RestController
class HelloController {

    private final WebClient webClient;

    @GetMapping("/world")
    public String world() {
        return "hello world";
    }

    @GetMapping("/universe")
    public String universe() {
        return "hello universe";
    }


    @GetMapping("/splitwise")
    public Mono<String> splitwise(@RegisteredOAuth2AuthorizedClient("splitwise") OAuth2AuthorizedClient authorizedSplitwiseClient, Authentication userPrincipal) {
        Mono<String> responseString = webClient
                .get()
                .uri("https://secure.splitwise.com/api/v3.0/get_current_user")
                .attributes(oauth2AuthorizedClient(authorizedSplitwiseClient))
                .retrieve()
                .bodyToMono(String.class);

        return responseString.map(s -> "welcome to splitwise: " + s);
    }

    @GetMapping("/notion")
    public Mono<String> notion(@RegisteredOAuth2AuthorizedClient("notion") OAuth2AuthorizedClient authorizedNotionClient, Authentication authentication) {
        Mono<String> responseString = webClient
                .post()
                .uri("https://api.notion.com/v1/search")
                .header("Notion-Version", "2022-02-22")
                .attributes(oauth2AuthorizedClient(authorizedNotionClient))
//                .bodyValue("{}")
                .retrieve()
                .bodyToMono(String.class);

        return responseString.map(s -> "welcome to notion: " + s);
//        return Mono.just("welcome to notion");
    }

    @GetMapping("/okta")
    public String okta(@RegisteredOAuth2AuthorizedClient("okta") OAuth2AuthorizedClient splitwise) {
        return "welcome to okta";
    }
}

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(path = {"/webclient", "/public/webclient"})
class OAuth2WebClientController {

    public static final String HTTPS_API_NOTION_COM_V_1 = "https://api.notion.com/v1";
    public static final String HTTPS_SECURE_SPLITWISE_COM_API_V_3_0 = "https://secure.splitwise.com/api/v3.0";
    private final WebClient webClient;

//    @GetMapping("/splitwise")
//    Mono<String> splitwise() {
//        // @formatter:off
//        Mono<String> responseString = this.webClient
//                .get()
//                .uri("https://secure.splitwise.com/api/v3.0/get_current_user")
//                .attributes(clientRegistrationId("splitwise"))
//                .retrieve()
//                .bodyToMono(String.class);
//        // @formatter:on
//        return responseString.map(s -> "welcome to splitwise: " + s);
//    }

    @GetMapping("/splitwise")
    Mono<ResponseEntity<?>> splitwise() {
        // @formatter:off
        Mono<String> responseString = this.webClient
                .get()
                .uri("https://secure.splitwise.com/api/v3.0/get_current_user")
                .attributes(clientRegistrationId("splitwise"))
                .retrieve()
                .bodyToMono(String.class);
//        URI.create()
        // @formatter:on
        return responseString
                .doOnNext(s -> log.info("Welcome to splitwise" + s))
                .map(s -> ResponseEntity.status(HttpStatus.FOUND).header(HttpHeaders.LOCATION, URI.create("/webclient/notion").toString()).build());
    }


    private final ObjectMapper objectMapper;

    @GetMapping("/splitwise/expense")
    Mono<ExpenseInfo> splitwiseExpense(@RequestParam(value = "limit", defaultValue = "20") int limit, @RequestParam(value = "offset", defaultValue = "0") int offset,
                                       @RequestParam(value = "dated_after", required = true) String dated_after, @RequestParam(value = "dated_before", required = true) String dated_before) {

//        Instant datedAfter = ZonedDateTime.of(LocalDate.of(2022, Month.JANUARY, 1), LocalTime.MIN, ZoneId.of("America/Los_Angeles")).toInstant();
        Instant datedAfter = ZonedDateTime.of(LocalDate.parse(dated_after), LocalTime.MIN, ZoneId.of("America/Los_Angeles")).toInstant();
//        Instant datedBefore = ZonedDateTime.of(LocalDate.of(2022, Month.MAY, 23), LocalTime.MIN, ZoneId.of("America/Los_Angeles")).toInstant();
        Instant datedBefore = ZonedDateTime.of(LocalDate.parse(dated_before), LocalTime.MIN, ZoneId.of("America/Los_Angeles")).toInstant();


        // @formatter:off
        Mono<ExpenseInfo> response = this.webClient
                .get()
                // dated_after, updated_after
                .uri(HTTPS_SECURE_SPLITWISE_COM_API_V_3_0, uriBuilder -> uriBuilder.path("/get_expenses")
                        .queryParam("dated_after", datedAfter.toString())
                        .queryParam("dated_before", datedBefore.toString())
                        .queryParam("limit", limit)
                        .queryParam("offset", offset)
                        .build())
                .attributes(clientRegistrationId("splitwise"))
                .retrieve()
//                .bodyToMono(String.class)
                .bodyToMono(ExpenseInfo.class);

        // @formatter:on

//        return response.map(s -> {
//            log.debug(s);
//            try {
//                return objectMapper.readValue(s, ExpenseInfo.class);
//            } catch (JsonProcessingException e) {
//                throw new RuntimeException(e);
//            }
//        });


//                .map(s -> "Splitwise expense: " + s);
//        return responseString
//                .doOnNext(s -> log.info("Welcome to splitwise" + s))
//                .map(s -> ResponseEntity.status(HttpStatus.FOUND).header(HttpHeaders.LOCATION, URI.create("/webclient/notion").toString()).build());
        return response;
    }

    @GetMapping("/splitwise/expense/allpage")
    Flux<ExpenseInfo> splitwiseExpenseAllPage(@RequestParam(value = "limit", defaultValue = "20") int limit, @RequestParam(value = "offset", defaultValue = "0") int offset,
                                              @RequestParam(value = "dated_after", required = true) String dated_after, @RequestParam(value = "dated_before", required = true) String dated_before) {

        AtomicInteger currentPageIndex = new AtomicInteger(0);

        // ideas from https://stackoverflow.com/a/53370449/6270888, https://stackoverflow.com/a/53789227/6270888 and https://stackoverflow.com/a/50387669/6270888
        return splitwiseExpense(limit, offset, dated_after, dated_before)
                .expand(expenseInfo -> {
                    if (expenseInfo.getExpenses().size() < limit ) {
                        return Mono.empty();
                    } else {
                        int nextPageIndex = currentPageIndex.getAndIncrement() + 1;
                        return Mono.just(1).delayElement(Duration.ofMillis(500)).then(splitwiseExpense(limit, offset + (nextPageIndex * limit), dated_after, dated_before));
                    }
                });
    }

//    @GetMapping("/from")
//    Mono<ResponseEntity<?>> from() {
//
//        return Mono.just(ResponseEntity.status(HttpStatus.FOUND).header(HttpHeaders.LOCATION, URI.create("/webclient/to").toString()).build());
//    }
//    @GetMapping("/to")
//    Mono<ResponseEntity<?>> to() {
//
//        return Mono.just(ResponseEntity.accepted().build());
//    }


    @GetMapping("/notion")
    Mono<ResponseEntity<?>> notion() {
        // @formatter:off
        Mono<String> responseString = webClient
                .post()
                .uri(HTTPS_API_NOTION_COM_V_1 + "/search")
                .header("Notion-Version", "2022-02-22")
                .attributes(clientRegistrationId("notion"))
//                .bodyValue("{}")
                .retrieve()
                .bodyToMono(String.class);
        // @formatter:on
        return responseString
                .doOnNext(s -> log.info("Welcome to notion" + s))
                .map(s -> ResponseEntity.accepted().build());
    }

    @GetMapping("/notion/search")
    Mono<NotionSearchResult> notionSearchForDatabase() {
        // @formatter:off
        return webClient
                .post()
                .uri(HTTPS_API_NOTION_COM_V_1 + "/search")
                .header("Notion-Version", "2022-02-22")
                .attributes(clientRegistrationId("notion"))
                .bodyValue(new Query("Test Log", new Filter("object", "database")))
                .retrieve()
                .bodyToMono(NotionSearchResult.class)
                .map(notionSearchResult -> {
                    log.debug(notionSearchResult.toString());
                    return notionSearchResult;
                });
        // @formatter:on

    }

    @GetMapping("/notion/database")
    Mono<NotionDatabasePageList> notionDatabasePages() {
        // @formatter:off
        Mono<NotionSearchResult> notionSearchResultMono = notionSearchForDatabase();

        return notionSearchResultMono
                .flatMap(notionSearchResult -> {
                            assert notionSearchResult.getResults().size() == 1;
                            return webClient
                                    .post()
                                    .uri(HTTPS_API_NOTION_COM_V_1, uriBuilder -> uriBuilder.path("/databases/{database_id}/query").build(notionSearchResult.getResults().get(0).getId()))
                                    .header("Notion-Version", "2022-02-22")
                                    .attributes(clientRegistrationId("notion"))
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .retrieve()
                                    .bodyToMono(NotionDatabasePageList.class);
                        }
                );

        // @formatter:on
    }

    @RequestMapping(value = "/notion/page", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    Mono<String> notionPage() {
        // @formatter:off
        Mono<NotionDatabasePageList> notionSearchResultMono = notionDatabasePages();

        return notionSearchResultMono
                .flatMap(notionSearchResult -> {
                            return webClient
                                    .get()
                                    .uri(HTTPS_API_NOTION_COM_V_1, uriBuilder -> uriBuilder.path("/pages/{page_id}/").build(notionSearchResult.getResults().get(0).getId()))
                                    .header("Notion-Version", "2022-02-22")
                                    .attributes(clientRegistrationId("notion"))
                                    .retrieve()
                                    .bodyToMono(String.class);
                        }
                );

        // @formatter:on
    }

    @RequestMapping(value = "/notion/page", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    Mono<Map<String, JsonNode>> notionPageCreate(@RequestParam(value = "dated_after", required = true) String dated_after, @RequestParam(value = "dated_before", required = true) String dated_before) {

        // indefinite sequence of database Id's
        final Flux<String> notionDatabaseIdFlux = notionSearchForDatabase()
                .map(notionSearchResult -> {

                            assert notionSearchResult.getResults().size() == 1;
                            return notionSearchResult.getResults().get(0).getId();

                        }
                )
                .cache()   // so that mono is subscribed only once
                .flatMapMany(databaseId -> Flux
                        .generate(synchronousSink -> synchronousSink.next(databaseId)));

        // getting data from Splitwise
//        final Flux<TransactionData> dataFromSplitwise = splitwiseExpense(20, 0)
//                .flatMapMany(expenseInfo -> {
        final Flux<TransactionData> dataFromSplitwise = splitwiseExpenseAllPage(20, 0, dated_after, dated_before)
                .flatMap(expenseInfo -> {
                    final List<Expense> expenses = expenseInfo.getExpenses();
                    return Flux.fromIterable(expenses)
                            .flatMap(expense -> {

                                // don't include "Payment" and "Settle all balances" expenses
                                if (expense.getDescription().equalsIgnoreCase("Payment") || expense.getDescription().equalsIgnoreCase("Settle all balances"))
                                    return Mono.empty();
                                else {
                                    final Optional<BigDecimal> owedShareOptional = expense
                                            .getUsers()
                                            .stream()
                                            .filter(user -> {
                                                final UserInfo userInfo = user.getUser();
                                                return "Sundari".equalsIgnoreCase(userInfo.getLast_name()) && "Amrita".equalsIgnoreCase(userInfo.getFirst_name());
                                            })
                                            .map(User::getNet_balance)
                                            .map(BigDecimal::new)
                                            .filter(netBalance -> netBalance.compareTo(BigDecimal.ZERO) < 0)
                                            .map(BigDecimal::negate)
                                            .reduce(BigDecimal::add);

                                    return Mono.justOrEmpty(owedShareOptional
                                            .map(BigDecimal::toString)
                                            .map(owedShare -> TransactionData.builder().source("Splitwise").description(expense.getDescription()).date(expense.getDate()).amount(owedShare).build()));
                                }


                            });
                });



        // getting database_id to send the data to; from Notion API's
        // @formatter:off

        // sample transaction Data flux
        return dataFromSplitwise
                .map(this::transactionDataToPropertiesNode)
                .zipWith(notionDatabaseIdFlux)
                .delayElements(Duration.ofMillis(500))  // to satisfy throttle limit of webclient apis
                .flatMap(tuple -> {
                    final ObjectNode propertiesNode = tuple.getT1();
                    final String notionDatabaseId = tuple.getT2();
                    final NotionPage notionPage = NotionPage.builder()
                            .parent(ParentDatabase.builder()
                                    .database_id(notionDatabaseId)
                                    .build())
                            .properties(propertiesNode)
                            .build();
                    return webClient
                            .post()
                            .uri(HTTPS_API_NOTION_COM_V_1, uriBuilder -> uriBuilder.path("/pages").build())
                            .body(BodyInserters.fromValue(
                                    notionPage))
                            .header("Notion-Version", "2022-02-22")
                            .attributes(clientRegistrationId("notion"))
                            .retrieve()
                            .bodyToMono(String.class);
                })
                .collectMap(s -> {
                    try {
                        JsonNode response = objectMapper.readTree(s);
                        return response.get("id").asText();
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                }, s -> {
                    try {
                        return objectMapper.readTree(s);
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                });


        // @formatter:on
    }




    @RequestMapping(value = "/notion/page/single", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    Mono<Map<String, JsonNode>> notionPageCreateSingle() {

        final Mono<String> notionDatabaseIdMono = notionSearchForDatabase()
                .map(notionSearchResult -> {

                            assert notionSearchResult.getResults().size() == 1;
                            return notionSearchResult.getResults().get(0).getId();

                        }
                )
                .cache();   // so that mono is subscribed only once



        final TransactionData transactionData = TransactionData.builder().amount("202").date("2022-05-20").description("Test Entry").source("Splitwise").build();

        // sample transaction Data flux
        return Flux.fromIterable(Collections.singletonList(transactionData))
                .map(this::transactionDataToPropertiesNode)
                .zipWith(notionDatabaseIdMono)
                .delaySequence(Duration.ofMillis(500))  // to satisfy throttle limit of webclient apis
                .flatMap(tuple -> {
                    final ObjectNode propertiesNode = tuple.getT1();
                    final String notionDatabaseId = tuple.getT2();
                    final NotionPage notionPage = NotionPage.builder()
                            .parent(ParentDatabase.builder()
                                    .database_id(notionDatabaseId)
                                    .build())
                            .properties(propertiesNode)
                            .build();
                    return webClient
                            .post()
                            .uri(HTTPS_API_NOTION_COM_V_1, uriBuilder -> uriBuilder.path("/pages").build())
                            .body(BodyInserters.fromValue(
                                    notionPage))
                            .header("Notion-Version", "2022-02-22")
                            .attributes(clientRegistrationId("notion"))
                            .retrieve()
                            .bodyToMono(String.class);
                })
                .collectMap(s -> {
                    try {
                        JsonNode response = objectMapper.readTree(s);
                        return response.get("id").asText();
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                }, s -> {
                    try {
                        return objectMapper.readTree(s);
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                });




//        webClient
//                .post()
//                .uri(HTTPS_API_NOTION_COM_V_1, uriBuilder -> uriBuilder.path("/pages/").build())
//                .body(BodyInserters.fromPublisher(
//                        notionDatabaseIdMono
//                                .map(notionDatabaseId -> NotionPage.builder()
//                                        .parent(ParentDatabase.builder()
//                                                .database_id(notionDatabaseId)
//                                                .build())
//                                        .properties()
//                                        .build())
//                        , NotionPage.class)
//                )
//                .header("Notion-Version", "2022-02-22")
//                .attributes(clientRegistrationId("notion"))
//                .retrieve()
//                .bodyToMono(String.class);


//        return notionDatabaseIdMono
//                .flatMap(notionDatabaseId -> {
//                            return
//                        }
//                );
    }

    private ObjectNode transactionDataToPropertiesNode(final TransactionData transactionData) {
        ObjectNode propertiesNode = objectMapper.createObjectNode();

        final ObjectNode sourceNode = Splitwise2notionApplication.getPropertyValueNode(objectMapper, "Source", transactionData.getSource());
        final ObjectNode amountNode = Splitwise2notionApplication.getPropertyValueNode(objectMapper, "Amount", transactionData.getAmount());
        final ObjectNode dateNode = Splitwise2notionApplication.getPropertyValueNode(objectMapper, "Date", transactionData.getDate());
        final ObjectNode descriptionNode = Splitwise2notionApplication.getPropertyValueNode(objectMapper, "Description", transactionData.getDescription());

        propertiesNode.set("Source", sourceNode);
        propertiesNode.set("Amount", amountNode);
        propertiesNode.set("Date", dateNode);
        propertiesNode.set("Description", descriptionNode);

        return propertiesNode;
    }


}

@EnableWebFluxSecurity
class OAuth2ClientSecurityConfig {

    //    @Bean
//    public MapReactiveUserDetailsService userDetailsService() {
//        // @formatter:off
//        UserDetails user = User.withDefaultPasswordEncoder()
//                .username("user")
//                .password("password")
//                .roles("USER")
//                .build();
//        // @formatter:on
//        return new MapReactiveUserDetailsService(user);
//    }
//


    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        http.authorizeExchange()
                .anyExchange()
                .authenticated()
                .and()
                .oauth2Client()

                .and()
                .formLogin();
        return http.build();
    }
}