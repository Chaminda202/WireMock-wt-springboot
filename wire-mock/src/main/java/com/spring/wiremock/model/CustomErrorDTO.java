package com.spring.wiremock.model;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class CustomErrorDTO {
    private List<String> details;
    private int statusCode;
}