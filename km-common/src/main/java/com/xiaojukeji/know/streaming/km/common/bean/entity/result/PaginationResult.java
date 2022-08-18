package com.xiaojukeji.know.streaming.km.common.bean.entity.result;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xiaojukeji.know.streaming.km.common.bean.dto.pagination.PaginationBaseDTO;
import com.xiaojukeji.know.streaming.km.common.bean.entity.pagination.Pagination;
import com.xiaojukeji.know.streaming.km.common.bean.entity.pagination.PaginationData;
import com.xiaojukeji.know.streaming.km.common.constant.PaginationConstant;
import com.xiaojukeji.know.streaming.km.common.utils.ValidateUtils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 分页结果
 * @author zengqiao
 * @date 21/07/13
 */
@Data
@ToString
@ApiModel(description = "分页结果")
public class PaginationResult<T> extends BaseResult {
    private static final long serialVersionUID = -7850208355453831640L;

    @ApiModelProperty(value = "数据")
    private PaginationData<T> data;

    // 不要修改为public，否则外部方法直接调用的时候，可能丢失页面信息
    private PaginationResult(PaginationBaseDTO dto) {
        this.data = new PaginationData<>();

        if (dto != null) {
            this.data.setPagination(new Pagination(0, dto.getPageNo(), dto.getPageSize()));
        } else {
            this.data.setPagination(new Pagination(0, PaginationConstant.DEFAULT_PAGE_NO, PaginationConstant.DEFAULT_PAGE_SIZE));
        }

        this.data.setBizData(Collections.emptyList());
        this.setCode(ResultStatus.SUCCESS.getCode());
        this.setMessage(ResultStatus.SUCCESS.getMessage());
    }

    public static <T> PaginationResult<T> buildSuc(PaginationBaseDTO dto) {
        return new PaginationResult<>(dto);
    }

    public static <T, U> PaginationResult<T> buildSuc(List<T> dataList, PaginationResult<U> anotherPaginationResult) {
        PaginationResult<T> paginationResult = new PaginationResult<>(null);
        paginationResult.data = new PaginationData<>();
        paginationResult.data.setPagination(anotherPaginationResult.getData().getPagination());
        paginationResult.data.setBizData(dataList);
        return paginationResult;
    }

    public static <T, U> PaginationResult<T> buildSuc(List<T> bizDataList, IPage<U> iPage) {
        return PaginationResult.buildSuc(bizDataList, iPage.getTotal(), iPage.getCurrent(), iPage.getSize());
    }

    public static <T> PaginationResult<T> buildSuc(List<T> bizDataList, long total, long pageNo, long pageSize) {
        PaginationResult<T> paginationResult = PaginationResult.buildSuc(null);

        PaginationData<T> paginationData = new PaginationData<>();
        paginationData.setBizData(bizDataList == null? new ArrayList<>(): bizDataList);
        paginationData.setPagination(new Pagination(total, pageNo, pageSize));

        paginationResult.setData(paginationData);
        return paginationResult;
    }

    public static <T> PaginationResult<T> buildFailure(ResultStatus rs, PaginationBaseDTO dto) {
        PaginationResult<T> paginationResult = new PaginationResult<>(dto);
        paginationResult.setCode(rs.getCode());
        paginationResult.setMessage(rs.getMessage());
        return paginationResult;
    }

    public static <T, U> PaginationResult<T> buildFailure(Result<U> anotherResult, PaginationBaseDTO dto) {
        PaginationResult<T> paginationResult = new PaginationResult<>(dto);

        paginationResult.setCode(anotherResult.getCode());
        paginationResult.setMessage(anotherResult.getMessage());
        return paginationResult;
    }

    public static <T> PaginationResult<T> buildFailure(String message, PaginationBaseDTO dto) {
        PaginationResult<T> paginationResult = new PaginationResult<>(dto);

        paginationResult.setCode(ResultStatus.FAIL.getCode());
        paginationResult.setMessage(message);
        return paginationResult;
    }

    public static <T, U> PaginationResult<T> buildFailure(PaginationResult<U> anotherPaginationResult, PaginationBaseDTO dto) {
        PaginationResult<T> paginationResult = new PaginationResult<>(dto);

        paginationResult.setCode(anotherPaginationResult.getCode());
        paginationResult.setMessage(anotherPaginationResult.getMessage());
        return paginationResult;
    }

    public boolean hasData() {
        return data != null && !ValidateUtils.isEmptyList(data.getBizData());
    }
}
