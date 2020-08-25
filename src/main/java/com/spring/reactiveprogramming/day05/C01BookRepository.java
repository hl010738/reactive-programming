package com.spring.reactiveprogramming.day05;

import com.spring.reactiveprogramming.domain.Book;
import com.spring.reactiveprogramming.domain.BookQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.core.DatabaseClient;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.sql.Where;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.springframework.data.relational.core.query.Criteria.where;

@RequiredArgsConstructor
@Repository
public class C01BookRepository {
    private final DatabaseClient databaseClient;

    public Mono<Void> insert(Book book){
        return databaseClient.insert().into(Book.class)
                .using(book)
                .then();
    }

    public Mono<Void> update(Book book){
        return databaseClient.update().table(Book.class)
                .using(book)
                .then();
    }

    public Mono<Book> findById(String isbn){
        return databaseClient.execute("select * from book where isbn = :isbn")
                .bind("isbn", isbn)
                .as(Book.class)
                .fetch()
                .one();
    }

    public Flux<Book> findAll(){
        return databaseClient.select().from(Book.class)
                .fetch()
                .all();
    }

    public  Mono<Void> delete(String isbn){
        return databaseClient.delete().from(Book.class)
                .matching(where("isbn").is(isbn))
                .then();
    }

    public Flux<Book> findBooksByQuery(BookQuery bookQuery){
        Criteria criteria = Criteria.empty();
        if (!StringUtils.isEmpty(bookQuery.getTitle())){
            criteria = criteria.and(where("title").like(bookQuery.getTitle()));
        }
        if (bookQuery.getMinPrice() != null){
            criteria = criteria.and(where("price").greaterThanOrEquals(bookQuery.getMinPrice()));
        }
        if (bookQuery.getMaxPrice() != null) {
            criteria = criteria.and(where("price").lessThanOrEquals(bookQuery.getMaxPrice()));
        }
        Pageable pageable = PageRequest.of(bookQuery.getPage(), bookQuery.getSize());
        return databaseClient.select().from(Book.class)
                .matching(criteria)
                .page(pageable)
                .fetch()
                .all();
    }
}
