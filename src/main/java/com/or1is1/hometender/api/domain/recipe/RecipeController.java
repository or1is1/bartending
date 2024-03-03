package com.or1is1.hometender.api.domain.recipe;

import com.or1is1.hometender.api.CommonResponse;
import com.or1is1.hometender.api.domain.recipe.dto.PostAndPutRecipeRequest;
import com.or1is1.hometender.api.domain.recipe.dto.GetRecipeDetailResponse;
import com.or1is1.hometender.api.domain.recipe.dto.GetRecipeListResponse;
import com.or1is1.hometender.api.domain.recipe.exception.RecipeIngredientIsEmptyException;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.or1is1.hometender.api.StringConst.LOGIN_MEMBER;

@RestController
@RequestMapping("/api/recipe")
@RequiredArgsConstructor
public class RecipeController {

	private final RecipeService recipeService;

	@PostMapping
	public CommonResponse<Void> postRecipe(@Validated @RequestBody PostAndPutRecipeRequest postAndPutRecipeRequest,
	                                       @SessionAttribute(LOGIN_MEMBER) Long memberId) {

		if (postAndPutRecipeRequest.recipeIngredientList() == null) {
			throw new RecipeIngredientIsEmptyException();
		}

		recipeService.post(memberId, postAndPutRecipeRequest);

		return new CommonResponse<>(null, null);
	}

	@GetMapping
	public CommonResponse<List<GetRecipeListResponse>> getRecipeList(@SessionAttribute(LOGIN_MEMBER) Long memberId) {

		List<GetRecipeListResponse> getRecipeListResponseList = recipeService.getList(memberId);

		return new CommonResponse<>(null, getRecipeListResponseList);
	}

	@GetMapping("/{recipeId}")
	public CommonResponse<GetRecipeDetailResponse> getRecipeDetail(@PathVariable Long recipeId,
	                                                               @SessionAttribute(LOGIN_MEMBER) Long memberId) {

		GetRecipeDetailResponse getRecipeDetailResponse = recipeService.get(recipeId, memberId);

		return new CommonResponse<>(null, getRecipeDetailResponse);
	}

	@PutMapping("/{recipeId}")
	public CommonResponse<Void> putRecipe(@PathVariable Long recipeId,
	                                      @SessionAttribute(LOGIN_MEMBER) Long memberId,
	                                      @Validated @RequestBody PostAndPutRecipeRequest postAndPutRecipeRequest) {

		recipeService.put(recipeId, memberId, postAndPutRecipeRequest);

		return new CommonResponse<>(null, null);
	}

	@DeleteMapping("/{recipeId}")
	public CommonResponse<Void> deleteRecipe(@PathVariable Long recipeId,
	                                         @SessionAttribute(LOGIN_MEMBER) Long memberId) {

		recipeService.delete(recipeId, memberId);

		return new CommonResponse<>(null, null);
	}
}
