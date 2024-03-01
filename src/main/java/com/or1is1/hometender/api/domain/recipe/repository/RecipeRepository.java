package com.or1is1.hometender.api.domain.recipe.repository;

import com.or1is1.hometender.api.domain.member.Member;
import com.or1is1.hometender.api.domain.recipe.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RecipeRepository extends JpaRepository<Recipe, Long>, RecipeRepositoryInterface {
	public List<Recipe> findByWriter(Member writer);
	public Optional<Recipe> findByWriterAndName(Member writer, String name);

	public void deleteByWriterAndName(Member writer, String name);
}
