package com.or1is1.hometender.api.domain.ingredient.repository;

import com.or1is1.hometender.api.domain.ingredient.Ingredient;
import com.or1is1.hometender.api.domain.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface IngredientRepository extends JpaRepository<Ingredient, Long>, IngredientRepositoryInterface {
	List<Ingredient> findByWriter(Member writer);
	Optional<Ingredient> findByNameAndWriter(String name, Member writer);
}
