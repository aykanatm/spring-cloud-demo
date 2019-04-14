package com.github.murataykanat.springclouddemo.controllers;

import com.github.murataykanat.springclouddemo.models.Book;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.netflix.ribbon.RibbonClient;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@RibbonClient(name = "example-microservice-loadbalancer")
@RestController
public class LibraryLoadbalancerController {
    private static final Log _logger = LogFactory.getLog(LibraryLoadbalancerController.class);

    @Value("${example.service.name}")
    private String serviceName;

    @LoadBalanced
    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder){
        return builder.build();
    }

    @Autowired
    private DiscoveryClient discoveryClient;
    @Autowired
    private RestTemplate restTemplate;

    @HystrixCommand(fallbackMethod = "getBookErrorFallback")
    @RequestMapping(value = "/books/{bookId}", method = RequestMethod.GET)
    public ResponseEntity<Book> getBook(@PathVariable String bookId){
        _logger.debug("getBook() >>");
        try{
            if(StringUtils.isNotBlank(bookId)){
                String prefix = getPrefix();
                if(StringUtils.isNotBlank(prefix)){
                    _logger.debug("<< getBook()");
                    String url = prefix + serviceName + "/books/" + bookId;
                    _logger.debug("URL: " + url);
                    return restTemplate.getForEntity(url, Book.class);
                }
                else{
                    _logger.error("Prefix is blank!");

                    Book book = new Book();
                    book.setMessage("Prefix is blank!");

                    _logger.debug("<< getBook()");
                    return new ResponseEntity<>(book, HttpStatus.INTERNAL_SERVER_ERROR);
                }
            }
            else{
                String errorMessage = "Book id is blank!";
                _logger.error(errorMessage);

                Book book = new Book();
                book.setMessage(errorMessage);

                _logger.debug("<< getBook()");
                return new ResponseEntity<>(book, HttpStatus.BAD_REQUEST);
            }
        }
        catch (Exception e){
            String errorMessage = "An error occurred while retrieving the book with id '" + bookId + "'. " + e.getLocalizedMessage();
            _logger.error(errorMessage, e);

            Book book = new Book();
            book.setMessage(errorMessage);

            _logger.debug("<< getBook()");
            return new ResponseEntity<>(book, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<Book> getBookErrorFallback(String bookId, Throwable e){
        _logger.debug("getBookErrorFallback() >>");
        Book book = new Book();
        if(StringUtils.isNotBlank(bookId)){
            String errorMessage;
            if(e.getLocalizedMessage() != null){
                errorMessage = "Unable retrieve the book with id '" + bookId + "'. " + e.getLocalizedMessage();
            }
            else{
                errorMessage = "Unable to get response from the service.";
            }

            _logger.error(errorMessage, e);
            book.setMessage(errorMessage);
            _logger.debug("<< getBookErrorFallback()");
            return new ResponseEntity<>(book, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        else{
            String errorMessage = "Selected assets are null!";
            _logger.error(errorMessage);
            book.setMessage(errorMessage);

            _logger.debug("<< getBookErrorFallback()");
            return new ResponseEntity<>(book, HttpStatus.BAD_REQUEST);
        }
    }

    private String getPrefix() throws Exception {
        _logger.debug("getPrefix() >>");
        List<ServiceInstance> instances = discoveryClient.getInstances(serviceName);
        if(!instances.isEmpty()){
            List<Boolean> serviceSecurity = new ArrayList<>();
            for(ServiceInstance serviceInstance: instances){
                serviceSecurity.add(serviceInstance.isSecure());
            }

            boolean result = serviceSecurity.get(0);

            for(boolean isServiceSecure : serviceSecurity){
                result ^= isServiceSecure;
            }

            if(!result){
                String prefix = result ? "https://" : "http://";

                _logger.debug("<< getPrefix() [" + prefix + "]");
                return prefix;
            }
            else{
                String errorMessage = "Not all services have the same transfer protocol!";
                _logger.error(errorMessage);

                throw new Exception(errorMessage);

            }
        }
        else{
            String errorMessage = "No asset services are running!";
            _logger.error(errorMessage);

            throw new Exception(errorMessage);
        }
    }
}
