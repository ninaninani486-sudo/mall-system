package com.delaytask.dto;

import lombok.Data;
import java.util.List;

@Data
public class OrderDTO {
    private String address;
    private String remark;
    private List<OrderItemDTO> items;
}
