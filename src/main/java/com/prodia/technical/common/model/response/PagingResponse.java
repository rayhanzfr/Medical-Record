package com.prodia.technical.common.model.response;

import com.prodia.technical.common.model.request.SortBy;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PagingResponse {

  private Integer page;

  private Integer pageSize;

  private Integer totalPage;

  private Long totalItem;

  private List<SortBy> sortBy;

}
