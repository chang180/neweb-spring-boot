package com.neweb.web.repository;

import com.neweb.web.model.Cart;
import com.neweb.web.model.Member;
import com.neweb.web.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
    List<Cart> findByMember(Member member);
    Cart findByMemberAndProduct(Member member, Product product);
}
