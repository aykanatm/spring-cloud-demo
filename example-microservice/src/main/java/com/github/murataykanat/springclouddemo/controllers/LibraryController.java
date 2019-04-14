package com.github.murataykanat.springclouddemo.controllers;

import com.github.murataykanat.springclouddemo.models.Book;
import com.github.murataykanat.springclouddemo.repository.BookRepository;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.Serializable;

@RestController
public class LibraryController implements Serializable {
    private static Log _logger = LogFactory.getLog(LibraryController.class);

    @RequestMapping(value = "/books/{bookId}", method = RequestMethod.GET)
    public ResponseEntity<Book> getBook(@PathVariable String bookId){
        _logger.debug("getBook() >> [" + bookId + "]");
        try{
            if(StringUtils.isNotBlank(bookId)){
                Book book = BookRepository.getInstance().getBook(bookId);
                if(book != null){
                    _logger.debug("<< getBook()");
                    return new ResponseEntity<>(book, HttpStatus.OK);
                }
                else{
                    String warningMessage = "No book found with id '" + bookId + "'";
                    _logger.error(warningMessage);

                    Book notFound = new Book();

                    notFound.setMessage(warningMessage);

                    _logger.debug("<< getBook()");
                    return new ResponseEntity<>(notFound, HttpStatus.INTERNAL_SERVER_ERROR);
                }
            }
            else{
                String errorMessage = "Book ID is blank!";
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
}
