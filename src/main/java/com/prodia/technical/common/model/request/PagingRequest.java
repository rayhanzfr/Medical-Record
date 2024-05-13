package com.prodia.technical.common.model.request;

import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PagingRequest {

  @NotNull
  private Integer page;

  @NotNull
  private Integer pageSize;

  private List<SortBy> sortBy;

}